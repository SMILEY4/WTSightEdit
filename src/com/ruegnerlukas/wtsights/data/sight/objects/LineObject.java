package com.ruegnerlukas.wtsights.data.sight.objects;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;

public class LineObject extends SightObject {

	public LineObject() {
		this.type = Type.LINE;
	}
	
	public Vector2d start = new Vector2d();
	public Vector2d end = new Vector2d();

}
