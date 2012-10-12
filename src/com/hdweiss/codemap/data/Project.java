package com.hdweiss.codemap.data;

import android.content.Context;
import android.text.Html;
import android.text.SpannableString;

public class Project {

	private String name;
	private String path;
	private Context context;
	
	private Cscope cscope;

	public Project(String name, String path, Context context) {
		this.name = name;
		this.path = path;
		this.context = context;
		
		init();
	}
	
	private void init() {
		this.cscope = new Cscope(context);
		cscope.generateNamefile(this.name, this.path);
		cscope.generateReffile(this.name, this.path);
	}
	
	public SpannableString getFunctionSource(String symbolName) {
		String content = cscope.getFunction(this.name, this.path, symbolName).trim();
		SpannableString spannableString = new SpannableString(
				Html.fromHtml(content));
		return spannableString;
	}
}
