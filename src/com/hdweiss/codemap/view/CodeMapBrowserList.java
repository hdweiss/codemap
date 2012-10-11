package com.hdweiss.codemap.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class CodeMapBrowserList extends ListView implements android.widget.AdapterView.OnItemClickListener {

	public CodeMapBrowserList(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		String[] items = {"hej", "ho"};
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_list_item_1, items);
		setAdapter(arrayAdapter);
		setOnItemClickListener(this);
	}

	public void onItemClick(AdapterView<?> adapterView, View parent, int position, long id) {
		String item = (String) getItemAtPosition(position);
		Toast.makeText(getContext(), item, Toast.LENGTH_SHORT).show();
	}
	
}
