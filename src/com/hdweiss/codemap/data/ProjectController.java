package com.hdweiss.codemap.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;

import com.hdweiss.codemap.util.SyntaxHighlighter;
import com.hdweiss.codemap.util.Utils;
import com.hdweiss.codemap.view.codemap.CodeMapView;

public class ProjectController {
	
	private Context context;
	
	private Project project;
	private CodeMapView codeMapView;	
	private Cscope cscope;
	
	public ProjectController(String name, Context context) {
		this.context = context;
		this.cscope = new Cscope(context);
		loadProject(name);
	}
	
	private void loadProject(String name) {
		if(TextUtils.isEmpty(name))
			throw new IllegalArgumentException("Invalid project name");
		
		try {
			this.project = Project.readProject(name, context);
		} catch (IOException e) {
			Log.e("CodeMap", e.getLocalizedMessage());
		}
		
		if(this.project == null)
			this.project = new Project(name);
	}
	
	public void buildIndex() {
		cscope.generateNamefile(project);
		cscope.generateReffile(project);
	}
	
	public void updateProject() {
		if(TextUtils.isEmpty(project.getUrl())) {
			Log.e("CodeMap", "Invalid url for project " + project.getUrl());
			return;
		}
			
		try {
			JGitWrapper jgit = new JGitWrapper(project, context);
			jgit.update(this);
		} catch (IOException e) {
			e.printStackTrace();
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
	
	public SpannableString getFunctionSource(String functionName) {
		try {
			String content = cscope.getFunction(project, functionName).trim();
			SyntaxHighlighter highlighter = new SyntaxHighlighter(content);
			highlighter.markupReferences(cscope.getReferences(project, functionName));

			return highlighter.formatToHtml();
		} catch (IllegalArgumentException e) {
			return new SpannableString("");
		}
	}
	
	public SpannableString getFileSource(String fileName) {
		try {
			String content = cscope.getFile(project, fileName).trim();
			SyntaxHighlighter highlighter = new SyntaxHighlighter(content);
			highlighter.markupReferences(cscope.getFileReferences(project, fileName));
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
	
	
	public void setView(CodeMapView codeMapView) {
		this.codeMapView = codeMapView;
	}
	
	
	public String[] getFiles() {
		if(project.files != null)
			return project.files;
		
		try {
			this.project.files = cscope.getFiles(project);
			return project.files;
		} catch (FileNotFoundException e) {
			Log.e("CodeMap", e.getStackTrace().toString());
			return new String[0];
		}
	}
	
	public ArrayList<String> getSymbols(String filename) {
		ArrayList<String> symbolList = project.symbols.get(filename);
		if(symbolList != null)
			return symbolList;
		else {
			ArrayList<String> declarations = cscope.getDeclarations(filename, project);
			project.symbols.put(filename, declarations);
			return declarations;
		}
	}

	
	public static void deleteProject(String projectName, Context context) {
		Cscope cscope = new Cscope(context);
		cscope.deleteNamefile(projectName);
		cscope.deleteReffile(projectName);
		
		new File(Project.getConfigFilePath(projectName, context)).delete();
		
		File sourceDir = new File(Project.getSourcePath(projectName, context));
		Utils.deleteRecursive(sourceDir);
	}
	
	public static ArrayList<String> getProjectsList(Context context) {
		ArrayList<String> result = new ArrayList<String>();
		String[] fileList = context.fileList();
		
		for(String filename: fileList) {
			if(filename.endsWith(".project")) {
				String projectName = filename.substring(0, filename.length()
						- ".project".length());
				result.add(projectName);
			}
		}

		return result;
	}
	
	public void saveState() {
		if(this.codeMapView == null)
			return;
		
	}
}
