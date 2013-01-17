package com.hdweiss.codemap.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.content.Context;
import android.test.AndroidTestCase;

import com.hdweiss.codemap.data.Cscope;
import com.hdweiss.codemap.data.Project;
import com.hdweiss.codemap.util.Utils;

public class CscopeTest extends AndroidTestCase {
	private static final String PROJECT_NAME = "Testproject";

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
		cscope.deleteNamefile(PROJECT_NAME);
		cscope.deleteReffile(PROJECT_NAME);
		Utils.deleteRecursive(project.getProjectDirectory(context));
	}

	@Override
	public void testAndroidTestCaseSetupProperly() {
		super.testAndroidTestCaseSetupProperly();
		
		
		File ctagsDir = new File(project.getSourcePath(context));
		if(ctagsDir.exists() == false)
			fail("Didn't find source directory " + project.getSourcePath(context));
	};
	
	public void testGenerateNamefile() {
		cscope.generateNamefile(project);
		
		try {
			FileInputStream namefile = cscope.getNamefileStream(PROJECT_NAME);
			assertTrue(namefile.available() > 0);
		} catch (IOException e) {
			fail("Couldn't generate namefile");
		}
	}
	
	
	public void testGenerateReffile() {
		cscope.generateNamefile(project);
		cscope.generateReffile(project);
		
		try {
			FileInputStream reffile = cscope.getReffileStream(PROJECT_NAME);
			assertTrue(reffile.available() > 0);
		} catch (IOException e) {
			fail("Couldn't generate reffile");
		}
	}

}
