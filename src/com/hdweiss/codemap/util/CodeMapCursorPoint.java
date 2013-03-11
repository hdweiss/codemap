package com.hdweiss.codemap.util;

import com.hdweiss.codemap.view.workspace.WorkspaceView;

import android.graphics.PointF;

/**
 * Wrapper for points that are clicked on canvas.
 */
public class CodeMapCursorPoint extends PointF {
	
	public CodeMapCursorPoint() {
		super();
	}
	
	public CodeMapCursorPoint(float x, float y) {
		super(x, y);
	}
	
	public CodeMapPoint getCodeMapPoint(WorkspaceView codeMapView) {
		CodeMapPoint transformedPoint = new CodeMapPoint();
		transformedPoint.x = (x + codeMapView.getScrollX()) / codeMapView.getScaleFactor();
		transformedPoint.y = (y + codeMapView.getScrollY()) / codeMapView.getScaleFactor();
		return transformedPoint;
	}
}
