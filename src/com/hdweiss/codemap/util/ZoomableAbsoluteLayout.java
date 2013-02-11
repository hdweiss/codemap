package com.hdweiss.codemap.util;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class ZoomableAbsoluteLayout extends AbsoluteLayout {

	private float mScaleFactor = 1;

	public ZoomableAbsoluteLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		float translatedX = event.getX() / this.mScaleFactor;
		float translatedY = event.getY() / this.mScaleFactor;
		Log.d("CodeMap",
				"dispatchTouchEvent(): " + (int) event.getX() + ":" + (int) event.getY()
						+ "->" + (int) translatedX + ":" + (int) translatedY + " @ " + getScrollX() + ":" + getScrollY());
		event.setLocation(translatedX, translatedY);
		return super.dispatchTouchEvent(event);
	}

	public float getScaleFactor() {
		return this.mScaleFactor;
	}
	
	public void setScaleFactor(float scaleFactor, CodeMapPoint pivot) {
		this.mScaleFactor = scaleFactor;
		invalidate();
	}
	
	protected void dispatchDraw(Canvas canvas) {
	    canvas.save();
	  //  canvas.translate(mPosX, mPosY);
	   // canvas.scale(mScaleFactor, mScaleFactor, mFocusX, mFocusY);
	    canvas.scale(mScaleFactor, mScaleFactor);
	    super.dispatchDraw(canvas);
	    canvas.restore();
	}
}
