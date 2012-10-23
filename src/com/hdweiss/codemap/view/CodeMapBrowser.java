package com.hdweiss.codemap.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.hdweiss.codemap.data.Project;

public class CodeMapBrowser extends ExpandableListView implements OnChildClickListener {

	private CodeMapBrowserAdapter adapter;
	private Project project;

	public CodeMapBrowser(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnChildClickListener(this);
	}

	public void setProject(Project project) {
		this.project = project;
		refresh();
	}
	
	public void refresh() {
		this.adapter = new CodeMapBrowserAdapter(getContext(), project);
		setAdapter(adapter);
	}

	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		String item = adapter.getChild(groupPosition, childPosition);
		project.addFunctionView(item);
		return true;
	}
}
