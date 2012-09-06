package com.hdweiss.codemap.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.test.AndroidTestCase;

import com.hdweiss.codemap.Ctags;

public class CtagsTest extends AndroidTestCase {
	private static String PROJECT_NAME = "Testproject";
	private static int SIZE_OF_EMPTY_TAGSFILE = 332;
	private static String CTAGS_OPTIONS = "/sdcard/ctags/*.h /sdcard/ctags/*.c";

	
	private Ctags ctags;
	
	protected void setUp() throws Exception {
		super.setUp();
		this.ctags = new Ctags(getContext());
	}
	
	protected void tearDown() {
		ctags.deleteTagsFile(PROJECT_NAME);
	}

	public void testRunCtagsHelp() throws IOException {
		String output = ctags.generateTagsFile(PROJECT_NAME, "--help", true);
		assertFalse(output.isEmpty());
	}
	
	public void testTagsGeneration() throws IOException {
		ctags.generateTagsFile(PROJECT_NAME, CTAGS_OPTIONS, false);
		try {
			FileInputStream tagsFileStream = ctags.getTagsFile(PROJECT_NAME);
			assertTrue(tagsFileStream.available() > SIZE_OF_EMPTY_TAGSFILE);
		} catch(FileNotFoundException e) {
			fail("No Tags file generated");
		}
	}
}
