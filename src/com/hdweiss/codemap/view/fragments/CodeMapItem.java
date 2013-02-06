package com.hdweiss.codemap.view.fragments;

import java.util.UUID;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hdweiss.codemap.R;
import com.hdweiss.codemap.util.CodeMapPoint;
import com.hdweiss.codemap.util.Utils;
import com.hdweiss.codemap.view.codemap.CodeMapView;

public abstract class CodeMapItem extends LinearLayout {
	public UUID id = UUID.randomUUID();

	public TextView titleView;
	private ImageButton removeButton;
	private LinearLayout containerView;
	
	private View contentView;
	protected CodeMapView codeMapView;
	
	private boolean moveItem = true;
	
	public CodeMapItem(Context context, AttributeSet attrs, String name) {
		super(context, attrs);
		
		inflate(context, R.layout.codemap_item, this);
				
		containerView = (LinearLayout) findViewById(R.id.codemap_item_container);
		
		titleView = (TextView) findViewById(R.id.title);
		titleView.setText(name);
		
		removeButton = (ImageButton) findViewById(R.id.remove);
		removeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {				
				remove();
			}
		});

		this.post(Utils.getTouchDelegateAction(this, removeButton, 50, 50, 50, 50));
	}

	
	public void setCodeMapView(CodeMapView codeMapView) {
		this.codeMapView = codeMapView;
	}
	

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		if(this.codeMapView != null && moveItem) {
			this.moveItem = false;
						
			if (this.codeMapView != null)
				this.codeMapView.moveFragment(this);
		}
	}

	protected void setContentView(View view) {
		this.contentView = view;
		containerView.addView(view);
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
	
	public void push(Point offset) {
		setX(getX() + offset.x);
		setY(getY() + offset.y);
	}

	public boolean contains(CodeMapPoint point) {
//		Log.d("CodeMap", "point : [" + getX() + " < " + point.x + " < "
//				+ (getX() + getWidth()) + "] [" + getY() + " < " + point.y
//				+ " < " + (getY() + getHeight()) + "]");
		if (point.x >= getX() && point.x <= getX() + getWidth()
				&& point.y >= getY() && point.y <= getY() + getHeight()) {
			//Log.d("CodeMap", "match!");
			return true;
		}
		else
			return false;
	}

	public void remove() {
		if(codeMapView != null)
			codeMapView.remove(this);
	}
	
	public String getUrl() {
		return this.titleView.getText().toString();
	}
	
	public float getContentViewYOffset() {
		return titleView.getHeight() + titleView.getPaddingTop()
				+ titleView.getPaddingBottom() + contentView.getPaddingTop()
				+ contentView.getPaddingBottom() + 5;
	}
	
	public float getTitleViewYMid() {
		return this.getY() + (titleView.getHeight() / 2);
	}
}
