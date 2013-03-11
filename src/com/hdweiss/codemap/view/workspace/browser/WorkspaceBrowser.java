package com.hdweiss.codemap.view.workspace.browser;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.LinearLayout;

import com.hdweiss.codemap.R;
import com.hdweiss.codemap.view.workspace.WorkspaceController;

public class WorkspaceBrowser extends LinearLayout implements OnChildClickListener, OnGroupClickListener {

	private ExpandableListView listView;
	private WorkspaceBrowserAdapter adapter;
	private WorkspaceController controller;

	public WorkspaceBrowser(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater.from(getContext()).inflate(R.layout.workspace_browser, this);
		this.listView = (ExpandableListView) findViewById(R.id.workspace_list);
		this.listView.setOnGroupClickListener(this);
		this.listView.setOnChildClickListener(this);
	}
	
	public void setController(WorkspaceController controller) {
		this.controller = controller;
		init();
	}
	
	public void init() {
		this.adapter = new WorkspaceBrowserAdapter(getContext(), controller);
		listView.setAdapter(adapter);
	}

	
	public boolean onGroupClick(ExpandableListView parent, View v,
			int groupPosition, long id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		// TODO Auto-generated method stub
		return false;
	}
}
