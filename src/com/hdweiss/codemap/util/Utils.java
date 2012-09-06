package com.hdweiss.codemap.util;

import java.io.DataInputStream;
import java.io.IOException;

import android.util.Log;

public class Utils {

	public static String runCommand(String command) {
		try {
			final String[] shellCommand = { "/system/bin/sh", "-c", command };
			Process process = Runtime.getRuntime().exec(shellCommand);

			try {
				process.waitFor();
			} catch (InterruptedException e) {
				Log.e("CodeMap", e.getStackTrace().toString());
			}

			String output = getProcessOutput(process);
			return output;

		} catch (IOException e) {
			Log.e("CodeMap", e.getStackTrace().toString());
			return "";
		}
	}

	@SuppressWarnings("deprecation")
	private static String getProcessOutput(Process process) throws IOException {
		DataInputStream dis = new DataInputStream(process.getInputStream());

		Log.d("CodeMap", "Terminated with: " + process.exitValue() + " and "
				+ dis.available() + " bytes output");

		StringBuilder builder = new StringBuilder();
		String output;
		while ((output = dis.readLine()) != null) {
			builder.append(output);
		}

		return builder.toString();
	}
}
