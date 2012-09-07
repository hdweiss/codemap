package com.hdweiss.codemap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import com.hdweiss.codemap.util.Utils;

public class CtagsReader {
	private int FILE_NAME = 1;
	private int REGEX = 2;

	String tagsFile = "";

	public CtagsReader(InputStream tagsFileStream) {
		this.tagsFile = Utils.inputStreamToString(tagsFileStream);
	}

	public TagsEntry getTagEntry(String symbol) {
		Pattern pattern = Pattern.compile("\\n" + symbol
				+ "\\t([^\\t]*)\\t([^\\t]*)");
		Matcher matcher = pattern.matcher(tagsFile);

		TagsEntry tagsEntry = new TagsEntry();

		if (matcher.find()) {
			tagsEntry.symbol = symbol;
			tagsEntry.filename = matcher.group(FILE_NAME);
			tagsEntry.regex = matcher.group(REGEX);
		}

		return tagsEntry;
	}
	
	public String getSource(TagsEntry tagsEntry) {
		try {
			FileInputStream sourceFile = new FileInputStream(tagsEntry.filename);
			String sourceFileString = Utils.inputStreamToString(sourceFile);
			
			final String javaTagRegex = "\\n(\\Qstatic boolean createTagsForEntry (const char *const entryName)\\E)\\s*\\{";

			Pattern pattern = Pattern.compile(javaTagRegex);
			Matcher matcher = pattern.matcher(sourceFileString);
			
			//Log.d("CodeMap", "Trying to match " + tagsEntry.getJavaRegex());

			if(matcher.find()) {
				int matchStart = matcher.start();
				
				int matchEnd = matchStart + 300;
				String sourceFragment = sourceFileString.substring(matchStart, matchEnd);
				Log.d("CodeMap", sourceFragment);
			} else
				Log.d("CodeMap", "No match found!");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}
}
