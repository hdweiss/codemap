package com.hdweiss.codemap;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hdweiss.codemap.drawables.FunctionDrawable;

public class CodeMapView extends SurfaceView implements
		SurfaceHolder.Callback {

	private ArrayList<FunctionDrawable> drawables = new ArrayList<FunctionDrawable>();
	private FunctionDrawable current;
	
	public CodeMapView(Context context, AttributeSet attrs) {
		super(context, attrs);	
        getHolder().addCallback(this);
        
        drawables.add(new FunctionDrawable(getContext(), "initial"));
	}
	
	public void updatePanel() {
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
		
		canvas.drawColor(Color.WHITE);
		
		for(FunctionDrawable drawable: drawables)
			drawable.draw(canvas);
		
		if(current != null)
			current.draw(canvas);
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		synchronized (getHolder()) {
	        if (event.getAction() == MotionEvent.ACTION_DOWN) {
	        	FunctionDrawable draw = getDrawableFromPoint(event.getX(), event.getY());
	        	
	        	if(draw != null) {
	        		current = draw;
	        	} else
	        		current = new FunctionDrawable(getContext(), "test");
	        	current.setXY(event.getX(), event.getY());
	        	
	        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
	        	current.setXY(event.getX(), event.getY());
	        } else if (event.getAction() == MotionEvent.ACTION_UP) {
	        	drawables.add(current);
	        	current = null;
	        }
		}
		updatePanel();
		return true;
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
		updatePanel();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		updatePanel();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		updatePanel();
	}

}
