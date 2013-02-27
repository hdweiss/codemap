package com.hdweiss.codemap.controller;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.hdweiss.codemap.controller.FindDeclarationTask.FindDeclarationCallback;
import com.hdweiss.codemap.data.CodeMapState;
import com.hdweiss.codemap.data.CscopeEntry;
import com.hdweiss.codemap.util.CodeMapCursorPoint;
import com.hdweiss.codemap.util.CodeMapPoint;
import com.hdweiss.codemap.view.browser.BrowserItem;
import com.hdweiss.codemap.view.codemap.CodeMapFragment;
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
	
	public int getOpenDeclarations(String url) {
		int numberOfDeclarations = codeMapView.getDeclarations(url).size();
		return numberOfDeclarations;
	}
	
	
	public void updateCodeBrowser() {
		Intent intent = new Intent(CodeMapFragment.INTENT_REFRESH);
		context.sendBroadcast(intent);
	}
	
	
	private static final int YScrollOffset = 200;
	private static final int XScrollOffset = 200;
	public void symbolClicked(String url, BrowserItem item) {
		ArrayList<CodeMapItem> declarations = codeMapView.getDeclarations(url);

		if (declarations.size() > 0) {
			int index = item.declarationCycle % declarations.size();
			item.declarationCycle++;
			CodeMapItem codeMapItem = declarations.get(index);
			float x = codeMapItem.getX() - XScrollOffset;
			float y = codeMapItem.getY() - YScrollOffset;
			codeMapView.setScroll(x, y);
		} else
			addFunctionView(url);
	}

	
	private CodeMapFunction instantiateFunctionFragment(String url,
			CodeMapPoint position) throws IllegalArgumentException {
		CodeMapFunction functionView = new CodeMapFunction(codeMapView.getContext(),
				position, url, new SpannableString(""));
		codeMapView.addMapItem(functionView);
		
		new FindDeclarationTask(url, new SearchCallback(functionView), this,
				codeMapView.getContext()).execute();
		
		return functionView;
	}
	
	private class SearchCallback implements FindDeclarationCallback {
		private CodeMapFunction functionView;

		public SearchCallback(CodeMapFunction functionView) {
			this.functionView = functionView;
		}
		
		public void onSuccess(ArrayList<CscopeEntry> entries) {
			populateFragment(entries, functionView);
		}

		public void onFailure() {
			functionView.remove();
			Toast.makeText(context, "Error finding entries",
					Toast.LENGTH_SHORT).show();
		}
	}


	public void addChildFragmentFromUrl(String functionName, CodeMapItem parent, float yOffset) {
		float offset = yOffset + parent.getContentViewYOffset();

		CodeMapPoint position = new CodeMapPoint();
		position.x = parent.getX() + parent.getWidth() + 30;
		position.y = parent.getY() + offset;
		
		CodeMapFunction item = instantiateFunctionFragment(functionName,
				position);
		codeMapView.addMapItem(item);
		codeMapView.addMapLink(new CodeMapLink(parent, item, offset));
	}
	
	
	private void populateFragment(ArrayList<CscopeEntry> entries,
			CodeMapFunction functionView) {
		if (entries.size() > 1)
			showDeclarationPopup(entries, functionView);
		else if (entries.size() == 1) {
			CscopeEntry entry = entries.get(0);
			final SpannableString content = getFunctionSource(entry);
			functionView
					.init(entry.getActualUrl(project.getSourcePath(context)),
							content);
		}
	}

	private void showDeclarationPopup(final ArrayList<CscopeEntry> entries,
			final CodeMapFunction functionView) {
		PopupMenu popupMenu = new PopupMenu(context, functionView);

		for (int i = 0; i < entries.size(); i++) {
			CscopeEntry entry = entries.get(i);
			String url = entry.getUrl(project.getSourcePath(context));
			if (TextUtils.isEmpty(url) == false) {
				popupMenu.getMenu().add(Menu.NONE, i, Menu.NONE, url);
			}
		}

		popupMenu
				.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						CscopeEntry entry = entries.get(item.getItemId());
						final SpannableString content = getFunctionSource(entry);
						String url = entry.getActualUrl(project
								.getSourcePath(context));
						functionView.init(url, content);
						return true;
					}

				});

		popupMenu.show();
	}
}
