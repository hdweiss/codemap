package com.hdweiss.codemap.view.fragments;

import android.graphics.Canvas;
import android.graphics.Paint;

public class CodeMapLink {

	private CodeMapItem parent;
	private CodeMapItem child;
	
	private float yOffset = 0;

	public CodeMapLink(CodeMapItem parent, CodeMapItem child, float yOffset) {
		this.parent = parent;
		this.child = child;
		this.yOffset = yOffset;
	}
	
	public void doDraw(Canvas canvas) {
		Paint paint = new Paint();
		
		float startX = parent.getX() + parent.getWidth();
		float startY = parent.getY() + yOffset;
		
		float endX = child.getX();
		float endY = child.getY();

		float midX = (startX + endX) / 2;
		
		canvas.drawLine(startX, startY, midX, startY, paint);
		canvas.drawLine(midX, startY, midX, endY, paint);
		canvas.drawLine(midX, endY, endX, endY, paint);
	}
	
	public String toString() {
		return parent.getName() + "->" + child.getName();
	}
}
