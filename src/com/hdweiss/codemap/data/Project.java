package com.hdweiss.codemap.data;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;

import com.hdweiss.codemap.util.Utils;

public class Project implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String path;

	public String[] files;
	public HashMap<String, ArrayList<String>> symbols = new HashMap<String, ArrayList<String>>();
	
	public Project(String name, String path) {
		this.name = name;
		this.path = path;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getPath() {
		return this.path;
	}

	public static String getFilename(String name) {
		return name + ".project";
	}
	
	public void writeProject(Context context) throws IOException {
		byte[] serializeObject = Utils.serializeObject(this);
		FileOutputStream fos = context.openFileOutput(getFilename(name),
				Context.MODE_PRIVATE);
		fos.write(serializeObject);
		fos.close();
	}

	public static Project readProject(String name, Context context)
			throws IOException {
		FileInputStream fis = context.openFileInput(getFilename(name));
		byte[] serializedObject = new byte[fis.available()];
		fis.read(serializedObject);
		fis.close();

		Project result = (Project) Utils.deserializeObject(serializedObject);
		return result;
	}
}
