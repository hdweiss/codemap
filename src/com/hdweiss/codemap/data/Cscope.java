package com.hdweiss.codemap.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

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
	
	public String runCommand(String projectName, String projectPath, String options) {
		if (this.cscopeExecPath == null || this.cscopeExecPath.isEmpty()) {
			Log.e("CodeMap", "Could not get path to" + EXE_FILENAME + "executable");
			return "";
		}
		
		String command = getCscopeCommand(projectName, projectPath, options);
		Log.d("CodeMap", "Running command " + command);
		
		File tmpdir = context.getFilesDir();
		String[] environment = {"TMPDIR=" + tmpdir.getAbsolutePath()};
		String output = Utils.runCommand(command, environment);
		return output;
	}
	
	private String getCscopeCommand(String projectName, String projectPath, String options) {
		return getCscopeCommand(projectName, projectPath, options, true);
	}
	
	private String getCscopeCommand(String projectName, String projectPath, String options, boolean includeIndex) {
		StringBuilder builder = new StringBuilder();
		builder.append(this.cscopeExecPath).append(" ");
		if(includeIndex)
			builder.append("-i ").append(getNamefilePath(projectName)).append(" ");
		builder.append("-f ").append(getReffilePath(projectName)).append(" ");
		builder.append("-P ").append(projectPath).append(" ");
		builder.append(options);
		return builder.toString();
	}
	
	/***************/
	
	public String generateNamefile(String projectName, String path) {
		String command = "find " + path + " " + SEARCH_PATTERN
				+ ">" + getNamefilePath(projectName);
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
	
	/******************/

	public String generateReffile(String projectName, String projectPath) {
		return runCommand(projectName, projectPath, BUILDINDEX_OPTIONS);
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
	
	private CscopeEntry getFunctionEntry(String projectName, String projectPath, String functionName) {
		String output = runCommand(projectName, projectPath, "-L -1 " + functionName);
		String[] entries = output.trim().split("\n");
		
		CscopeEntry cscopeEntry = new CscopeEntry(entries[0]);
		return cscopeEntry;
	}
	
	private int getFunctionEndLine(String projectName, String projectPath, CscopeEntry cscopeEntry) {	
		String options = "-L -1 '.*' " + cscopeEntry.file;
		String symbols = runCommandSpecial(projectName, projectPath, options);
		
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
	
	
	public String getFunction (String projectName, String projectPath, String functionName) {
		CscopeEntry cscopeEntry = getFunctionEntry(projectName, projectPath, functionName);
		int endLine = getFunctionEndLine(projectName, projectPath, cscopeEntry);
		
		return Utils.getFileFragment(cscopeEntry.file, cscopeEntry.lineNumber, endLine);
	}
	
	
	public ArrayList<CscopeEntry> getReferences(String projectName, String projectPath, String functionName) {
		CscopeEntry cscopeEntry = getFunctionEntry(projectName, projectPath, functionName);
		int endLine = getFunctionEndLine(projectName, projectPath, cscopeEntry);
		
		String options = "-L -2 '" + functionName + "'";
		String symbols = runCommand(projectName, projectPath, options);
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
	
	public String runCommandSpecial(String projectName, String projectPath, String options) {
		if (this.cscopeExecPath == null || this.cscopeExecPath.isEmpty()) {
			Log.e("CodeMap", "Could not get path to" + EXE_FILENAME + "executable");
			return "";
		}
		
		String command = getCscopeCommand(projectName, projectPath, options, false);
		
		File tmpdir = context.getFilesDir();
		String[] environment = {"TMPDIR=" + tmpdir.getAbsolutePath()};
		String output = Utils.runCommand(command, environment);
		return output;
	}
	
	/******************/
	
	public ArrayList<String> getDeclarations(String filename, String projectName, String projectPath) {
		ArrayList<String> result = new ArrayList<String>();
		
		String options = "-k -L -1 '.*' " + filename;
		String symbols = runCommandSpecial(projectName, projectPath, options);
		
		Log.d("CodeMap", "Got declarations of " + filename + "\n" + symbols);
		
		for(CscopeEntry entry: getAllReferences(symbols, 0, 0))
			result.add(entry.name);
		
		return result;
	}
}
