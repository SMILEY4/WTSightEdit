package com.ruegnerlukas.wtsights.data.sight.objects;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.wtsights.data.sight.objects.SightObject.Type;

public class CircleObject extends SightObject {

	public CircleObject() {
		this.type = Type.CIRCLE;
	}
	
	public Vector2d pos = new Vector2d();
	public double diameter = 0;
	public Vector2d segment = new Vector2d(0, 360);
	public double size = 1;

	
}
