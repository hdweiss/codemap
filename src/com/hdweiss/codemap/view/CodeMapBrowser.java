package com.hdweiss.codemap.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.hdweiss.codemap.data.Project;

public class CodeMapBrowser extends ExpandableListView implements OnChildClickListener, android.widget.AdapterView.OnItemLongClickListener {

	private CodeMapBrowserAdapter adapter;
	private Project project;

	public CodeMapBrowser(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnChildClickListener(this);
		setOnItemLongClickListener(this);
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

	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            int groupPosition = ExpandableListView.getPackedPositionGroup(id);
			//int childPosition = ExpandableListView.getPackedPositionChild(id);

            String filename = adapter.getGroup(groupPosition);
            project.addFileView(filename);
            
            return true;
        }

        return false;
    }
}
