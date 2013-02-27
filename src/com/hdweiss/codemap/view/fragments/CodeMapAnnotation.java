package com.hdweiss.codemap.view.fragments;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import com.hdweiss.codemap.util.CodeMapPoint;

public class CodeMapAnnotation extends CodeMapItem {

	private EditText editText;
	
	public CodeMapAnnotation(Context context, AttributeSet attrs) {
		this(context, null, "");
	}

	public CodeMapAnnotation(Context context, CodeMapPoint point, String contents) {
		super(context, null, "Annotation");
		
		this.editText = new EditText(getContext());
		this.editText.setMaxWidth(300);
		setContentView(editText);
		
		init(contents);
		setPosition(point);
		setupForAnnotation();
	}
	
	public void init(String contents) {
		if (contents.isEmpty())
			editText.setHint("Click to add note");
		else
			editText.setText(contents);
	}
	
	public String getContents() {
		return editText.getText().toString();
	}
}
