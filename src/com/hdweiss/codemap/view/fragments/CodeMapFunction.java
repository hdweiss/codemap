package com.hdweiss.codemap.view.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hdweiss.codemap.R;
import com.hdweiss.codemap.util.CodeMapPoint;
import com.hdweiss.codemap.util.SpanUtils;
import com.hdweiss.codemap.view.codemap.CodeMapView;
import com.hdweiss.codemap.view.fragments.FunctionLinkSpan.FunctionLinkSpanConverter;

public class CodeMapFunction extends LinearLayout {

	private TextView titleView;
	private TextView sourceView;
	private CodeMapView codeMapView;
	
	public CodeMapFunction(Context context) {
		this(context, new CodeMapPoint(0, 0), "", new SpannableString(""), null);
	}
	
	public CodeMapFunction(Context context, CodeMapPoint point, String name,
			SpannableString content, CodeMapView codeMapView) {
		super(context);
		
		this.codeMapView = codeMapView;
		
		inflate(getContext(), R.layout.codemap_fragment, this);
		titleView = (TextView) findViewById(R.id.title);
		sourceView = (TextView) findViewById(R.id.source);
		ImageButton removeButton = (ImageButton) findViewById(R.id.remove);
		removeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {				
				remove();
			}
		});
		
		init(name, content);
		setPosition(point);
	}
	
	private void init(String name, SpannableString content) {
		titleView.setText(name);

		sourceView.setText(content);
		
		Spannable span = SpanUtils.replaceAll(content,
				URLSpan.class, new FunctionLinkSpanConverter(this));
		
		sourceView.setText(span);
		sourceView.setLinksClickable(true);
		sourceView.setMovementMethod(LinkMovementMethod.getInstance());
	}
	

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);

		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		float parentScale = ((CodeMapView) getParent()).getZoom();
		
		measureChildren(widthMeasureSpec, heightMeasureSpec);
		
		int chosenHeight, chosenWidth;
		
//		if (parentScale > 1.0f) {
			chosenWidth = (int) (parentScale * (float)widthSize);
			chosenHeight = (int) (parentScale * (float)heightSize);
//		} 
//		else 
//		{
//			chosenHeight = heightSize;
//			chosenWidth = widthSize;
//		}
			
	//	super.onMeasure((int)(widthMeasureSpec * parentScale), (int)(heightMeasureSpec * parentScale));
//		chosenHeight = (int)((float)getMeasuredHeight() * parentScale);
//		chosenWidth = (int)((float)getMeasuredWidth() * parentScale);
		
		measureChild(sourceView, widthMeasureSpec, heightMeasureSpec);
		measureChild(titleView, widthMeasureSpec, heightMeasureSpec);
		
		
		
		Log.d("CodeMap", "Measured " + titleView.getText() + " to " + chosenWidth + "x" + chosenHeight);
		setMeasuredDimension(chosenWidth, chosenHeight);
	}
	

	@Override
	protected void onDraw(Canvas canvas) {
		float parentScale = ((CodeMapView) getParent()).getZoom();
		
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
	    canvas.scale(parentScale, parentScale);
	    super.dispatchDraw(canvas);
	    canvas.restore();	
	}

	public void openNewFragment(String url) {
		this.codeMapView.openFragmentFromUrl(url, this);
	}

	public void setPosition(CodeMapPoint point) {
		setX(point.x);
		setY(point.y);
	}
	
	public CodeMapPoint getPosition() {
		Log.d("CodeMap", "getPosition():" + titleView.getText().toString() + " @ " + getX() + ":" + getY());
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
	
	public void remove() {
		codeMapView.remove(this);
	}
	
	public String getName() {
		return this.titleView.getText().toString();
	}
}
