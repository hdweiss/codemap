package com.hdweiss.codemap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
		
		File tmpdir = context.getFilesDir();
		String[] environment = {"TMPDIR=" + tmpdir.getAbsolutePath()};
		String output = Utils.runCommand(command, environment);
		return output;
	}
	
	private String getCscopeCommand(String projectName, String projectPath, String options) {
		StringBuilder builder = new StringBuilder();
		builder.append(this.cscopeExecPath).append(" ");
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
	
	public String getFunction (String projectName, String projectPath, String functionName) {
		String output = runCommand(projectName, projectPath, "-L -1 " + functionName);
		String[] entries = output.trim().split("\n");
		
		CscopeEntry cscopeEntry = new CscopeEntry(entries[0]);
		
		String options = "-L -1 '.*' \\| head" ; //| grep \'" + cscopeEntry.file
				//+ "\' | grep -A 1 \'" + cscopeEntry.name + "\'";
		String symbols = runCommand(projectName, projectPath, options);
		Log.d("CodeMap", "Got :" + symbols);
		
		Log.d("CodeMap", cscopeEntry.toString());
		return Utils.getFileFragment(cscopeEntry.file, cscopeEntry.lineNumber, cscopeEntry.lineNumber+10);
	}
}
