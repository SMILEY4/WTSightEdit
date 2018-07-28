package com.ruegnerlukas.wtsights.data.sight.objects;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;

public class SightObject {

	public static enum Type {
		LINE, TEXT, CIRCLE, QUAD;
		
		public static Type get(String str) {
			if(LINE.toString().equals(str)) { return LINE; }
			if(TEXT.toString().equals(str)) { return TEXT; }
			if(CIRCLE.toString().equals(str)) { return CIRCLE; }
			if(QUAD.toString().equals(str)) { return QUAD; }
			return null;
		}
	}
	
	
	public static enum Movement {
		STATIC, MOVE, MOVE_RADIAL;
		
		public static Movement get(String str) {
			if(STATIC.toString().equals(str)) { return STATIC; }
			if(MOVE.toString().equals(str)) { return MOVE; }
			if(MOVE_RADIAL.toString().equals(str)) { return MOVE_RADIAL; }
			return null;
		}
	}
	
	
	
	
	public String name = "";
	
	public Type type = Type.LINE;
	
	public boolean 		cmnUseThousandth 	= false;
	public Movement 	cmnMovement 		= Movement.STATIC;
	public double 		cmnAngle 			= 0;
	public boolean 		useAutoCenter 		= true;
	public Vector2d 	cmnCenter 			= new Vector2d(0,0);
	public Vector2d 	cmnRadCenter 		= new Vector2d(0,0);
	public double 		cmnSpeed 			= 1;
}
