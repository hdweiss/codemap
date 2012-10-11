package com.hdweiss.codemap.view;

import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.widget.Scroller;

import com.hdweiss.codemap.util.CodeMapCursorPoint;
import com.hdweiss.codemap.view.fragments.CodeMapFunction;

public class CodeMapListeners {
	
	public static class CodeMapGestureListener implements OnGestureListener {
		private CodeMapFunction selectedDrawable = null;
		private CodeMapView codeMapView;
		private Scroller scroller;
		
		public CodeMapGestureListener(CodeMapView codeMapView, Scroller scroller) {
			this.codeMapView = codeMapView;
			this.scroller = scroller;
		}
		
		public boolean onDown(MotionEvent e) {
			if (!scroller.isFinished())
				scroller.forceFinished(true);

			CodeMapCursorPoint point = new CodeMapCursorPoint(e.getX(), e.getY());
			selectedDrawable = codeMapView.getDrawable(point);
			
			return true;
		}
		
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}
		
		public void onLongPress(MotionEvent e) {
			CodeMapCursorPoint point = new CodeMapCursorPoint(e.getX(), e.getY());
			this.codeMapView.addFunctionCentered(point);
		}

		public void onShowPress(MotionEvent e) {
		}
		
		
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			float startX = e2.getX() + distanceX;
			float startY = e2.getY() + distanceY;
			
			if(selectedDrawable != null) {
				CodeMapCursorPoint point = new CodeMapCursorPoint(startX, startY);
				selectedDrawable.setPositionCenter(point.getCodeMapPoint(codeMapView));
				codeMapView.refresh();
			}
			else
				scroller.startScroll((int) startX, (int) startY, (int) distanceX,
						(int) distanceY);
			return true;
		}

		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			// TODO Implement
			return false;
		}
	}
	
	
	public static class CodeMapScaleListener implements OnScaleGestureListener {
		
		private CodeMapView codeMapView;
		private float initialZoom = 1;

		public CodeMapScaleListener(CodeMapView codeMapView) {
			this.codeMapView = codeMapView;
		}
		
		public void onScaleEnd(ScaleGestureDetector detector) {			
		}
		
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			initialZoom = codeMapView.getZoom();
			return true;
		}
		
		public boolean onScale(ScaleGestureDetector detector) {
			float currentZoom = detector.getScaleFactor();
			if(Math.abs(currentZoom - codeMapView.getZoom()) > 0.05) {
				CodeMapCursorPoint point = new CodeMapCursorPoint(detector.getFocusX(), detector.getFocusY());
				codeMapView.setZoom(currentZoom, point.getCodeMapPoint(codeMapView));
			}
			return false;
		}
	};
}
