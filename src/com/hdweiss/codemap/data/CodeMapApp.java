package com.hdweiss.codemap.data;

import java.util.HashMap;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;

public class CodeMapApp extends Application {

	private HashMap<String, ProjectController> controllers = new HashMap<String, ProjectController>();
	
	public ProjectController getProjectController(String projectName) {
		if(TextUtils.isEmpty(projectName))
			throw new IllegalArgumentException("Project name is invalid");
		
		ProjectController controller = controllers.get(projectName);
		
		if(controller == null) {
			controller = new ProjectController(projectName, this);
			controllers.put(projectName, controller);
		}
		
		return controller;
	}
	
	public static CodeMapApp get(Activity activity) {
		return (CodeMapApp) activity.getApplication();
	}
}
