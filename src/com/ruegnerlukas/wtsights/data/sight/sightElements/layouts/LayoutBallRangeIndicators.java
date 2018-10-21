package com.ruegnerlukas.wtsights.data.sight.sightElements.layouts;

import com.ruegnerlukas.simplemath.geometry.shapes.circle.Circlef;
import com.ruegnerlukas.simplemath.geometry.shapes.rectangle.Rectanglef;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.simplemath.vectors.vec3.Vector3d;
import com.ruegnerlukas.simplemath.vectors.vec4.Vector4d;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ILayoutData;

public class LayoutBallRangeIndicators implements ILayoutData {

	public double fontSize;
	public Vector3d corrLabel = new Vector3d(); // x, y, fontSize

	
	// VERTICAL
	public Rectanglef[] vMainBounds = null;
	public Rectanglef[] vCenterBounds = null;
	public Vector2d[] vTextPositions = null;
	
	
	// RADIAL - LINES
	public Vector2d rlCenter = new Vector2d();
	public double rlRadius;
	public double rlRadiusOutside;
	public double rlLineSize;
	public Vector4d[] rlLines = null;
	public Vector2d[] rlTextPositions = null;

	
	// RADIAL - CIRCLES
	public Vector2d rcCenter = new Vector2d();
	public double rcRadius;
	public double rcLineWidth;
	public Circlef[] rcCircles = null;
	public Vector2d[] rcTextPositions = null;

}
