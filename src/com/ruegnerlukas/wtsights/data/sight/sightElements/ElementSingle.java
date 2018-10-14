package com.ruegnerlukas.wtsights.data.sight.sightElements;

import com.ruegnerlukas.wtsights.data.DataPackage;

public abstract class ElementSingle extends Element {

	private ILayoutData layoutData;
	
	
	
	
	protected ElementSingle(String name, ElementType type) {
		super(name, type);
	}

	
	
	
	@Override
	public void setDirty(boolean dirty) {
		if(layoutData != null) {
			layoutData.setDirty(dirty);
		}
	}
	
	
	
	
	@Override
	public boolean isDirty() {
		if(layoutData != null) {
			return layoutData.isDirty();
		} else {
			return false;
		}
	}
	
	
	
	
	protected void setLayout(ILayoutData layoutData) {
		this.layoutData = layoutData;
	}
	
	
	
	
	protected ILayoutData getLayout() {
		return this.layoutData;
	}
	
	
	
	
	public abstract ILayoutData layout(DataPackage data, double canvasWidth, double canvasHeight);
	
}
