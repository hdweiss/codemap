package com.hdweiss.codemap.view.browser;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;

import com.hdweiss.codemap.controller.ProjectController;

public class BrowserItem {
	public enum TYPE {DIRECTORY, FILE, SYMBOL};
	
	public String name = "";
	public int level = 0;
	public TYPE type = TYPE.FILE;
	
	public int declarationCycle = 0;
	
	public BrowserItem(String name, int level, TYPE type) {
		this.name = name;
		this.level = level;
		this.type = type;
	}
	
	public ArrayList<BrowserItem> getChildren(ProjectController controller, Context context) {
		ArrayList<BrowserItem> items = new ArrayList<BrowserItem>();

		if (this.type == TYPE.SYMBOL)
			return items;
		
		File file = new File(controller.project.getSourcePath(context)
				+ File.separator + this.name);
		
		if(file.isDirectory()) {
			for (String filename: file.list())
				items.add(new BrowserItem(this.name + File.separator + filename, this.level + 1, TYPE.FILE));
		} else {
			ArrayList<String> declarations = controller.getDeclarations(this.name);
			
			for(String declaration: declarations)
				items.add(new BrowserItem(declaration, this.level + 1, TYPE.SYMBOL));
		}
		
		return items;
	}
}
