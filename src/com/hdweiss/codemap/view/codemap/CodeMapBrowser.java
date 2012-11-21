package com.hdweiss.codemap.view.codemap;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.hdweiss.codemap.data.ProjectController;

public class CodeMapBrowser extends ListView implements OnItemClickListener, android.widget.AdapterView.OnItemLongClickListener {

	private CodeMapBrowserAdapter adapter;
	private ProjectController controller;

	public CodeMapBrowser(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnItemClickListener(this);
		setOnItemLongClickListener(this);
	}

	public void setController(ProjectController controller) {
		this.controller = controller;
		refresh();
	}
	
	public void refresh() {
		this.adapter = new CodeMapBrowserAdapter(getContext(), controller);
		setAdapter(adapter);
	}

	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//        if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
//            int groupPosition = ExpandableListView.getPackedPositionGroup(id);
//			//int childPosition = ExpandableListView.getPackedPositionChild(id);
//
//            String filename = adapter.getGroup(groupPosition);
//            controller.addFileView(filename);
//            
//            return true;
//        }

        return true;
    }

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		boolean changed = adapter.collapseExpand(position);

		if(changed == false) {
			CodeMapBrowserItem item = adapter.getItem(position);
			if(item.type == CodeMapBrowserItem.TYPE.SYMBOL)
				controller.addFunctionView(item.name);
		}
	}
}
