package com.hdweiss.codemap.view;

import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.widget.Scroller;

import com.hdweiss.codemap.util.CodeMapCursorPoint;
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
			int startX = (int) (e2.getX() + distanceX);
			int startY = (int) (e2.getY() + distanceY);
			
			if(selectedDrawable != null) {
				CodeMapCursorPoint point = new CodeMapCursorPoint(startX, startY);
				selectedDrawable.setPositionCenter(point.getCodeMapPoint(codeMapView));
			}
			else
				scroller.startScroll(startX, startY, (int) distanceX,
						(int) distanceY);
			return true;
		}

		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			return false;
		}
	}
	
	
	public static class CodeMapScaleListener implements OnScaleGestureListener {
		
		private CodeMapView codeMapView;

		public CodeMapScaleListener(CodeMapView codeMapView) {
			this.codeMapView = codeMapView;
		}
		
		public void onScaleEnd(ScaleGestureDetector detector) {			
		}
		
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			return true;
		}
		
		public boolean onScale(ScaleGestureDetector detector) {
			float zoom = detector.getScaleFactor();
			if(Math.abs(zoom - codeMapView.getZoom()) > 0.05) {
				CodeMapCursorPoint point = new CodeMapCursorPoint(detector.getFocusX(), detector.getFocusY());
				codeMapView.setZoom(zoom, point.getCodeMapPoint(codeMapView));
			}
			return false;
		}
	};
}
