package com.hdweiss.codemap.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.test.AndroidTestCase;

import com.hdweiss.codemap.data.Cscope;
import com.hdweiss.codemap.data.CscopeEntry;
import com.hdweiss.codemap.data.Project;

@SuppressLint("SdCardPath")
public class CscopeTest extends AndroidTestCase {
	private static final String PROJECT_NAME = "Testproject";
	private static final String PROJECT_PATH = "/sdcard/ctags/";
	private static final int NUMBER_OF_FILES = 87;
	
	private static final String ADDTOTALS_FIRSTLINE = "extern void addTotals";
	private static final int declarationsInMain = 29;
	private static final int declarationsInMainClean = 17;


	private Context context;
	private Cscope cscope;
	private Project project;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.context = getContext();
		this.cscope = new Cscope(context);
		this.project = new Project(PROJECT_NAME, PROJECT_PATH, context);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		cscope.deleteNamefile(PROJECT_NAME);
		cscope.deleteReffile(PROJECT_NAME);
	}

	@Override
	public void testAndroidTestCaseSetupProperly() {
		super.testAndroidTestCaseSetupProperly();
		
		File ctagsDir = new File(PROJECT_PATH);
		if(ctagsDir.exists() == false)
			fail("Didn't find source directory");
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
	
	
	public void testGetFunction() {
		cscope.generateNamefile(project);
		cscope.generateReffile(project);
		String contents = cscope.getFunction(project, "addTotals");
		assertTrue(contents.contains(ADDTOTALS_FIRSTLINE));
	}
	
	public void testGetDeclarations() {
		cscope.generateNamefile(project);
		cscope.generateReffile(project);
		
		ArrayList<String> declarations = cscope.getDeclarations("main.c", project);
		assertEquals(declarationsInMainClean, declarations.size());
	}
	
	public void testGetAllDeclarations() {
		cscope.generateNamefile(project);
		cscope.generateReffile(project);
		
		HashMap<String,ArrayList<CscopeEntry>> declarations = cscope.getAllDeclarations(project);
		assertEquals(NUMBER_OF_FILES, declarations.size());
		
		ArrayList<CscopeEntry> mainDeclarations = declarations.get(PROJECT_PATH + "main.c");
		assertNotNull(mainDeclarations);
		assertEquals(declarationsInMain, mainDeclarations.size());
	}
}
