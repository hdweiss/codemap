package com.hdweiss.codemap.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.hdweiss.codemap.R;
import com.hdweiss.codemap.data.Project;

public class CodeMapActivity extends Activity {

    private Project project;

	@SuppressLint("SdCardPath")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
		project = new Project("Testproject", "/sdcard/ctags/", this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public Project getProject() {
    	return this.project;
    }
}
