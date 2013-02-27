package com.hdweiss.codemap.view.browser;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.hdweiss.codemap.R;
import com.hdweiss.codemap.controller.CodeMapController;
import com.hdweiss.codemap.controller.FindDeclarationTask;
import com.hdweiss.codemap.controller.FindDeclarationTask.FindDeclarationCallback;
import com.hdweiss.codemap.data.CscopeEntry;
import com.hdweiss.codemap.view.browser.BrowserItem.TYPE;

public class Browser extends LinearLayout implements OnItemClickListener,
		android.widget.AdapterView.OnItemLongClickListener {

	private BrowserAdapter adapter;
	private CodeMapController controller;
	private ListView browserListView;
	private EditText searchbarView;
	private ImageButton cancelButton;

	public Browser(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater.from(getContext()).inflate(R.layout.browser, this);
		
		this.browserListView = (ListView) findViewById(R.id.browser_list);
		this.searchbarView = (EditText) findViewById(R.id.browser_search_input);
		searchbarView.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				search();
				return false;
			}
		});
		
		ImageButton searchButton = (ImageButton) findViewById(R.id.browser_search_button);
		searchButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {	
				search();
			}
		});
		
		this.cancelButton = (ImageButton) findViewById(R.id.browser_search_cancel);
		cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				searchbarView.setText("");
				showDeclarations();
			}
		});
		
		showDeclarations();
	}
	
	public void setController(CodeMapController controller) {
		this.controller = controller;
		this.adapter = new BrowserAdapter(getContext(), controller);
		browserListView.setAdapter(adapter);
	}

	
	private void showDeclarations() {
		browserListView.setOnItemClickListener(this);
		browserListView.setOnItemLongClickListener(this);
		browserListView.setAdapter(adapter);
		cancelButton.setVisibility(GONE);
	}
	
	public void search() {
		String searchString = searchbarView.getText().toString();
		
		if (TextUtils.isEmpty(searchString))
			return;
		
		new FindDeclarationTask(searchString, searchCallback, controller,
				getContext()).execute();
	}
	
	public void showSearch(ArrayList<CscopeEntry> entries) {
		final CscopeEntryAdapter searchAdapter = new CscopeEntryAdapter(
				getContext());
		searchAdapter.addAll(entries);
		browserListView.setAdapter(searchAdapter);
		browserListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parentView, View view, int position,
					long id) {
				CscopeEntry item = searchAdapter.getItem(position);
				controller.addFunctionView(item.file + ":" + item.actualName);
			}
		});
		cancelButton.setVisibility(VISIBLE);
	}

	
	FindDeclarationCallback searchCallback = new FindDeclarationCallback() {
		public void onSuccess(ArrayList<CscopeEntry> entries) {
			showSearch(entries);
		}

		public void onFailure() {
			Toast.makeText(getContext(), "Error finding entries",
					Toast.LENGTH_SHORT).show();
		}
	};


	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		BrowserItem item = adapter.getItem(position);
		
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
			BrowserItem item = adapter.getItem(position);
			if(item.type == BrowserItem.TYPE.SYMBOL) {
				final String url = adapter.getItemUrl(position);
				controller.symbolClicked(url, item);
			}
		}
	}
	
	public void refresh() {
		adapter.refresh();
	}
}
