package com.ruegnerlukas.wtsights.data.sight.objects;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.wtsights.data.sight.objects.SightObject.Type;

public class QuadObject extends SightObject {

	public QuadObject() {
		this.type = Type.QUAD;
	}
	
	public Vector2d pos1 = new Vector2d();
	public Vector2d pos2 = new Vector2d();
	public Vector2d pos3 = new Vector2d();
	public Vector2d pos4 = new Vector2d();

	
}
