package com.ruegnerlukas.wtsights.data.sight.elements.layouts;

import com.ruegnerlukas.simplemath.geometry.shapes.circle.Circlef;
import com.ruegnerlukas.simplemath.geometry.shapes.rectangle.Rectanglef;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.simplemath.vectors.vec4.Vector4d;

public class LayoutBallRangeIndicators implements ILayoutData {

	// LABEL
	public Vector2d posLabel = new Vector2d();
	
	// VERTICAL
	public Rectanglef[] vMainBounds = null;
	public Rectanglef[] vCenterBounds = null;
	public Vector2d[] vTextPositions = null;
	
	
	// RADIAL - LINES
	public double rlLineSize;
	public Vector4d[] rlLines = null;
	public Vector2d[] rlTextPositions = null;

	
	// RADIAL - CIRCLES
	public double rcLineWidth;
	public Circlef[] rcCircles = null;
	public Vector2d[] rcTextPositions = null;

	
}
