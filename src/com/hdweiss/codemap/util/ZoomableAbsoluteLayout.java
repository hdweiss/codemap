package com.hdweiss.codemap.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ZoomableAbsoluteLayout extends AbsoluteLayout {

	private float mScaleFactor = 1;

	public ZoomableAbsoluteLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

    @Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int count = getChildCount();

		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			if (child.getVisibility() != GONE) {

				AbsoluteLayout.LayoutParams lp = (AbsoluteLayout.LayoutParams) child
						.getLayoutParams();

				int childLeft = getPaddingLeft() + lp.x;
				int childTop = getPaddingTop() + lp.y;
				child.layout(
						(int) ((float)childLeft * mScaleFactor),
						(int) ((float)childTop * mScaleFactor),
						(int) ((float)(childLeft + child.getMeasuredWidth()) * mScaleFactor),
						(int) ((float)(childTop + child.getMeasuredHeight()) * mScaleFactor));

			}
		}
	}
    
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		//event.setLocation(event.getX() * this.mScaleFactor, event.getY() * this.mScaleFactor);
		return super.dispatchTouchEvent(event);
	}

	public float getScaleFactor() {
		return this.mScaleFactor;
	}
	
	public void setScaleFactor(float scaleFactor, CodeMapPoint pivot) {
		this.mScaleFactor = scaleFactor;
		
		for(int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			child.setScaleX(mScaleFactor);
			child.setScaleY(mScaleFactor);
		}
		
		invalidate();
		requestLayout();
	}
}
