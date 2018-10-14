package com.ruegnerlukas.wtsights.data.sight.sightElements;

public abstract class Element {

	public String name;
	public ElementType type;
	
	protected Element(String name, ElementType type) {
		this.name = name;
		this.type = type;
	}
	
	
	public abstract void setDirty(boolean dirty);
	
	public abstract boolean isDirty();

}
