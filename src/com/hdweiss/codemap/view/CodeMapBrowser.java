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
				android.R.layout.simple_list_item_1, cleanSymbols(symbols));
		setAdapter(arrayAdapter);
	}
	
	private ArrayList<String> cleanSymbols(ArrayList<String> symbols) {
		ArrayList<String> result = new ArrayList<String>();
		for(String symbol: symbols) {
			if(symbol.startsWith("#")) {
				continue;
			}
			
			int startParenthesis = symbol.indexOf("(");
			
			if(startParenthesis == -1)
				continue;
			
			String substring = symbol.substring(0, startParenthesis).trim();
			int funcNameStart = substring.lastIndexOf(" ");
			
			if(funcNameStart == -1)
				funcNameStart = 0;
			
			String funcName = substring.substring(funcNameStart, substring.length()).trim();
			result.add(funcName);
		}
		
		return result;
	}
}
