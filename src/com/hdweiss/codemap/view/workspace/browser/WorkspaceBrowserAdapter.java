package com.hdweiss.codemap.view.workspace.browser;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.hdweiss.codemap.R;
import com.hdweiss.codemap.view.workspace.WorkspaceController;
import com.hdweiss.codemap.view.workspace.WorkspaceState;

public class WorkspaceBrowserAdapter extends BaseExpandableListAdapter {

	private Context context;
	private WorkspaceController controller;

	private ArrayList<String> workspaceStateList;

	public WorkspaceBrowserAdapter(Context context, WorkspaceController controller) {
		super();
		this.context = context;
		this.controller = controller;
		init();
	}
	
	private void init() {
		this.workspaceStateList = WorkspaceState.getWorkspaceStateList(
				controller, context);
	}
	
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View view = convertView;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			view = inflater.inflate(R.layout.workspace_child, parent, false);
		}
		
		TextView textView = (TextView) view
				.findViewById(R.id.workspace_item_text);
		textView.setText("Child");
		
		return view;
	}

	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return 1;
	}

	public String getGroup(int groupPosition) {
		return this.workspaceStateList.get(groupPosition);
	}

	public int getGroupCount() {
		return this.workspaceStateList.size();
	}

	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View view = convertView;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			view = inflater.inflate(R.layout.workspace_group, parent, false);
		}
		
		String groupText = getGroup(groupPosition);
		
		TextView textView = (TextView) view
				.findViewById(R.id.workspace_item_text);
		textView.setText(groupText);
		
		return view;
	}

	public boolean hasStableIds() {
		return false;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
