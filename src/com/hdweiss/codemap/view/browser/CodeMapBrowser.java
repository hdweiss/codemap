package com.hdweiss.codemap.view.browser;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.hdweiss.codemap.controller.CodeMapController;
import com.hdweiss.codemap.view.browser.CodeMapBrowserItem.TYPE;

public class CodeMapBrowser extends ListView implements OnItemClickListener, android.widget.AdapterView.OnItemLongClickListener {

	private CodeMapBrowserAdapter adapter;
	private CodeMapController controller;

	public CodeMapBrowser(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnItemClickListener(this);
		setOnItemLongClickListener(this);
	}

	public void setController(CodeMapController controller) {
		this.controller = controller;
		refresh();
	}
	
	public void refresh() {
		this.adapter = new CodeMapBrowserAdapter(getContext(), controller);
		setAdapter(adapter);
	}

	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		CodeMapBrowserItem item = adapter.getItem(position);
		
		if(item.type == TYPE.FILE) {
			controller.addFileView(item.name);
			return true;
		}
		
        return false;
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
