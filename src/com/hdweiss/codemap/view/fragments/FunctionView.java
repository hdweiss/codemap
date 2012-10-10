package com.hdweiss.codemap.view.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hdweiss.codemap.R;

public class FunctionView extends LinearLayout {

	private TextView titleView;
	private TextView sourceView;
	
	public FunctionView(Context context) {
		this(context, 0, 0);
	}
	
	public FunctionView(Context context, float x, float y) {
		super(context);
		setOrientation(LinearLayout.VERTICAL);

		inflate(getContext(), R.layout.map_fragment, this);
		titleView = (TextView) findViewById(R.id.title);
		sourceView = (TextView) findViewById(R.id.source);
		
		init();
		setXY(x, y);
	}
	
	private void init() {
		titleView.setText("main()");
		
		sourceView.setLinksClickable(true);
		
		SpannableString spannableString = new SpannableString(Html.fromHtml(
				"void main() { <br>\n int i = <a href=\"http://www.google.com\">func</a>;<br>"
						+ "i++; <br>}"));

		spannableString.setSpan(new ForegroundColorSpan(Color.RED), 5, 9, 0);
		
		sourceView.setText(spannableString);
		sourceView.setMovementMethod(LinkMovementMethod.getInstance());
	}


	public void setXY(float startX, float startY) {
		setX(startX);
		setY(startY);
	}

	public void setCenterPoint(float x, float y) {
		float startX = x - (getWidth() / 2);
		float startY = y - (getHeight() / 2);
		setX(startX);
		setY(startY);
	}

	public boolean contains(PointF point, float zoom) {
		if (point.x >= getX() && point.x <= getX() + getWidth()
				&& point.y >= getY() && point.y <= getY() + getHeight())
			return true;
		else
			return false;
	}
}
