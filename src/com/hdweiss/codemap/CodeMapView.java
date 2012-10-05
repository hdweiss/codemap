package com.hdweiss.codemap;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Scroller;

import com.hdweiss.codemap.drawables.FunctionDrawable;

public class CodeMapView extends SurfaceView implements
		SurfaceHolder.Callback {

	private GestureDetector gestureDetector;
	private Scroller scroller;
	private int scrollPosX = 0;
	private int scrollPosY = 0;
	
	private ArrayList<FunctionDrawable> drawables = new ArrayList<FunctionDrawable>();

	public CodeMapView(Context context, AttributeSet attrs) {
		super(context, attrs);	
        getHolder().addCallback(this);
        
		this.gestureDetector = new GestureDetector(getContext(), new CodeMapGestureListener());
		this.scroller = new Scroller(getContext());
        
        drawables.add(new FunctionDrawable(getContext(), "initial", 100, 100));
        drawables.add(new FunctionDrawable(getContext(), "test", 400, 400));
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
		
		for (FunctionDrawable drawable : drawables)
			drawable.drawWithOffset(canvas, scrollPosX, scrollPosY);
		
	    if(scroller.computeScrollOffset())
	    	scroll(scroller);
	}

	
	private void scroll(Scroller scroller) {
		float dx = (scroller.getStartX() - scroller.getFinalX());
		float dy = (scroller.getStartY() - scroller.getFinalY());
		scrollBy((int)dx, (int)dy);
	}
	
	@Override
	public void scrollBy(int x, int y) {
		super.scrollBy(x, y);
		this.scrollPosX += x;
		this.scrollPosY += y;
	}

	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
		this.scrollPosX = x;
		this.scrollPosY = y;
	}
	

	private final static int DEFAULT_SLEEP_TO_REDRAW = 55;
	public void startAnimateDrag() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				while (scroller.isFinished() == false) {
					updatePanel();
					
					try {
						Thread.sleep(DEFAULT_SLEEP_TO_REDRAW);
					} catch (InterruptedException e) {
					}
				}
			}
		});
		thread.run();
	}
	
	private class CodeMapGestureListener implements OnGestureListener {
		
		public boolean onDown(MotionEvent event) {
			if (!scroller.isFinished())
				scroller.forceFinished(true);
			selectedDrawable = getDrawableFromPoint(event.getX(), event.getY());
			return true;
		}
		
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}
		
		public void onLongPress(MotionEvent e) {
		}

		public void onShowPress(MotionEvent e) {
		}
		
		
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			int startX = (int) (e2.getX() + distanceX);
			int startY = (int) (e2.getY() + distanceY);
			
			if(selectedDrawable != null)
				selectedDrawable.setXY(startX - scrollPosX, startY - scrollPosY);
			else
				scroller.startScroll(startX, startY, (int) distanceX,
					(int) distanceY);
			return true;
		}

		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			return false;
		}
	}

	FunctionDrawable selectedDrawable = null;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		
		gestureDetector.onTouchEvent(event);
		
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
