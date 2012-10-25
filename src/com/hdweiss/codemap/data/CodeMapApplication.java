package com.hdweiss.codemap.data;

import java.util.HashMap;

import android.app.Application;
import android.text.TextUtils;

public class CodeMapApplication extends Application {

	private HashMap<String, ProjectController> controllers = new HashMap<String, ProjectController>();
	
	public ProjectController getProjectController(String projectName) {
		if(TextUtils.isEmpty(projectName))
			throw new IllegalArgumentException("Project name is invalid");
		
		ProjectController controller = controllers.get(projectName);
		
		if(controller == null) {
			controller = new ProjectController(projectName, this);
			controller.init();
			controllers.put(projectName, controller);
		}
		
		return controller;
	}
	
}
