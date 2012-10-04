package com.hdweiss.codemap;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hdweiss.codemap.drawables.FunctionDrawable;

public class CodeMapView extends SurfaceView implements
		SurfaceHolder.Callback {

	class CodeMapThread extends Thread {
		private SurfaceHolder mSurfaceHolder;
		
		private boolean mRunning;

		public CodeMapThread(SurfaceHolder surfaceHolder, Context context,
				Handler handler) {
			mSurfaceHolder = surfaceHolder;
		}

		@Override
		public void run() {
			Canvas c = null;
			while (mRunning) {
				try {
					c = mSurfaceHolder.lockCanvas(null);
					synchronized (mSurfaceHolder) {
						doDraw(c);
					}
				} finally {
					if (c != null) {
						mSurfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
		}
		
		public void setRunning(boolean enabled) {
			mRunning = enabled;
		}
		
		private void doDraw(Canvas canvas) {
			canvas.drawColor(Color.WHITE);
			
			for(FunctionDrawable drawable: drawables) {
				drawable.draw(canvas);
			}
			
			if(current != null)
				current.draw(canvas);
		}
		
        public void setSurfaceSize(int width, int height) {
            synchronized (mSurfaceHolder) {
            	
//                mCanvasWidth = width;
//                mCanvasHeight = height;
            }
        }
	}

	private CodeMapThread thread;
	private SurfaceHolder mSurfaceHolder;

	public CodeMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        thread = new CodeMapThread(mSurfaceHolder, context, null);
        
        drawables.add(new FunctionDrawable(getContext(), "initial"));
        
        setFocusable(true);
	}

	
	ArrayList<FunctionDrawable> drawables = new ArrayList<FunctionDrawable>();
	FunctionDrawable current;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		synchronized (mSurfaceHolder) {
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
		return true;
	}

	public FunctionDrawable getDrawableFromPoint(float x, float y) {
		for (FunctionDrawable drawable : drawables) {
			if (drawable.getBounds().contains((int) x, (int) y))
				return drawable;
		}
		return null;
	}

	public CodeMapThread getThread() {
		return this.thread;
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
        thread.setSurfaceSize(width, height);
	}

	public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }

	public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
	}

}
