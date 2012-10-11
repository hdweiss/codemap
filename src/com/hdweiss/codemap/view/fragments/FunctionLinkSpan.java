package com.hdweiss.codemap.view.fragments;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.Toast;

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

	public interface OnClickListener {
		void onClick();
	}
	
	
	public static <A extends CharacterStyle, B extends CharacterStyle> Spannable replaceAll(
			Spanned original, Class<A> sourceType, SpanConverter<A, B> converter) {
		SpannableString result = new SpannableString(original);
		A[] spans = result.getSpans(0, result.length(), sourceType);

		for (A span : spans) {
			int start = result.getSpanStart(span);
			int end = result.getSpanEnd(span);
			int flags = result.getSpanFlags(span);

			result.removeSpan(span);
			result.setSpan(converter.convert(span), start, end, flags);
		}

		return (result);
	}

	public interface SpanConverter<A extends CharacterStyle, B extends CharacterStyle> {
		B convert(A span);
	}
	
	public static class URLSpanConverter implements SpanConverter<URLSpan, FunctionLinkSpan> {

		private Context context;

		public URLSpanConverter(Context context) {
			this.context = context;
		}
		
		public FunctionLinkSpan convert(URLSpan span) {
			FunctionClickListener listener = new FunctionClickListener(context, span.getURL());
			return (new FunctionLinkSpan(listener));
		}
	}
	
	public static class FunctionClickListener implements OnClickListener {

		private Context context;
		private String url;

		public FunctionClickListener (Context context, String url) {
			this.context = context;
			this.url = url;
		}
		
		public void onClick() {
			Toast.makeText(context, url, Toast.LENGTH_SHORT).show();
		}
		
	}
}
