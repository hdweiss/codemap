package com.hdweiss.codemap.util;

import android.text.Html;
import android.text.SpannableString;

public class SyntaxHighlighter {
	
	private final String[] C_KEYWORDS =  {
			"auto",
			"break",
			"case",
			"char",
			"const",
			"continue",
			"default",
			"do",
			"double",
			"else",
			"enum",
			"extern",
			"float",
			"for",
			"goto",
			"if",
			"int",
			"long",
			"register",
			"return",
			"short",
			"signed",
			"sizeof",
			"static",
			"struct",
			"switch",
			"typedef",
			"union",
			"unsigned",
			"void",
			"volatile",
			"while" };
					
	private String content;
	
	public SyntaxHighlighter(String contents) {
		this.content = contents;
	}
	
	public SpannableString formatToHtml() {
		formatNewline();
		highlightKeywords();
		highlightComments();
		
		SpannableString spannableString = new SpannableString(
				Html.fromHtml(content));
		return spannableString;
	}
	
	private void formatNewline() {
		content = content.replaceAll("\n\r", "<br />");
		content = content.replaceAll("\n", "<br />");
		content = content.replaceAll("\r", "<br />");
	}
	
	private void highlightKeywords() {
		for(String keyword: C_KEYWORDS) {
			content = content.replaceAll(keyword, "<font color=\"purple\">" + keyword + "</font>");
		}
	}
	
	private void highlightComments() {
		content = content.replaceAll("/\\*.*\\*/", "<font color=\"green\">$0</font>");
		content = content.replaceAll("//[^\n]*\n", "<font color=\"green\">$0</font>");
	}
}
