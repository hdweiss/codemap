package com.hdweiss.codemap.view.fragments;

import android.content.Context;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.hdweiss.codemap.util.CodeMapPoint;
import com.hdweiss.codemap.util.SpanUtils;
import com.hdweiss.codemap.view.codemap.CodeMapView;
import com.hdweiss.codemap.view.fragments.FunctionLinkSpan.FunctionLinkSpanConverter;

public class CodeMapFunction extends CodeMapItem {

	private TextView sourceView;
	private CodeMapView codeMapView;
	
	public CodeMapFunction(Context context, AttributeSet attrs) {
		this(context, new CodeMapPoint(0, 0), "", new SpannableString(""), null);
	}
	
	public CodeMapFunction(Context context, CodeMapPoint point, String name,
			SpannableString content, CodeMapView codeMapView) {
		super(context, null, name);
		
		this.codeMapView = codeMapView;
		
		sourceView = new TextView(getContext());
		setContentView(sourceView);
		
		init(name, content);
		setPosition(point);
	}
	
	private void init(String name, SpannableString content) {
		sourceView.setText(content);
		
		Spannable span = SpanUtils.replaceAll(content,
				URLSpan.class, new FunctionLinkSpanConverter(this), sourceView);
		
		sourceView.setText(span);
		sourceView.setLinksClickable(true);
		sourceView.setMovementMethod(LinkMovementMethod.getInstance());
		
		setupSpanLinksOffsets(span, sourceView);
	}
	
	
	public static void setupSpanLinksOffsets(Spannable totalSpan, TextView sourceView) {
		FunctionLinkSpan[] spans = totalSpan.getSpans(0, totalSpan.length(), FunctionLinkSpan.class);
		for (FunctionLinkSpan spanInst : spans) {
			int endOffset = totalSpan.getSpanEnd(spanInst);
			Log.d("CodeMap", "setting up span @" + endOffset);

			Layout layout = sourceView.getLayout();
			float secondaryHorizontal = layout.getSecondaryHorizontal(endOffset);
			((FunctionLinkSpan) totalSpan).setYOffset(secondaryHorizontal);
		}
	}

	public void addChildFragment(String url, float yOffset) {
		this.codeMapView.openFragmentFromUrl(url, this, yOffset);
	}
}
