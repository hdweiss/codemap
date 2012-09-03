package com.hdweiss.codemap.test;

import com.hdweiss.codemap.Ctags;

import junit.framework.TestCase;

public class CtagsTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testInit() {
		Ctags ctags = new Ctags();
		assertEquals(3, ctags.testLib());
	}
	
	public void testMain() throws Exception {
		Ctags ctags = new Ctags();
		assertEquals(0, ctags.runMain());
	}
}
