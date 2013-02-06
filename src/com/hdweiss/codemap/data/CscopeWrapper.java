package com.hdweiss.codemap.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.hdweiss.codemap.util.Utils;

public class CscopeWrapper {
	private Context context;
	
	private Cscope cscope;
	private Project project;
	
	public CscopeWrapper(Cscope cscope, Project project, Context context) {
		this.cscope = cscope;
		this.context = context;
		this.project = project;
	}
	
	public String getFunction (CscopeEntry cscopeEntry) {
		String source = Utils.getFileFragment(cscopeEntry.file,
				cscopeEntry.lineNumber, cscopeEntry.getEndLine(this));
		int index = source.lastIndexOf("}");
		
		if(index != -1)
			return source.substring(0, index+1);
		else
			return source;
	}
	
	
	public CscopeEntry getFunctionEntry(String functionName, String fileName) {
		Log.d("Cscope", "-> getFunctionEntry()");
		
		final String options = "-k -L -1 '" + functionName + "'";
		String output;
		if (TextUtils.isEmpty(fileName))
			output = cscope.runCommand(project, options);
		else
			output = cscope.runCommand(project, options,
					project.getSourcePath(context) + File.separator + fileName);
		
		Log.d("Cscope", "getFunctionEntry(): cscope returned");
		String[] entries = output.trim().split("\n");
		
		if (TextUtils.isEmpty(fileName))
			return new CscopeEntry(entries[0]);
		
		String absoluteFilePath = new File(project.getSourcePath(context),
				fileName).getAbsolutePath();
		for (String entry: entries) {
			CscopeEntry cscopeEntry = new CscopeEntry(entry);
			
			if (cscopeEntry.file.equals(absoluteFilePath)) {
				Log.d("Cscope", "<- getFunctionEntry(): returning");
				return cscopeEntry;
			}
		}
		
		throw new IllegalArgumentException("Url " + fileName + ":" + functionName + " not found");
	}
	
	public int getFunctionEndLine(CscopeEntry cscopeEntry) {
		Log.d("Cscope", "-> getFunctionEndLine()");
		String options = "-k -L -1 '.*' ";
		String symbols = cscope.runCommand(project, options, cscopeEntry.file);
		Log.d("Cscope", "getFunctionEndLine(): cscope returned");
		
		
		CscopeEntry nextEntry = getNextEntry(symbols, cscopeEntry);
		while (nextEntry != null) {
			if (nextEntry.name.startsWith("#") == false)
				return nextEntry.lineNumber - 2;
			
			nextEntry = getNextEntry(symbols, nextEntry);
		}
		
		return Integer.MAX_VALUE;			
	}

	
	public ArrayList<CscopeEntry> getReferences(CscopeEntry cscopeEntry) {
		Log.d("Cscope", "-> getReferences()");
		
		String options = "-L -2 '" + cscopeEntry.actualName + "'";
		
		String symbols;
		if (TextUtils.isEmpty(cscopeEntry.file))
			symbols = cscope.runCommand(project, options);
		else
			symbols = cscope.runCommand(project, options, cscopeEntry.file);
		
		Log.d("Cscope", "getReferences(): cscope returned");
		ArrayList<CscopeEntry> references = parseReferences(symbols,
				cscopeEntry.lineNumber, cscopeEntry.getEndLine(this));
		return references;
	}
	
	
	public ArrayList<CscopeEntry> getFileReferences(String fileName) {
		Log.d("Cscope", "-> getFileReferences()");
		String options = "-L -2 '.*' ";
		String symbols = cscope.runCommand(project, options,
				project.getSourcePath(context) + File.separator + fileName);
		Log.d("Cscope", "getFileReferences(): cscope returned");
		ArrayList<CscopeEntry> references = parseReferences(symbols, 0, Integer.MAX_VALUE);
		return references;
	}
	
	private ArrayList<CscopeEntry> parseReferences(String symbols,
			int startLine, int endLine) {
		ArrayList<CscopeEntry> references = new ArrayList<CscopeEntry>();
		
		String[] entries = symbols.trim().split("\n");

		if(entries.length == 1 && entries[0].isEmpty())
			return references;
		
		for (int i = 0; i < entries.length; i++) {
			CscopeEntry entry = new CscopeEntry(entries[i]);
			entry.lineNumber -= startLine;
			references.add(entry);
		}
		
		return references;
	}

		
	public ArrayList<String> getDeclarations(String filename) {
		Log.d("Cscope", "-> getDeclarations()");
		ArrayList<String> result = new ArrayList<String>();
		
		String options = "-k -L -1 '.*' ";
		String symbols = cscope.runCommand(project, options,
				project.getSourcePath(context) + "/" + filename);
		Log.d("Cscope", "getDeclarations(): cscope returned");
				
		for(CscopeEntry entry: parseReferences(symbols, 0, 0))
			result.add(entry.name);
		
		return cleanSymbols(result);
	}
	
	private ArrayList<String> cleanSymbols(ArrayList<String> symbols) {
		ArrayList<String> result = new ArrayList<String>();
		for(String symbol: symbols) {
			if(symbol.startsWith("#")) {
				continue;
			}
			
			int startParenthesis = symbol.indexOf("(");
			
			if(startParenthesis == -1)
				continue;
			
			String substring = symbol.substring(0, startParenthesis).trim();
			int funcNameStart = substring.lastIndexOf(" ");
			
			if(funcNameStart == -1)
				funcNameStart = 0;
			
			String funcName = substring.substring(funcNameStart,
					substring.length()).trim();
			
			if (funcName.startsWith("*"))
				funcName = funcName.substring(1);
			
			result.add(funcName);
		}
		
		return result;
	}

	
	
	
	public ArrayList<CscopeEntry> getAllEntries(String functionName,
			String fileName) throws IllegalArgumentException {
		ArrayList<CscopeEntry> entries = new ArrayList<CscopeEntry>();
		
		final String options = "-k -L -1 '" + functionName + "'";
		String output;
		if (TextUtils.isEmpty(fileName))
			output = cscope.runCommand(project, options);
		else
			output = cscope.runCommand(project, options,
					project.getSourcePath(context) + File.separator + fileName);
				
		String[] entrySymbols = output.trim().split("\n");
		
		if (entrySymbols.length == 1 && TextUtils.isEmpty(entrySymbols[0])) {
			throw new IllegalArgumentException("Couldn't find entry for "
					+ fileName + ":" + functionName);
		}
		
		for(int i = 0; i < entrySymbols.length; i++) {
			try {
				CscopeEntry entry = new CscopeEntry(entrySymbols[i]);
				entries.add(entry);
				
			} catch (IllegalArgumentException e) {
				if (TextUtils.isEmpty(entrySymbols[i]) == false)
					Log.e("CodeMap", "Couldn't parse entry " + entrySymbols[i]);
			}
		}
		
		return entries;
	}
	
	private CscopeEntry getNextEntry(String symbols, CscopeEntry entry) {
		String[] entries = symbols.trim().split("\n");
		
		for(int i = 0; i < entries.length; i++) {
			CscopeEntry cscopeEntry = new CscopeEntry(entries[i]);
			if(cscopeEntry.lineNumber == entry.lineNumber) {
				if(entries.length > i+1)
					return new CscopeEntry(entries[i+1]);
			}
		}
		
		return null;
	}
	
	public String getFile (String fileName) throws FileNotFoundException {
		FileInputStream stream = new FileInputStream(
				project.getSourcePath(context) + File.separator + fileName);
		String content = Utils.inputStreamToString(stream);
		return content;
	}
	
	
	
	public HashMap<String,ArrayList<CscopeEntry>> getAllDeclarations() {
		HashMap<String, ArrayList<CscopeEntry>> result = new HashMap<String, ArrayList<CscopeEntry>>();
		
		String options = "-d -k -L -1 '.*' ";
		String symbols = cscope.runCommand(project, options);
				
		for(CscopeEntry entry: parseReferences(symbols, 0, 0)) {
			ArrayList<CscopeEntry> list = result.get(entry.file);
			
			if(list == null) {
				list = new ArrayList<CscopeEntry>();
				result.put(entry.file, list);
			}
			
			list.add(entry);
		}
		
		return result;
	}
}
