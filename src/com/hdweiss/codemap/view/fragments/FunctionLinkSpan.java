package com.hdweiss.codemap.view.fragments;

import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;

import com.hdweiss.codemap.util.SpanUtils.OnClickListener;
import com.hdweiss.codemap.util.SpanUtils.SpanConverter;

public class FunctionLinkSpan extends ClickableSpan {

	private OnClickListener mListener;

	public FunctionLinkSpan(OnClickListener listener) {
		this.mListener = listener;
	}

	@Override
	public void onClick(View widget) {
		if (mListener != null)
			mListener.onClick();
	}

	
	public static class SpanClickListener implements OnClickListener {

		private CodeMapFunction codeMapFunction;
		private String url;

		public SpanClickListener (CodeMapFunction codeMapFunction, String url) {
			this.codeMapFunction = codeMapFunction;
			this.url = url;
		}
		
		public void onClick() {
			codeMapFunction.openNewFragment(url);
		}
	}
	
	
	public static class FunctionLinkSpanConverter implements
			SpanConverter<URLSpan, FunctionLinkSpan> {

		private CodeMapFunction codeMapFunction;

		public FunctionLinkSpanConverter(CodeMapFunction codeMapFunction) {
			this.codeMapFunction = codeMapFunction;
		}

		public FunctionLinkSpan convert(URLSpan span) {
			SpanClickListener listener = new SpanClickListener(
					codeMapFunction, span.getURL());
			return (new FunctionLinkSpan(listener));
		}
	}
}
