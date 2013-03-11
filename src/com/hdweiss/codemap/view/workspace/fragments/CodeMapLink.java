package com.hdweiss.codemap.view.workspace.fragments;

import android.graphics.Canvas;
import android.graphics.Paint;

public class CodeMapLink {

	public CodeMapItem parent;
	public CodeMapItem child;
	
	public float yOffset = 0;

	public CodeMapLink(CodeMapItem parent, CodeMapItem child, float yOffset) {
		this.parent = parent;
		this.child = child;
		this.yOffset = yOffset;
	}
	
	public boolean hasItem(CodeMapItem item) {
		return parent == item || child == item;
	}
	
	public void doDraw(Canvas canvas) {
		Paint paint = new Paint();
		
		float startX = parent.getX() + parent.getWidth();
		float startY = parent.getY() + yOffset;
		
		float endX = child.getX();
		float endY = child.getTitleViewYMid();

		float midX = (startX + endX) / 2;
		
		canvas.drawLine(startX, startY, midX, startY, paint);
		canvas.drawLine(midX, startY, midX, endY, paint);
		canvas.drawLine(midX, endY, endX, endY, paint);
	}
	
	public String toString() {
		return parent.getUrl() + "->" + child.getUrl();
	}
}
