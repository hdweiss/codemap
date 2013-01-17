package com.hdweiss.codemap.test;

import java.util.ArrayList;

import android.content.Context;
import android.test.AndroidTestCase;

import com.hdweiss.codemap.data.Cscope;
import com.hdweiss.codemap.data.CscopeEntry;
import com.hdweiss.codemap.data.CscopeWrapper;
import com.hdweiss.codemap.data.Project;

public class CscopeLinuxTest extends AndroidTestCase {
	private static final String PROJECT_NAME = "linux";
	
	private static final String FILENAME = "crypto/zlib.c";
	private static final String FUNC_NAME = "zlib_comp_exit";
	private static final int FUNC_END_LINE = 54;
	private static final int FILE_DECLARATION_NUM = 15;

	
	private Context context;
	private Project project;
	private Cscope cscope;
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
	}
	
	public void testGetFunctionEntry() {
		CscopeEntry entry = cscopeWrapper.getFunctionEntry(FUNC_NAME, FILENAME);
		
		assertEquals(project.getSourcePath(context) + "/" + FILENAME,
				entry.file);
		assertTrue(entry.name.contains(FUNC_NAME));
	}
	
	public void testGetFunctionEndLine() {
		CscopeEntry entry = cscopeWrapper.getFunctionEntry(FUNC_NAME, FILENAME);
		int functionEndLine = cscopeWrapper.getFunctionEndLine( entry);
		
		assertEquals(FUNC_END_LINE, functionEndLine);
	}
	
	public void testGetFunctionLinux() {
		CscopeEntry functionEntry = cscopeWrapper.getFunctionEntry(FUNC_NAME, FILENAME);
		String contents = cscopeWrapper.getFunction(functionEntry);
		assertTrue(contents.isEmpty() == false);
	}
	
	public void testGetDeclarationsLinux() {
		ArrayList<String> declarations = cscopeWrapper.getDeclarations(FILENAME);
		assertEquals(FILE_DECLARATION_NUM, declarations.size());
	}
}
