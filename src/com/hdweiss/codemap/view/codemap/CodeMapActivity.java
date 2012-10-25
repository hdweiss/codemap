package com.hdweiss.codemap.view.codemap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.hdweiss.codemap.R;
import com.hdweiss.codemap.data.ProjectController;

public class CodeMapActivity extends Activity {

	public final static String PROJECT_NAME = "projectName";
	
    private ProjectController controller;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.codemap);
        
        Intent intent = getIntent();
        String projectName = intent.getStringExtra(PROJECT_NAME);
        
		controller = new ProjectController(projectName, this);
		controller.init();
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
