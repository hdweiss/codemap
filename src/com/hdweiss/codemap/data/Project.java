package com.hdweiss.codemap.data;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

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

	private String[] files;
	private HashMap<String, ArrayList<String>> symbols = new HashMap<String, ArrayList<String>>();
	
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
	
	public SpannableString getFileSource(String fileName) {
		try {
			String content = cscope.getFile(this, fileName).trim();
			SyntaxHighlighter highlighter = new SyntaxHighlighter(content);
			highlighter.markupReferences(cscope.getFileReferences(this, fileName));
			Log.d("CodeMap", "Content : \n" + highlighter.formatToHtml());

			return highlighter.formatToHtml();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return new SpannableString("");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new SpannableString("");
		}
	}
	
	public String[] getFiles() {
		if(files != null)
			return files;
		
		try {
			this.files = cscope.getFiles(this);
			return files;
		} catch (FileNotFoundException e) {
			Log.e("CodeMap", e.getStackTrace().toString());
			return new String[0];
		}
	}
	
	public ArrayList<String> getSymbols(String filename) {
		ArrayList<String> symbolList = symbols.get(filename);
		if(symbolList != null)
			return symbolList;
		else {
			ArrayList<String> declarations = cscope.getDeclarations(filename, this);
			symbols.put(filename, declarations);
			return declarations;
		}
	}
	
	public void addFunctionView(String functionName) {
		if(codeMapView != null)
			codeMapView.createFunctionFragment(functionName);
	}
	
	public void addFileView(String fileName) {
		if(codeMapView != null)
			codeMapView.createFileFragment(fileName);
	}
}
