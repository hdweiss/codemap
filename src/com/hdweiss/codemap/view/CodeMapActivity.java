package com.hdweiss.codemap.view;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.hdweiss.codemap.R;
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
            bar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }
	}
	

    @Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
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
		Bundle bundle = new Bundle();
		bundle.putString(WorkspaceFragment.WORKSPACE_NAME, name);
		bundle.putString(WorkspaceFragment.PROJECT_NAME, this.projectName);
		ActionBar bar = getActionBar();
		bar.addTab(bar
				.newTab()
				.setText(name)
				.setTabListener(
						new CodeMapTabListener<WorkspaceFragment>(this, name,
								WorkspaceFragment.class, bundle)));
	}
	
	public void closeTab() {
		ActionBar bar = getActionBar();
		int tabIndex = bar.getSelectedNavigationIndex();
		
		if (tabIndex < 0)
			return;
		
		bar.removeTabAt(tabIndex);
	}
}
