package com.hdweiss.codemap.drawables;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;

public class FunctionDrawable extends ShapeDrawable {

	private Rect position = new Rect();
	private String mTitle;
	
	public FunctionDrawable(Context context, String title, int x, int y) {
		super();
		mTitle = title;

		float[] outerR = new float[] { 6, 6, 6, 6, 0, 0, 0, 0 };
		RectF inset = new RectF(3, 3, 3, 3);
		setShape(new RoundRectShape(outerR, inset, null));
		getPaint().setColor(0xFFFF0000);
		
		setBounds(0, 0, 100, 100); // Set initial size
		
		setXY(x, y);
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
		Rect bounds = new Rect();
		bounds.left = position.left + x;
		bounds.right = position.right + x;
		bounds.top = position.top + y;
		bounds.bottom = position.bottom + y;
		
		setBounds(bounds);
		draw(canvas);
	}

	public void setXY(float x, float y) {
		position.left = (int) (x - getShape().getHeight() / 2);
		position.right = (int) (x + getShape().getHeight() / 2);

		position.top = (int) (y - getShape().getWidth() / 2);
		position.bottom = (int) (y + getShape().getWidth() / 2);
		
		setBounds(position);
	}
}
