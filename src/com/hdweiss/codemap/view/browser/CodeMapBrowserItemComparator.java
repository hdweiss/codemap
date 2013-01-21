package com.hdweiss.codemap.view.browser;

import java.util.Comparator;

public class CodeMapBrowserItemComparator implements Comparator<CodeMapBrowserItem>{
    public int compare(CodeMapBrowserItem item1, CodeMapBrowserItem item2) {
    	return item1.name.compareTo(item2.name);
    }
}
