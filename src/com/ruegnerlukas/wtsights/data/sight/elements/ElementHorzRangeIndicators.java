package com.ruegnerlukas.wtsights.data.sight.elements;

import java.util.ArrayList;
import java.util.List;

import com.ruegnerlukas.simplemath.geometry.shapes.rectangle.Rectanglef;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationAmmoData;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationData;
import com.ruegnerlukas.wtsights.data.sight.HIndicator;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.data.sight.elements.layouts.LayoutHorzRangeIndicators;
import com.ruegnerlukas.wtutils.Conversion;

public class ElementHorzRangeIndicators extends Element {

	
	public double sizeMajor = 3;	// the size of the major notches in mils
	public double sizeMinor = 2;	// the size of the minor notches in mils
	public List<HIndicator> indicators = new ArrayList<HIndicator>();
	
	public LayoutHorzRangeIndicators layoutData = new LayoutHorzRangeIndicators();
	
	
	
	
	public ElementHorzRangeIndicators(String name) {
		super(name, ElementType.HORZ_RANGE_INDICATORS);
		resetIndicators();
	}
	
	
	public ElementHorzRangeIndicators() {
		super(ElementType.HORZ_RANGE_INDICATORS.defaultName, ElementType.HORZ_RANGE_INDICATORS);
		resetIndicators();
	}
	
	
	
	
	
	
	public void resetIndicators() {
		indicators.clear();
		for(int i=-32, j=0; i<=32; i+=4, j++) {
			if(i == 0) { continue; }
			HIndicator line = new HIndicator(i, (j%2==0) );
			indicators.add(line);
		}
	}

	
	
	
	@Override
	public void setDirty() {
		this.layoutData.dirty = true;
	}
	
	
	
	
	@Override
	public LayoutHorzRangeIndicators layout(SightData sightData, CalibrationData calibData, CalibrationAmmoData ammoData, double canvasWidth, double canvasHeight) {
		
		if(!layoutData.dirty) {
			return layoutData;
		}
		layoutData.dirty = false;
		
		if(layoutData.bounds == null || layoutData.textPositions == null || layoutData.bounds.length != indicators.size()) {
			layoutData.bounds = new Rectanglef[indicators.size()];
			layoutData.textPositions = new Vector2d[indicators.size()];
			for(int i=0; i<indicators.size(); i++) {
				layoutData.bounds[i] = new Rectanglef();
				layoutData.textPositions[i] = new Vector2d();
			}
		}
		
		final double lineSize = sightData.gnrLineSize * sightData.gnrFontScale;
		
		layoutData.fontSize = 12.5 * sightData.gnrFontScale * (sightData.envZoomedIn ? Conversion.get().zoomInMul : 1);
		
		for(int i=0; i<indicators.size(); i++) {
			
			HIndicator indicator = indicators.get(i);
			final int mil = indicator.getMil();
			final boolean isMajor = indicator.isMajor();
			
			// length
			final double length = Conversion.get().mil2pixel(isMajor ? sizeMajor : sizeMinor, canvasHeight, sightData.envZoomedIn);
			
			// x pos
			final double x = canvasWidth/2 + Conversion.get().mil2pixel(mil, canvasHeight, sightData.envZoomedIn);
			
			// y pos
			final double y = canvasHeight/2;
			final double yLabel = y - length - Conversion.get().screenspace2pixel(0.013, canvasHeight, sightData.envZoomedIn) * sightData.gnrFontScale;
			
			layoutData.bounds[i].set(x-lineSize/2, y-length, lineSize, length*2);
			layoutData.textPositions[i].set(x, yLabel);
			
		}	 
		
		return layoutData;
	}
	
	
}
