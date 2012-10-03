package com.hdweiss.codemap.drawables;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.MotionEvent;
import android.view.View;

public class FunctionDrawable extends View {

	private String mTitle;
	private ShapeDrawable drawable = new ShapeDrawable();
	
	public FunctionDrawable(Context context, String title) {
		super(context);
		mTitle = title;
		
		float[] outerR = new float[] { 6, 6, 6, 6, 0, 0, 0, 0 };
        RectF inset = new RectF(3, 3, 3, 3);
        drawable.setShape(new RoundRectShape(outerR, inset, null));
        drawable.getPaint().setColor(0xFFFF0000);
        drawable.setBounds(100, 100, 200, 200);
        
        setFocusable(true);
	}
		
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawable.draw(canvas);
		canvas.drawText(mTitle, drawable.getBounds().left + 10,
				drawable.getBounds().top + 15, drawable.getPaint());
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			setXY(event.getX(), event.getY());
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
		}
		return true;
	}

	public void setXY(float x, float y) {
		final int left = (int) (x - drawable.getShape().getHeight() / 2);
		final int top = (int) (y - drawable.getShape().getWidth() / 2);

		final int right = (int) (x + drawable.getShape().getHeight() / 2);
		final int bottom = (int) (y + drawable.getShape().getWidth() / 2);

		drawable.setBounds(left, top, right, bottom);
	}
}
