package com.hdweiss.codemap.view;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ListView;

import com.hdweiss.codemap.R;
import com.hdweiss.codemap.controller.ProjectController;
import com.hdweiss.codemap.data.CodeMapApp;
import com.hdweiss.codemap.data.Project;
import com.hdweiss.codemap.view.codemap.CodeMapActivity;

public class ProjectBrowser extends FragmentActivity implements OnItemClickListener {

	private ListView listView;
	private BroadcastReceiver syncReceiver;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.projects);
        
        this.listView = (ListView) findViewById(R.id.projects_list);
        listView.setOnItemClickListener(this);
        registerForContextMenu(listView);
        
        this.syncReceiver = new SynchServiceReceiver();
		registerReceiver(this.syncReceiver, new IntentFilter(
				SynchServiceReceiver.SYNC_UPDATE));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		refresh();
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(this.syncReceiver);
		super.onDestroy();
	}

	public void refresh() {
		ArrayList<String> projectsList = ProjectController.getProjectsList(this);
		ProjectAdapter adapter = new ProjectAdapter(this, projectsList);
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
			updateProject(projectName, this);
			break;
			
		case R.id.projects_edit:
			editProject(projectName);
			break;
			
		default:
			return super.onContextItemSelected(item);
		}
		
		return true;
	}
	
	private void removeProject(String name) {
		Project.delete(name, this);
		refresh();
	}
	
	public static void updateProject(String name, Activity activity) {
		ProjectController controller = CodeMapApp.get(activity).getProjectController(name);
		controller.updateProject();
	}
	
	public void editProject(String name) {
		try {
			Project project = Project.readProject(name, this);
			
			ProjectWizard projectWizard = new ProjectWizard();
			projectWizard.setProject(project);
			projectWizard.show(getFragmentManager(), "wizard");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	
	public class SynchServiceReceiver extends BroadcastReceiver {
		public static final String SYNC_UPDATE = "com.hdweiss.codemap.action.SYNC_UPDATE";
		
		public static final String SYNC_NAME = "sync_name";
		public static final String SYNC_STATUS = "sync_status"; 
		public static final String SYNC_START = "sync_start";
		public static final String SYNC_DONE = "sync_done";
		public static final String SYNC_PROGRESS_UPDATE = "sync_update";

		@Override
		public void onReceive(Context context, Intent intent) {
			boolean syncStart = intent.getBooleanExtra(SYNC_START, false);
			boolean syncDone = intent.getBooleanExtra(SYNC_DONE, false);
			int progress = intent.getIntExtra(SYNC_PROGRESS_UPDATE, -1);

			String status = intent.getStringExtra(SYNC_STATUS);
			String projectName = intent.getStringExtra(SYNC_NAME);
			int position = ((ProjectAdapter)listView.getAdapter()).getItemPosition(projectName);
			ProjectItemView itemView = (ProjectItemView) listView.getChildAt(position);
			
			if (itemView == null)
				return;

			if(syncStart) {
				itemView.beginUpdate();
			} else if (syncDone) {
				itemView.endUpdate();
				
			} else {
				if(progress > -1)
					itemView.setProgress(progress);
				itemView.setStatus(status);
			}
		}
	}
}
