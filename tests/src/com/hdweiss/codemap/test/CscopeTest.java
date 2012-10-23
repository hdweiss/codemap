package com.hdweiss.codemap.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.test.AndroidTestCase;

import com.hdweiss.codemap.data.Cscope;
import com.hdweiss.codemap.data.CscopeEntry;

@SuppressLint("SdCardPath")
public class CscopeTest extends AndroidTestCase {
	private static String PROJECT_NAME = "Testproject";
	private static String PROJECT_PATH = "/sdcard/ctags/";
	
	private static String ADDTOTALS_FIRSTLINE = "extern void addTotals";
	@SuppressWarnings("unused")
	private static String MAIN_FIRSTLINE = "extern int main (int __unused__ argc, char **argv)";

	private static int NUMBER_OF_FILES = 87;
	
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
//		cscope.deleteNamefile(PROJECT_NAME);
//		cscope.deleteReffile(PROJECT_NAME);
	}

	@Override
	public void testAndroidTestCaseSetupProperly() {
		super.testAndroidTestCaseSetupProperly();
		
		File ctagsDir = new File(PROJECT_PATH);
		if(ctagsDir.exists() == false)
			fail("Didn't find source directory");
	};
	
	public void testGenerateNamefile() {
		cscope.generateNamefile(PROJECT_NAME, PROJECT_PATH);
		
		try {
			FileInputStream namefile = cscope.getNamefileStream(PROJECT_NAME);
			assertTrue(namefile.available() > 0);
			// Log.d("CodeMap", Utils.inputStreamToString(namefile));
		} catch (IOException e) {
			fail("Couldn't generate namefile");
		}
	}
	
	
	public void testGenerateReffile() {
		cscope.generateNamefile(PROJECT_NAME, PROJECT_PATH);
		cscope.generateReffile(PROJECT_NAME, PROJECT_PATH);
		
		try {
			FileInputStream reffile = cscope.getReffileStream(PROJECT_NAME);
			assertTrue(reffile.available() > 0);
		} catch (IOException e) {
			fail("Couldn't generate reffile");
		}
	}
	
	public void testGetFunction() {
		cscope.generateNamefile(PROJECT_NAME, PROJECT_PATH);
		cscope.generateReffile(PROJECT_NAME, PROJECT_PATH);
		String contents = cscope.getFunction(PROJECT_NAME, PROJECT_PATH, "addTotals");
		assertTrue(contents.contains(ADDTOTALS_FIRSTLINE));
	}
	
	public void testAllDeclarations() {
		cscope.generateNamefile(PROJECT_NAME, PROJECT_PATH);
		cscope.generateReffile(PROJECT_NAME, PROJECT_PATH);
		
		HashMap<String,CscopeEntry> declarations = cscope.getAllDeclarations(PROJECT_NAME, PROJECT_PATH);
		
		assertEquals(NUMBER_OF_FILES, declarations.size());
	}
}
