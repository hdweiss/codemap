package com.hdweiss.codemap.view.fragments;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.hdweiss.codemap.util.CodeMapPoint;
import com.hdweiss.codemap.util.SpanUtils;
import com.hdweiss.codemap.view.fragments.FunctionLinkSpan.FunctionLinkSpanConverter;

public class CodeMapFunction extends CodeMapItem {

	private TextView sourceView;
	
	private float yOffset = 0;
	
	public CodeMapFunction(Context context, AttributeSet attrs) {
		this(context, new CodeMapPoint(0, 0), "", new SpannableString(""));
	}
	
	public CodeMapFunction(Context context, CodeMapPoint point, String name,
			SpannableString content) {
		super(context, null, name);
				
		sourceView = new TextView(getContext());
		setContentView(sourceView);
		
		init(name, content);
		setPosition(point);
	}
	
	public void init(String name, SpannableString content) {
		sourceView.setText(content);
		
		Spannable span = SpanUtils.replaceAll(content,
				URLSpan.class, new FunctionLinkSpanConverter(this), sourceView);
		
		sourceView.setText(span);
		sourceView.setLinksClickable(true);
		sourceView.setMovementMethod(LinkMovementMethod.getInstance());
		
		sourceView.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				yOffset = event.getY();
				return false;
			}
		});
	}

	public void addChildFragment(String url) {
		if (this.codeMapView != null) {
			this.codeMapView.getController().addChildFragmentFromUrl(url,
					this, yOffset);
			this.yOffset = 0;
		}
	}
}
