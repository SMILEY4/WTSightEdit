package com.ruegnerlukas.wtsights.data.sight.sightElements.elements;

import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.sight.sightElements.BaseElement;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.layouts.LayoutCentralVertLine;

public class ElementCentralVertLine extends BaseElement {

	
	public boolean drawCentralVertLine = true; // display the vertical sight line
	
	
	
	
	public ElementCentralVertLine() {
		this(ElementType.CENTRAL_VERT_LINE.defaultName);
	}
	
	public ElementCentralVertLine(String name) {
		super(name, ElementType.CENTRAL_VERT_LINE);
		this.setLayout(new LayoutCentralVertLine());
	}
	
	
	
	
	@Override
	public LayoutCentralVertLine layout(DataPackage data, double canvasWidth, double canvasHeight) {
		
		LayoutCentralVertLine layout = (LayoutCentralVertLine)getLayout();
		
		if(isDirty()) {
			setDirty(false);
			if(drawCentralVertLine) {
				final double lineSize = 1.0 * data.dataSight.gnrLineSize * data.dataSight.gnrFontScale;
				layout.bounds.set(canvasWidth/2 - (lineSize/2f), 0, lineSize, canvasHeight);
			} else {
				layout.bounds.set(-100000, -100000, 0, 0);
			}
		}
		
		return layout;
	}
	
}
