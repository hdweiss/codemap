package com.hdweiss.codemap.view;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hdweiss.codemap.R;
import com.hdweiss.codemap.data.CodeMapApp;
import com.hdweiss.codemap.data.ProjectController;
import com.hdweiss.codemap.view.codemap.CodeMapActivity;

public class ProjectBrowser extends FragmentActivity implements OnItemClickListener {

	private ListView listView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.projects);
        
        this.listView = (ListView) findViewById(R.id.projects_list);
        listView.setOnItemClickListener(this);
        registerForContextMenu(listView);
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
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.projects_context, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		final String projectName = (String) listView.getItemAtPosition(info.position);
		
		switch(item.getItemId()) {
		case R.id.projects_remove:
			removeProject(projectName);
			break;
		
		case R.id.projects_update:
			updateProject(projectName);
			break;
			
		default:
			return super.onContextItemSelected(item);
		}
		
		return true;
	}
	
	private void removeProject(String name) {
		ProjectController.deleteProject(name, this);
		refresh();
	}
	
	private void updateProject(String name) {
		ProjectController controller = CodeMapApp.get(this).getProjectController(name);
		
		controller.updateProject();
		controller.buildIndex();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.projects, menu);
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
