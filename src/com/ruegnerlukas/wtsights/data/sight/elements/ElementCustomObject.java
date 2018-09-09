package com.ruegnerlukas.wtsights.data.sight.elements;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;

public abstract class ElementCustomObject extends Element {
	
	public static enum Movement {
		STATIC, MOVE, MOVE_RADIAL;
		
		public static Movement get(String str) {
			if(STATIC.toString().equals(str)) 		{ return STATIC; }
			if(MOVE.toString().equals(str)) 		{ return MOVE; }
			if(MOVE_RADIAL.toString().equals(str)) 	{ return MOVE_RADIAL; }
			return null;
		}
	}
	
	
	public boolean 		useThousandth 	= false;
	public Movement 	movement 		= Movement.STATIC;
	public double 		angle			= 0;
	public boolean 		autoCenter		= true;
	public Vector2d		center			= new Vector2d(0,0);
	public Vector2d		radCenter		= new Vector2d(0,0);
	public double		speed			= 1;
	
	
	protected ElementCustomObject(String name, ElementType type) {
		super(name, type);
	}
	
	
}
