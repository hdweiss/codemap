package com.hdweiss.codemap.controller;

import java.util.ArrayList;

import android.graphics.Rect;
import android.util.Log;

import com.hdweiss.codemap.view.fragments.CodeMapItem;

public class CollisionManager {

	private static final int padding = 5;

	public static boolean moveMapItemToEmptyPosition(CodeMapItem item,
			ArrayList<CodeMapItem> items) {
		Rect rect = item.getBounds();
		rect.bottom += 1;
		rect.right += 1;

		boolean foundEmpty = false;
		while (foundEmpty == false) {
			foundEmpty = true;
			for (CodeMapItem view : items) {
				// Log.d("CodeMap", "Comparing " + item.getBounds().toString() +
				// " " + view.getBounds().toString());
				if (view != item && Rect.intersects(view.getBounds(), rect)) {
					Log.d("CodeMap",
							item.getName() + " collieded with "
									+ view.getName());
					int height = rect.bottom - rect.top;
					rect.top = view.getBounds().bottom + padding;
					rect.bottom = rect.top + height;
					foundEmpty = false;
					break;
				}
			}
		}

		item.setX(rect.left);
		item.setY(rect.top);
		return true;
	}

	public static boolean moveFragmentsToAllowItem(CodeMapItem masterItem,
			ArrayList<CodeMapItem> items) {
		Rect masterBounds = masterItem.getBounds();

		boolean foundEmpty = false;
		while (foundEmpty == false) {
			foundEmpty = true;
			for (CodeMapItem item : items) {
				if (item != masterItem
						&& Rect.intersects(item.getBounds(), masterBounds)) {

					ArrayList<OVERLAPPING> overlaps = getOverlaps(masterBounds, item.getBounds());
					
					for(OVERLAPPING overlap: overlaps) {
						switch (overlap) {
						case RIGHT:
							Log.d("CodeMap", "Right!");
							item.setX(masterBounds.right + padding);
							break;
						case LEFT:
							Log.d("CodeMap", "Left!");
							item.setX(masterBounds.left + item.getWidth() + padding);
							break;
						case ABOVE:
							Log.d("CodeMap", "Above!");
							item.setY(masterBounds.top + padding);
							break;
						case BELOW:
							Log.d("CodeMap", "Below!");
							item.setY(masterBounds.bottom + item.getHeight() + padding);
							break;
						
						default:
							break;
						}
					}
					
					foundEmpty = false;
				}
			}
		}

		return true;
	}

	private enum OVERLAPPING {
		RIGHT, LEFT, ABOVE, BELOW
	};

	public static ArrayList<OVERLAPPING> getOverlaps(Rect rect1, Rect rect2) {
		ArrayList<OVERLAPPING> overlap = new ArrayList<OVERLAPPING>();

		if (rect1.left >= rect2.left && rect1.left <= rect2.right ) // Left overlaps
			overlap.add(OVERLAPPING.LEFT);
			
		if (rect1.right >= rect2.left && rect1.right <= rect2.right ) // Right overlaps
			overlap.add(OVERLAPPING.RIGHT);

		if (rect1.top >= rect2.top && rect1.top <= rect2.bottom ) // Top overlaps
			overlap.add(OVERLAPPING.ABOVE);
			
		if (rect1.bottom >= rect2.top && rect1.bottom <= rect2.bottom ) // Bottom overlaps
			overlap.add(OVERLAPPING.BELOW);

		return overlap;
	}

}
