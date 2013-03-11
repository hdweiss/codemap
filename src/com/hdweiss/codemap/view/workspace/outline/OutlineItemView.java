package com.hdweiss.codemap.view.workspace.outline;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hdweiss.codemap.R;
import com.hdweiss.codemap.R.color;
import com.hdweiss.codemap.view.workspace.outline.OutlineItem.TYPE;

public class OutlineItemView extends LinearLayout {

	private TextView textView;
	private TextView declarationView;
	
	public OutlineItemView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.outline_item, this);
		this.textView = (TextView) findViewById(R.id.browser_item);
		this.declarationView = (TextView) findViewById(R.id.browser_declare);
	}

	public void setItem(OutlineItem item) {
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
			this.textView.setTextColor(getResources().getColor(color.gray));
			setDeclarations(0);
			break;
		default:
			this.textView.setTextColor(getResources().getColor(color.black));
			break;
		}
	}
	
	public void setDeclarations(int numberOfDeclarations) {
		if (numberOfDeclarations > 0) {
			this.declarationView.setText(Integer.toString(numberOfDeclarations));
			this.declarationView.setVisibility(VISIBLE);
		} else
			this.declarationView.setVisibility(GONE);
	}
	
	public void setDirectory(boolean isDirectory) {
		if (isDirectory) {
			this.textView.setTextSize(15);
			this.textView.setTextColor(getResources().getColor(color.blue));
		} else {
			this.textView.setTextSize(14);
		}
	}
}
