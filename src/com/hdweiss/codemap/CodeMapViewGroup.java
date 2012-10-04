package com.hdweiss.codemap;

import com.hdweiss.codemap.drawables.FunctionDrawable;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class CodeMapViewGroup extends ViewGroup {

	private FunctionDrawable draw;
	private FunctionDrawable current;
	
	public CodeMapViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		draw = new FunctionDrawable(getContext(), "hej");
		//addView(draw);
		setFocusable(true);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
	        if (event.getAction() == MotionEvent.ACTION_DOWN) {
	        	current = new FunctionDrawable(getContext(), "test");
	        	
	        	current.setXY(event.getX(), event.getY());
	        	//addView(current);
	        	this.invalidate();
	        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
	        	current.setXY(event.getX(), event.getY());
	        } else if (event.getAction() == MotionEvent.ACTION_UP) {
	        	current = null;
	        }
		return true;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		int itemWidth = (right - left) / getChildCount();
		for (int i = 0; i < this.getChildCount(); i++) {
			View v = getChildAt(i);
			v.layout(itemWidth * i, top, (i + 1) * itemWidth, bottom);
		}
	}

	
}
