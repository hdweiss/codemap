package com.hdweiss.codemap.test;

import java.io.InputStream;

import android.test.InstrumentationTestCase;

import com.hdweiss.codemap.CtagsReader;
import com.hdweiss.codemap.TagsEntry;

public class CtagsReaderTest extends InstrumentationTestCase {
	
	private final String TAGS_TESTDATA_SIMPLE = "tags_testdata_simple";

	private final String tagSymbol = "createTagsForEntry";
	private final String tagFile = "/sdcard/ctags/main.c";
	private final String tagRegex = "/^static boolean createTagsForEntry (const char *const entryName)$/;\"";
	private final String javaTagRegex = "\\^\\s*static boolean createTagsForEntry (const char \\*const entryName)\\s*\\$";
	private final String tagSource = new StringBuilder().append("static boolean createTagsForEntry (const char *const entryName)")
			.append("\n{")
			.append("\n        boolean resize = FALSE;")
			.append("\n        fileStatus *status = eStat (entryName);").toString();

	
	private CtagsReader ctagsReader;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		InputStream tagsStream = getInstrumentation().getContext().getAssets()
				.open(TAGS_TESTDATA_SIMPLE);
		this.ctagsReader = new CtagsReader(tagsStream);
	}
	
	protected void tearDown() {
	}
	
	public void testTagsEntryGetJavaRegex() {
		TagsEntry tagsEntry = new TagsEntry();
		tagsEntry.regex = tagRegex;
		assertEquals(javaTagRegex, tagsEntry.getJavaRegex());
	}
	
	public void testGetSymbol() {
		TagsEntry tagsEntry = ctagsReader.getTagEntry(tagSymbol);
		assertEquals(tagSymbol, tagsEntry.symbol);
		assertEquals(tagFile, tagsEntry.filename);
		assertEquals(tagRegex, tagsEntry.regex);
	}
	
	public void testGetSource() {
		TagsEntry tagsEntry = ctagsReader.getTagEntry(tagSymbol);
		String source = ctagsReader.getSource(tagsEntry);
		assertTrue(source.contains(tagSource));
	}
}
