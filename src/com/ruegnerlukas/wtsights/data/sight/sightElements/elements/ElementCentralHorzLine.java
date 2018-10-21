package com.ruegnerlukas.wtsights.data.sight.sightElements.elements;

import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.sight.sightElements.Element;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.layouts.LayoutCentralHorzLine;

public class ElementCentralHorzLine extends Element {

	
	public boolean drawCentralHorzLine = true; // display the horizontal sight line
	
	
	
	
	public ElementCentralHorzLine() {
		this(ElementType.CENTRAL_HORZ_LINE.defaultName);
	}
	
	
	public ElementCentralHorzLine(String name) {
		super(name, ElementType.CENTRAL_HORZ_LINE);
		this.setLayout(new LayoutCentralHorzLine());
	}
	
	
	

	@Override
	public LayoutCentralHorzLine layout(DataPackage data, double canvasWidth, double canvasHeight) {
		
		LayoutCentralHorzLine layout = (LayoutCentralHorzLine)getLayout();
		
		if(isDirty()) {
			setDirty(false);
			if(drawCentralHorzLine) {
				final double lineSize = 1.0 * data.dataSight.gnrLineSize * data.dataSight.gnrFontScale;
				layout.bounds.set(0, canvasHeight/2 - (lineSize/2f), canvasWidth, lineSize);
			} else {
				layout.bounds.set(-100000, -100000, 0, 0);
			}
		}
		
		return layout;
	}
	
	
}
