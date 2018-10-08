package com.ruegnerlukas.wtsights.data.sight.elements;

import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.sight.elements.layouts.LayoutCentralVertLine;

public class ElementCentralVertLine extends Element {

	
	public boolean drawCentralVertLine = true; // display the vertical sight line
	
	public LayoutCentralVertLine layoutData = new LayoutCentralVertLine();
	
	
	
	public ElementCentralVertLine(String name) {
		super(name, ElementType.CENTRAL_VERT_LINE);
	}
	
	
	public ElementCentralVertLine() {
		super(ElementType.CENTRAL_VERT_LINE.defaultName, ElementType.CENTRAL_VERT_LINE);
	}
	
	

	
	
	
	@Override
	public void setDirty() {
		this.layoutData.dirty = true;
	}
	
	
	
	@Override
	public LayoutCentralVertLine layout(DataPackage data, double canvasWidth, double canvasHeight) {
		
		if(!layoutData.dirty) {
			return layoutData;
		}
		layoutData.dirty = false;
		
		if(drawCentralVertLine) {
			final double lineSize = 1.0 * data.dataSight.gnrLineSize * data.dataSight.gnrFontScale;
			layoutData.bounds.set(canvasWidth/2 - (lineSize/2f), 0, lineSize, canvasHeight);
		} else {
			layoutData.bounds.set(-100000, -100000, 0, 0);
		}
		return layoutData;
	}
	
}
