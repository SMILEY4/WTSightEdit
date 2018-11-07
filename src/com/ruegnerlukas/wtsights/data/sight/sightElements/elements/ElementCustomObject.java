package com.ruegnerlukas.wtsights.data.sight.sightElements.elements;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.wtsights.data.sight.sightElements.BaseElement;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;

public abstract class ElementCustomObject extends BaseElement {
	
	public boolean 		useThousandth 	= false;
	public Movement 	movement 		= Movement.STATIC;
	public double 		angle			= 0;
	public boolean 		autoCenter		= true;
	public Vector2d		center			= new Vector2d(0,0);	// by default: the center of weight
	public Vector2d		radCenter		= new Vector2d(0,0);	// the origin of the rotation
	public double		speed			= 1;
	
	public Vector2d 	positionOffset	= new Vector2d(0,0);
	
	
	
	
	protected ElementCustomObject(String name, ElementType type) {
		super(name, type);
	}
	
	
}
