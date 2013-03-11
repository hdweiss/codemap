package com.hdweiss.codemap.view.workspace.fragments;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.hdweiss.codemap.R;
import com.hdweiss.codemap.util.CodeMapPoint;
import com.hdweiss.codemap.util.SpanUtils;
import com.hdweiss.codemap.util.Utils;
import com.hdweiss.codemap.view.workspace.fragments.FunctionLinkSpan.FunctionLinkSpanConverter;

public class CodeMapFunction extends CodeMapItem {

	private TextView sourceView;
	
	private float yOffset = 0;

	private View scrollView;
	
	public CodeMapFunction(Context context, AttributeSet attrs) {
		this(context, new CodeMapPoint(0, 0), "", new SpannableString(""));
	}
	
	public CodeMapFunction(Context context, CodeMapPoint point, String name,
			SpannableString content) {
		super(context, null, name);
				
		scrollView = inflate(context, R.layout.codemap_function, null);
		sourceView = (TextView) scrollView.findViewById(R.id.codemap_function);
		sourceView.setTextSize(Utils.getSourceFontsize(getContext()));
		setContentView(scrollView);
		
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
		
		this.titleView.setText(name);
		makeItemMoveable();
	}

	public void addChildFragment(String url) {
		if (this.codeMapView != null) {
			this.codeMapView.getController().addChildFragmentFromUrl(url,
					this, yOffset);
			this.yOffset = 0;
		}
	}
	
	@Override
	public void setFontSize(int fontSize) {
		this.sourceView.setTextSize(fontSize);
	}
}

