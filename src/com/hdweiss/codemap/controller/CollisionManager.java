package com.hdweiss.codemap.controller;

import java.util.ArrayList;

import android.graphics.Point;
import android.graphics.Rect;

import com.hdweiss.codemap.view.fragments.CodeMapItem;

public class CollisionManager {

	private static final int padding = 5;
	
	
	public static boolean moveFragmentsToAllowItem(CodeMapItem masterItem,
			ArrayList<CodeMapItem> items) {		
		boolean foundEmpty = false;
		while (foundEmpty == false) {
			foundEmpty = true;
			for (CodeMapItem item : items) {
				if (item == masterItem)
					continue;
				
				Point pushOffset = getPushOffset(masterItem.getBounds(),
						item.getBounds(), padding, false);
				item.setX(item.getX() + pushOffset.x);
				item.setY(item.getY() + pushOffset.y);
			}
		}

		return true;
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
		
		
		if (allowXPush && Math.abs(pushX) < Math.abs(pushY))
			return new Point(pushX + padding, 0);
		else
			return new Point(0, pushY + padding);
	}

}
