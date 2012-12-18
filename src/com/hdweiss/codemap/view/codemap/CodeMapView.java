package com.hdweiss.codemap.view.codemap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.Scroller;

import com.hdweiss.codemap.data.CodeMapState;
import com.hdweiss.codemap.data.ProjectController;
import com.hdweiss.codemap.data.SerializableItem;
import com.hdweiss.codemap.data.SerializableLink;
import com.hdweiss.codemap.util.CodeMapCursorPoint;
import com.hdweiss.codemap.util.CodeMapPoint;
import com.hdweiss.codemap.util.ZoomableAbsoluteLayout;
import com.hdweiss.codemap.view.codemap.CodeMapListeners.CodeMapGestureListener;
import com.hdweiss.codemap.view.codemap.CodeMapListeners.CodeMapScaleListener;
import com.hdweiss.codemap.view.fragments.CodeMapFunction;
import com.hdweiss.codemap.view.fragments.CodeMapItem;
import com.hdweiss.codemap.view.fragments.CodeMapLink;

public class CodeMapView extends ZoomableAbsoluteLayout {

	private GestureDetector gestureDetector;
	private ScaleGestureDetector scaleDetector;
	private Scroller scroller;
	
	private HashMap<UUID, CodeMapItem> items2 = new HashMap<UUID, CodeMapItem>();
	private ArrayList<CodeMapItem> items = new ArrayList<CodeMapItem>();
	private ArrayList<CodeMapLink> links = new ArrayList<CodeMapLink>();
	private ProjectController controller;

	public CodeMapView(Context context, AttributeSet attrs) {
		super(context, attrs);	
		
        this.scroller = new Scroller(getContext());
		this.gestureDetector = new GestureDetector(getContext(), new CodeMapGestureListener(this, scroller));
		this.scaleDetector = new ScaleGestureDetector(getContext(), new CodeMapScaleListener(this));
		
		setWillNotDraw(false);
		setFocusable(false);
	}
	
	@SuppressWarnings("unchecked")
	public void setState(CodeMapState state) {
		if(state == null)
			return;
		
		new LoadState(state).execute(state.items);
		setScrollX(state.scrollX);
		setScrollY(state.scrollY);
		//setZoom(state.zoom, new CodeMapPoint());
	}

	private CodeMapFunction loadObjectState(SerializableItem item) {
		CodeMapFunction functionFragment = instantiateFunctionFragment(item.name, new CodeMapPoint(
				item.point));
		functionFragment.id = item.id;
		return functionFragment;
	}
	
	private void loadLinksState(CodeMapState state) {
		for(SerializableLink link: state.links) {
			CodeMapItem parent = items2.get(link.parent);
			CodeMapItem child = items2.get(link.child);
			
			addMapLink(new CodeMapLink(parent, child, link.offset));
		}
	}

	
	public CodeMapState getState() {
		CodeMapState state = new CodeMapState(controller.project.getName());
		
		for(CodeMapItem item: items)
			state.items.add(new SerializableItem(item));
		
		for(CodeMapLink link: links) {
			state.links.add(new SerializableLink(link));
		}
		
		//state.zoom = zoom;
		state.scrollX = getScrollX();
		state.scrollY = getScrollY();
		return state;
	}
	
	public void setController(ProjectController controller) {
		this.controller = controller;
	}
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		
		scaleDetector.onTouchEvent(event);
		gestureDetector.onTouchEvent(event);
		
		updateScroll();
		return true;
	}
	
	private void updateScroll() {
	    if(scroller.computeScrollOffset()) {
			float dx = (scroller.getStartX() - scroller.getFinalX());
			float dy = (scroller.getStartY() - scroller.getFinalY());
			scrollBy(-(int)dx, -(int)dy);	    
		}
	}

	
	public CodeMapFunction instantiateFunctionFragment(String functionName, CodeMapPoint position) {
		final SpannableString content = controller.getFunctionSource(functionName);
		
		CodeMapFunction functionView = new CodeMapFunction(getContext(),
				position, functionName, content, this);
		return functionView;
	}
	
	
	
	public void addMapLink(CodeMapLink link) {
		if(link.parent != null && link.child != null) {
			links.add(link);
			refresh();
		}
	}
	
	
	public void addMapItem(CodeMapItem item) {
		addView(item);
		items.add(item);
		items2.put(item.id, item);
		item.setCodeMapView(this);
	}
	
	public void moveFragment(CodeMapItem item) {
		//CollisionManager.moveMapItemToEmptyPosition(item, this.items);
		CollisionManager.moveFragmentsToAllowItem(item, this.items);		
	}
	
	public CodeMapItem getMapFragmentAtPoint(CodeMapCursorPoint cursorPoint) {
		CodeMapPoint point = cursorPoint.getCodeMapPoint(this);
		for (CodeMapItem view : items) {
			if (view.contains(point))
				return view;
		}
		return null;
	}

	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		for(CodeMapLink link: links)
			link.doDraw(canvas);
	}

	public void refresh() {
		invalidate();
	}
	
	public void remove(CodeMapItem item) {
		removeView(item);
		items.remove(item);
		item.setCodeMapView(null);
		
		Iterator<CodeMapLink> linksIt = links.iterator();
		while(linksIt.hasNext()) {
			CodeMapLink link = linksIt.next();
			if(link.hasItem(item))
				linksIt.remove();
		}
	}
	
	public void clear() {
		removeAllViews();
		items.clear();
		links.clear();
	}
	
	
	private class LoadState extends AsyncTask<ArrayList<SerializableItem>, CodeMapItem, Long> {
		private ProgressDialog dialog;
		private CodeMapState state;
		
		public LoadState(CodeMapState state) {
			this.state = state;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			this.dialog = ProgressDialog.show(getContext(), "Loading",
					"Loading state...");
		}

		protected Long doInBackground(ArrayList<SerializableItem>... objects) {
			ArrayList<SerializableItem> items = objects[0];
			
			for (int i = 0; i < items.size(); i++) {
				CodeMapFunction fragment = loadObjectState(items.get(i));
				this.publishProgress(fragment);
			}

			return (long) 0;
		}

		protected void onProgressUpdate(CodeMapItem... progress) {
			for(int i = 0; i < progress.length; i++)
				addMapItem(progress[i]);
		}

		protected void onPostExecute(Long result) {
			loadLinksState(state);
			dialog.dismiss();
		}
	}


	public ProjectController getController() {
		return this.controller;
	}
}
