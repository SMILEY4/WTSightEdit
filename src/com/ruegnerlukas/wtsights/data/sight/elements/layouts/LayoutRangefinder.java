package com.ruegnerlukas.wtsights.data.sight.elements.layouts;

import com.ruegnerlukas.simplemath.geometry.shapes.rectangle.Rectanglef;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;

public class LayoutRangefinder implements ILayoutData {
	
	public Rectanglef bounds = new Rectanglef();
	public Vector2d textPos = new Vector2d();
	public double fontSize;
	
}
