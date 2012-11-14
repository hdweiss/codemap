package com.hdweiss.codemap.view.codemap;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Rect;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.hdweiss.codemap.data.CodeMapObject;
import com.hdweiss.codemap.data.CodeMapState;
import com.hdweiss.codemap.data.ProjectController;
import com.hdweiss.codemap.util.CodeMapCursorPoint;
import com.hdweiss.codemap.util.CodeMapPoint;
import com.hdweiss.codemap.util.AbsoluteZoomableLayout;
import com.hdweiss.codemap.view.codemap.CodeMapListeners.CodeMapGestureListener;
import com.hdweiss.codemap.view.codemap.CodeMapListeners.CodeMapScaleListener;
import com.hdweiss.codemap.view.fragments.CodeMapAnnotation;
import com.hdweiss.codemap.view.fragments.CodeMapFunction;
import com.hdweiss.codemap.view.fragments.CodeMapImage;
import com.hdweiss.codemap.view.fragments.CodeMapItem;

public class CodeMapView extends AbsoluteZoomableLayout {

	private GestureDetector gestureDetector;
	private ScaleGestureDetector scaleDetector;
	private Scroller scroller;
		
	private ArrayList<CodeMapItem> views = new ArrayList<CodeMapItem>();
	private ProjectController controller;

	public CodeMapView(Context context, AttributeSet attrs) {
		super(context, attrs);	
		
        this.scroller = new Scroller(getContext());
		this.gestureDetector = new GestureDetector(getContext(), new CodeMapGestureListener(this, scroller));
		this.scaleDetector = new ScaleGestureDetector(getContext(), new CodeMapScaleListener(this));
		
		setFocusable(false);
	}
	
	public void setState(CodeMapState state) {
		for(CodeMapObject data: state.drawables) {
			CodeMapFunction fragment = createFunctionFragment(data.name);
			fragment.setPosition(new CodeMapPoint(data.point));
			Log.d("CodeMap", data.toString());
		}
		
		addMapItem(new CodeMapImage(getContext()));
		addMapItem(new CodeMapAnnotation(getContext()));
		
		setScrollX(state.scrollX);
		setScrollY(state.scrollY);
		//setZoom(state.zoom, new CodeMapPoint());
	}
	
	public CodeMapState getState() {
		CodeMapState state = new CodeMapState(controller.project.getName());
		
		for(CodeMapItem view: views)
			state.drawables.add(new CodeMapObject(view.getName(), view.getPosition()));
		
		//state.zoom = zoom;
		state.scrollX = getScrollX();
		state.scrollY = getScrollY();
		return state;
	}
	
	public void setController(ProjectController controller) {
		this.controller = controller;
	}
	
	
//	@Override
//	public boolean dispatchTouchEvent(MotionEvent event) {
//		event.setLocation(event.getX() * zoom, event.getY() * zoom);
//		return super.dispatchTouchEvent(event);
//	}

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
	

	public CodeMapFunction createFileFragment(String fileName) {
		CodeMapPoint position = new CodeMapCursorPoint(100, 100).getCodeMapPoint(this);
		final SpannableString content = controller.getFileSource(fileName);
		
		CodeMapFunction functionView = new CodeMapFunction(getContext(),
				position, fileName, content, this);
		addMapItem(functionView);
		return functionView;
	}

	
	public CodeMapFunction createFunctionFragment(String functionName) {
		CodeMapPoint position = new CodeMapCursorPoint(100, 100).getCodeMapPoint(this);
		return createFunctionFragment(functionName, position);
	}
	
	public CodeMapFunction createFunctionFragment(String functionName, CodeMapPoint position) {
		final SpannableString content = controller.getFunctionSource(functionName);
		
		CodeMapFunction functionView = new CodeMapFunction(getContext(),
				position, functionName, content, this);
		
		addMapItem(functionView);
		return functionView;
	}
	

	public CodeMapFunction openFragmentFromUrl(String url, CodeMapFunction codeMapFunction) {
		CodeMapPoint position = new CodeMapPoint();
		position.x = codeMapFunction.getX() + codeMapFunction.getWidth() + 30;
		position.y = codeMapFunction.getY() + 20;

		return createFunctionFragment(url, position);
	}
	
	
	public void addMapItem(CodeMapItem item) {
		addView(item);
		views.add(item);
		moveMapItemToEmptyPosition(item);
	}
	
	public boolean moveMapItemToEmptyPosition(CodeMapItem item) {
		Rect rect = item.getBounds();
		rect.bottom += 1;
		rect.right += 1;
		final int offset = 5;
		
		boolean foundEmpty = false;
		while (foundEmpty == false) {
			foundEmpty = true;
			for (CodeMapItem view : views) {
				Log.d("CodeMap", "Comparing " + item.getBounds().toString() + " " + view.getBounds().toString());
				if (view != item && Rect.intersects(view.getBounds(), rect)) {
					Log.d("CodeMap", item.getName() + " collieded with " + view.getName());
					int height = rect.bottom - rect.top;
					rect.top = view.getBounds().bottom + offset;
					rect.bottom = rect.top + height;
					foundEmpty = false;
					break;
				}
			}
		}
		
		item.setX(rect.left);
		item.setY(rect.top);
		return true;
	}
	
	public CodeMapItem getMapFragmentAtPoint(CodeMapCursorPoint cursorPoint) {
		CodeMapPoint point = cursorPoint.getCodeMapPoint(this);
		for (CodeMapItem view : views) {
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
