package com.hdweiss.codemap.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

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

	public static byte[] serializeObject(Object o) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try {
			ObjectOutput out = new ObjectOutputStream(bos);
			out.writeObject(o);
			out.close();

			byte[] buf = bos.toByteArray();

			return buf;
		} catch (IOException ioe) {
			Log.e("serializeObject", "error", ioe);

			return null;
		}
	}

	public static Object deserializeObject(byte[] b) {
		try {
			ObjectInputStream in = new ObjectInputStream(
					new ByteArrayInputStream(b));
			Object object = in.readObject();
			in.close();

			return object;
		} catch (ClassNotFoundException cnfe) {
			Log.e("deserializeObject", "class not found error", cnfe);

			return null;
		} catch (IOException ioe) {
			Log.e("deserializeObject", "io error", ioe);

			return null;
		}
	}
	
	public static void deleteRecursive(File fileOrDirectory) {
	    if (fileOrDirectory.isDirectory())
	        for (File child : fileOrDirectory.listFiles())
	            deleteRecursive(child);

	    fileOrDirectory.delete();
	}
}
