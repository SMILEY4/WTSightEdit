package com.ruegnerlukas.wtsights.data.sight.elements;



public class ElementCentralVertLine extends Element {

	public boolean drawCentralVertLine = true; // display the vertical sight line
	
	
	public ElementCentralVertLine(String name) {
		super(name, ElementType.CENTRAL_VERT_LINE);
	}
	
	public ElementCentralVertLine() {
		super(ElementType.CENTRAL_VERT_LINE.defaultName, ElementType.CENTRAL_VERT_LINE);
	}
	
}
