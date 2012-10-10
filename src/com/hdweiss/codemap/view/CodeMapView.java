package com.hdweiss.codemap.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.Scroller;

import com.hdweiss.codemap.view.CodeMapListeners.CodeMapGestureListener;
import com.hdweiss.codemap.view.CodeMapListeners.CodeMapScaleListener;
import com.hdweiss.codemap.view.fragments.FunctionView;

public class CodeMapView extends CodeMapLayout {

	private GestureDetector gestureDetector;
	private ScaleGestureDetector scaleDetector;
	private Scroller scroller;
	
	private float zoom = 1;
	
	private ArrayList<FunctionView> views = new ArrayList<FunctionView>();

	public CodeMapView(Context context, AttributeSet attrs) {
		super(context, attrs);	
		
        this.scroller = new Scroller(getContext());
		this.gestureDetector = new GestureDetector(getContext(), new CodeMapGestureListener(this, scroller));
		this.scaleDetector = new ScaleGestureDetector(getContext(), new CodeMapScaleListener(this));
		
		setFocusable(false);
       
		initState();
	}
	
	private void initState() {
		addFunction(new PointF(200, 200));
		addFunction(new PointF(300, 500));
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		
		scaleDetector.onTouchEvent(event);
		gestureDetector.onTouchEvent(event);
		
		updateScroll();
		return true;
	}
	
	private void updateScroll() {
	    if(scroller.computeScrollOffset()) {
			float dx = (scroller.getStartX() - scroller.getFinalX());
			float dy = (scroller.getStartY() - scroller.getFinalY());
			scrollBy(-(int)dx, -(int)dy);	    
		}
	}
	
	
	public float getZoom() {
		return this.zoom;
	}
	
	public void setZoom(float zoom, PointF pivot) {
		this.zoom = zoom;
		setPivotX(pivot.x);
		setPivotY(pivot.y);
		setScaleX(zoom);
		setScaleY(zoom);
	}
	
	
//	@Override
//	public void dispatchDraw(Canvas canvas) {
//	    super.onDraw(canvas);
//	    
//	    for(int i = 0; i < getChildCount(); i++)
//	    	getChildAt(i).draw(canvas);
//	    
//	    canvas.save();
//	    //canvas.translate(getScrollX(), getScrollY());
//	    canvas.scale(zoom, zoom);
//	    canvas.restore();
//	}
	
	public FunctionView getDrawableFromPoint(PointF point) {
		for (FunctionView view : views) {
			if (view.contains(point, zoom)) {
				Log.d("CodeMap" , "!Found view at point");
				return view;
			}
		}
		Log.d("CodeMap" , "?Didn't find any view at point");
		return null;
	}

	public void addFunction(PointF center) {
		float offsetX = center.x + getScrollX();
		float offsetY = center.y + getScrollY();
		FunctionView functionView = new FunctionView(getContext(), offsetX, offsetY);
		addView(functionView);
		views.add(functionView);
	}

	public void clear() {
		removeAllViews();
	}
}
