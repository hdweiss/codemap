package com.hdweiss.codemap.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;

import com.hdweiss.codemap.util.Utils;

public class Cscope {
	private final static String EXE_FILENAME = "cscope";
	private final static String BUILDINDEX_OPTIONS = "-b -q";
	private final static String SEARCH_PATTERN = "-iname \'*.c\' -o -iname \'*.h\'";
	
	private Context context;
	private String cscopeExecPath = "";

	public Cscope(Context context) {
		this.context = context;

		prepareExecutable();
	}

	private void prepareExecutable() {
		try {
			context.openFileInput(EXE_FILENAME);
		} catch (FileNotFoundException e) {
			copyExecutableToInternalStorage();
		} finally {
			File file = context.getFileStreamPath(EXE_FILENAME);
			if (file.exists() && file.canExecute()) {
				this.cscopeExecPath = file.getAbsolutePath();
			}
		}
	}

	private void copyExecutableToInternalStorage() {
		try {
			InputStream is = context.getAssets().open(EXE_FILENAME);
			byte[] buffer = new byte[is.available()];
			is.read(buffer);
			is.close();

			FileOutputStream fos = context.openFileOutput(EXE_FILENAME,
					Context.MODE_PRIVATE);
			fos.write(buffer);
			fos.close();

			File file = context.getFileStreamPath(EXE_FILENAME);
			file.setExecutable(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String runCommand(Project project, String options) {
		return runCommand(project, options, true);
	}
	
	
	public String runCommand(Project project, String options, boolean includeIndex) {
		if (this.cscopeExecPath == null || this.cscopeExecPath.isEmpty()) {
			Log.e("CodeMap", "Could not get path to" + EXE_FILENAME + "executable");
			return "";
		}
		
		String command = getCscopeCommand(project, options, includeIndex);
		
		File tmpdir = context.getFilesDir();
		String[] environment = {"TMPDIR=" + tmpdir.getAbsolutePath()};
		String output = Utils.runCommand(command, environment);
		return output;
	}
	
	private String getCscopeCommand(Project project, String options, boolean includeIndex) {
		StringBuilder builder = new StringBuilder();
		builder.append(this.cscopeExecPath).append(" ");
		if(includeIndex)
			builder.append("-i ").append(getNamefilePath(project.getName())).append(" ");
		builder.append("-f ").append(getReffilePath(project.getName())).append(" ");
		builder.append("-P ").append(project.getPath()).append(" ");
		builder.append(options);
		return builder.toString();
	}
	
	/***************/
	
	public String generateNamefile(Project project) {
		String command = "find " + project.getPath() + " " + SEARCH_PATTERN
				+ ">" + getNamefilePath(project.getName());
		return Utils.runCommand(command);
	}
	
	public void deleteNamefile(String projectName) {
		File file = new File(getNamefilePath(projectName));
		file.delete();
	}

	private String getNamefileName(String projectName) {
		return projectName + ".files";
	}
	
	private String getNamefilePath (String projectName) {
		return context.getFileStreamPath(getNamefileName(projectName)).getAbsolutePath();
	}
	
	public FileInputStream getNamefileStream(String projectName) throws FileNotFoundException {
		return context.openFileInput(getNamefileName(projectName));
	}
	
	public String[] getFiles(Project project) throws FileNotFoundException {		
		FileInputStream stream = getNamefileStream(project.getName());
		String contents = Utils.inputStreamToString(stream);
		
		String[] files = contents.trim().split("\n");
		
		int pathLength = project.getPath().length();
		for(int i = 0; i < files.length; i++)
			files[i] = files[i].substring(pathLength);
		
		Arrays.sort(files);
		
		return files;
	}
	
	/******************/

	public String generateReffile(Project project) {
		return runCommand(project, BUILDINDEX_OPTIONS);
	}

	public void deleteReffile(String projectName) {
		File file = new File(getReffilePath(projectName));
		file.delete();
	}

	private String getReffileName(String projectName) {
		return projectName + ".out";
	}
	
	private String getReffilePath(String projectName) {
		return context.getFileStreamPath(getReffileName(projectName)).getAbsolutePath();
	}
	
	public FileInputStream getReffileStream(String projectName) throws FileNotFoundException {
		return context.openFileInput(getReffileName(projectName));
	}
	
	/******************/
	
	private CscopeEntry getFunctionEntry(Project project, String functionName) {
		String output = runCommand(project, "-L -1 " + functionName);
		String[] entries = output.trim().split("\n");
		
		CscopeEntry cscopeEntry = new CscopeEntry(entries[0]);
		return cscopeEntry;
	}
	
	private int getFunctionEndLine(Project project, CscopeEntry cscopeEntry) {	
		String options = "-L -1 '.*' " + cscopeEntry.file;
		String symbols = runCommand(project, options, false);
		
		CscopeEntry nextEntry = getNextEntry(symbols, cscopeEntry);
		
		if(nextEntry == null)
			return Integer.MAX_VALUE;
			
		return nextEntry.lineNumber - 2;
	}
	
	private CscopeEntry getNextEntry(String symbols, CscopeEntry entry) {
		String[] entries = symbols.trim().split("\n");
		
		for(int i = 0; i < entries.length; i++) {
			CscopeEntry cscopeEntry = new CscopeEntry(entries[i]);
			if(cscopeEntry.lineNumber == entry.lineNumber) {
				if(entries.length > i+1)
					return new CscopeEntry(entries[i+1]);
			}
		}
		
		return null;
	}
	
	
	public String getFunction (Project project, String functionName) {
		CscopeEntry cscopeEntry = getFunctionEntry(project, functionName);
		int endLine = getFunctionEndLine(project, cscopeEntry);
		
		String source = Utils.getFileFragment(cscopeEntry.file, cscopeEntry.lineNumber, endLine);
		int index = source.lastIndexOf("}");
		
		if(index != -1)
			return source.substring(0, index+1);
		else
			return source;
	}
	
	
	public ArrayList<CscopeEntry> getReferences(Project project, String functionName) {
		CscopeEntry cscopeEntry = getFunctionEntry(project, functionName);
		int endLine = getFunctionEndLine(project, cscopeEntry);
		
		String options = "-L -2 '" + functionName + "'";
		String symbols = runCommand(project, options);
		ArrayList<CscopeEntry> references = getAllReferences(symbols, cscopeEntry.lineNumber, endLine);
		return references;
	}
	
	private ArrayList<CscopeEntry> getAllReferences(String symbols, int startLine, int endLine) {
		ArrayList<CscopeEntry> references = new ArrayList<CscopeEntry>();
		
		String[] entries = symbols.trim().split("\n");

		if(entries.length == 1 && entries[0].isEmpty())
			return references;
		
		for (int i = 0; i < entries.length; i++) {
			CscopeEntry entry = new CscopeEntry(entries[i]);
			entry.lineNumber -= startLine;
			references.add(entry);
		}
		
		return references;
	}

	
	/******************/
	
	public ArrayList<String> getDeclarations(String filename, Project project) {
		ArrayList<String> result = new ArrayList<String>();
		
		String options = "-k -L -1 '.*' " + project.getPath() + "/" + filename;
		String symbols = runCommand(project, options, false);
				
		for(CscopeEntry entry: getAllReferences(symbols, 0, 0))
			result.add(entry.name);
		
		return cleanSymbols(result);
	}
	
	
	private ArrayList<String> cleanSymbols(ArrayList<String> symbols) {
		ArrayList<String> result = new ArrayList<String>();
		for(String symbol: symbols) {
			if(symbol.startsWith("#")) {
				continue;
			}
			
			int startParenthesis = symbol.indexOf("(");
			
			if(startParenthesis == -1)
				continue;
			
			String substring = symbol.substring(0, startParenthesis).trim();
			int funcNameStart = substring.lastIndexOf(" ");
			
			if(funcNameStart == -1)
				funcNameStart = 0;
			
			String funcName = substring.substring(funcNameStart, substring.length()).trim();
			result.add(funcName);
		}
		
		return result;
	}
	
	public HashMap<String,ArrayList<CscopeEntry>> getAllDeclarations(Project project) {
		HashMap<String, ArrayList<CscopeEntry>> result = new HashMap<String, ArrayList<CscopeEntry>>();
		
		String options = "-k -L -1 '.*' ";
		String symbols = runCommand(project, options, true);
				
		for(CscopeEntry entry: getAllReferences(symbols, 0, 0)) {
			ArrayList<CscopeEntry> list = result.get(entry.file);
			
			if(list == null) {
				list = new ArrayList<CscopeEntry>();
				result.put(entry.file, list);
			}
			
			list.add(entry);
		}
		
		return result;
	}
}
