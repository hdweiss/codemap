package com.hdweiss.codemap.view.fragments;

import java.util.UUID;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hdweiss.codemap.R;
import com.hdweiss.codemap.util.CodeMapPoint;
import com.hdweiss.codemap.util.ZoomableLinearLayout;
import com.hdweiss.codemap.view.codemap.CodeMapView;

public abstract class CodeMapItem extends ZoomableLinearLayout {
	public UUID id = UUID.randomUUID();

	private TextView titleView;
	private ImageButton removeButton;
	
	private View contentView;
	protected CodeMapView codeMapView;
	
	private boolean moveItem = true;

	
	public CodeMapItem(Context context, AttributeSet attrs, String name) {
		super(context, attrs);
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		setLayoutParams(params);
		
		setOrientation(VERTICAL);
		setBackgroundResource(R.drawable.codebrowser_bg);
		setPadding(5, 5, 5, 5);
		
		
		inflate(context, R.layout.codemap_item, this);
		
		titleView = (TextView) findViewById(R.id.title);
		titleView.setText(name);
		
		removeButton = (ImageButton) findViewById(R.id.remove);
		removeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {				
				remove();
			}
		});
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
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		view.setLayoutParams(params);
		addView(contentView);
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
