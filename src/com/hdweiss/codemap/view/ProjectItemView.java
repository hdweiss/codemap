package com.hdweiss.codemap.view;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hdweiss.codemap.R;

public class ProjectItemView extends LinearLayout {

	private TextView titleView;
	private TextView statusView;
	private TextView urlView;
	private ProgressBar progressBar;

	public ProjectItemView(Context context) {
		super(context);

		inflate(context, R.layout.project_item, this);

		this.titleView = (TextView) findViewById(R.id.project_name);
		this.statusView = (TextView) findViewById(R.id.project_status);
		this.urlView = (TextView) findViewById(R.id.project_url);
		this.progressBar = (ProgressBar) findViewById(R.id.project_progress);
	}

	public void setName(String name) {
		titleView.setText(name);
	}

	public void setUrl(String url) {
		urlView.setText(url);
		urlView.setVisibility(VISIBLE);
	}
	
	public void beginUpdate() {
		statusView.setText("");
		statusView.setVisibility(VISIBLE);
		progressBar.setProgress(0);
		progressBar.setVisibility(VISIBLE);
		progressBar.setIndeterminate(true);
	}
	
	public void endUpdate() {
		statusView.setVisibility(INVISIBLE);
		progressBar.setVisibility(INVISIBLE);
	}
	
	public void setStatus(String status) {
		statusView.setText(status);
	}

	public void setProgress(int progress) {
		progressBar.setIndeterminate(false);
		progressBar.setProgress(progress);
	}

}
