package com.hdweiss.codemap.view.fragments;

import android.content.Context;
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

	public void openNewFragment(String url) {
		this.codeMapView.openFragmentFromUrl(url, this);
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
