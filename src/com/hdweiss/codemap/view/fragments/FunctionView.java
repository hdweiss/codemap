package com.hdweiss.codemap.view.fragments;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hdweiss.codemap.R;
import com.hdweiss.codemap.util.CodeMapPoint;

public class FunctionView extends LinearLayout {

	private TextView titleView;
	private TextView sourceView;
	
	public FunctionView(Context context) {
		this(context, new CodeMapPoint(0, 0));
	}
	
	public FunctionView(Context context, CodeMapPoint point) {
		super(context);
		setOrientation(LinearLayout.VERTICAL);

		inflate(getContext(), R.layout.map_fragment, this);
		titleView = (TextView) findViewById(R.id.title);
		sourceView = (TextView) findViewById(R.id.source);
		
		init();
		setPosition(point);
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


	public void setPosition(CodeMapPoint point) {
		setX(point.x);
		setY(point.y);
	}

	public void setPositionCenter(CodeMapPoint point) {
		float startX = point.x - (getWidth() / 2);
		float startY = point.y - (getHeight() / 2);
		setX(startX);
		setY(startY);
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
}
