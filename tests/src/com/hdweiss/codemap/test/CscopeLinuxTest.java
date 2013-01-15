package com.hdweiss.codemap.test;

import java.util.ArrayList;

import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;

import com.hdweiss.codemap.data.Cscope;
import com.hdweiss.codemap.data.Project;

public class CscopeLinuxTest extends AndroidTestCase {
	private static final String PROJECT_NAME = "linux";
	
	private Context context;
	private Cscope cscope;
	private Project project;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.context = getContext();
		this.cscope = new Cscope(context);
		this.project = new Project(PROJECT_NAME);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testGetFunctionLinux() {
		String contents = cscope.getFunction(project, "zlib_comp_exit", "crypto/zlib.c");
		Log.d("CodeMap", "Got output:\n" + contents);
		assertTrue(contents.isEmpty() == false);
	}
	
	public void testGetDeclarationsLinux() {
		ArrayList<String> declarations = cscope.getDeclarations("crypto/zlib.c", project);
		Log.d("CodeMap", "Declaration size " + declarations.size());
		assertTrue(declarations.size() > 0);
	}
}
