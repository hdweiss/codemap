package com.hdweiss.codemap.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.hdweiss.codemap.util.SyntaxHighlighter;

public class ProjectController {
	
	protected Context context;
	
	public Project project;
	private Cscope cscope;
	private CscopeWrapper cscopeWrapper;

	
	public ProjectController(String name, Context context) {
		this.context = context;
		loadProject(name);
		this.cscope = new Cscope(context);
		this.cscopeWrapper = new CscopeWrapper(cscope, project, context);
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
			} catch (IllegalStateException e) {
				Toast.makeText(context, "Error: " + e.getLocalizedMessage(),
						Toast.LENGTH_SHORT).show();
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
			ArrayList<String> declarations = cscopeWrapper.getDeclarations(filename);
			project.symbols.put(filename, declarations);
			return declarations;
		}
	}
	
	public static String getFileFromUrl(String url) {
		int colonIndex = url.indexOf(":");
		
		if (colonIndex < 0)
			return "";
		else
			return url.substring(0, colonIndex);
	}
	
	public static String getFunctionFromUrl(String url) {
		int colonIndex = url.indexOf(":");
		
		if (colonIndex < 0)
			return url;
		
		return url.substring(colonIndex + 1);
	}
	
	
	
	public ArrayList<CscopeEntry> getUrlEntries(String url)
			throws IllegalArgumentException {
		final String fileName = getFileFromUrl(url);
		final String functionName = getFunctionFromUrl(url);

		ArrayList<CscopeEntry> allEntries = cscopeWrapper.getAllEntries(
				functionName, fileName);
		
		return allEntries;
	}
	
	
	/**
	 * Call in case of full url.
	 */
	// TODO Refactor, only createCodeMapItem() calls this
	public SpannableString getFunctionSource(String url) {
		final String fileName = getFileFromUrl(url);
		final String functionName = getFunctionFromUrl(url);

		try {
			ArrayList<CscopeEntry> allEntries = cscopeWrapper.getAllEntries(
					functionName, fileName);
			return getFunctionSource(allEntries.get(0));
		} catch (IllegalArgumentException e) {
			Log.e("CodeMap", e.getLocalizedMessage());
			return new SpannableString("");
		}
	}
	
	public SpannableString getFunctionSource(CscopeEntry entry) {
		String content = cscopeWrapper.getFunction(entry).trim();
		SyntaxHighlighter highlighter = new SyntaxHighlighter(content);
		highlighter.markupReferences(cscopeWrapper.getReferences(entry));

		return highlighter.formatToHtml();
	}
	
	
	public SpannableString getFileSource(String fileName) {
		try {
			String content = cscopeWrapper.getFile(fileName).trim();
			SyntaxHighlighter highlighter = new SyntaxHighlighter(content);
			highlighter.markupReferences(cscopeWrapper.getFileReferences( fileName));

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
