package com.hdweiss.codemap.view.workspace.browser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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
	private HashMap<String, WorkspaceState> workspaceStates = new HashMap<String, WorkspaceState>();
	
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
	
	private WorkspaceState getState(String workspaceName) {
		WorkspaceState state = workspaceStates.get(workspaceName);
		if (state == null ) {
			try {
				state = WorkspaceState.readState(controller.project, workspaceName, context);
				workspaceStates.put(workspaceName, state);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return state;
	}
	
	public String getChild(int groupPosition, int childPosition) {
		String workspaceName = this.workspaceStateList.get(groupPosition);
		WorkspaceState state = getState(workspaceName);
		
		if (state != null)
			return state.items.get(childPosition).url;

		return "";
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
		
		String child = getChild(groupPosition, childPosition);
		
		TextView textView = (TextView) view
				.findViewById(R.id.workspace_item_text);
		textView.setText("  " + child);
		
		return view;
	}

	public int getChildrenCount(int groupPosition) {
		WorkspaceState state = getState(getGroup(groupPosition));
		
		if (state != null)
			return state.items.size();
		else
			return 0;
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
