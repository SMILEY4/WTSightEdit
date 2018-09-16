package com.ruegnerlukas.wtsights.data.sight.elements;

import com.ruegnerlukas.simplemath.MathUtils;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.wtsights.data.WorkingData;
import com.ruegnerlukas.wtsights.data.sight.elements.layouts.LayoutLineObject;
import com.ruegnerlukas.wtutils.Conversion;
import com.ruegnerlukas.wtutils.SightUtils;

public class ElementCustomLine extends ElementCustomObject {

	
	public Vector2d start = new Vector2d(0,0);
	public Vector2d end = new Vector2d(0.1,0.1);
	
	public LayoutLineObject layoutData = new LayoutLineObject();
	
	
	
	
	public ElementCustomLine(String name) {
		super(name, ElementType.CUSTOM_LINE);
	}
	
	
	public ElementCustomLine() {
		super(ElementType.CUSTOM_LINE.defaultName, ElementType.CUSTOM_LINE);
	}
	
	

	
	
	
	@Override
	public void setDirty() {
		this.layoutData.dirty = true;
	}
	
	
	
	@Override
	public LayoutLineObject layout(WorkingData data, double canvasWidth, double canvasHeight) {
		
		if(!layoutData.dirty) {
			return layoutData;
		}
		layoutData.dirty = false;
		
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
			
			if(data.elementBallistic != null) {
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
			
			Vector2d toStart = Vector2d.createVectorAB(radCenter, start);
			Vector2d toEnd = Vector2d.createVectorAB(radCenter, end);
			if(MathUtils.isNearlyEqual(toStart.length2(), 0) && MathUtils.isNearlyEqual(toEnd.length2(), 0)) {
				toStart.set(start);
				toEnd.set(end);
			} else {
				toStart.rotateDeg(-angle).rotateRad(-rangeAngle);
				toEnd.rotateDeg(-angle).rotateRad(-rangeAngle);
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
		
		layoutData.start.set(sxPX, syPX);
		layoutData.end.set(exPX, eyPX);
		layoutData.lineSize = data.dataSight.gnrLineSize*data.dataSight.gnrFontScale;
		return layoutData;
	}
	
}
