package com.hdweiss.codemap;

public class TagsEntry {

	public String symbol = "";
	public String regex = "";
	public String filename = "";
	
	public String getJavaRegex() {
		String javaRegex = regex;
		javaRegex = javaRegex.replace("^", "\\n");
		javaRegex = javaRegex.replace("$", "\\$");
		javaRegex = javaRegex.replace("*", "\\*");
		javaRegex = javaRegex.replace("\\$/;\"", "\\s*\\$");
		javaRegex = javaRegex.replace("/\\^", "\\^\\s*");
		return javaRegex;
	}
}
