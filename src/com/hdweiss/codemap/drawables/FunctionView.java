package com.hdweiss.codemap.drawables;

import com.hdweiss.codemap.CodeMapLayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.webkit.WebView;
import android.widget.LinearLayout;

public class FunctionView extends LinearLayout {

	private WebView webView;
	private Rect position = new Rect();

	public FunctionView(Context context, int x, int y) {
		super(context);
		
		webView = getWebView(getContext(), x, y);
				
		setX(x);
		setY(y);
		
//		setXY2(x, y, 1);
//		setXY(x, y, 1);
		addView(webView);
	}
	
	
	public static WebView getWebView(Context context, int x, int y) {
		WebView webView = new WebView(context);
		String html = "<html><body><font color='" + "black" + "'>"
				+ "<a href=\"www.slashdot.org\">hellllllo!</a>"
				+ "</font></body></html>";
		webView.setClickable(true);
		webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
		
		webView.setLayoutParams(new CodeMapLayout.LayoutParams(100, 100, x, y));
		
//		setFocusable(true);
//		setFocusableInTouchMode(true);
//		webView.setFocusable(true);
//		webView.setFocusableInTouchMode(true);
		
		return webView;
	}
	
//	int height = 200;
//	int width = 200;
//
//	public void setXY2(float x, float y, float zoom) {
//		setLeft( (int) (x / zoom - height / 2));
//		setRight ((int) (x / zoom + height / 2));
//
//		setTop ((int) (y / zoom - width / 2));
//		setBottom ((int) (y / zoom + width / 2));
//	}
//	
//
//	public void setXY(float x, float y, float zoom) {
//		position.left = (int) (x / zoom - height / 2);
//		position.right = (int) (x / zoom + height / 2);
//
//		position.top = (int) (y / zoom - width / 2);
//		position.bottom = (int) (y / zoom + width / 2);
//				
////		webView.layout(position.left, position.top, position.right, position.bottom);
////		webView.setX(position.left + 50);
////		webView.setY(position.top + 30);
////		webView.setScaleX(zoom);
////		webView.setScaleY(zoom);
//	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawText("hej", position.left + webView.getWidth(), position.top, new Paint());
		

		//webView.draw(canvas);
	}


	public void setXY(int startX, int startY, float zoom) {
		setX(startX);
		setY(startY);
	}
}
