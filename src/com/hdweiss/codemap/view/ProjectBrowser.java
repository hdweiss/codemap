package com.hdweiss.codemap.view;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hdweiss.codemap.R;
import com.hdweiss.codemap.data.ProjectController;
import com.hdweiss.codemap.view.codemap.CodeMapActivity;

public class ProjectBrowser extends FragmentActivity implements OnItemClickListener, OnItemLongClickListener {

	private ListView listView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.projects);
        
        this.listView = (ListView) findViewById(R.id.projects_list);
        listView.setOnItemClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		refresh();
	}

	public void refresh() {
		ArrayList<String> projectsList = ProjectController.getProjectsList(this);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, projectsList);
		listView.setAdapter(adapter);
	}

	
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String projectName = (String) listView.getAdapter().getItem(position);
		Intent intent = new Intent(this, CodeMapActivity.class);
		intent.putExtra(CodeMapActivity.PROJECT_NAME, projectName);
		startActivity(intent);
	}
	
	
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
			long id) {
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.projects_browser, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.projects_add:
			startProjectWizard();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	public void startProjectWizard() {
		ProjectWizard projectWizard = new ProjectWizard();
		projectWizard.show(getFragmentManager(), "wizard");
	}
}
