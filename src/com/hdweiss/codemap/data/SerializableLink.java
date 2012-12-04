package com.hdweiss.codemap.data;

import java.io.Serializable;
import java.util.UUID;

import com.hdweiss.codemap.view.fragments.CodeMapLink;

public class SerializableLink implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public UUID parent;
	public UUID child;
	public float offset;
	
	public SerializableLink(UUID parent, UUID child, float offset) {
		this.parent = parent;
		this.child = child;
		this.offset = offset;
	}
	
	public SerializableLink(CodeMapLink link) {
		this(link.parent.id, link.child.id, link.yOffset);
	}
}
