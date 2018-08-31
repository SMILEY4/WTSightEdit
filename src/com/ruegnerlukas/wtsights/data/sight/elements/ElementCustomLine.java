package com.ruegnerlukas.wtsights.data.sight.elements;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;

public class ElementCustomLine extends ElementCustomObject {

	public Vector2d start = new Vector2d(0,0);
	public Vector2d end = new Vector2d(0.1,0.1);
	
	
	public ElementCustomLine(String name) {
		super(name, ElementType.CUSTOM_LINE);
	}
	
	public ElementCustomLine() {
		super(ElementType.CUSTOM_LINE.defaultName, ElementType.CUSTOM_LINE);
	}
}
