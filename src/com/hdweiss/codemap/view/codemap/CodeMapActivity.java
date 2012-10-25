package com.hdweiss.codemap.view.codemap;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.hdweiss.codemap.R;
import com.hdweiss.codemap.data.CodeMapApp;
import com.hdweiss.codemap.data.ProjectController;

public class CodeMapActivity extends Activity {
	public final static String PROJECT_NAME = "projectName";
	
    private ProjectController controller;

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
    
    public ProjectController getController() {
    	return this.controller;
    }
}
