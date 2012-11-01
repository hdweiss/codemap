package com.hdweiss.codemap.util;

import java.io.Serializable;

import android.graphics.PointF;

public class CodeMapPoint extends PointF implements Serializable {
	private static final long serialVersionUID = 1L;

	public CodeMapPoint() {
		super();
	}
	
	public CodeMapPoint(float x, float y) {
		super(x, y);
	}
}
