package com.hdweiss.codemap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CscopeEntry {
	private static int CSCOPE_FILE = 1;
	private static int CSCOPE_NAME = 2;
	private static int CSCOPE_LINENUMBER = 3;
	
	public String name = "";
	public String file = "";
	public int lineNumber = -1; 
	
	public CscopeEntry(String line) {
		Pattern pattern = Pattern.compile("(\\S*)\\s(\\S*)\\s(\\d*)");
		Matcher matcher = pattern.matcher(line);
		
		if(matcher.find()) {
			this.file = matcher.group(CSCOPE_FILE);
			this.name = matcher.group(CSCOPE_NAME);
			this.lineNumber = Integer.parseInt(matcher.group(CSCOPE_LINENUMBER));
		} else
			throw new IllegalArgumentException("Couldn't parse " + line);
	}
	
	public String toString() {
		return file + ":" + name + "@" + lineNumber;
	}
}
