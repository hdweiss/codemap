package com.hdweiss.codemap.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.Html;
import android.text.SpannableString;

import com.hdweiss.codemap.data.CscopeEntry;

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
	
	public void markupReferences(ArrayList<CscopeEntry> references) {
		StringBuilder result = new StringBuilder(content);
		for(CscopeEntry entry: references) {
			//Log.d("CodeMap", entry.toString());
			Matcher matcher = Pattern.compile(Pattern.quote(entry.actualName)).matcher(result);
			
			if(matcher.find()) {
				result.insert(matcher.end(), "</a>");
				result.insert(matcher.start(), "<a href=\"" + entry.actualName + "\">");
			}
		}
		content = result.toString();
	}
}
