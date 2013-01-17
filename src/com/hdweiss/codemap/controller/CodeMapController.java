package com.hdweiss.codemap.controller;

import java.io.IOException;

import android.content.Context;
import android.text.SpannableString;
import android.util.Log;

import com.hdweiss.codemap.data.CodeMapState;
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
		Log.d("Controller", "addFunctionView: " + url);
		if(codeMapView != null) {
			CodeMapPoint position = new CodeMapCursorPoint(100, 100).getCodeMapPoint(codeMapView);
			
			CodeMapFunction functionView = instantiateFunctionFragment(url, position);
			codeMapView.addMapItem(functionView);
		}
	}
	
	private CodeMapFunction instantiateFunctionFragment(String url, CodeMapPoint position) {
		final SpannableString content = getFunctionSource(url);
		
		CodeMapFunction functionView = new CodeMapFunction(codeMapView.getContext(),
				position, url, content);
		return functionView;
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
