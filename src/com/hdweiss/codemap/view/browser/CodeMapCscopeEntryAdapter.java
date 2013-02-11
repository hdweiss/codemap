package com.hdweiss.codemap.view.browser;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hdweiss.codemap.data.CscopeEntry;

public class CodeMapCscopeEntryAdapter extends ArrayAdapter<CscopeEntry> {

	public CodeMapCscopeEntryAdapter(Context context) {
		super(context, android.R.layout.simple_list_item_1);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {				
		TextView listItem = (TextView) convertView;
		
		if (convertView == null)
			listItem = new TextView(getContext());

		CscopeEntry item = getItem(position);
		listItem.setText(item.name);
		listItem.setTextSize(18);
		listItem.setPadding(5, 5, 5, 5);
				
		return listItem;
	}
}
