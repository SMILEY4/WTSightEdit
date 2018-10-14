package com.ruegnerlukas.wtsights.data.sight.sightElements.elements;

import com.ruegnerlukas.simplemath.MathUtils;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.layouts.LayoutLineObject;
import com.ruegnerlukas.wtutils.Conversion;
import com.ruegnerlukas.wtutils.SightUtils;

public class ElementCustomLine extends ElementCustomObject {

	
	public Vector2d start = new Vector2d(0,0);
	public Vector2d end = new Vector2d(0.1,0.1);
	
	
	
	public ElementCustomLine() {
		this(ElementType.CUSTOM_LINE.defaultName);
	}

	
	public ElementCustomLine(String name) {
		super(name, ElementType.CUSTOM_LINE);
		this.setLayout(new LayoutLineObject());
	}
	
	

	
	@Override
	public LayoutLineObject layout(DataPackage data, double canvasWidth, double canvasHeight) {
		
		LayoutLineObject layout = (LayoutLineObject)getLayout();
		
		if(isDirty()) {
			setDirty(false);
			
			
			if(movement == Movement.MOVE_RADIAL) {
				if(autoCenter) {
					layout.center.set(0).add(start).add(end).scale(0.5f);
				} else {
					layout.center.set(center);
				}
				if(useThousandth) {
					layout.center.set(
							Conversion.get().mil2pixel(layout.center.x,canvasHeight, data.dataSight.envZoomedIn),
							Conversion.get().mil2pixel(layout.center.y, canvasHeight, data.dataSight.envZoomedIn));
					layout.radCenter.set(
							Conversion.get().mil2pixel(radCenter.x,canvasHeight, data.dataSight.envZoomedIn),
							Conversion.get().mil2pixel(radCenter.y, canvasHeight, data.dataSight.envZoomedIn));
				} else {
					layout.center.set(
							Conversion.get().screenspace2pixel(layout.center.x,canvasHeight, data.dataSight.envZoomedIn),
							Conversion.get().screenspace2pixel(layout.center.y, canvasHeight, data.dataSight.envZoomedIn));
					layout.radCenter.set(
							Conversion.get().screenspace2pixel(radCenter.x,canvasHeight, data.dataSight.envZoomedIn),
							Conversion.get().screenspace2pixel(radCenter.y, canvasHeight, data.dataSight.envZoomedIn));
				}
				layout.center.add(canvasWidth/2, canvasHeight/2);
				layout.radCenter.add(canvasWidth/2, canvasHeight/2);
			} else {
				layout.center.set(-10000, -10000);
				layout.radCenter.set(-10000, -10000);
			}
			
			
			double sxPX = 0;
			double syPX = 0;
			double exPX = 0;
			double eyPX = 0;

			if(movement == Movement.STATIC) {
				
				if(useThousandth) {
					sxPX = Conversion.get().mil2pixel(start.x, canvasHeight, data.dataSight.envZoomedIn);
					syPX = Conversion.get().mil2pixel(start.y, canvasHeight, data.dataSight.envZoomedIn);
					exPX = Conversion.get().mil2pixel(end.x, canvasHeight, data.dataSight.envZoomedIn);
					eyPX = Conversion.get().mil2pixel(end.y, canvasHeight, data.dataSight.envZoomedIn);
				} else {
					sxPX = Conversion.get().screenspace2pixel(start.x, canvasHeight, data.dataSight.envZoomedIn);
					syPX = Conversion.get().screenspace2pixel(start.y, canvasHeight, data.dataSight.envZoomedIn);
					exPX = Conversion.get().screenspace2pixel(end.x, canvasHeight, data.dataSight.envZoomedIn);
					eyPX = Conversion.get().screenspace2pixel(end.y, canvasHeight, data.dataSight.envZoomedIn);
				}
				
				
			} else if(movement == Movement.MOVE) {
				
				double rangeCorrectionMil = 0;
				
				if(data.elementBallistic != null && !(data.elementBallistic.ammunition.get(0).type.contains("rocket") || data.elementBallistic.ammunition.get(0).type.contains("atgm")) ) {
					final double rangeCorrectionResultPX = data.elementBallistic.function.eval(data.dataSight.envRangeCorrection);
					rangeCorrectionMil = Conversion.get().pixel2mil(rangeCorrectionResultPX, canvasHeight, false);
					
				} else {
					// found values by testing  50m = 0.6875mil
					rangeCorrectionMil = data.dataSight.envRangeCorrection * (0.6875/50.0);
				}
				
				final double rangeCorrectionPX = Conversion.get().mil2pixel(rangeCorrectionMil, canvasHeight, data.dataSight.envZoomedIn);
				
				if(useThousandth) {
					sxPX = Conversion.get().mil2pixel(start.x, canvasHeight, data.dataSight.envZoomedIn);
					syPX = Conversion.get().mil2pixel(start.y, canvasHeight, data.dataSight.envZoomedIn);
					exPX = Conversion.get().mil2pixel(end.x, canvasHeight, data.dataSight.envZoomedIn);
					eyPX = Conversion.get().mil2pixel(end.y, canvasHeight, data.dataSight.envZoomedIn);
				} else {
					sxPX = Conversion.get().screenspace2pixel(start.x, canvasHeight, data.dataSight.envZoomedIn);
					syPX = Conversion.get().screenspace2pixel(start.y, canvasHeight, data.dataSight.envZoomedIn);
					exPX = Conversion.get().screenspace2pixel(end.x, canvasHeight, data.dataSight.envZoomedIn);
					eyPX = Conversion.get().screenspace2pixel(end.y, canvasHeight, data.dataSight.envZoomedIn);
				}
				
				if(data.dataSight.gnrApplyCorrectionToGun) {
					syPX -= rangeCorrectionPX;
					eyPX -= rangeCorrectionPX;
				} else {
					syPX += rangeCorrectionPX;
					eyPX += rangeCorrectionPX;
				}
				
				
				
			} else if(movement == Movement.MOVE_RADIAL) {
				
				Vector2d centerOW = new Vector2d();
				if(autoCenter) {
					centerOW.add(start).add(end).scale(0.5f);
				} else {
					centerOW = center;
				}
				final double radius = useThousandth ? centerOW.dist(radCenter) : Conversion.get().screenspace2mil(centerOW.dist(radCenter), data.dataSight.envZoomedIn);
				if(MathUtils.isNearlyEqual(radius, 0)) {
					return null;
				}
				
				double rangeCorrAngleSMil = SightUtils.rangeCorrection_meters2sovmil(data.dataSight.envRangeCorrection);
				double rangeAngle = SightUtils.calcAngle_rad(rangeCorrAngleSMil, radius, speed);
				
				if(!data.dataSight.gnrApplyCorrectionToGun) {
					rangeAngle = -rangeAngle;
				}
				
				Vector2d toStart = Vector2d.createVectorAB(radCenter, start);
				Vector2d toEnd = Vector2d.createVectorAB(radCenter, end);
				if(MathUtils.isNearlyEqual(toStart.length2(), 0) && MathUtils.isNearlyEqual(toEnd.length2(), 0)) {
					toStart.set(start);
					toEnd.set(end);
				} else {
					toStart.rotateDeg(-angle).rotateRad(rangeAngle).add(radCenter);
					toEnd.rotateDeg(-angle).rotateRad(rangeAngle).add(radCenter);
				}

				
				if(useThousandth) {
					sxPX = Conversion.get().mil2pixel(toStart.x, canvasHeight, data.dataSight.envZoomedIn);
					syPX = Conversion.get().mil2pixel(toStart.y, canvasHeight, data.dataSight.envZoomedIn);
					exPX = Conversion.get().mil2pixel(toEnd.x, canvasHeight, data.dataSight.envZoomedIn);
					eyPX = Conversion.get().mil2pixel(toEnd.y, canvasHeight, data.dataSight.envZoomedIn);
				} else {
					sxPX = Conversion.get().screenspace2pixel(toStart.x, canvasHeight, data.dataSight.envZoomedIn);
					syPX = Conversion.get().screenspace2pixel(toStart.y, canvasHeight, data.dataSight.envZoomedIn);
					exPX = Conversion.get().screenspace2pixel(toEnd.x, canvasHeight, data.dataSight.envZoomedIn);
					eyPX = Conversion.get().screenspace2pixel(toEnd.y, canvasHeight, data.dataSight.envZoomedIn);
				}
				
			}
			
			
			sxPX += canvasWidth/2;
			syPX += canvasHeight/2;
			exPX += canvasWidth/2;
			eyPX += canvasHeight/2;
			
			layout.start.set(sxPX, syPX);
			layout.end.set(exPX, eyPX);
			layout.lineSize = data.dataSight.gnrLineSize*data.dataSight.gnrFontScale;
			
		}
		
		return layout;
	}
	
}
