package com.hdweiss.codemap.view;

import android.graphics.PointF;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.Scroller;

import com.hdweiss.codemap.util.MultiTouchSupport.MultiTouchZoomListener;
import com.hdweiss.codemap.view.fragments.FunctionView;

public class CodeMapListeners {
	
	public static class CodeMapGestureListener implements OnGestureListener {
		private FunctionView selectedDrawable = null;
		private CodeMapView codeMapView;
		private Scroller scroller;
		
		public CodeMapGestureListener(CodeMapView codeMapView, Scroller scroller) {
			this.codeMapView = codeMapView;
			this.scroller = scroller;
		}
		
		public boolean onDown(MotionEvent event) {
			if (!scroller.isFinished())
				scroller.forceFinished(true);
			selectedDrawable = codeMapView.getDrawableFromPoint(event.getX(),
					event.getY());
			return true;
		}
		
		public boolean onSingleTapUp(MotionEvent e) {
			this.codeMapView.addFunction(e.getX(), e.getY());
			return true;
		}
		
		public void onLongPress(MotionEvent e) {
		}

		public void onShowPress(MotionEvent e) {
		}
		
		
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			int startX = (int) (e2.getX() + distanceX);
			int startY = (int) (e2.getY() + distanceY);
			
			if(selectedDrawable != null)
				selectedDrawable.setXY(startX, startY);
			else
				scroller.startScroll(startX, startY, (int) distanceX,
					(int) distanceY);
			return true;
		}

		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			return false;
		}
	}
	
	
	public static class CodeMapMultiTouchListener implements MultiTouchZoomListener {

		private float minZoom = 0.5f;
		private float maxZoom = 5;
		
		private CodeMapView codeMapView;
		private float initialMultiTouchZoom;

		public CodeMapMultiTouchListener(CodeMapView codeMapView) {
			this.codeMapView = codeMapView;
		}
		
		public void onZoomStarted(float distance, PointF centerPoint) {
			initialMultiTouchZoom = codeMapView.getZoom();
		}

		public void onZooming(float distance, float relativeToStart) {
			float dz = (float) (Math.log(relativeToStart) / Math.log(2) * 1.5);
			float calcZoom = initialMultiTouchZoom + dz;
			if (Math.abs(calcZoom - codeMapView.getZoom()) > 0.05) {
				if(calcZoom >= minZoom && calcZoom <= maxZoom) {
					codeMapView.setZoom(calcZoom);
					//zoomPositionChanged(calcZoom);
				}
			}
		}

		public void onZoomEnded(float distance, float relativeToStart) {
		}

		public void onGestureInit(float x1, float y1, float x2, float y2) {
		}
		
	}
}
