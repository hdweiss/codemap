package com.hdweiss.codemap;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.Scroller;

import com.hdweiss.codemap.CodeMapListeners.CodeMapGestureListener;
import com.hdweiss.codemap.CodeMapListeners.CodeMapMultiTouchListener;
import com.hdweiss.codemap.drawables.FunctionDrawable;
import com.hdweiss.codemap.drawables.FunctionView;
import com.hdweiss.codemap.util.MultiTouchSupport;

public class CodeMapView extends CodeMapLayout implements
		SurfaceHolder.Callback {

	private MultiTouchSupport multiTouchSupport;
	private GestureDetector gestureDetector;
	private Scroller scroller;
	
	private float zoom = 1;
	
	private ArrayList<FunctionView> drawables = new ArrayList<FunctionView>();

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
		
		functionView2.setX(500);
	}

	
	public void refresh() {
		doDraw();
	}
    
	private void doDraw() {	
		for (FunctionView drawable : drawables) {
			//drawable.drawWithOffset(getScrollX(), getScrollY());
			drawable.setX(drawable.getX() + getScrollX());
			drawable.setY(drawable.getY() + getScrollY());
		}
	    if(scroller.computeScrollOffset())
	    	scroll(scroller);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

		if (!multiTouchSupport.onTouchEvent(event))
			gestureDetector.onTouchEvent(event);
		
		refresh();
		return true;
	}
	
	
	private void scroll(Scroller scroller) {
		float dx = (scroller.getStartX() - scroller.getFinalX());
		float dy = (scroller.getStartY() - scroller.getFinalY());
		scrollBy((int)dx, (int)dy);
	}
	
	public float getZoom() {
		return this.zoom;
	}
	
	public void setZoom(float zoom) {
		this.zoom = zoom;
		refresh();
	}

	
	public FunctionView getDrawableFromPoint(float x, float y) {
		for (FunctionView drawable : drawables) {
//			if (drawable.contains(x, y, zoom))
//				return drawable;
		}
		return null;
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		refresh();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		refresh();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		refresh();
	}

}
