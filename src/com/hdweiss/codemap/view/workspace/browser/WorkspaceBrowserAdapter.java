package com.hdweiss.codemap.view.workspace.browser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.hdweiss.codemap.R;
import com.hdweiss.codemap.data.CodeMapApp;
import com.hdweiss.codemap.data.ICodeMapItem;
import com.hdweiss.codemap.data.ProjectController;
import com.hdweiss.codemap.data.SerializableItem;
import com.hdweiss.codemap.view.workspace.WorkspaceController;
import com.hdweiss.codemap.view.workspace.WorkspaceState;
import com.hdweiss.codemap.view.workspace.fragments.CodeMapItem;

public class WorkspaceBrowserAdapter extends BaseExpandableListAdapter {

	private Context context;
	private ProjectController projectController;

	private ArrayList<String> workspaceStateList;
	private HashMap<String, ArrayList<ICodeMapItem>> workspaceUrlsMap;
	
	public WorkspaceBrowserAdapter(Context context, ProjectController controller) {
		super();
		this.context = context;
		this.projectController = controller;
		init();
	}
	
	public void refresh() {
		init();
	}
	
	public void refresh(String workspaceName) {
		this.workspaceUrlsMap.remove(workspaceName);
		this.workspaceUrlsMap.put(workspaceName, getWorkspaceUrls(workspaceName));
		notifyDataSetInvalidated();
	}
	
	private void init() {
		this.workspaceStateList = WorkspaceState.getWorkspaceStateList(
				projectController, context);
		
		workspaceUrlsMap = new HashMap<String, ArrayList<ICodeMapItem>>();
		for (String workspaceName: workspaceStateList) {
			ArrayList<ICodeMapItem> workspaceUrls = getWorkspaceUrls(workspaceName);
			this.workspaceUrlsMap.put(workspaceName, workspaceUrls);
		}
		notifyDataSetInvalidated();
	}
	
	private ArrayList<ICodeMapItem> getWorkspaceUrls(String workspaceName) {
		ArrayList<ICodeMapItem> result = new ArrayList<ICodeMapItem>();

		final String projectName = projectController.project.getName();
		
		WorkspaceController controller = ((CodeMapApp) context
				.getApplicationContext()).getController(projectName,
				workspaceName);
		if (controller != null) {
			for (CodeMapItem item: controller.codeMapView.items)
				result.add(item);
		} else {
			try {
				WorkspaceState state = WorkspaceState.readState(
						projectController.project, workspaceName, context);
				for (SerializableItem item: state.items)
					result.add(item);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public String getChild(int groupPosition, int childPosition) {
		String workspaceName = this.workspaceStateList.get(groupPosition);
		ArrayList<ICodeMapItem> urls = this.workspaceUrlsMap.get(workspaceName);
		return urls.get(childPosition).getUrl();
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
		String workspaceName = getGroup(groupPosition);
		ArrayList<ICodeMapItem> urls = this.workspaceUrlsMap.get(workspaceName);
		return urls.size();
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
			((ExpandableListView) parent).expandGroup(groupPosition);
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
