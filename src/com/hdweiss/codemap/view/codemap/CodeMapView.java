package com.hdweiss.codemap.view.codemap;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.Scroller;

import com.hdweiss.codemap.data.CodeMapObject;
import com.hdweiss.codemap.data.CodeMapState;
import com.hdweiss.codemap.data.ProjectController;
import com.hdweiss.codemap.util.CodeMapCursorPoint;
import com.hdweiss.codemap.util.CodeMapPoint;
import com.hdweiss.codemap.util.ZoomableAbsoluteLayout;
import com.hdweiss.codemap.view.codemap.CodeMapListeners.CodeMapGestureListener;
import com.hdweiss.codemap.view.codemap.CodeMapListeners.CodeMapScaleListener;
import com.hdweiss.codemap.view.fragments.CodeMapFunction;
import com.hdweiss.codemap.view.fragments.CodeMapItem;
import com.hdweiss.codemap.view.fragments.CodeMapLink;

public class CodeMapView extends ZoomableAbsoluteLayout {

	private GestureDetector gestureDetector;
	private ScaleGestureDetector scaleDetector;
	private Scroller scroller;
		
	private ArrayList<CodeMapItem> views = new ArrayList<CodeMapItem>();
	private ArrayList<CodeMapLink> links = new ArrayList<CodeMapLink>();
	private ProjectController controller;

	public CodeMapView(Context context, AttributeSet attrs) {
		super(context, attrs);	
		
        this.scroller = new Scroller(getContext());
		this.gestureDetector = new GestureDetector(getContext(), new CodeMapGestureListener(this, scroller));
		this.scaleDetector = new ScaleGestureDetector(getContext(), new CodeMapScaleListener(this));
		
		setWillNotDraw(false);
		setFocusable(false);
	}
	
	@SuppressWarnings("unchecked")
	public void setState(CodeMapState state) {
		new LoadState().execute(state.drawables);
		setScrollX(state.scrollX);
		setScrollY(state.scrollY);
		//setZoom(state.zoom, new CodeMapPoint());
	}

	
	private class LoadState extends AsyncTask<ArrayList<CodeMapObject>, CodeMapItem, Long> {
		private ProgressDialog dialog;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			this.dialog = ProgressDialog.show(getContext(), "Loading",
					"Loading state...");
		}

		protected Long doInBackground(ArrayList<CodeMapObject>... objects) {
			ArrayList<CodeMapObject> items = objects[0];
			
			for (int i = 0; i < items.size(); i++) {
				CodeMapFunction fragment = instantiateFunctionFragment(items.get(i).name, new CodeMapPoint(items.get(i).point));
				//fragment.setPosition();
				this.publishProgress(fragment);
			}

			return (long) 0;
		}

		protected void onProgressUpdate(CodeMapItem... progress) {
			for(int i = 0; i < progress.length; i++)
				addMapItem(progress[i]);
		}

		protected void onPostExecute(Long result) {
			dialog.dismiss();
		}
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
	
	public CodeMapFunction instantiateFunctionFragment(String functionName, CodeMapPoint position) {
		final SpannableString content = controller.getFunctionSource(functionName);
		
		CodeMapFunction functionView = new CodeMapFunction(getContext(),
				position, functionName, content, this);
		return functionView;
	}
	
	public CodeMapFunction createFunctionFragment(String functionName, CodeMapPoint position) {
		CodeMapFunction functionView = instantiateFunctionFragment(functionName, position);
		addMapItem(functionView);
		return functionView;
	}
	

	public CodeMapItem openFragmentFromUrl(String url, CodeMapItem parent, float yOffset) {
		CodeMapPoint position = new CodeMapPoint();
		position.x = parent.getX() + parent.getWidth() + 30;
		position.y = parent.getY() + 20;

		CodeMapItem item = createFunctionFragment(url, position);
		links.add(new CodeMapLink(parent, item, yOffset + parent.getTitleViewOffset()));
		refresh();
		return item;
	}
	
	
	public void addMapItem(CodeMapItem item) {
		addView(item);
		views.add(item);
		item.setCodeMapView(this);
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
				//Log.d("CodeMap", "Comparing " + item.getBounds().toString() + " " + view.getBounds().toString());
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

	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		for(CodeMapLink link: links)
			link.doDraw(canvas);
	}

	public void refresh() {
		invalidate();
	}
	
	public void remove(CodeMapItem item) {
		removeView(item);
		views.remove(item);
		item.setCodeMapView(null);
		
		Iterator<CodeMapLink> linksIt = links.iterator();
		while(linksIt.hasNext()) {
			CodeMapLink link = linksIt.next();
			if(link.hasItem(item))
				linksIt.remove();
		}
	}
	
	public void clear() {
		removeAllViews();
		views.clear();
		links.clear();
	}
}
