package com.ruegnerlukas.wtsights.data.sight.elements;

import com.ruegnerlukas.wtsights.data.calibration.CalibrationAmmoData;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationData;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.data.sight.elements.layouts.ILayoutData;

public abstract class Element {

	public String name;
	public ElementType type;
	
	protected Element(String name, ElementType type) {
		this.name = name;
		this.type = type;
	}
	
	
	public abstract ILayoutData layout(SightData sightData, CalibrationData calibData, CalibrationAmmoData ammoData, double canvasWidth, double canvasHeight);
	
	public abstract void setDirty();
	
}
