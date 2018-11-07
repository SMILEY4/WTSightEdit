package com.ruegnerlukas.wtsights.data.sight.sightElements;

import com.ruegnerlukas.wtsights.data.DataPackage;

public abstract class BaseElement {

	public String name;
	public ElementType type;
	private boolean dirty;
	private ILayoutData layout;
	
	
	
	
	protected BaseElement(String name, ElementType type) {
		this.name = name;
		this.type = type;
		setDirty(true);
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
