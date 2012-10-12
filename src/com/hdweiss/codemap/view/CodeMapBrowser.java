package com.hdweiss.codemap.view;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hdweiss.codemap.data.Project;

public class CodeMapBrowser extends ListView implements android.widget.AdapterView.OnItemClickListener {

	private Project project;

	public CodeMapBrowser(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnItemClickListener(this);
	}

	public void onItemClick(AdapterView<?> view, View parent, int position, long id) {
		String item = (String) getItemAtPosition(position);
		project.addFunctionView(item);
	}

	public void setProject(Project project) {
		this.project = project;
		refresh();
	}
	
	@SuppressLint("SdCardPath")
	public void refresh() {
		ArrayList<String> symbols = project.getSymbols("/sdcard/ctags/main.c");
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(),
				android.R.layout.simple_list_item_1, symbols);
		setAdapter(arrayAdapter);
	}
}
