package com.hdweiss.codemap.data;

import java.io.Serializable;
import java.util.UUID;

import android.content.Context;
import android.text.SpannableString;

import com.hdweiss.codemap.util.CodeMapPoint;
import com.hdweiss.codemap.view.workspace.fragments.CodeMapAnnotation;
import com.hdweiss.codemap.view.workspace.fragments.CodeMapFunction;
import com.hdweiss.codemap.view.workspace.fragments.CodeMapImage;
import com.hdweiss.codemap.view.workspace.fragments.CodeMapItem;

public class SerializableItem implements Serializable {
	private static final long serialVersionUID = 2L;
	
	public UUID id;
	public String url;
	public String contents;
	public TYPE type;
	public CodeMapPoint point;
	
	public enum TYPE {
		function, annotation, image
	}
	
	public SerializableItem(String url, CodeMapPoint point, UUID id) {
		this.url = url;
		this.point = point;
		this.id = id;
	}
	
	public SerializableItem(CodeMapItem item) {
		this(item.getUrl(), item.getPosition(), item.id);
		setupType(item);
	}
	
	private void setupType(CodeMapItem item) {
		TYPE type = TYPE.function;
		
		if (item instanceof CodeMapFunction)
			type = TYPE.function;
		
		if (item instanceof CodeMapAnnotation) {
			this.contents = ((CodeMapAnnotation) item).getContents();
			type = TYPE.annotation;
		}
		
		if (item instanceof CodeMapImage)
			type = TYPE.image;
		
		this.type = type;
	}
	
	
	public CodeMapItem createCodeMapItem(ProjectController controller, Context context) {
		switch (type) {
		case function:
			final SpannableString content = controller.getFunctionSource(this.url);

			CodeMapFunction functionView = new CodeMapFunction(context,
					this.point, this.url, content);
			return functionView;
			
		case annotation:
			CodeMapAnnotation annotationView = new CodeMapAnnotation(context, this.point,
					this.contents);
			return annotationView;
			
		default:
			throw new IllegalArgumentException();
		}
	}
	
	public String toString() {
		return this.url + " @ " + point.x + ":" + point.y;
	}
}
