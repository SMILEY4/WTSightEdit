package com.ruegnerlukas.wtsights.data.sight.sightElements.layouts;

public class LayoutPolygonOutlineObject extends LayoutCustomMultiObject {

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
