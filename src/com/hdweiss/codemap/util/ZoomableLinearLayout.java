package com.hdweiss.codemap.util;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.widget.LinearLayout;

import com.hdweiss.codemap.view.workspace.WorkspaceView;

public class ZoomableLinearLayout extends LinearLayout {

	public ZoomableLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public float getScaleFactor() {
		return ((WorkspaceView) getParent()).getScaleFactor();
	}
	

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//
//		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		float parentScale = getScaleFactor();
		
		measureChildren(widthMeasureSpec, heightMeasureSpec);
		
		int chosenHeight = 0;
		int chosenWidth = 0;
		
		for(int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			
			chosenHeight += child.getMeasuredHeight();
			chosenWidth = Math.max(child.getMeasuredWidth(), chosenWidth);
		}

		chosenHeight *= parentScale;
		chosenWidth *= parentScale;

		chosenWidth += getPaddingLeft() + getPaddingRight();
		chosenHeight +=  getPaddingBottom() + getPaddingTop();
		
		//Log.d("CodeMap", "Measured to " + chosenWidth + "x" + chosenHeight);
		setMeasuredDimension(chosenWidth, chosenHeight);
	}


//	@Override
//	protected void onDraw(Canvas canvas) {
//		float parentScale = getScaleFactor();
//		
//	    canvas.scale(parentScale, parentScale);
//	    canvas.save(Canvas.MATRIX_SAVE_FLAG);
//	    super.dispatchDraw(canvas);
//	    canvas.restore();
//	}

	@Override
	public ViewParent invalidateChildInParent(int[] location, Rect dirty) {
		return super.invalidateChildInParent(location, dirty);
	}
}
