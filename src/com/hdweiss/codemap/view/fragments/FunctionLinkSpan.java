package com.hdweiss.codemap.view.fragments;

import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;

import com.hdweiss.codemap.util.SpanUtils.OnClickListener;
import com.hdweiss.codemap.util.SpanUtils.SpanConverter;

public class FunctionLinkSpan extends ClickableSpan implements OnClickListener {

	private CodeMapFunction codeMapFunction;
	private String url;
	
	private float yOffset;

	public FunctionLinkSpan(CodeMapFunction codeMapFunction, String url) {
		this.codeMapFunction = codeMapFunction;
		this.url = url;
	}

	public void setYOffset(float yOffset) {
		this.yOffset = yOffset;
	}


	@Override
	public void onClick(View widget) {
		onClick();
	}
	
	public void onClick() {
		codeMapFunction.addChildFragment(url, yOffset);
	}
	
	
	public static class FunctionLinkSpanConverter implements
			SpanConverter<URLSpan, FunctionLinkSpan> {

		private CodeMapFunction codeMapFunction;

		public FunctionLinkSpanConverter(CodeMapFunction codeMapFunction) {
			this.codeMapFunction = codeMapFunction;
		}

		public FunctionLinkSpan convert(URLSpan span) {
			return (new FunctionLinkSpan(codeMapFunction, span.getURL()));
		}
	}
}
