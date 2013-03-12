package com.hdweiss.codemap.data;

import java.util.HashMap;

import android.app.Application;

import com.hdweiss.codemap.view.workspace.WorkspaceController;

public class CodeMapApp extends Application {

	private HashMap<String, HashMap<String,WorkspaceController>> controllers 
		= new HashMap<String, HashMap<String, WorkspaceController>>();
	
	public WorkspaceController getController(String projectName, String workspaceName) {
		HashMap<String,WorkspaceController> projectControllers = getProjectControllers(projectName);
		WorkspaceController controller = projectControllers.get(workspaceName);
		return controller;
	}
	
	public void addController(WorkspaceController controller) {
		HashMap<String,WorkspaceController> projectControllers = getProjectControllers(controller.project.getName());
		projectControllers.put(controller.getWorkspaceName(), controller);
	}
	
	private HashMap<String, WorkspaceController> getProjectControllers(String projectName) {
		HashMap<String, WorkspaceController> projectControllers = this.controllers.get(projectName);
		
		if (projectControllers == null) {
			projectControllers = new HashMap<String, WorkspaceController>();
			this.controllers.put(projectName, projectControllers);			
		}
		
		return projectControllers;
	}
}
