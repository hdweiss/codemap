package com.hdweiss.codemap.util;

import java.io.IOException;
import java.io.Serializable;

import android.graphics.PointF;

/**
 * Absolute point on the CodeMap canvas.
 */
public class CodeMapPoint extends PointF implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public CodeMapPoint() {
		super();
	}
	
	public CodeMapPoint(float x, float y) {
		super(x, y);
	}

	public CodeMapPoint(PointF point) {
		super(point.x, point.y);
	}
	
    private void writeObject(final java.io.ObjectOutputStream out)
            throws IOException {
        out.writeFloat(x);
        out.writeFloat(y);
    }

    private void readObject(final java.io.ObjectInputStream in)
            throws IOException {
        x = in.readFloat();
        y = in.readFloat();
    }
}
