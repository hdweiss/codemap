package com.hdweiss.codemap.view.workspace;

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

import com.hdweiss.codemap.data.CscopeEntry;
import com.hdweiss.codemap.data.ProjectController;
import com.hdweiss.codemap.util.CodeMapCursorPoint;
import com.hdweiss.codemap.util.CodeMapPoint;
import com.hdweiss.codemap.view.workspace.FindDeclarationTask.FindDeclarationCallback;
import com.hdweiss.codemap.view.workspace.fragments.CodeMapAnnotation;
import com.hdweiss.codemap.view.workspace.fragments.CodeMapFunction;
import com.hdweiss.codemap.view.workspace.fragments.CodeMapItem;
import com.hdweiss.codemap.view.workspace.fragments.CodeMapLink;
import com.hdweiss.codemap.view.workspace.outline.OutlineItem;

public class WorkspaceController extends ProjectController {

	private String workspaceName;
	private WorkspaceView codeMapView;

	public WorkspaceController(String projectName, String workspaceName, Context context) {
		super(projectName, context);
		this.workspaceName = workspaceName;
	}
	
	public void setView(WorkspaceView codeMapView) {
		this.codeMapView = codeMapView;
		this.codeMapView.setController(this);
		loadCodeMapState();
	}
	
	public String getWorkspaceName() {
		return this.workspaceName;
	}
	 
	
    public void loadCodeMapState() {
    	try {
			WorkspaceState state = WorkspaceState.readState(project, workspaceName, context);
			setState(state);
		} catch (IOException e) {
			e.printStackTrace();
			addAnnotationView("Welcome to CodeMap!\nYou can start browsing your project by using the explorer on the right.");
		}
    }
	
    @SuppressWarnings("unchecked")
	private void setState(WorkspaceState state) {
		if(state == null)
			return;
		
		WorkspaceStateLoader loadState = new WorkspaceStateLoader(state, codeMapView, this);
		loadState.execute(state.items);
		
		codeMapView.setScrollX(state.scrollX);
		codeMapView.setScrollY(state.scrollY);
		codeMapView.setScaleFactor(state.zoom, new CodeMapPoint());
	}
    
    
    public void saveCodeMapState() {
    	try {
			codeMapView.getState().writeState(project, context);
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
		Intent intent = new Intent(WorkspaceFragment.INTENT_REFRESH);
		context.sendBroadcast(intent);
	}
	
	
	private static final int YScrollOffset = 200;
	private static final int XScrollOffset = 200;
	public void symbolClicked(String url, OutlineItem item) {
		ArrayList<CodeMapItem> declarations = codeMapView.getDeclarations(url);

		if (declarations.size() > 0) {
			int index = 0;
			if (item != null) {
				index = item.declarationCycle % declarations.size();
				item.declarationCycle++;
			}
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
		position.x = parent.getX() + parent.getWidth() + 15;
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
