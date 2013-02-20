package com.hdweiss.codemap.view.codemap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.hdweiss.codemap.R;
import com.hdweiss.codemap.controller.CodeMapController;
import com.hdweiss.codemap.data.CodeMapApp;
import com.hdweiss.codemap.view.Preferences;

public class CodeMapActivity extends Activity {
	public final static String PROJECT_NAME = "projectName";
	
    private CodeMapController controller;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.codemap);
        
        String projectName = getIntent().getStringExtra(PROJECT_NAME);
        
        controller = CodeMapApp.get(this).getProjectController(projectName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public CodeMapController getController() {
    	return this.controller;
    }

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivity(new Intent(this, Preferences.class));
			return true;

		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}
    
}
