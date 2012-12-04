package com.hdweiss.codemap.data;

import java.io.Serializable;
import java.util.UUID;

import com.hdweiss.codemap.util.CodeMapPoint;
import com.hdweiss.codemap.view.fragments.CodeMapItem;

public class SerializableItem implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public UUID id;
	public String name;
	public CodeMapPoint point;
	
	public SerializableItem(String name, CodeMapPoint point, UUID id) {
		this.name = name;
		this.point = point;
		this.id = id;
	}
	
	public SerializableItem(CodeMapItem item) {
		this(item.getName(), item.getPosition(), item.id);
	}
	
	public String toString() {
		return this.name + " @ " + point.x + ":" + point.y;
	}
}
