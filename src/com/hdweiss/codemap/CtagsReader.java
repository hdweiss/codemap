package com.hdweiss.codemap;

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
		Log.d("CodeMap", "Got tags file: \n" + tagsFile);
	}

	public TagsEntry getTagEntry(String symbol) {
		Pattern pattern = Pattern.compile("\\n" + symbol + "\\t([^\\t]*)\\t([^\\t]*)");
		Matcher matcher = pattern.matcher(tagsFile);

		TagsEntry tagsEntry = new TagsEntry();
		
		if(matcher.find()) {
			tagsEntry.symbol = symbol;
			tagsEntry.filename = matcher.group(FILE_NAME);
			tagsEntry.regex = matcher.group(REGEX);
		}

		return tagsEntry;
	}
}
