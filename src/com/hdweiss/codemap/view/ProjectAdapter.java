package com.hdweiss.codemap.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.hdweiss.codemap.controller.ProjectController;
import com.hdweiss.codemap.data.CodeMapApp;

public class ProjectAdapter extends BaseAdapter {

	private ArrayList<String> data;
	private Context context;

	public ProjectAdapter(Context context, ArrayList<String> data) {
		super();
		this.context = context;
		this.data = data;
	}
	
	public int getCount() {
		return data.size();
	}

	public String getItem(int position) {
		return data.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ProjectItemView view = (ProjectItemView) convertView;
		if(view == null) {
			view = new ProjectItemView(context);
		}
		
		final String projectName = data.get(position);
		ProjectController controller = CodeMapApp.get((Activity)context).getProjectController(projectName);
		
		view.setName(controller.project.getName());
		view.setUrl(controller.project.getUrl());
		
		return view;
	}

	public int getItemPosition(String name) {
		return data.indexOf(name);
	}
}
