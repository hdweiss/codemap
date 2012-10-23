package com.hdweiss.codemap.data;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.content.Context;
import android.text.SpannableString;
import android.util.Log;

import com.hdweiss.codemap.util.SyntaxHighlighter;
import com.hdweiss.codemap.view.CodeMapView;

public class Project {

	private String name;
	private String path;
	private Context context;
	
	private Cscope cscope;
	private CodeMapView codeMapView;

	public Project(String name, String path, Context context) {
		this.name = name;
		this.path = path;
		this.context = context;
	}
	
	public void setView(CodeMapView codeMapView) {
		this.codeMapView = codeMapView;
	}
	
	public void init() {
		this.cscope = new Cscope(context);
		cscope.generateNamefile(this);
		cscope.generateReffile(this);
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public SpannableString getFunctionSource(String functionName) {
		try {
			String content = cscope.getFunction(this, functionName).trim();
			SyntaxHighlighter highlighter = new SyntaxHighlighter(content);
			highlighter.markupReferences(cscope.getReferences(this, functionName));

			return highlighter.formatToHtml();
		} catch (IllegalArgumentException e) {
			return new SpannableString("");
		}
	}
	
	public String[] getFiles() {
		try {
			return cscope.getFiles(this);
		} catch (FileNotFoundException e) {
			Log.e("CodeMap", e.getStackTrace().toString());
			return new String[0];
		}
	}
	
	public ArrayList<String> getSymbols(String filename) {
		return cscope.getDeclarations(filename, this);
	}
	
	public void addFunctionView(String functionName) {
		if(codeMapView != null)
			codeMapView.createFunction(functionName);
	}
}
