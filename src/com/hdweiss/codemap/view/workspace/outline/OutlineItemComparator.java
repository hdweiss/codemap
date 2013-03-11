package com.hdweiss.codemap.view.workspace.outline;

import java.util.Comparator;

public class OutlineItemComparator implements Comparator<OutlineItem>{
    public int compare(OutlineItem item1, OutlineItem item2) {
    	return item1.name.compareTo(item2.name);
    }
}
