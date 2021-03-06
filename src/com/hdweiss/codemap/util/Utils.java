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
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.hdweiss.codemap.view.project.ProjectBrowser.SynchServiceReceiver;

public class Utils {

	private static final int DEFAULT_FONTSIZE = 15;

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
	
	public static void setupSpinner(Spinner spinner, ArrayList<String> data,
			String selection) {		
		if(!TextUtils.isEmpty(selection) && !data.contains(selection))
			data.add(selection);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(spinner.getContext(),
				android.R.layout.simple_spinner_item, data);
		spinner.setAdapter(adapter);
		int pos = data.indexOf(selection);
		if (pos < 0) {
			pos = 0;
		}
		spinner.setSelection(pos, true);
	}
	
	
	public static void announceSyncDone(Context context, String name) {
		Intent intent = new Intent(SynchServiceReceiver.SYNC_UPDATE);
		intent.putExtra(SynchServiceReceiver.SYNC_DONE, true);
		intent.putExtra(SynchServiceReceiver.SYNC_NAME, name);
		context.sendBroadcast(intent);
	}

	public static void announceSyncStart(Context context, String name) {
		Intent intent = new Intent(SynchServiceReceiver.SYNC_UPDATE);
		intent.putExtra(SynchServiceReceiver.SYNC_START, true);
		intent.putExtra(SynchServiceReceiver.SYNC_NAME, name);
		context.sendBroadcast(intent);
	}

	public static void announceSyncUpdateProgress(Context context, String name, int progress, String status) {
		Intent intent = new Intent(SynchServiceReceiver.SYNC_UPDATE);
		intent.putExtra(SynchServiceReceiver.SYNC_PROGRESS_UPDATE, progress);
		intent.putExtra(SynchServiceReceiver.SYNC_NAME, name);
		intent.putExtra(SynchServiceReceiver.SYNC_STATUS, status);
		context.sendBroadcast(intent);
	}
	
	
	public static Runnable getTouchDelegateAction(final View parent,
			final View delegate, final int topPadding, final int bottomPadding,
			final int leftPadding, final int rightPadding) {
        return new Runnable() {
            public void run() {
                
                //Construct a new Rectangle and let the Delegate set its values
                Rect touchRect = new Rect();
                delegate.getHitRect(touchRect);
                
                //Modify the dimensions of the Rectangle
                //Padding values below zero are replaced by zeros
                touchRect.top-=Math.max(0, topPadding);
                touchRect.bottom+=Math.max(0, bottomPadding);
                touchRect.left-=Math.max(0, leftPadding);
                touchRect.right+=Math.max(0, rightPadding);
                
                //Now we are going to construct the TouchDelegate
                TouchDelegate touchDelegate = new TouchDelegate(touchRect, delegate);
                
                //And set it on the parent
                parent.setTouchDelegate(touchDelegate);
                
            }
        };
    }
	
	public static int getSourceFontsize(Context context) {
		try {
			int fontSize = Integer.parseInt(PreferenceManager
					.getDefaultSharedPreferences(context).getString(
							"sourceFontSize", "15"));

			if (fontSize > 6)
				return fontSize;
		} catch (NumberFormatException e) {
		}

		return DEFAULT_FONTSIZE;
	}
	
	public static boolean isNetworkOnline(Context context) {
		ConnectivityManager conMan = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		
		if (mobile == NetworkInfo.State.CONNECTED) {
		  	return true;
		} 
		if (wifi == NetworkInfo.State.CONNECTED) {
		   return true;
		}
		
		return false;

	}
}
