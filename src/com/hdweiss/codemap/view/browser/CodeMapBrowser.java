package com.hdweiss.codemap.view.browser;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.hdweiss.codemap.R;
import com.hdweiss.codemap.controller.CodeMapController;
import com.hdweiss.codemap.controller.FindDeclarationTask;
import com.hdweiss.codemap.controller.FindDeclarationTask.FindDeclarationCallback;
import com.hdweiss.codemap.data.CscopeEntry;
import com.hdweiss.codemap.view.browser.CodeMapBrowserItem.TYPE;

public class CodeMapBrowser extends LinearLayout implements OnItemClickListener,
		android.widget.AdapterView.OnItemLongClickListener {

	private CodeMapBrowserAdapter adapter;
	private CodeMapController controller;
	private ListView browserListView;
	private EditText searchbarView;

	public CodeMapBrowser(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater.from(getContext()).inflate(R.layout.browser, this);
		
		this.browserListView = (ListView) findViewById(R.id.browser_list);
		browserListView.setOnItemClickListener(this);
		browserListView.setOnItemLongClickListener(this);
		
		this.searchbarView = (EditText) findViewById(R.id.browser_search_input);
		Button searchButton = (Button) findViewById(R.id.browser_search_button);
		searchButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				search();
			}
		});
	}
	
	public void search() {
		String searchString = searchbarView.getText().toString();
		
		new FindDeclarationTask(searchString, searchCallback, controller,
				getContext()).execute();
		
	}
	
	FindDeclarationCallback searchCallback = new FindDeclarationCallback() {
		public void onSuccess(ArrayList<CscopeEntry> entries) {
			Toast.makeText(getContext(), "Error finding entries",
					Toast.LENGTH_SHORT).show();
		}

		public void onFailure() {
			Toast.makeText(getContext(), "Error finding entries",
					Toast.LENGTH_SHORT).show();
		}		
	};
	
	public void setController(CodeMapController controller) {
		this.controller = controller;
		update();
	}
	
	public void update() {
		this.adapter = new CodeMapBrowserAdapter(getContext(), controller);
		browserListView.setAdapter(adapter);
	}
	
	public void refresh() {
		adapter.refresh();
	}

	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		CodeMapBrowserItem item = adapter.getItem(position);
		
		if(item.type == TYPE.FILE) {
			controller.addFileView(item.name);
			return true;
		}
		else if (item.type == TYPE.SYMBOL) {
			final String url = adapter.getItemUrl(position);
			controller.addFunctionView(url);
		}
		
        return false;
    }

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		boolean changed = adapter.collapseExpand(position);

		if(changed == false) {
			CodeMapBrowserItem item = adapter.getItem(position);
			if(item.type == CodeMapBrowserItem.TYPE.SYMBOL) {
				final String url = adapter.getItemUrl(position);
				controller.symbolClicked(url, item);
			}
		}
	}
}
