package com.ruegnerlukas.wtsights.data.sight.sightElements.elements;

import java.util.ArrayList;
import java.util.List;

import com.ruegnerlukas.simplemath.geometry.shapes.rectangle.Rectanglef;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.sight.HIndicator;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementSingle;
import com.ruegnerlukas.wtsights.data.sight.sightElements.layouts.LayoutHorzRangeIndicators;
import com.ruegnerlukas.wtutils.Conversion;

public class ElementHorzRangeIndicators extends ElementSingle {

	
	public double sizeMajor = 3;	// the size of the major notches in mils
	public double sizeMinor = 2;	// the size of the minor notches in mils
	public List<HIndicator> indicators = new ArrayList<HIndicator>();
	
	
	

	public ElementHorzRangeIndicators() {
		this(ElementType.HORZ_RANGE_INDICATORS.defaultName);
	}
	
	
	public ElementHorzRangeIndicators(String name) {
		super(name, ElementType.HORZ_RANGE_INDICATORS);
		this.setLayout(new LayoutHorzRangeIndicators());
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
	public LayoutHorzRangeIndicators layout(DataPackage data, double canvasWidth, double canvasHeight) {
		
		LayoutHorzRangeIndicators layout = (LayoutHorzRangeIndicators)getLayout();
		
		if(isDirty()) {
			setDirty(false);
			
			if(layout.bounds == null || layout.textPositions == null || layout.bounds.length != indicators.size()) {
				layout.bounds = new Rectanglef[indicators.size()];
				layout.textPositions = new Vector2d[indicators.size()];
				for(int i=0; i<indicators.size(); i++) {
					layout.bounds[i] = new Rectanglef();
					layout.textPositions[i] = new Vector2d();
				}
			}
			
			final double lineSize = data.dataSight.gnrLineSize * data.dataSight.gnrFontScale;
			
			layout.fontSize = 12.5 * data.dataSight.gnrFontScale * (data.dataSight.envZoomedIn ? Conversion.get().zoomInMul : 1);
			
			for(int i=0; i<indicators.size(); i++) {
				
				HIndicator indicator = indicators.get(i);
				final int mil = indicator.getMil();
				final boolean isMajor = indicator.isMajor();
				
				// length
				final double length = Conversion.get().mil2pixel(isMajor ? sizeMajor : sizeMinor, canvasHeight, data.dataSight.envZoomedIn);
				
				// x pos
				final double x = canvasWidth/2 + Conversion.get().mil2pixel(mil, canvasHeight, data.dataSight.envZoomedIn);
				
				// y pos
				final double y = canvasHeight/2;
				final double yLabel = y - length - Conversion.get().screenspace2pixel(0.013, canvasHeight, data.dataSight.envZoomedIn) * data.dataSight.gnrFontScale;
				
				layout.bounds[i].set(x-lineSize/2, y-length, lineSize, length*2);
				layout.textPositions[i].set(x, yLabel);
				
			}	 
		}
		
		return layout;
	}
	
	
}
