package com.hdweiss.codemap.util;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.CharacterStyle;

public class SpanUtils {
	
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

	
}
