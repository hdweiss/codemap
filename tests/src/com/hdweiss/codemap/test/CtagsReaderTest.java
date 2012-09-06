package com.hdweiss.codemap.test;

import java.io.InputStream;

import android.test.InstrumentationTestCase;

import com.hdweiss.codemap.CtagsReader;

public class CtagsReaderTest extends InstrumentationTestCase {
	
	private final String TAGS_TESTDATA_SIMPLE = "tags_testdata_simple";
	
	private CtagsReader ctagsReader;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		InputStream tagsStream = getInstrumentation().getContext().getAssets()
				.open(TAGS_TESTDATA_SIMPLE);
		this.ctagsReader = new CtagsReader(tagsStream);
	}
	
	protected void tearDown() {
	}
	
	public void testGetSymbol() {
		final String tagSymbol = "createTagsForEntry";
		final String tagFile = "/sdcard/ctags/main.c";
		String symbolFile = ctagsReader.getSymbolFile(tagSymbol);
		assertEquals(tagFile, symbolFile);
	}
}
