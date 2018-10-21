package com.ruegnerlukas.wtsights.data.sight.sightElements.layouts;

import com.ruegnerlukas.simplemath.geometry.shapes.rectangle.Rectanglef;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ILayoutData;

public class LayoutRangefinder implements ILayoutData {
	
	public Rectanglef bounds = new Rectanglef();
	public Vector2d textPos = new Vector2d();
	public double fontSize;
	
}
