package com.hdweiss.codemap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.hdweiss.codemap.util.Utils;

import android.content.Context;
import android.util.Log;

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
		if (this.cscopeExecPath == null || this.cscopeExecPath.isEmpty()) {
			Log.e("CodeMap", "Could not get path to" + EXE_FILENAME + "executable");
			return "";
		}
		
		String command = getCscopeCommand(projectName, projectPath, BUILDINDEX_OPTIONS);
		
		File tmpdir = context.getFilesDir();
		String[] environment = {"TMPDIR=" + tmpdir.getAbsolutePath()};
		String output = Utils.runCommand(command, environment);
		
		return output;
	}

	public void deleteReffile(String projectName) {
		File file = new File(getReffilePath(projectName));
		file.delete();
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
	
	private String getReffileName(String projectName) {
		return projectName + ".out";
	}
	
	private String getReffilePath(String projectName) {
		return context.getFileStreamPath(getReffileName(projectName)).getAbsolutePath();
	}
	
	public FileInputStream getReffileStream(String projectName) throws FileNotFoundException {
		return context.openFileInput(getReffileName(projectName));
	}
}
