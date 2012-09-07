package com.hdweiss.codemap.test;

import java.io.FileInputStream;
import java.io.IOException;

import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;

import com.hdweiss.codemap.Cscope;
import com.hdweiss.codemap.util.Utils;

public class CscopeTest extends AndroidTestCase {
	private static String PROJECT_NAME = "Testproject";
	private static String PROJECT_PATH = "/sdcard/ctags/";
	
	private Context context;
	private Cscope cscope;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.context = getContext();
		this.cscope = new Cscope(context);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		cscope.deleteNamefile(PROJECT_NAME);
		cscope.deleteReffile(PROJECT_NAME);
	}

	public void testGenerateNamefile() throws IOException {
		cscope.generateNamefile(PROJECT_NAME, PROJECT_PATH);
		
		FileInputStream namefile = cscope.getNamefileStream(PROJECT_NAME);
		assertTrue(namefile.available() > 0);
		//Log.d("CodeMap", Utils.inputStreamToString(namefile));
	}
	
	
	public void testGenerateReffile() throws IOException {
		cscope.generateNamefile(PROJECT_NAME, PROJECT_PATH);
		cscope.generateReffile(PROJECT_NAME, PROJECT_PATH);
		
		FileInputStream reffile = cscope.getReffileStream(PROJECT_NAME);
		assertTrue(reffile.available() > 0);
	}

}
