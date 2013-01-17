package com.hdweiss.codemap.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.NoSuchElementException;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.hdweiss.codemap.util.Utils;

public class Cscope {
	public final static boolean WITH_INDEX = true;
	public final static boolean NO_INDEX = false;
	public final static boolean WITH_REFFILE = true;
	public final static boolean NO_REFFILE = false;
	
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
		return runCommand(project, options, true, true);
	}
	
	public String runCommand(Project project, String options, String fileName) {
		if (TextUtils.isEmpty(fileName) == false && new File(fileName).exists())
			return runCommand(project, options + " " + fileName, false, false);
		else {
			Log.e("Cscope", "runCommand got invalid file: " + fileName);
			return runCommand(project, options, true, true);			
		}
	}
	
	private String runCommand(Project project, String options, boolean includeIndex, boolean includeReffile) {
		if (this.cscopeExecPath == null || this.cscopeExecPath.isEmpty()) {
			Log.e("CodeMap", "Could not get path to" + EXE_FILENAME + "executable");
			return "";
		}
		
		String command = getCscopeCommand(project, options, includeIndex, includeReffile);
		Log.d("Cscope", "runCommand: " + command);
		
		File tmpdir = context.getFilesDir();
		String[] environment = {"TMPDIR=" + tmpdir.getAbsolutePath()};
		String output = Utils.runCommand(command, environment);
		return output;
	}
	
	private String getCscopeCommand(Project project, String options, boolean includeIndex, boolean includeReffile) {
		StringBuilder builder = new StringBuilder();
		builder.append(this.cscopeExecPath).append(" ");
		if(includeIndex)
			builder.append("-i ").append(getNamefilePath(project.getName())).append(" ");
		
		if(includeReffile)
			builder.append("-f ").append(getReffilePath(project.getName())).append(" ");
		else
			builder.append("-f ").append(getReffilePath(project.getName())).append(".temp ");

			
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
}
