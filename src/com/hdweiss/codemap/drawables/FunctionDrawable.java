package com.hdweiss.codemap.drawables;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;

public class FunctionDrawable extends ShapeDrawable {

	private String mTitle;
	
	public FunctionDrawable(Context context, String title) {
		super();
		mTitle = title;
		
		float[] outerR = new float[] { 6, 6, 6, 6, 0, 0, 0, 0 };
        RectF inset = new RectF(3, 3, 3, 3);
        setShape(new RoundRectShape(outerR, inset, null));
        getPaint().setColor(0xFFFF0000);
        setBounds(100, 100, 200, 200);
	}
		
	public void setText(String message) {
		mTitle = message;
	}
	
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		canvas.drawText(mTitle, getBounds().left + 10,
				getBounds().top + 15, getPaint());
	}
	
	public void drawWithOffset(Canvas canvas, int x, int y) {
		Rect bounds = getBounds();
		bounds.left += x;
		bounds.right += x;
		bounds.top += y;
		bounds.bottom += y;
		
		setBounds(bounds);
		draw(canvas);
	}
	
	public void drawAt(Canvas canvas, int x, int y) {
		Rect bounds = getBounds();
		bounds.left = x;
		bounds.right = x + 100;
		bounds.top = y;
		bounds.bottom = y + 100;
		
		setBounds(bounds);
		draw(canvas);
	}

	public void setXY(float x, float y) {
		final int left = (int) (x - getShape().getHeight() / 2);
		final int top = (int) (y - getShape().getWidth() / 2);

		final int right = (int) (x + getShape().getHeight() / 2);
		final int bottom = (int) (y + getShape().getWidth() / 2);

		setBounds(left, top, right, bottom);
	}
}
