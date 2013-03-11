package com.hdweiss.codemap.test;

import android.graphics.Point;
import android.graphics.Rect;
import android.test.AndroidTestCase;

import com.hdweiss.codemap.view.workspace.CollisionManager;

public class CollisionManagerTest extends AndroidTestCase {

	public void test_getPushOffset_nonOverlap() {
		Rect rect1 = new Rect(100, 100, 200, 200);
		Rect rect2 = new Rect(300, 300, 400, 400);
				
		Point pushOffset = CollisionManager.getPushOffset(rect1, rect2);
		
		assertEquals(0, pushOffset.x);
		assertEquals(0, pushOffset.y);
	}
	
	public void test_getPushOffset_pushUp() {
		Rect rect1 = new Rect(100, 100, 200, 200);
		Rect rect2 = new Rect(100, 50, 200, 140);
		
		Point pushOffset = CollisionManager.getPushOffset(rect1, rect2);
		assertEquals(0, pushOffset.x);
		assertEquals(-40, pushOffset.y);
	}
	
	public void test_getPushOffset_pushDown() {
		Rect rect1 = new Rect(100, 100, 200, 200);
		Rect rect2 = new Rect(100, 160, 200, 400);
		
		Point pushOffset = CollisionManager.getPushOffset(rect1, rect2);
		assertEquals(0, (int) pushOffset.x);
		assertEquals(40, (int) pushOffset.y);
	}
}
