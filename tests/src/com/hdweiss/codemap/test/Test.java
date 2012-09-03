package com.hdweiss.codemap.test;

import com.hdweiss.codemap.MainActivity;

import junit.framework.TestCase;

public class Test extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testTest() {
		assertEquals(2, MainActivity.two());
	}
}
