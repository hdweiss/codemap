package com.hdweiss.codemap.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.hdweiss.codemap.data.Project;
import com.hdweiss.codemap.util.CodeMapCursorPoint;
import com.hdweiss.codemap.util.CodeMapPoint;
import com.hdweiss.codemap.util.MyAbsoluteLayout;
import com.hdweiss.codemap.view.CodeMapListeners.CodeMapGestureListener;
import com.hdweiss.codemap.view.CodeMapListeners.CodeMapScaleListener;
import com.hdweiss.codemap.view.fragments.CodeMapFunction;

public class CodeMapView extends MyAbsoluteLayout {

	private GestureDetector gestureDetector;
	private ScaleGestureDetector scaleDetector;
	private Scroller scroller;
	
	private float zoom = 1;
	
	private ArrayList<CodeMapFunction> views = new ArrayList<CodeMapFunction>();
	private Project project;

	public CodeMapView(Context context, AttributeSet attrs) {
		super(context, attrs);	
		
        this.scroller = new Scroller(getContext());
		this.gestureDetector = new GestureDetector(getContext(), new CodeMapGestureListener(this, scroller));
		this.scaleDetector = new ScaleGestureDetector(getContext(), new CodeMapScaleListener(this));
		
		setFocusable(false);
	}
	
	private void initState() {		
		createFunction(new CodeMapPoint(200, 200), "addTotals");
		createFunction(new CodeMapPoint(300, 500), "createTagsFromFileInput");
	}
	
	public void setProject(Project project) {
		this.project = project;
		initState();
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
	
	public void setZoom(float zoom, CodeMapPoint pivot) {
		this.zoom = zoom;
		invalidate();
	}
	
	
	@Override
	public void dispatchDraw(Canvas canvas) {	    
	    canvas.save();
	    canvas.scale(zoom, zoom);
//	    canvas.translate(getScrollX() * zoom, getScrollY() * zoom);
	    super.dispatchDraw(canvas);
	    canvas.restore();
	}
	

	public CodeMapFunction createFunction(String functionName) {
		CodeMapPoint position = new CodeMapCursorPoint(100, 100).getCodeMapPoint(this);
		return createFunction(position, functionName);
	}
	
	public CodeMapFunction createFunction(CodeMapPoint position, String functionName) {
		final SpannableString content = project.getFunctionSource(functionName);
		
		CodeMapFunction functionView = new CodeMapFunction(getContext(), position, functionName, content, this);
		addFunction(functionView);
		return functionView;
	}
	
	
	public void createFunctionCentered(CodeMapCursorPoint cursorPosition, String url) {
		CodeMapPoint position = cursorPosition.getCodeMapPoint(this);
		CodeMapFunction functionView = createFunction(position, url);
		functionView.setPositionCenter(position);
	}
	
	public void addFunction(CodeMapFunction function) {
		addView(function);
		views.add(function);
	}

	public CodeMapFunction openFunctionFromFragment(String url, CodeMapFunction codeMapFunction) {
		CodeMapPoint position = new CodeMapPoint();
		position.x = codeMapFunction.getX() + codeMapFunction.getWidth() + 30;
		position.y = codeMapFunction.getY() + 20;

		return createFunction(position, url);
	}
	
	
	public CodeMapFunction getDrawable(CodeMapCursorPoint cursorPoint) {
		CodeMapPoint point = cursorPoint.getCodeMapPoint(this);
		for (CodeMapFunction view : views) {
			if (view.contains(point))
				return view;
		}
		return null;
	}
	
	
	public void refresh() {
		invalidate();
	}
	
	public void remove(LinearLayout view) {
		removeView(view);
		views.remove(view);
	}
	
	public void clear() {
		removeAllViews();
		views.clear();
	}
}
