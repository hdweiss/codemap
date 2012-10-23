package com.hdweiss.codemap.view;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CodeMapBrowserItem extends LinearLayout {

	private TextView textView;
	
	public CodeMapBrowserItem(Context context) {
		super(context);
		
		this.textView = new TextView(getContext());
		addView(textView);
	}

	public void setText(String text) {
		this.textView.setText(text);
	}
	
	public void setChild(boolean isChild) {
		if (isChild) {
			this.textView.setPadding(50, 5, 10, 5);
			this.textView.setTextSize(14);
		} else {
			this.textView.setPadding(40, 5, 10, 5);
			this.textView.setTextSize(15);
		}
	}
}
