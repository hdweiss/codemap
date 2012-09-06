package com.hdweiss.codemap.test;

import android.test.AndroidTestCase;

import com.hdweiss.codemap.Ctags;

public class CtagsTest extends AndroidTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testInit() {
		Ctags ctags = new Ctags(getContext());
		ctags.run();
	}
}
