package com.hdweiss.codemap.controller;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;

import com.hdweiss.codemap.data.CodeMapState;
import com.hdweiss.codemap.data.CscopeEntry;
import com.hdweiss.codemap.util.CodeMapCursorPoint;
import com.hdweiss.codemap.util.CodeMapPoint;
import com.hdweiss.codemap.view.codemap.CodeMapView;
import com.hdweiss.codemap.view.fragments.CodeMapAnnotation;
import com.hdweiss.codemap.view.fragments.CodeMapFunction;
import com.hdweiss.codemap.view.fragments.CodeMapItem;
import com.hdweiss.codemap.view.fragments.CodeMapLink;

public class CodeMapController extends ProjectController {

	private CodeMapView codeMapView;

	public CodeMapController(String projectName, Context context) {
		super(projectName, context);
	}
	
	public void setView(CodeMapView codeMapView) {
		this.codeMapView = codeMapView;
		this.codeMapView.setController(this);
		loadCodeMapState();
	}
	 
	
    public void loadCodeMapState() {
    	try {
			CodeMapState state = CodeMapState.readState(project.getName(), context);
			setState(state);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
    @SuppressWarnings("unchecked")
	private void setState(CodeMapState state) {
		if(state == null)
			return;
		
		CodeMapStateLoader loadState = new CodeMapStateLoader(state, codeMapView, this);
		loadState.execute(state.items);
		
		codeMapView.setScrollX(state.scrollX);
		codeMapView.setScrollY(state.scrollY);
		codeMapView.setScaleFactor(state.zoom, new CodeMapPoint());
	}
    
    
    public void saveCodeMapState() {
    	try {
			codeMapView.getState().writeState(context);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    
	public void addAnnotationView(String content) {
		if(codeMapView != null) {
			CodeMapPoint position = new CodeMapCursorPoint(100, 100).getCodeMapPoint(codeMapView);
			
			CodeMapAnnotation annotationView = new CodeMapAnnotation(codeMapView.getContext(),
					position, content);
			codeMapView.addMapItem(annotationView);
		}
	}
    
	public void addFileView(String fileName) {
		if(codeMapView != null) {
			CodeMapPoint position = new CodeMapCursorPoint(100, 100).getCodeMapPoint(codeMapView);
			final SpannableString content = getFileSource(fileName);
			
			CodeMapFunction functionView = new CodeMapFunction(codeMapView.getContext(),
					position, fileName, content);
			codeMapView.addMapItem(functionView);
		}
	}
	
    
	public void addFunctionView(String url) {
		if(codeMapView != null) {
			CodeMapPoint position = new CodeMapCursorPoint(100, 100).getCodeMapPoint(codeMapView);
			instantiateFunctionFragment(url, position);
		}
	}
	
	private CodeMapFunction instantiateFunctionFragment(String url, CodeMapPoint position) {
		ArrayList<CscopeEntry> entries;
		try {
			entries = getUrlEntries(url);
		} catch (IllegalArgumentException e) {
			Log.e("CodeMap",
					"Error creating function fragment: "
							+ e.getLocalizedMessage());
			throw(e);
		}

		CodeMapFunction functionView = new CodeMapFunction(codeMapView.getContext(),
				position, url, new SpannableString(""));
		codeMapView.addMapItem(functionView);
				
		if (entries.size() > 1)
			popup(entries, functionView);
		else {
			final SpannableString content = getFunctionSource(entries.get(0));
			functionView.init(url, content);
		}
		
		return functionView;
	}
	
	private void popup(final ArrayList<CscopeEntry> entries, final CodeMapFunction functionView) {
		PopupMenu popupMenu = new PopupMenu(context, functionView);
				
		for (int i = 0; i < entries.size(); i++) {
			CscopeEntry entry = entries.get(i);
			String url = entry.getUrl();
			if (TextUtils.isEmpty(url) == false) {
				url = url.substring(project.getSourcePath(context).length());
				popupMenu.getMenu().add(Menu.NONE, i, Menu.NONE, url);
			}
		}

		popupMenu
				.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						CscopeEntry entry = entries.get(item.getItemId());
						final SpannableString content = getFunctionSource(entry);
						functionView.init(entry.actualName, content);
						return true;
					}

				});

		popupMenu.show();
	}


	public CodeMapItem addChildFragmentFromUrl(String functionName, CodeMapItem parent, float yOffset) {
		float offset = yOffset + parent.getContentViewYOffset();

		CodeMapPoint position = new CodeMapPoint();
		position.x = parent.getX() + parent.getWidth() + 30;
		position.y = parent.getY() + offset;
		
		CodeMapFunction item = instantiateFunctionFragment(functionName, position);
		codeMapView.addMapItem(item);
		codeMapView.addMapLink(new CodeMapLink(parent, item, offset));
		
		return item;
	}
}
