package com.hdweiss.codemap.view.fragments;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.EditText;

public class CodeMapAnnotation extends CodeMapItem {

	public CodeMapAnnotation(Context context) {
		this(context, null);
	}
	
	public CodeMapAnnotation(Context context, AttributeSet attrs) {
		super(context, attrs, "Annotation");
		
		EditText editText = new EditText(getContext());
		editText.setBackgroundColor(Color.YELLOW);
		
		setContentView(editText);
	}

}
