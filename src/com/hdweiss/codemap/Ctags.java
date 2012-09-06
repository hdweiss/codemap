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

public class Ctags {
	private final static String EXE_FILENAME = "ctags";
	
	private Context context;
	private String ctagsExecPath = "";

	public Ctags(Context context) {
		this.context = context;

		prepareCtagsExe();
	}

	private void prepareCtagsExe() {
		try {
			context.openFileInput(EXE_FILENAME);
		} catch (FileNotFoundException e) {
			copyCtagsExecutableToInternalStorage();
		} finally {
			File file = context.getFileStreamPath(EXE_FILENAME);
			if (file.exists() && file.canExecute()) {
				this.ctagsExecPath = file.getAbsolutePath();
			}
		}
	}

	private void copyCtagsExecutableToInternalStorage() {
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
	
	public String generateTagsFile(String projectName, String options, boolean verbose) {
		if (this.ctagsExecPath == null || this.ctagsExecPath.isEmpty()) {
			Log.e("CodeMap", "Could not get path to ctags executable");
			return "";
		}
		
		String command = getCtagsCommand(projectName, options, verbose);
		String output = Utils.runCommand(command);
		Log.d("CodeMap", "Ctags output: \n" + output);

		return output;
	}

	
	private String getCtagsCommand(String projectName, String options, boolean verbose) {
		StringBuilder builder = new StringBuilder();
		final String ctagsOutfile = "-f " + getTagsFileLocation(projectName);
		
		builder.append(this.ctagsExecPath).append(" ").append(ctagsOutfile)
				.append(" ").append(options);
		
		if(!verbose)
			builder.append("> /dev/null");
		return builder.toString();
	}

	private String getTagsFileName(String projectName) {
		return projectName + "_TAGS";
	}
	
	private String getTagsFileLocation (String projectName) {
		return context.getFileStreamPath(getTagsFileName(projectName)).getAbsolutePath();
	}

	public FileInputStream getTagsFile(String projectName) throws FileNotFoundException {
		FileInputStream tagsFile = context
				.openFileInput(getTagsFileName(projectName));
		return tagsFile;
	}
	
	public void deleteTagsFile(String projectName) {
		context.getFileStreamPath(getTagsFileName(projectName)).delete();
	}
}
