package com.hdweiss.codemap.view.workspace;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.Scroller;

import com.hdweiss.codemap.data.SerializableItem;
import com.hdweiss.codemap.data.SerializableLink;
import com.hdweiss.codemap.util.CodeMapCursorPoint;
import com.hdweiss.codemap.util.CodeMapPoint;
import com.hdweiss.codemap.util.ZoomableAbsoluteLayout;
import com.hdweiss.codemap.view.workspace.WorkspaceViewListeners.CodeMapGestureListener;
import com.hdweiss.codemap.view.workspace.WorkspaceViewListeners.CodeMapScaleListener;
import com.hdweiss.codemap.view.workspace.fragments.CodeMapItem;
import com.hdweiss.codemap.view.workspace.fragments.CodeMapLink;

public class WorkspaceView extends ZoomableAbsoluteLayout {

	private GestureDetector gestureDetector;
	private ScaleGestureDetector scaleDetector;
	private Scroller scroller;
	
	public ArrayList<CodeMapItem> items = new ArrayList<CodeMapItem>();
	private ArrayList<CodeMapLink> links = new ArrayList<CodeMapLink>();
	private WorkspaceController controller;

	public WorkspaceView(Context context, AttributeSet attrs) {
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

		canvas.save();
	    canvas.scale(getScaleFactor(), getScaleFactor());
		
		for(CodeMapLink link: links)
			link.doDraw(canvas);
		
		canvas.restore();
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
		if (items.contains(item))
			return;
		
		addView(item);
		items.add(item);
		item.setCodeMapView(this);
		controller.updateCodeBrowser();
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

		controller.updateCodeBrowser();
	}
	
	public void clear() {
		removeAllViews();
		items.clear();
		links.clear();
	}
	
	
	public void moveFragment(CodeMapItem item, CodeMapPoint position) {
		item.setPosition(position);
		moveFragment(item);
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


	public void setController(WorkspaceController controller) {
		this.controller = controller;
	}
	
	public WorkspaceController getController() {
		return this.controller;
	}
	
	public WorkspaceState getState() {
		WorkspaceState state = new WorkspaceState(controller.getWorkspaceName());
		
		for(CodeMapItem item: items)
			state.items.add(new SerializableItem(item));
		
		for(CodeMapLink link: links)
			state.links.add(new SerializableLink(link));
		
		state.zoom = getScaleFactor();
		state.scrollX = getScrollX();
		state.scrollY = getScrollY();
		return state;
	}
	
	// TODO Make more efficient
	public ArrayList<CodeMapItem> getDeclarations(String url) {
		ArrayList<CodeMapItem> result = new ArrayList<CodeMapItem>();
		Iterator<CodeMapItem> i = this.items.iterator();
		while (i.hasNext()) {
			CodeMapItem item = i.next();
				
			if (item.getUrl().equals(url))
				result.add(item);
		}
		
		return result;
	}
	
	public void setScroll(float x, float y) {
		setScrollX((int) x);
		setScrollY((int) y);
	}
	
	public void setFontSize(int fontSize) {
		for (CodeMapItem item: this.items) {
			item.setFontSize(fontSize);
		}
	}
}
