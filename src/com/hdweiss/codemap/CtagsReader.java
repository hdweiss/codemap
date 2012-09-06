package com.hdweiss.codemap;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import com.hdweiss.codemap.util.Utils;

public class CtagsReader {
	private int SYMBOL_NAME = 0;
	private int FILE_NAME = 1;
	private int REGEX = 2;
	
	String tagsFile = "";

	public CtagsReader(InputStream tagsFileStream) {
		this.tagsFile = Utils.inputStreamToString(tagsFileStream);
		Log.d("CodeMap", "Got tags file: \n" + tagsFile);
	}

	public String getSymbolFile(String symbol) {
		Pattern pattern = Pattern.compile("\\n" + symbol + "\\t([^\\t]*)");
		Matcher matcher = pattern.matcher(tagsFile);

		if(matcher.find())
			return matcher.group(FILE_NAME);
		else
			return "";
	}
}
