package com.ruegnerlukas.wtsights.data.sight.sightElements.elements;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementMulti;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;

public abstract class ElementCustomMultiObject extends ElementMulti {
	
	public boolean 		useThousandth 	= false;
	public Movement 	movement 		= Movement.STATIC;
	public double 		angle			= 0;
	public boolean 		autoCenter		= true;
	public Vector2d		center			= new Vector2d(0,0);
	public Vector2d		radCenter		= new Vector2d(0,0);
	public double		speed			= 1;
	
	
	protected ElementCustomMultiObject(String name, ElementType type) {
		super(name, type);
	}
	
	
}
