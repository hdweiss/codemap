package com.hdweiss.codemap;

import java.io.FileNotFoundException;

import android.content.Context;

public class Project {
	
	private CtagsReader ctagsReader;
	
	public Project(String name, String path, Context context) {
		Ctags ctags = new Ctags(context);
		ctags.generateTagsFile(name, "", false);
		try {
			this.ctagsReader = new CtagsReader(ctags.getTagsFile(name));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public String getSymbolSource(String symbolName) {
		TagsEntry tagEntry = ctagsReader.getTagEntry(symbolName);
		
		return "";
	}
}
