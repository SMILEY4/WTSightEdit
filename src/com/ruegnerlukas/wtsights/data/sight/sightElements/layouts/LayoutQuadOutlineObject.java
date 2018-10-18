package com.ruegnerlukas.wtsights.data.sight.sightElements.layouts;

import com.ruegnerlukas.simplemath.geometry.shapes.rectangle.Rectanglef;

public class LayoutQuadOutlineObject extends LayoutCustomMultiObject {
	
	public Rectanglef bounds = new Rectanglef();

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
