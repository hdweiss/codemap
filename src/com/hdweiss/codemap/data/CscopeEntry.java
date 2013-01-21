package com.hdweiss.codemap.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CscopeEntry {
	private static int CSCOPE_FILE = 1;
	private static int CSCOPE_ACTUALNAME = 2;
	private static int CSCOPE_NAME = 4;
	private static int CSCOPE_LINENUMBER = 3;
	
	public String actualName = "";
	public String name = "";
	public String file = "";
	public int lineNumber = -1;
	private int endLine = -2;
	
	private static Pattern CscopeEntryPattern = Pattern.compile("(\\S*)\\s(\\S*)\\s(\\d*)\\s(.*)");
	public CscopeEntry(String line) {
		Matcher matcher = CscopeEntryPattern.matcher(line);
		
		if(matcher.find()) {
			this.file = matcher.group(CSCOPE_FILE);
			this.name = matcher.group(CSCOPE_NAME);
			this.actualName = matcher.group(CSCOPE_ACTUALNAME);
			this.lineNumber = Integer.parseInt(matcher.group(CSCOPE_LINENUMBER));
		} else
			throw new IllegalArgumentException("Couldn't parse " + line);
	}
	
	public int getEndLine(CscopeWrapper cscopeWrapper) {
		if (endLine == -2)
			endLine = cscopeWrapper.getFunctionEndLine(this);
		return endLine;
	}
	
	public String getUrl(String projectPath) {
		if (file.length() > projectPath.length()) {
			String relativeFilename = file.substring(projectPath.length() + 1);
			return relativeFilename + ":" + actualName;
		} else
			return file + ":" + actualName;
	}
	
	public String toString() {
		return file + ":" + name + "@" + lineNumber;
	}
}
