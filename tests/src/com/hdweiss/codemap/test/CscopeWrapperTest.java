package com.hdweiss.codemap.test;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.test.AndroidTestCase;

import com.hdweiss.codemap.data.Cscope;
import com.hdweiss.codemap.data.CscopeEntry;
import com.hdweiss.codemap.data.CscopeWrapper;
import com.hdweiss.codemap.data.Project;
import com.hdweiss.codemap.util.Utils;

/**
 * Tests cscope source.
 */
public class CscopeWrapperTest extends AndroidTestCase {
	private static final String PROJECT_NAME = "Testproject";
	private static final int NUMBER_OF_FILES = 87;
	
	private static final String ADDTOTALS_FIRSTLINE = "extern void addTotals";
	private static final int declarationsInMain = 29;
	private static final int declarationsInMainClean = 17;
	
	private Context context;
	private Cscope cscope;
	private Project project;
	private CscopeWrapper cscopeWrapper;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.context = getContext();
		this.project = new Project(PROJECT_NAME);
		this.cscope = new Cscope(context);
		this.cscopeWrapper = new CscopeWrapper(cscope, project, context);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		cscope.deleteNamefile(PROJECT_NAME);
		cscope.deleteReffile(PROJECT_NAME);
		Utils.deleteRecursive(project.getProjectDirectory(context));
	}
	
	
	public void testGetFunction() {
		cscope.generateNamefile(project);
		cscope.generateReffile(project);
		CscopeEntry functionEntry = cscopeWrapper.getFunctionEntry("addTotals", "");
		String contents = cscopeWrapper.getFunction(functionEntry);
		assertTrue(contents.contains(ADDTOTALS_FIRSTLINE));
	}
	
	public void testGetDeclarations() {
		cscope.generateNamefile(project);
		cscope.generateReffile(project);
		
		ArrayList<String> declarations = cscopeWrapper.getDeclarations("main.c");
		assertEquals(declarationsInMainClean, declarations.size());
	}
	
	public void testGetAllDeclarations() {
		cscope.generateNamefile(project);
		cscope.generateReffile(project);
		
		HashMap<String,ArrayList<CscopeEntry>> declarations = cscopeWrapper.getAllDeclarations();
		assertEquals(NUMBER_OF_FILES, declarations.size());
		
		ArrayList<CscopeEntry> mainDeclarations = declarations.get(project.getSourcePath(context) + "/main.c");
		assertNotNull(mainDeclarations);
		assertEquals(declarationsInMain, mainDeclarations.size());
	}
}
