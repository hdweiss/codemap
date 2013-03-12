package com.hdweiss.codemap.view;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.hdweiss.codemap.R;
import com.hdweiss.codemap.data.CodeMapApp;
import com.hdweiss.codemap.view.workspace.WorkspaceController;
import com.hdweiss.codemap.view.workspace.WorkspaceFragment;

public class CodeMapActivity extends Activity {
	public final static String PROJECT_NAME = "projectName";
	
	private String projectName;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.codemap);
        
        this.projectName = getIntent().getStringExtra(PROJECT_NAME);
        
        final ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
		
        if (savedInstanceState != null) {
        	ArrayList<String> tabs = savedInstanceState.getStringArrayList("tabs");
        	
        	for (String tabName: tabs)
        		addWorkspaceFragment(tabName);
        	
        	int index = savedInstanceState.getInt("tab", 0);
        	bar.setSelectedNavigationItem(index);
        }
	}

    @Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
        
        final ActionBar bar = getActionBar();
        ArrayList<String> tabs = new ArrayList<String>();
        for (int i = 0; i < bar.getTabCount(); i++) {
        	Tab tab = bar.getTabAt(i);
        	tabs.add(tab.getText().toString());
        }
        
        outState.putStringArrayList("tabs", tabs);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	
    
    @Override
	public boolean onPrepareOptionsMenu(Menu menu) {
    	MenuItem closeItem = menu.findItem(R.id.menu_closetab);
    	if (getActionBar().getTabCount() == 0)
    		closeItem.setVisible(false);
    	else
    		closeItem.setVisible(true);
    	
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivity(new Intent(this, Preferences.class));
			return true;

		case R.id.menu_addtab:
			addWorkspaceFragment();
			return true;
			
		case R.id.menu_closetab:
			closeTab();
			return true;
			
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}
    
	public void addWorkspaceFragment() {
		final String name = "Workspace " + getActionBar().getTabCount();
		addWorkspaceFragment(name);
	}
	
	public void addWorkspaceFragment(String name) {
		Bundle bundle = new Bundle();
		bundle.putString(WorkspaceFragment.WORKSPACE_NAME, name);
		bundle.putString(WorkspaceFragment.PROJECT_NAME, this.projectName);
		ActionBar bar = getActionBar();
		bar.addTab(bar
				.newTab()
				.setText(name)
				.setTabListener(
						new CodeMapTabListener<WorkspaceFragment>(this, name,
								WorkspaceFragment.class, bundle)), true);
	}
	
	public void closeTab() {
		ActionBar bar = getActionBar();
		int tabIndex = bar.getSelectedNavigationIndex();
		
		if (tabIndex < 0)
			return;
				
		bar.removeTabAt(tabIndex);
	}
	
	public void navigateToTab(String workspaceName, String url) {
		try {
			int index = findTabIndex(workspaceName);
			ActionBar bar = getActionBar();
			bar.setSelectedNavigationItem(index);
		} catch (IllegalArgumentException e) {
			addWorkspaceFragment(workspaceName);
		}
		
		CodeMapApp app = (CodeMapApp) getApplication();
		WorkspaceController controller = app.getController(projectName, workspaceName);
		
		Log.d("CodeMap", "navigateToTab()");
		if (controller != null) {
			Log.d("CodeMap", "navigateToTab(): symbol clicked");
			controller.symbolClicked(url, null);
		}
	}
	
	private int findTabIndex(String tabName) {
		ActionBar bar = getActionBar();

		for (int i = 0; i < bar.getTabCount(); i++) {
			Tab tab = bar.getTabAt(i);
			
			if (tabName.equals(tab.getText()))
				return i;
		}
		
		throw new IllegalArgumentException("Could not find tab with name " + tabName);
	}
}
