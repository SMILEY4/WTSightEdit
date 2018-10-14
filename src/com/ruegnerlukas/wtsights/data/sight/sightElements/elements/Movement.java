package com.ruegnerlukas.wtsights.data.sight.sightElements.elements;

public enum Movement {
	STATIC, MOVE, MOVE_RADIAL;
	
	public static Movement get(String str) {
		if(STATIC.toString().equals(str)) 		{ return STATIC; }
		if(MOVE.toString().equals(str)) 		{ return MOVE; }
		if(MOVE_RADIAL.toString().equals(str)) 	{ return MOVE_RADIAL; }
		return null;
	}
}
