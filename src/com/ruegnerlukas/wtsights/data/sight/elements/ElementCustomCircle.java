package com.ruegnerlukas.wtsights.data.sight.elements;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;

public class ElementCustomCircle extends ElementCustomObject {

	
	public Vector2d 	position 	= new Vector2d(0,0);
	public double 		diameter 	= 0.1;
	public Vector2d 	segment 	= new Vector2d(0, 360);
	public double 		size 		= 1;
	
	
	public ElementCustomCircle(String name) {
		super(name, ElementType.CUSTOM_CIRCLE);
	}
	
	public ElementCustomCircle() {
		super(ElementType.CUSTOM_CIRCLE.defaultName, ElementType.CUSTOM_CIRCLE);
	}

	
	
	
}
