package com.hdweiss.codemap.view.browser;

import java.util.Comparator;

public class BrowserItemComparator implements Comparator<BrowserItem>{
    public int compare(BrowserItem item1, BrowserItem item2) {
    	return item1.name.compareTo(item2.name);
    }
}
