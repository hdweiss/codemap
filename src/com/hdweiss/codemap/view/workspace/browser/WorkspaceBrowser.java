package com.hdweiss.codemap.view.workspace.browser;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hdweiss.codemap.R;
import com.hdweiss.codemap.view.CodeMapActivity;
import com.hdweiss.codemap.view.workspace.WorkspaceController;

public class WorkspaceBrowser extends LinearLayout implements
		OnChildClickListener, OnGroupClickListener, AdapterView.OnCreateContextMenuListener {

	private ExpandableListView listView;
	private WorkspaceBrowserAdapter adapter;
	private WorkspaceController controller;
	private CodeMapActivity activity;

	public WorkspaceBrowser(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater.from(getContext()).inflate(R.layout.workspace_browser,
				this);
		this.listView = (ExpandableListView) findViewById(R.id.workspace_list);
		this.listView.setOnGroupClickListener(this);
		this.listView.setOnChildClickListener(this);
		this.listView.setOnCreateContextMenuListener(this);
	}

	public void setController(WorkspaceController controller,
			CodeMapActivity activity) {
		this.controller = controller;
		this.activity = activity;
		init();
	}

	public void init() {
		this.adapter = new WorkspaceBrowserAdapter(getContext(), controller);
		listView.setAdapter(adapter);
	}

	public boolean onGroupClick(ExpandableListView parent, View v,
			int groupPosition, long id) {
		return false;
	}

	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		String workspaceName = adapter.getGroup(groupPosition);
		String url = adapter.getChild(groupPosition, childPosition);

		activity.navigateToTab(workspaceName, url);
		return false;
	}

	public void refresh() {
		adapter.refresh();
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;

		int type = ExpandableListView
				.getPackedPositionType(info.packedPosition);
		if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
			new MenuInflater(getContext()).inflate(R.menu.workspace_browser, menu);
		}
	}

	public void handleLongClick(MenuItem item) {
		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item
				.getMenuInfo();

		Log.d("CodeMap", "handlelongclick!");
		int type = ExpandableListView
				.getPackedPositionType(info.packedPosition);
		if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
			int groupPosition = ExpandableListView
					.getPackedPositionGroup(info.packedPosition);
			String group = adapter.getGroup(groupPosition);
			Toast.makeText(getContext(), "Clicked! " + group, Toast.LENGTH_SHORT).show();
		}
	}
}
