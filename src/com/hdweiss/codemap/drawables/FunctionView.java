package com.hdweiss.codemap.drawables;

import android.content.Context;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hdweiss.codemap.view.CodeMapLayout;

public class FunctionView extends LinearLayout {

	private WebView webView;

	public FunctionView(Context context) {
		this(context, 0, 0);
	}
	
	public FunctionView(Context context, int x, int y) {
		super(context);
		setOrientation(LinearLayout.VERTICAL);

		init();
		setXY(x, y);
	}
	
	private void init() {
		TextView text = new TextView(getContext());
		text.setText("heeelllooooo!");
		addView(text);

		webView = getWebView(getContext());
		addView(webView);
	}
	
	
	private static WebView getWebView(Context context) {
		WebView webView = new WebView(context);
		webView.setLayoutParams(new CodeMapLayout.LayoutParams(100, 100, 0, 0));

		String html = "<html><body><font color='" + "black" + "'>"
				+ "<a href=\"www.slashdot.org\">hellllllo!</a>"
				+ "</font></body></html>";
		webView.setClickable(true);
		webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
		
		return webView;
	}


	public void setXY(int startX, int startY) {
		setX(startX);
		setY(startY);
	}


	public boolean contains(float x, float y, float zoom) {
		return false;
	}

	public void setZoom(float zoom) {
		webView.setScaleX(zoom);
		webView.setScaleY(zoom);
	}
}
