package com.hdweiss.codemap.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
			builder.append(output).append("\n");
		}

		return builder.toString();
	}
	
	public static String inputStreamToString(InputStream inputStream) {
		return new java.util.Scanner(inputStream).useDelimiter("\\A").next();
	}
	
	public static String getFileFragment(String filePath, int startLine, int endLine) {
		StringBuilder result = new StringBuilder();
		
		int realStartLine = startLine - 2;
		int linesToRead = endLine - startLine + 2;
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			
			int numOfLines = 0;
			while((reader.readLine()) != null && numOfLines < realStartLine) {
				numOfLines++;
			}
			String line;
			int functionLines = 0;
			while((line = reader.readLine()) != null && functionLines < linesToRead) {
				result.append(line).append("\n");
				functionLines++;
			}
			
			reader.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		
		return result.toString();
	}
}
