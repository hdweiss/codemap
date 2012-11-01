package com.hdweiss.codemap.data;

import java.io.Serializable;

import com.hdweiss.codemap.util.CodeMapPoint;

public class CodeMapObject implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public String name;
	public CodeMapPoint point;
	
	public CodeMapObject(String name, CodeMapPoint point) {
		this.name = name;
		this.point = point;
	}
	
	public String toString() {
		return this.name + " @ " + point.x + ":" + point.y;
	}
}
