package com.hdweiss.codemap.view.codemap;

import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.widget.Scroller;

import com.hdweiss.codemap.util.CodeMapCursorPoint;
import com.hdweiss.codemap.util.CodeMapPoint;
import com.hdweiss.codemap.view.fragments.CodeMapItem;

public class CodeMapListeners {
	
	public static class CodeMapGestureListener implements OnGestureListener {
		private CodeMapView codeMapView;
		private Scroller scroller;
		
		private CodeMapItem selectedDrawable = null;
		private float dragYoffset = 0;
		private float dragXoffset = 0;
		
		public CodeMapGestureListener(CodeMapView codeMapView, Scroller scroller) {
			this.codeMapView = codeMapView;
			this.scroller = scroller;
		}
		
		public boolean onDown(MotionEvent e) {
			if (!scroller.isFinished())
				scroller.forceFinished(true);

			prepareDrag(e);
			return true;
		}
		
		private void prepareDrag(MotionEvent e) {
			CodeMapCursorPoint point = new CodeMapCursorPoint(e.getX(), e.getY());
			this.selectedDrawable = codeMapView.getMapFragmentAtPoint(point);
			
			if (this.selectedDrawable != null) {
				CodeMapPoint drawablePosition = selectedDrawable.getPosition();
				CodeMapPoint clickedPosition = point.getCodeMapPoint(codeMapView);
								
				dragXoffset = drawablePosition.x - clickedPosition.x;
				dragYoffset = drawablePosition.y - clickedPosition.y;
				Log.d("CodeMap", "Drag Offset - " + dragXoffset + ":" + dragYoffset);
			}
		}
		
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}
		
		public void onLongPress(MotionEvent e) {
			codeMapView.getController().addAnnotationView("");
		}

		public void onShowPress(MotionEvent e) {
		}
		
		
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			float startX = e2.getX() + distanceX;
			float startY = e2.getY() + distanceY;
			
			if(selectedDrawable != null) {
				CodeMapCursorPoint cursorPoint = new CodeMapCursorPoint(startX, startY);
				CodeMapPoint point = cursorPoint.getCodeMapPoint(codeMapView);
				point.offset(dragXoffset, dragYoffset);
				codeMapView.moveFragment(selectedDrawable, point);
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
			initialZoom = codeMapView.getScaleFactor();
			return true;
		}
		
		public boolean onScale(ScaleGestureDetector detector) {
			float currentZoom = detector.getScaleFactor() * initialZoom;
			CodeMapCursorPoint point = new CodeMapCursorPoint(detector.getFocusX(), detector.getFocusY());
			codeMapView.setScaleFactor(currentZoom, point.getCodeMapPoint(codeMapView));
			return false;
		}
	};
}
