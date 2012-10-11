package com.hdweiss.codemap.view.fragments;

import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hdweiss.codemap.R;
import com.hdweiss.codemap.util.CodeMapPoint;
import com.hdweiss.codemap.util.SpanUtils;
import com.hdweiss.codemap.view.CodeMapView;
import com.hdweiss.codemap.view.fragments.FunctionLinkSpan.FunctionLinkSpanConverter;

public class CodeMapFunction extends LinearLayout {

	private TextView titleView;
	private TextView sourceView;
	private CodeMapView codeMapView;
	
	public CodeMapFunction(Context context) {
		this(context, new CodeMapPoint(0, 0), "", "", null);
	}
	
	public CodeMapFunction(Context context, CodeMapPoint point, String name, String content, CodeMapView codeMapView) {
		super(context);
		
		this.codeMapView = codeMapView;
		
		inflate(getContext(), R.layout.map_fragment, this);
		titleView = (TextView) findViewById(R.id.title);
		sourceView = (TextView) findViewById(R.id.source);
		
		init(name, content);
		setPosition(point);
	}
	
	private void init(String name, String content) {
		titleView.setText(name);

		SpannableString spannableString = new SpannableString(
				Html.fromHtml(content));
		sourceView.setText(spannableString);
		sourceView.setLinksClickable(true);
		sourceView.setMovementMethod(LinkMovementMethod.getInstance());

		Spannable span = SpanUtils.replaceAll((Spanned) sourceView.getText(),
				URLSpan.class, new FunctionLinkSpanConverter(this));
		sourceView.setText(span);
	}

	public void openNewFragment(String url) {
		this.codeMapView.openFunctionFromFragment(url, this);
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
