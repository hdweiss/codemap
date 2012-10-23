package com.hdweiss.codemap.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.hdweiss.codemap.data.Project;

public class CodeMapBrowserAdapter extends BaseExpandableListAdapter {

	private Context context;

	private Project project;
	private String[] files;
	
	public CodeMapBrowserAdapter(Context context, Project project) {
		super();
		this.context = context;
		this.project = project;
		files = project.getFiles();
	}
	
	public String getChild(int groupPosition, int childPosition) {
		return project.getSymbols(files[groupPosition]).get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
		CodeMapBrowserItem listItem = (CodeMapBrowserItem) convertView;
		
		if (convertView == null) {
			listItem = new CodeMapBrowserItem(getContext());
		}

		listItem.setText(getChild(groupPosition, childPosition));
		listItem.setChild(true);
		
		return listItem;
	}

	public int getChildrenCount(int groupPosition) {
		return project.getSymbols(files[groupPosition]).size();
	}

	public String getGroup(int groupPosition) {
		return files[groupPosition];
	}

	public int getGroupCount() {
		return files.length;
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		CodeMapBrowserItem listItem = (CodeMapBrowserItem) convertView;
		
		if (convertView == null) {
			listItem = new CodeMapBrowserItem(getContext());
		}

		listItem.setText(files[groupPosition]);
		listItem.setChild(false);
		
		return listItem;
	}

	public boolean hasStableIds() {
		return false;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public Context getContext() {
		return this.context;
	}
}
