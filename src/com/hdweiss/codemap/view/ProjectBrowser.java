package com.hdweiss.codemap.view;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hdweiss.codemap.R;
import com.hdweiss.codemap.data.Project;

public class ProjectBrowser extends Activity implements OnItemClickListener, OnItemLongClickListener {

	private ListView listView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.projects);
        
        this.listView = (ListView) findViewById(R.id.projects_list);
        listView.setOnItemClickListener(this);
        refresh();
	}

	private void refresh() {
		ArrayList<String> projectsList = Project.getProjectsList();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, projectsList);
		listView.setAdapter(adapter);
	}

	
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		listView.getAdapter().getItem(position);
	}

	public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
			long id) {
		return false;
	}
}
