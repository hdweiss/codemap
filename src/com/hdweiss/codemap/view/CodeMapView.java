package com.hdweiss.codemap.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Scroller;

import com.hdweiss.codemap.drawables.FunctionView;
import com.hdweiss.codemap.util.MultiTouchSupport;
import com.hdweiss.codemap.view.CodeMapListeners.CodeMapGestureListener;
import com.hdweiss.codemap.view.CodeMapListeners.CodeMapMultiTouchListener;

public class CodeMapView extends CodeMapLayout {

	private MultiTouchSupport multiTouchSupport;
	private GestureDetector gestureDetector;
	private Scroller scroller;
	
	private float zoom = 1;
	
	private ArrayList<FunctionView> views = new ArrayList<FunctionView>();

	public CodeMapView(Context context, AttributeSet attrs) {
		super(context, attrs);	
		
        this.scroller = new Scroller(getContext());
		this.gestureDetector = new GestureDetector(getContext(), new CodeMapGestureListener(this, scroller));
		this.multiTouchSupport = new MultiTouchSupport(getContext(), new CodeMapMultiTouchListener(this));
		setFocusable(false);
        
		
		FunctionView functionView = new FunctionView(getContext(), 200, 200);
		addView(functionView);
		
		FunctionView functionView2 = new FunctionView(getContext(), 300, 300);
		addView(functionView2);
	}

	
	public void updateScroll() {
	    if(scroller.computeScrollOffset()) {
			float dx = (scroller.getStartX() - scroller.getFinalX());
			float dy = (scroller.getStartY() - scroller.getFinalY());
			scrollBy((int)dx, (int)dy);	    }
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

		if (!multiTouchSupport.onTouchEvent(event))
			gestureDetector.onTouchEvent(event);
		
		updateScroll();
		return true;
	}
	
	public float getZoom() {
		return this.zoom;
	}
	
	public void setZoom(float zoom) {
		this.zoom = zoom;
		updateScroll();
	}

	
	public FunctionView getDrawableFromPoint(float x, float y) {
		for (FunctionView view : views) {
//			if (view.contains(x, y, zoom))
//				return view;
		}
		return null;
	}

}
