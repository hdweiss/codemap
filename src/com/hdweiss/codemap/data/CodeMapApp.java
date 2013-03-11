package com.hdweiss.codemap.data;

import java.util.HashMap;

import com.hdweiss.codemap.view.codemap.WorkspaceController;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;

public class CodeMapApp extends Application {

	private HashMap<String, WorkspaceController> controllers = new HashMap<String, WorkspaceController>();
	
	public WorkspaceController getProjectController(String projectName) {
		checkProjectName(projectName);
		
		WorkspaceController controller = controllers.get(projectName);
		
		if(controller == null) {
			controller = new WorkspaceController(projectName, this);
			controllers.put(projectName, controller);
		}
		
		return controller;
	}
	
	public void removeProjectController(String projectName) {
		checkProjectName(projectName);

		controllers.remove(projectName);
	}
	
	public void checkProjectName(String projectName) {
		if(TextUtils.isEmpty(projectName))
			throw new IllegalArgumentException("Project name is invalid");
	}
	
	public static CodeMapApp get(Activity activity) {
		return (CodeMapApp) activity.getApplication();
	}
}
