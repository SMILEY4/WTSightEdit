package com.ruegnerlukas.wtsights.data.sight.sightElements.layouts;

import com.ruegnerlukas.simplemath.geometry.shapes.rectangle.Rectanglef;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ILayoutData;

public class LayoutCentralHorzLine implements ILayoutData {
	
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
