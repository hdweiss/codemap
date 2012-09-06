package com.hdweiss.codemap.test;

import java.io.InputStream;

import android.test.InstrumentationTestCase;

import com.hdweiss.codemap.CtagsReader;
import com.hdweiss.codemap.TagsEntry;

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
		final String tagRegex = "/^static boolean createTagsForEntry (const char *const entryName)$/;\"";
		TagsEntry tagsEntry = ctagsReader.getTagEntry(tagSymbol);
		assertEquals(tagSymbol, tagsEntry.symbol);
		assertEquals(tagFile, tagsEntry.filename);
		assertEquals(tagRegex, tagsEntry.regex);
	}
}
