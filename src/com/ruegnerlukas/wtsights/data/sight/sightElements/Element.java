package com.ruegnerlukas.wtsights.data.sight.sightElements;

import com.ruegnerlukas.wtsights.data.DataPackage;

public abstract class Element {

	public String name;
	public ElementType type;
	private boolean dirty;
	private ILayoutData layout;
	
	
	
	
	protected Element(String name, ElementType type) {
		this.name = name;
		this.type = type;
	}

	
	
	
	public abstract ILayoutData layout(DataPackage data, double canvasWidth, double canvasHeight);
	
	
	
	
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
	
	
	
	public boolean isDirty() {
		return this.dirty;
	}
	
	
	
	
	public void setLayout(ILayoutData layout) {
		this.layout = layout;
	}

	
	
	
	public ILayoutData getLayout() {
		return this.layout;
	}
	
	
}
