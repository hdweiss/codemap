package com.hdweiss.codemap;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.util.Log;

public class Ctags {
	private final static String FILENAME = "ctags";
	private final static String CTAGS_OPTIONS = "--help";

	private Context context;
	private String ctagsExecPath = "";

	public Ctags(Context context) {
		this.context = context;

		prepareCtagsExe();
	}

	private void prepareCtagsExe() {
		try {
			context.openFileInput(FILENAME);
		} catch (FileNotFoundException e) {
			copyCtagsExecutableToInternalStorage();
		} finally {
			File file = context.getFileStreamPath(FILENAME);
			if (file.exists() && file.canExecute()) {
				this.ctagsExecPath = file.getAbsolutePath();
			}
		}
	}

	private void copyCtagsExecutableToInternalStorage() {
		try {
			InputStream is = context.getAssets().open(FILENAME);
			byte[] buffer = new byte[is.available()];
			is.read(buffer);
			is.close();

			FileOutputStream fos = context.openFileOutput(FILENAME,
					Context.MODE_PRIVATE);
			fos.write(buffer);
			fos.close();

			File file = context.getFileStreamPath(FILENAME);
			file.setExecutable(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		if (this.ctagsExecPath == null || this.ctagsExecPath.isEmpty()) {
			Log.e("CodeMap", "Could not get path to ctags executable");
			return;
		}

		try {
			Log.d("CodeMap", "Running ctags with options: " + CTAGS_OPTIONS);
			Process process = Runtime.getRuntime().exec(
					this.ctagsExecPath + " " + CTAGS_OPTIONS);

			try {
				process.waitFor();
			} catch (InterruptedException e) {
				Log.e("CodeMap", e.getStackTrace().toString());
			}

			printProcessOutput(process);

		} catch (IOException e) {
			Log.e("CodeMap", e.getStackTrace().toString());
		}
	}

	@SuppressWarnings("deprecation")
	private void printProcessOutput(Process process) throws IOException {
		DataInputStream dis = new DataInputStream(process.getInputStream());

		Log.d("CodeMap", "Terminated with: " + process.exitValue() + " and "
				+ dis.available() + " output");

		String output;
		while ((output = dis.readLine()) != null) {
			Log.d("CodeMap", output);
		}
	}
}
