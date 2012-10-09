package com.hdweiss.codemap.drawables;

import com.hdweiss.codemap.R;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.widget.LinearLayout;
import android.widget.TextView;

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


	public boolean contains(float x, float y, float zoom) {
		return false;
	}

	public void setZoom(float zoom) {
	}
}
