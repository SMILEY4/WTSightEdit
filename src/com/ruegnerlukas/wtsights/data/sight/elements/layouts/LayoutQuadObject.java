package com.ruegnerlukas.wtsights.data.sight.elements.layouts;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;

public class LayoutQuadObject implements ILayoutData {

	public Vector2d p0 = new Vector2d();
	public Vector2d p1 = new Vector2d();
	public Vector2d p2 = new Vector2d();
	public Vector2d p3 = new Vector2d();

	public boolean dirty = true;

}
