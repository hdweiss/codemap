package com.hdweiss.codemap.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.hdweiss.codemap.data.Cscope;
import com.hdweiss.codemap.data.JGitWrapper;
import com.hdweiss.codemap.data.Project;
import com.hdweiss.codemap.util.SyntaxHighlighter;

public class ProjectController {
	
	protected Context context;
	
	public Project project;
	private Cscope cscope;
	
	public ProjectController(String name, Context context) {
		this.context = context;
		this.cscope = new Cscope(context);
		loadProject(name);
	}

	
	public void loadProject(String name) {
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
	
	public String[] getProjectFiles() {
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
	
	public void updateProject() {
		if(project.isUrlValid()) {
			try {
				JGitWrapper jgit = new JGitWrapper(project, context);
				jgit.update(this);
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
		else {
			buildIndex();
			Toast.makeText(context, "Updated " + project.getName(),
					Toast.LENGTH_SHORT).show();
		}
	}
	
	public void buildIndex() {
		cscope.generateNamefile(project);
		cscope.generateReffile(project);
	}

	public ArrayList<String> getDeclarations(String filename) {
		ArrayList<String> symbolList = project.symbols.get(filename);
		if(symbolList != null)
			return symbolList;
		else {
			ArrayList<String> declarations = cscope.getDeclarations(filename, project);
			project.symbols.put(filename, declarations);
			return declarations;
		}
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
}
