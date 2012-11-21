package com.hdweiss.codemap.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.hdweiss.codemap.util.Utils;

public class Project implements Serializable {

	private static final long serialVersionUID = 3L;
	
	private String name;
	private String url;

	public String[] files;
	public HashMap<String, ArrayList<String>> symbols = new HashMap<String, ArrayList<String>>();
	
	public Project(String name) {
		this.name = name;
	}
	
	public String getName() {
		if(TextUtils.isEmpty(name))
			return "";
		else
			return this.name;
	}

	public String getUrl() {
		if(TextUtils.isEmpty(url))
			return "";
		else
			return this.url;	
	}
	
	public boolean isUrlValid() {
		if(TextUtils.isEmpty(url))
			return false;
		else
			return true;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public static String getFilename(String name) {
		return name + ".project";
	}

	
	public static String getConfigFilePath(String name, Context context) {
		return context.getFileStreamPath(getFilename(name)).getAbsolutePath();
	}
	
	public String getSourcePath(Context context) {
		return Project.getSourcePath(name, context);
	}
	
	public static String getSourcePath(String name, Context context) {
		return context.getExternalCacheDir() + "/" + name;
	}
	
	public File getSourceDirectory(Context context) {
		return getSourceDirectory(name, context);
	}
	
	public static File getSourceDirectory(String name, Context context) {
		return new File(getSourcePath(name, context));
	}
	
	public File getProjectDirectory(Context context) {
		return getProjectDirectory(name, context);
	}
	
	public static File getProjectDirectory(String name, Context context) {
		return context.getDir(name, Context.MODE_PRIVATE);
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
	
	public static void delete(String name, Context context) {
		File projectDir = getProjectDirectory(name, context);
		Utils.deleteRecursive(projectDir);		
		new File(getConfigFilePath(name, context)).delete();
		
		File sourceDir = new File(getSourcePath(name, context));
		Utils.deleteRecursive(sourceDir);
		
		CodeMapApp.get((Activity) context).removeProjectController(name);
		
		CodeMapState.deleteState(name, context);
	}
}
