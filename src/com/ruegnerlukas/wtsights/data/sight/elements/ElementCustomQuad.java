package com.ruegnerlukas.wtsights.data.sight.elements;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;

public class ElementCustomQuad extends ElementCustomObject {

	
	public Vector2d pos1 = new Vector2d(-0.1, -0.1);
	public Vector2d pos2 = new Vector2d(+0.1, -0.1);
	public Vector2d pos3 = new Vector2d(+0.1, +0.1);
	public Vector2d pos4 = new Vector2d(-0.1, +0.1);
	
	
	public ElementCustomQuad(String name) {
		super(name, ElementType.CUSTOM_QUAD);
	}
	
	public ElementCustomQuad() {
		super(ElementType.CUSTOM_QUAD.defaultName, ElementType.CUSTOM_QUAD);
	}
	
}
