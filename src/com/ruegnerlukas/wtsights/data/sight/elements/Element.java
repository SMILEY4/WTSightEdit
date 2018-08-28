package com.ruegnerlukas.wtsights.data.sight.elements;



public class Element {

	public String name;
	public ElementType type;
	
	protected Element(String name, ElementType type) {
		this.name = name;
		this.type = type;
	}
	
}
