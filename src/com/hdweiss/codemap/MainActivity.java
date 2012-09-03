package com.hdweiss.codemap;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testLib();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    private void testLib() {
    	System.loadLibrary("ctags");
    	
    	char[] argv = {};
    	int returnCode = main(0, argv);
    }
    
    public static int two() {
    	return 2;
    }
    
    public native int main (int argc, char argv[]);
}
