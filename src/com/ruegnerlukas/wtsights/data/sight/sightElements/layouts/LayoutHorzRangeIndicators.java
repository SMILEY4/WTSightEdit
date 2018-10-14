package com.ruegnerlukas.wtsights.data.sight.sightElements.layouts;

import com.ruegnerlukas.simplemath.geometry.shapes.rectangle.Rectanglef;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ILayoutData;

public class LayoutHorzRangeIndicators implements ILayoutData {
	
	public Rectanglef[] bounds = null;
	public Vector2d[] textPositions = null;
	public double fontSize;
	
	private boolean dirty = true;

	@Override
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}
	
	
}
