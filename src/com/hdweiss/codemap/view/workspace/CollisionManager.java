package com.hdweiss.codemap.view.workspace;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import com.hdweiss.codemap.view.workspace.fragments.CodeMapItem;

public class CollisionManager {

	private static final int padding = 5;
	
	public static boolean pushItems(CodeMapItem pushingItem,
			ArrayList<CodeMapItem> items) {
		Log.d("Collision", "-> pushItems(): " + pushingItem.getUrl());	
		
		for (CodeMapItem item : items) {
			if (item == pushingItem)
				continue;

			Point pushOffset = getPushOffset(pushingItem.getBounds(),
					item.getBounds(), padding, true);
			
			if (pushOffset.equals(0, 0) == false) {
				item.push(pushOffset);
				Log.d("Collision", "pushItems(): pushed " + item.getUrl());	
				fixPush(item, new ArrayList<CodeMapItem>(items), pushOffset);
			}
		}

		return true;
	}
	
	private static void fixPush(CodeMapItem pushingItem,
			ArrayList<CodeMapItem> items, Point pushOffset) {
		items.remove(pushingItem);
		Iterator<CodeMapItem> iterator = items.iterator();
		
		while (iterator.hasNext()) {
			CodeMapItem item;
			try {
				item = iterator.next();
			} catch (ConcurrentModificationException e) {
				Log.e("Collision", "Collision manager caught ConcurrentModificationException");
				return;
			}
			if (pushingItem == item)
				continue;
			
			if (Rect.intersects(pushingItem.getBounds(), item.getBounds())) {
				Log.d("Collision", "fixPush(): " + pushingItem.getUrl() + " collided " + item.getUrl());
				item.push(pushOffset);
				iterator.remove();
				fixPush(item, items, pushOffset);
			}
		}
	}
	
	
	public static Point getPushOffset(Rect pusher, Rect pushed) {
		return getPushOffset(pusher, pushed, 0, true);
	}
	
	public static Point getPushOffset(Rect pusher, Rect pushed, int padding,
			boolean allowXPush) {
		if (Rect.intersects(pusher, pushed) == false)
			return new Point(0, 0);
		
		int pushUp = Math.abs(pusher.top - pushed.bottom);
		int pushDown = Math.abs(pusher.bottom - pushed.top);
		int pushRight = Math.abs(pusher.right - pushed.left);
		int pushLeft = Math.abs(pusher.left - pushed.right);
		
		int pushX;
		int pushY;
		
		if (pushUp < pushDown)
			pushY = -pushUp;
		else
			pushY = pushDown;
		
		if (pushLeft < pushRight)
			pushX = -pushLeft;
		else
			pushX = pushRight;
		
		if (pushX == 0 && pushY == 0)
			return new Point(0, 0);
		
		if (allowXPush && Math.abs(pushX) < Math.abs(pushY))
			return new Point(pushX + padding, 0);
		else
			return new Point(0, pushY + padding);
	}

}
