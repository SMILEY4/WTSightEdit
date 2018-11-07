package com.ruegnerlukas.wtsights.data.sight.sightElements.layouts;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ILayoutData;

public class LayoutCustomObject implements ILayoutData {

	public Vector2d	center		= new Vector2d(0,0);	// by default: the center of weight (IN PIXEL)
	public Vector2d	radCenter	= new Vector2d(0,0);	// the origin of the rotation (IN PIXEL)
	
}
