package com.ruegnerlukas.wtsights.data.sight.elements;

import java.util.ArrayList;
import java.util.List;

import com.ruegnerlukas.wtsights.data.sight.HIndicator;

public class ElementHorzRangeIndicators extends Element {

	public double sizeMajor = 3;	// the size of the major notches in mils
	public double sizeMinor = 2;	// the size of the minor notches in mils
	public List<HIndicator> indicators = new ArrayList<HIndicator>();
	
	
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
	
	
}
