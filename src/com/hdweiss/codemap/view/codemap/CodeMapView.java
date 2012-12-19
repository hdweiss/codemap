package com.hdweiss.codemap.view.codemap;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.Scroller;

import com.hdweiss.codemap.controller.CodeMapController;
import com.hdweiss.codemap.controller.CollisionManager;
import com.hdweiss.codemap.data.CodeMapState;
import com.hdweiss.codemap.data.SerializableItem;
import com.hdweiss.codemap.data.SerializableLink;
import com.hdweiss.codemap.util.CodeMapCursorPoint;
import com.hdweiss.codemap.util.CodeMapPoint;
import com.hdweiss.codemap.util.ZoomableAbsoluteLayout;
import com.hdweiss.codemap.view.codemap.CodeMapListeners.CodeMapGestureListener;
import com.hdweiss.codemap.view.codemap.CodeMapListeners.CodeMapScaleListener;
import com.hdweiss.codemap.view.fragments.CodeMapItem;
import com.hdweiss.codemap.view.fragments.CodeMapLink;

public class CodeMapView extends ZoomableAbsoluteLayout {

	private GestureDetector gestureDetector;
	private ScaleGestureDetector scaleDetector;
	private Scroller scroller;
	
	private ArrayList<CodeMapItem> items = new ArrayList<CodeMapItem>();
	private ArrayList<CodeMapLink> links = new ArrayList<CodeMapLink>();
	private CodeMapController controller;

	public CodeMapView(Context context, AttributeSet attrs) {
		super(context, attrs);	
		
        this.scroller = new Scroller(getContext());
		this.gestureDetector = new GestureDetector(getContext(), new CodeMapGestureListener(this, scroller));
		this.scaleDetector = new ScaleGestureDetector(getContext(), new CodeMapScaleListener(this));
		
		setWillNotDraw(false);
		setFocusable(false);
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

	
	public void addMapItem(CodeMapItem item) {
		addView(item);
		items.add(item);
		item.setCodeMapView(this);
	}

	public void addMapLink(CodeMapLink link) {
		if(link.parent != null && link.child != null) {
			links.add(link);
			refresh();
		}
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
	
	
	public void moveFragment(CodeMapItem item) {
		CollisionManager.pushItems(item, this.items);		
	}
	
	public CodeMapItem getMapFragmentAtPoint(CodeMapCursorPoint cursorPoint) {
		CodeMapPoint point = cursorPoint.getCodeMapPoint(this);
		for (CodeMapItem view : items) {
			if (view.contains(point))
				return view;
		}
		return null;
	}


	public void setController(CodeMapController controller) {
		this.controller = controller;
	}
	
	public CodeMapController getController() {
		return this.controller;
	}
	
	public CodeMapState getState() {
		CodeMapState state = new CodeMapState(controller.project.getName());
		
		for(CodeMapItem item: items)
			state.items.add(new SerializableItem(item));
		
		for(CodeMapLink link: links)
			state.links.add(new SerializableLink(link));
		
		state.zoom = getScaleFactor();
		state.scrollX = getScrollX();
		state.scrollY = getScrollY();
		return state;
	}
}
