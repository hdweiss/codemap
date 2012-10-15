package com.hdweiss.codemap.data;

import java.util.ArrayList;

import android.content.Context;
import android.text.SpannableString;

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
		
		init();
	}
	
	public void setView(CodeMapView codeMapView) {
		this.codeMapView = codeMapView;
	}
	
	private void init() {
		this.cscope = new Cscope(context);
		cscope.generateNamefile(this.name, this.path);
		cscope.generateReffile(this.name, this.path);
	}
	
	public SpannableString getFunctionSource(String functionName) {
		try {
			String content = cscope.getFunction(this.name, this.path, functionName).trim();
			SyntaxHighlighter highlighter = new SyntaxHighlighter(content);
			highlighter.markupReferences(cscope.getReferences(name, path, functionName));

			return highlighter.formatToHtml();
		} catch (IllegalArgumentException e) {
			return new SpannableString("");
		}
	}
	
	public ArrayList<String> getFiles() {
		return new ArrayList<String>();
	}
	
	public ArrayList<String> getSymbols(String filename) {
		return cscope.getDeclarations(filename, name, path);
	}
	
	public void addFunctionView(String functionName) {
		if(codeMapView != null)
			codeMapView.createFunction(functionName);
	}
}
