package com.ruegnerlukas.wtsights.data.sight.elements;

import com.ruegnerlukas.wtsights.data.calibration.CalibrationAmmoData;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationData;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.data.sight.elements.layouts.LayoutCentralHorzLine;

public class ElementCentralHorzLine extends Element {

	
	public boolean drawCentralHorzLine = true; // display the horizontal sight line
	
	public LayoutCentralHorzLine layoutData = new LayoutCentralHorzLine();
	
	
	
	
	public ElementCentralHorzLine(String name) {
		super(name, ElementType.CENTRAL_HORZ_LINE);
	}
	
	
	public ElementCentralHorzLine() {
		super(ElementType.CENTRAL_HORZ_LINE.defaultName, ElementType.CENTRAL_HORZ_LINE);
	}
	
	
	
	
	
	
	@Override
	public LayoutCentralHorzLine layout(SightData sightData, CalibrationData calibData, CalibrationAmmoData ammoData, double canvasWidth, double canvasHeight) {
		if(drawCentralHorzLine) {
			final double lineSize = 1.0 * sightData.gnrLineSize * sightData.gnrFontScale;
			layoutData.bounds.set(0, canvasHeight/2 - (lineSize/2f), canvasWidth, lineSize);
		} else {
			layoutData.bounds.set(-100000, -100000, 0, 0);
		}
		return layoutData;
	}
	
	
}
