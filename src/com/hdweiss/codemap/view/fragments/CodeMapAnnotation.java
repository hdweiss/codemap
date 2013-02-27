package com.hdweiss.codemap.view.fragments;

import com.hdweiss.codemap.util.CodeMapPoint;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.EditText;

public class CodeMapAnnotation extends CodeMapItem {

	private EditText editText;
	
	public CodeMapAnnotation(Context context, AttributeSet attrs) {
		this(context, null, "");
	}

	public CodeMapAnnotation(Context context, CodeMapPoint point, String contents) {
		super(context, null, "Annotation");
		
		this.editText = new EditText(getContext());
		this.editText.setBackgroundColor(Color.YELLOW);
		this.editText.setMaxWidth(300);
		setContentView(editText);
		
		init(contents);
		setPosition(point);
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
