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
import java.util.NoSuchElementException;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.hdweiss.codemap.util.Utils;

public class Cscope {
	private final static String EXE_FILENAME = "cscope";
	private final static String BUILDINDEX_OPTIONS = "-b -q";
	private final static String SEARCH_PATTERN = "-iname \'*.c\' -o -iname \'*.h\'";
	
	private final static String CSCOPE_NAMEFILE = "cscope.files";
	private final static String CSCOPE_REFFILE = "cscope.out";
	
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
		builder.append("-P ").append(project.getSourcePath(context)).append(" ");
		builder.append(options);
		return builder.toString();
	}
	
	/***************/
	
	public String generateNamefile(Project project) {
		String command = "find " + project.getSourcePath(context) + " " + SEARCH_PATTERN
				+ ">" + getNamefilePath(project.getName());
		return Utils.runCommand(command);
	}
	
	public void deleteNamefile(String projectName) {
		File file = new File(getNamefilePath(projectName));
		file.delete();
	}
	
	private String getNamefilePath (String projectName) {
		File directory = Project.getProjectDirectory(projectName, context);
		return directory.getAbsolutePath() + File.separator + CSCOPE_NAMEFILE;
	}
	
	public FileInputStream getNamefileStream(String projectName) throws FileNotFoundException {
		File namefile = new File(Project.getProjectDirectory(projectName,
				context) + File.separator + CSCOPE_NAMEFILE);
		return new FileInputStream(namefile);
	}
	
	public String[] getFiles(Project project) throws FileNotFoundException {		
		FileInputStream stream = getNamefileStream(project.getName());
		
		String contents;
		try {
			 contents = Utils.inputStreamToString(stream);
		} catch (NoSuchElementException e) {
			throw new FileNotFoundException(e.getLocalizedMessage());
		}
		
		String[] files = contents.trim().split("\n");
		
		int pathLength = project.getSourcePath(context).length() + 1;
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
	
	private String getReffilePath(String projectName) {		
		File directory = Project.getProjectDirectory(projectName, context);
		return directory.getAbsolutePath() + File.separator + CSCOPE_REFFILE;
	}
	
	public FileInputStream getReffileStream(String projectName) throws FileNotFoundException {
		File namefile = new File(Project.getProjectDirectory(projectName,
				context) + File.separator + CSCOPE_REFFILE);
		return new FileInputStream(namefile);
	}
	
	/******************/
	
	private CscopeEntry getFunctionEntry(Project project, String functionName, String fileName) {
		String output = runCommand(project, "-L -1 " + functionName);
		String[] entries = output.trim().split("\n");
		
		if (TextUtils.isEmpty(fileName))
			return new CscopeEntry(entries[0]);
		
		String absoluteFilePath = new File(project.getSourcePath(context), fileName).getAbsolutePath();
		for (String entry: entries) {
			CscopeEntry cscopeEntry = new CscopeEntry(entry);
			
			if (cscopeEntry.file.equals(absoluteFilePath))
				return cscopeEntry;
		}
		
		throw new IllegalArgumentException("Url " + fileName + ":" + functionName + " not found");
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
	
	
	public String getFunction (Project project, String functionName, String fileName) {
		CscopeEntry cscopeEntry = getFunctionEntry(project, functionName, fileName);
		int endLine = getFunctionEndLine(project, cscopeEntry);
		
		String source = Utils.getFileFragment(cscopeEntry.file, cscopeEntry.lineNumber, endLine);
		int index = source.lastIndexOf("}");
		
		if(index != -1)
			return source.substring(0, index+1);
		else
			return source;
	}
	
	public String getFile (Project project, String fileName) throws FileNotFoundException {
		FileInputStream stream = new FileInputStream(
				project.getSourcePath(context) + File.separator + fileName);
		String content = Utils.inputStreamToString(stream);
		return content;
	}
	
	public ArrayList<CscopeEntry> getReferences(Project project, String functionName, String fileName) {
		CscopeEntry cscopeEntry = getFunctionEntry(project, functionName, fileName);
		int endLine = getFunctionEndLine(project, cscopeEntry);
		
		String options = "-L -2 '" + functionName + "'";
		String symbols = runCommand(project, options);
		ArrayList<CscopeEntry> references = parseReferences(symbols, cscopeEntry.lineNumber, endLine);
		return references;
	}
	
	public ArrayList<CscopeEntry> getFileReferences(Project project, String fileName) {
		String options = "-L -2 '.*' " + project.getSourcePath(context) + fileName;
		String symbols = runCommand(project, options, false);
		ArrayList<CscopeEntry> references = parseReferences(symbols, 0, Integer.MAX_VALUE);
		return references;
	}
	
	private ArrayList<CscopeEntry> parseReferences(String symbols, int startLine, int endLine) {
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
		
		String options = "-k -L -1 '.*' " + project.getSourcePath(context) + "/" + filename;
		String symbols = runCommand(project, options, false);
				
		for(CscopeEntry entry: parseReferences(symbols, 0, 0))
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
				
		for(CscopeEntry entry: parseReferences(symbols, 0, 0)) {
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
