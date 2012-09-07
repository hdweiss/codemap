package com.hdweiss.codemap.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.util.Log;

public class Utils {

	public static String runCommand(String command) {
		return runCommand(command, null);
	}
	
	public static String runCommand(String command, String[] environment) {
		try {
			final String[] shellCommand = { "/system/bin/sh", "-c", command };

			Process process;
			if(environment != null)
				process = Runtime.getRuntime().exec(shellCommand, environment);
			else
				process = Runtime.getRuntime().exec(shellCommand);
			
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

		StringBuilder builder = new StringBuilder();
		String output;
		while ((output = dis.readLine()) != null) {
			builder.append(output);
		}

		return builder.toString();
	}
	
	public static String inputStreamToString(InputStream inputStream) {
		return new java.util.Scanner(inputStream).useDelimiter("\\A").next();
	}
}
