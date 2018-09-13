package com.ruegnerlukas.wtsights.data.sight.elements.layouts;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;

public class LayoutTextObject implements ILayoutData {

	public double fontSize;
	public Vector2d pos = new Vector2d();

	public boolean dirty = true;
}
