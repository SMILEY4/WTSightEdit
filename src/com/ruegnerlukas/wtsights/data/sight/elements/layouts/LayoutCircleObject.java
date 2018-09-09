package com.ruegnerlukas.wtsights.data.sight.elements.layouts;

import com.ruegnerlukas.simplemath.geometry.shapes.circle.Circlef;
import com.ruegnerlukas.simplemath.vectors.vec4.Vector4d;

public class LayoutCircleObject implements ILayoutData {

	public double lineWidth;

	public boolean useLineSegments = false;
	public Circlef circle = new Circlef();
	public Vector4d[] lines;
}
