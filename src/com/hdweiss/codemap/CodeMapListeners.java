package com.hdweiss.codemap;

import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.GestureDetector.OnGestureListener;
import android.widget.Scroller;

import com.hdweiss.codemap.drawables.FunctionDrawable;
import com.hdweiss.codemap.util.MultiTouchSupport.MultiTouchZoomListener;

public class CodeMapListeners {
	
	public static class CodeMapGestureListener implements OnGestureListener {
		FunctionDrawable selectedDrawable = null;
		private CodeMapView view;
		private Scroller scroller;
		
		public CodeMapGestureListener(CodeMapView view, Scroller scroller) {
			this.view = view;
			this.scroller = scroller;
		}
		
		public boolean onDown(MotionEvent event) {
			if (!scroller.isFinished())
				scroller.forceFinished(true);
			selectedDrawable = view.getDrawableFromPoint(event.getX(), event.getY());
			return true;
		}
		
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}
		
		public void onLongPress(MotionEvent e) {
		}

		public void onShowPress(MotionEvent e) {
		}
		
		
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			int startX = (int) (e2.getX() + distanceX);
			int startY = (int) (e2.getY() + distanceY);
			
			if(selectedDrawable != null)
				selectedDrawable.setXY(startX - view.getScrollPosX(), startY - view.getScrollPosY());
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

		private float initialMultiTouchZoom;
		private CodeMapView view;

		public CodeMapMultiTouchListener(CodeMapView view) {
			this.view = view;
		}
		
		public void onZoomStarted(float distance, PointF centerPoint) {
			initialMultiTouchZoom = view.getZoom();
		}

		public void onZooming(float distance, float relativeToStart) {
			float dz = (float) (Math.log(relativeToStart) / Math.log(2) * 1.5);
			float calcZoom = initialMultiTouchZoom + dz;
			if (Math.abs(calcZoom - view.getZoom()) > 0.05) {
				view.setZoom(calcZoom);
				//zoomPositionChanged(calcZoom);
			}
		}

		public void onZoomEnded(float distance, float relativeToStart) {
		}

		public void onGestureInit(float x1, float y1, float x2, float y2) {
		}
		
	}
}
