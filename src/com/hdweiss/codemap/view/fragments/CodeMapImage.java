package com.hdweiss.codemap.view.fragments;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CodeMapImage extends CodeMapItem {

	public CodeMapImage(Context context) {
		this(context, null);
	}
	
	public CodeMapImage(Context context, AttributeSet attrs) {
		super(context, attrs, "Image");
		
		ImageView imageView = new ImageView(getContext());
		imageView.setImageResource(android.R.drawable.ic_dialog_email);

		setContentView(imageView);
	}
}
