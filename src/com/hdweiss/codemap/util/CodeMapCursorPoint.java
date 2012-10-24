package com.hdweiss.codemap.util;

import com.hdweiss.codemap.view.codemap.CodeMapView;

import android.graphics.PointF;

public class CodeMapCursorPoint extends PointF {
	
	public CodeMapCursorPoint() {
		super();
	}
	
	public CodeMapCursorPoint(float x, float y) {
		super(x, y);
	}
	
	public CodeMapPoint getCodeMapPoint(CodeMapView codeMapView) {
		CodeMapPoint transformedPoint = new CodeMapPoint();
		transformedPoint.x = (x + codeMapView.getScrollX()) / codeMapView.getZoom();
		transformedPoint.y = (y + codeMapView.getScrollY()) / codeMapView.getZoom();
		return transformedPoint;
	}
}
