package com.hdweiss.codemap.view.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hdweiss.codemap.R;
import com.hdweiss.codemap.util.CodeMapPoint;
import com.hdweiss.codemap.view.codemap.CodeMapView;

public abstract class CodeMapItem extends LinearLayout {

	private TextView titleView;
	private ImageButton removeButton;
	
	private View content;
	
	public CodeMapItem(Context context, AttributeSet attrs) {
		this(context, attrs, "");
	}
	
	public CodeMapItem(Context context, AttributeSet attrs, String name) {
		super(context, attrs);
		
		titleView = (TextView) findViewById(R.id.title);
		titleView.setText(name);
		
		removeButton = (ImageButton) findViewById(R.id.remove);
		removeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {				
				remove();
			}
		});
	}

	

	protected void setContentView(View view) {
		this.content = view;
		addView(content);
	}
	

	public void setPosition(CodeMapPoint point) {
		setX(point.x);
		setY(point.y);
	}
	
	public CodeMapPoint getPosition() {
		return new CodeMapPoint(getX(), getY());
	}

	public void setPositionCenter(CodeMapPoint point) {
		float startX = point.x - (getWidth() / 2);
		float startY = point.y - (getHeight() / 2);
		setX(startX);
		setY(startY);
	}
	
	public Rect getBounds() {
		final int top = (int) getY();
		final int bottom = top + getHeight();

		final int left = (int) getX();
		final int right = left + getWidth();
				
		return new Rect(left, top, right, bottom);
	}

	public boolean contains(CodeMapPoint point) {
//		Log.d("CodeMap", "point : [" + getX() + " < " + point.x + " < "
//				+ (getX() + getWidth()) + "] [" + getY() + " < " + point.y
//				+ " < " + (getY() + getHeight()) + "]");
		if (point.x >= getX() && point.x <= getX() + getWidth()
				&& point.y >= getY() && point.y <= getY() + getHeight()) {
			Log.d("CodeMap", "match!");
			return true;
		}
		else
			return false;
	}

//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//
//		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//
//		float parentScale = ((CodeMapView) getParent()).getZoom();
//		
//		measureChildren(widthMeasureSpec, heightMeasureSpec);
//		
//		int chosenHeight, chosenWidth;
//		
////		if (parentScale > 1.0f) {
//			chosenWidth = (int) (parentScale * (float)widthSize);
//			chosenHeight = (int) (parentScale * (float)heightSize);
////		} 
////		else 
////		{
////			chosenHeight = heightSize;
////			chosenWidth = widthSize;
////		}
//			
//	//	super.onMeasure((int)(widthMeasureSpec * parentScale), (int)(heightMeasureSpec * parentScale));
////		chosenHeight = (int)((float)getMeasuredHeight() * parentScale);
////		chosenWidth = (int)((float)getMeasuredWidth() * parentScale);
//				
//		
//		Log.d("CodeMap", "Measured " + titleView.getText() + " to " + chosenWidth + "x" + chosenHeight);
//		setMeasuredDimension(chosenWidth, chosenHeight);
//	}
	

	@Override
	protected void onDraw(Canvas canvas) {
		float parentScale = ((CodeMapView) getParent()).getZoom();
		
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
	    canvas.scale(parentScale, parentScale);
	    super.dispatchDraw(canvas);
	    canvas.restore();	
	}
	
	
	public void remove() {
		//codeMapView.remove(this);
	}
	
	public String getName() {
		return this.titleView.getText().toString();
	}
}
