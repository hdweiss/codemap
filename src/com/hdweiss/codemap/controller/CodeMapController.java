package com.hdweiss.codemap.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.SpannableString;

import com.hdweiss.codemap.data.CodeMapState;
import com.hdweiss.codemap.data.SerializableItem;
import com.hdweiss.codemap.data.SerializableLink;
import com.hdweiss.codemap.util.CodeMapCursorPoint;
import com.hdweiss.codemap.util.CodeMapPoint;
import com.hdweiss.codemap.view.codemap.CodeMapView;
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
		
		LoadState loadState = new LoadState(state);
		loadState.execute(state.items);
		
		codeMapView.setScrollX(state.scrollX);
		codeMapView.setScrollY(state.scrollY);
		//codeMapView.setScaleFactor(state.zoom, new CodeMapPoint());
	}
    
    
    public void saveCodeMapState() {
    	try {
			codeMapView.getState().writeState(context);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    
	public void addFunctionView(String functionName) {
		if(codeMapView != null) {
			CodeMapPoint position = new CodeMapCursorPoint(100, 100).getCodeMapPoint(codeMapView);
			
			CodeMapFunction functionView = instantiateFunctionFragment(functionName, position);
			codeMapView.addMapItem(functionView);
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

	

	public CodeMapItem openChildFragmentFromUrl(String url, CodeMapItem parent, float yOffset) {
		float offset = yOffset + parent.getContentViewYOffset();

		CodeMapPoint position = new CodeMapPoint();
		position.x = parent.getX() + parent.getWidth() + 30;
		position.y = parent.getY() + offset;
		
		CodeMapFunction item = instantiateFunctionFragment(url, position);
		codeMapView.addMapItem(item);
		codeMapView.addMapLink(new CodeMapLink(parent, item, offset));
		
		return item;
	}
	
	
	
	public CodeMapFunction instantiateFunctionFragment(String functionName, CodeMapPoint position) {
		final SpannableString content = getFunctionSource(functionName);
		
		CodeMapFunction functionView = new CodeMapFunction(codeMapView.getContext(),
				position, functionName, content);
		return functionView;
	}
	
	private class LoadState extends AsyncTask<ArrayList<SerializableItem>, CodeMapItem, Long> {
		private ProgressDialog dialog;
		private CodeMapState state;
		
		private HashMap<UUID, CodeMapItem> codeMapItems = new HashMap<UUID, CodeMapItem>();
		
		public LoadState(CodeMapState state) {
			this.state = state;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			this.dialog = ProgressDialog.show(codeMapView.getContext(), "Loading",
					"Loading state...");
		}

		protected Long doInBackground(ArrayList<SerializableItem>... objects) {
			ArrayList<SerializableItem> items = objects[0];
			
			for (int i = 0; i < items.size(); i++) {
				CodeMapFunction fragment = loadObjectState(items.get(i));
				this.publishProgress(fragment);
				codeMapItems.put(fragment.id, fragment);
			}

			return (long) 0;
		}

		@Override
		protected void onProgressUpdate(CodeMapItem... progress) {
			super.onProgressUpdate(progress);
			for(int i = 0; i < progress.length; i++)
				codeMapView.addMapItem(progress[i]);
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			loadLinksState(state);
			dialog.dismiss();
		}
		

		private CodeMapFunction loadObjectState(SerializableItem item) {
			CodeMapFunction functionFragment = instantiateFunctionFragment(
					item.name, item.point);
			functionFragment.id = item.id;
			return functionFragment;
		}
		
		private void loadLinksState(CodeMapState state) {
			for (SerializableLink link : state.links) {
				CodeMapItem parent = codeMapItems.get(link.parent);
				CodeMapItem child = codeMapItems.get(link.child);

				codeMapView.addMapLink(new CodeMapLink(parent, child,
						link.offset));
			}
		}
	}
}
