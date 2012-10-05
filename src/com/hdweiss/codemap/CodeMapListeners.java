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
				selectedDrawable.setXY(startX - codeMapView.getScrollX(),
						startY - codeMapView.getScrollY());
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
		private CodeMapView codeMapView;

		public CodeMapMultiTouchListener(CodeMapView codeMapView) {
			this.codeMapView = codeMapView;
		}
		
		public void onZoomStarted(float distance, PointF centerPoint) {
			initialMultiTouchZoom = codeMapView.getScaleX();
		}

		public void onZooming(float distance, float relativeToStart) {
			float dz = (float) (Math.log(relativeToStart) / Math.log(2) * 1.5);
			float calcZoom = initialMultiTouchZoom + dz;
			if (Math.abs(calcZoom - codeMapView.getScaleX()) > 0.05) {
				codeMapView.setZoom(calcZoom);
				//zoomPositionChanged(calcZoom);
			}
		}

		public void onZoomEnded(float distance, float relativeToStart) {
		}

		public void onGestureInit(float x1, float y1, float x2, float y2) {
		}
		
	}
}
