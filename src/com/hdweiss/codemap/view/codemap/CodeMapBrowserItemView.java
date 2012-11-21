package com.hdweiss.codemap.view.codemap;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hdweiss.codemap.R.color;
import com.hdweiss.codemap.view.codemap.CodeMapBrowserItem.TYPE;

public class CodeMapBrowserItemView extends LinearLayout {

	private TextView textView;
	
	public CodeMapBrowserItemView(Context context) {
		super(context);
		
		this.textView = new TextView(getContext());
		this.textView.setPadding(30, 5, 10, 5);
		addView(textView);
	}

	public void setItem(CodeMapBrowserItem item) {
		String text = item.name;
		if(item.type == TYPE.FILE) {
			if(text.lastIndexOf("/") >= 0)
				text = text.substring(text.lastIndexOf("/") + 1);
		}
		
		for(int i = 0; i < item.level; i++)
			text = "  " + text;
		
		this.textView.setText(text);
		
		switch(item.type) {
		case SYMBOL:
			this.textView.setTextColor(color.dark_blue);
			break;
		default:
			this.textView.setTextColor(color.black);
			break;
		}
	}
	
	public void setDirectory(boolean isDirectory) {
		if (isDirectory) {
			this.textView.setTextSize(15);
			this.textView.setTextColor(color.blue);
		} else {
			this.textView.setTextSize(14);
		}
	}
}
