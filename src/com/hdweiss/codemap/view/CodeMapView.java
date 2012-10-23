package com.hdweiss.codemap.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.util.Log;
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
		createFunctionFragment("addTotals", new CodeMapPoint(200, 200));
		createFunctionFragment("createTagsFromFileInput", new CodeMapPoint(300, 500));
	}
	
	public void setProject(Project project) {
		this.project = project;
		initState();
	}

	
	public float getZoom() {
		return this.zoom;
	}
	
	public void setZoom(float zoom, CodeMapPoint pivot) {
		this.zoom = zoom;
		invalidate();
	}
	
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		event.setLocation(event.getX() * zoom, event.getY() * zoom);
		return super.dispatchTouchEvent(event);
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

	
	@Override
	public void dispatchDraw(Canvas canvas) {	    
	    canvas.scale(zoom, zoom);
	    canvas.save(Canvas.MATRIX_SAVE_FLAG);
//	    canvas.translate(getScrollX() * zoom, getScrollY() * zoom);
	    super.dispatchDraw(canvas);
	    canvas.restore();
	}

	

	public CodeMapFunction createFileFragment(String fileName) {
		CodeMapPoint position = new CodeMapCursorPoint(100, 100).getCodeMapPoint(this);
		final SpannableString content = project.getFileSource(fileName);
		
		CodeMapFunction functionView = new CodeMapFunction(getContext(),
				position, fileName, content, this);
		addMapFragment(functionView);
		return functionView;
	}

	
	public CodeMapFunction createFunctionFragment(String functionName) {
		CodeMapPoint position = new CodeMapCursorPoint(100, 100).getCodeMapPoint(this);
		return createFunctionFragment(functionName, position);
	}
	
	public CodeMapFunction createFunctionFragment(String functionName, CodeMapPoint position) {
		final SpannableString content = project.getFunctionSource(functionName);
		
		CodeMapFunction functionView = new CodeMapFunction(getContext(),
				position, functionName, content, this);
		
		addMapFragment(functionView);
		return functionView;
	}
	

	public CodeMapFunction openFragmentFromUrl(String url, CodeMapFunction codeMapFunction) {
		CodeMapPoint position = new CodeMapPoint();
		position.x = codeMapFunction.getX() + codeMapFunction.getWidth() + 30;
		position.y = codeMapFunction.getY() + 20;

		return createFunctionFragment(url, position);
	}
	
	
	public void addMapFragment(CodeMapFunction function) {
		addView(function);
		views.add(function);
		moveMapFragmentToEmptyPosition(function);
	}
	
	public boolean moveMapFragmentToEmptyPosition(CodeMapFunction function) {
		Rect rect = new Rect();
		function.getDrawingRect(rect);
		final int offset = 5;
		
		boolean foundEmpty = false;
		while (foundEmpty == false) {
			foundEmpty = true;
			for (CodeMapFunction view : views) {
				Log.d("CodeMap", "Comparing " + function.getBounds().toString() + " " + view.getBounds().toString());
				if (view != function && Rect.intersects(view.getBounds(), rect)) {
					Log.d("CodeMap", function.getName() + " collieded with " + view.getName());
					int height = rect.bottom - rect.top;
					rect.top = view.getBounds().bottom + offset;
					rect.bottom = rect.top + height;
					foundEmpty = false;
					break;
				}
			}
		}
		
		function.setX(rect.left);
		function.setY(rect.top);
		return true;
	}
	
	public CodeMapFunction getMapFragmentAtPoint(CodeMapCursorPoint cursorPoint) {
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
