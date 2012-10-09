package com.hdweiss.codemap.drawables;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.hdweiss.codemap.view.CodeMapLayout;

public class FunctionView extends LinearLayout {

	private WebView webView;
	private Rect position = new Rect();

	public FunctionView(Context context, int x, int y) {
		super(context);
		
		webView = getWebView(getContext());

		setXY(x, y, 1);

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
	

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawText("hej", position.left + webView.getWidth(), position.top, new Paint());
		webView.draw(canvas);
	}


	public void setXY(int startX, int startY, float zoom) {
		setX(startX);
		setY(startY);
		webView.setScaleX(zoom);
		webView.setScaleY(zoom);
	}
}
