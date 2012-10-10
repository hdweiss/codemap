package com.hdweiss.codemap.view;

import android.graphics.PointF;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.widget.Scroller;

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

			PointF point = new PointF(e.getX(), e.getY());
			selectedDrawable = codeMapView.getDrawableFromPoint(point);
			
			return true;
		}
		
		public boolean onSingleTapUp(MotionEvent e) {
			PointF point = new PointF(e.getX(), e.getY());
			this.codeMapView.addFunction(point);
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
				selectedDrawable.setCenterPoint(startX, startY);
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
			
			if(Math.abs(zoom - codeMapView.getZoom()) > 0.05)
				codeMapView.setZoom(zoom, new PointF(0, 0));
			return false;
		}
	};
}
