package com.ruegnerlukas.wtsights.data.sight.elements;



public class ElementCentralHorzLine extends Element {

	public boolean drawCentralHorzLine = true; // display the horizontal sight line
	
	public ElementCentralHorzLine(String name) {
		super(name, ElementType.CENTRAL_HORZ_LINE);
	}
	
	public ElementCentralHorzLine() {
		super(ElementType.CENTRAL_HORZ_LINE.defaultName, ElementType.CENTRAL_HORZ_LINE);
	}
	
}
