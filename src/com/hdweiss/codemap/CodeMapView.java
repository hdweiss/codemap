package com.hdweiss.codemap;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.GestureDetector.OnGestureListener;
import android.view.animation.DecelerateInterpolator;
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
		
		for (FunctionDrawable drawable : drawables)
			drawable.drawAt(canvas, scrollPosX, scrollPosY);
		
	    if(scroller.computeScrollOffset()) {
	    	scroll(scroller);
//	    	Log.d("CodeMap",
//					"computeScrollOffset! " + scroller.getCurrVelocity() + "@"
//							+ scroller.getCurrX() + ":" + scroller.getCurrY());
	    }
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


	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
//		Log.d("CodeMap", "Scroll changed!" + l + ":" + t + " " + oldl + ":" + oldt);
//		this.ScrollPosX += oldl - l;
//		this.ScrollPosY += oldt - t;
		//updatePanel();
	}

	
	
	private void scroll(Scroller scroller) {
		float dx = (scroller.getStartX() - scroller.getFinalX());
		float dy = (scroller.getStartY() - scroller.getFinalY());
		Log.d("CodeMap", "scroll! " + dx + ":" + dy);
		scrollBy((int)dx, (int)dy);
	}
	
	private class CodeMapGestureListener implements OnGestureListener {
		float mMaxScrollX = 50;
		float mMaxScrollY = 50;

		public boolean onDown(MotionEvent e) {
			if (!scroller.isFinished())
				scroller.forceFinished(true);
			return true;
		}
		
		public boolean onSingleTapUp(MotionEvent e) {
			//PointF point = new PointF(e.getX(), e.getY());
			return false;
		}
		
		public void onLongPress(MotionEvent e) {
		}

		public void onShowPress(MotionEvent e) {
		}
		
		
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			scroller.startScroll((int) (e2.getX() + distanceX),
					(int) (e2.getY() + distanceY), (int) distanceX,
					(int) distanceY);
			return true;
		}

		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//			startDragging(velocityX, velocityY, 
//						e1.getX(), e1.getY(), e2.getX(), e2.getY(), true);

//	        scroller.fling(getScrollX(), getScrollY(),
//	                -(int)velocityX, -(int)velocityY, 0, (int)mMaxScrollX, 0, (int)mMaxScrollY);

//			scroller.fling(scrollPosX, scrollPosY, -(int) velocityX,
//					-(int) velocityY, 0, (int) mMaxScrollX, 0,
//					(int) mMaxScrollY);
			
//			int distanceX = 100;
//			int distanceY = 100;
//			scroller.startScroll((int) (e1.getX()),
//					(int) (e1.getY()), (int) distanceX,
//					(int) distanceY, 500);
			startAnimateDrag();
			//invalidate();

			return true;
		}
	}
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
//		synchronized (getHolder()) {
//	        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//	        	FunctionDrawable draw = getDrawableFromPoint(event.getX(), event.getY());
//	        	
//	        	if(draw != null) {
//	        		current = draw;
//	        	} else
//	        		current = new FunctionDrawable(getContext(), "test");
//	        	current.setXY(event.getX(), event.getY());
//	        	
//	        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
//	        	current.setXY(event.getX(), event.getY());
//	        } else if (event.getAction() == MotionEvent.ACTION_UP) {
//	        	drawables.add(current);
//	        	current = null;
//	        }
//		}
		gestureDetector.onTouchEvent(event);
		updatePanel();
		return true;
	}
	

	private final static float DRAGGING_ANIMATION_TIME = 1200f;
	public void startDragging(final float velocityX, final float velocityY,
			float startX, float startY, final float endX, final float endY,
			final boolean notifyListener) {
		final float animationTime = DRAGGING_ANIMATION_TIME;
		float curX = endX;
		float curY = endY;
		DecelerateInterpolator interpolator = new DecelerateInterpolator(1);

		long timeMillis = SystemClock.uptimeMillis();
		float normalizedTime = 0f;
		float prevNormalizedTime = 0f;
		normalizedTime = (SystemClock.uptimeMillis() - timeMillis)
				/ animationTime;
		if (normalizedTime >= 1f) {
			return;
		}
		float interpolation = interpolator.getInterpolation(normalizedTime);

		float newX = velocityX * (1 - interpolation)
				* (normalizedTime - prevNormalizedTime) + curX;
		float newY = velocityY * (1 - interpolation)
				* (normalizedTime - prevNormalizedTime) + curY;

		//dragToAnimate(curX, curY, newX, newY, notifyListener);
	}

	public void startAnimateDrag() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				while (scroller.isFinished() == false) {
					updatePanel();
				}
			}
		});
		thread.run();
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
