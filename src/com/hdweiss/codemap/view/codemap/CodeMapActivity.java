package com.hdweiss.codemap.view.codemap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.hdweiss.codemap.R;
import com.hdweiss.codemap.data.ProjectController;

public class CodeMapActivity extends Activity {

    private ProjectController controller;

	@SuppressLint("SdCardPath")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
		controller = new ProjectController("Testproject", this);
		controller.init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public ProjectController getController() {
    	return this.controller;
    }
}
