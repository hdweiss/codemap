package com.hdweiss.codemap;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Scroller;

import com.hdweiss.codemap.CodeMapListeners.CodeMapGestureListener;
import com.hdweiss.codemap.CodeMapListeners.CodeMapMultiTouchListener;
import com.hdweiss.codemap.drawables.FunctionDrawable;
import com.hdweiss.codemap.util.MultiTouchSupport;

public class CodeMapView extends SurfaceView implements
		SurfaceHolder.Callback {

	private MultiTouchSupport multiTouchSupport;
	private GestureDetector gestureDetector;
	private Scroller scroller;
	
	private float zoom = 1;
	
	private ArrayList<FunctionDrawable> drawables = new ArrayList<FunctionDrawable>();

	public CodeMapView(Context context, AttributeSet attrs) {
		super(context, attrs);	
        getHolder().addCallback(this);
        
        this.scroller = new Scroller(getContext());
		this.gestureDetector = new GestureDetector(getContext(), new CodeMapGestureListener(this, scroller));
		this.multiTouchSupport = new MultiTouchSupport(getContext(), new CodeMapMultiTouchListener(this));
        
        drawables.add(new FunctionDrawable(getContext(), "initial", 100, 100));
        drawables.add(new FunctionDrawable(getContext(), "test", 400, 400));
	}
	
	
	public void refresh() {
		Canvas canvas = null;
		try {
			canvas = getHolder().lockCanvas(null);
			synchronized (getHolder()) {
				doDraw(canvas);
			}
		} finally {
			if (canvas != null) {
				getHolder().unlockCanvasAndPost(canvas);
			}
		}
	}
    
	private void doDraw(Canvas canvas) {
		if(canvas == null)
			return;
		
		canvas.save();
		canvas.scale(zoom, zoom);
		
		canvas.drawColor(Color.WHITE);
		
		for (FunctionDrawable drawable : drawables)
			drawable.drawWithOffset(canvas, getScrollX(), getScrollY());
		
	    if(scroller.computeScrollOffset())
	    	scroll(scroller);
	    
	    canvas.restore();
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

	
	public FunctionDrawable getDrawableFromPoint(float x, float y) {
		for (FunctionDrawable drawable : drawables) {
			if (drawable.getBounds().contains((int) x, (int) y))
				return drawable;
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
