package com.ruegnerlukas.wtsights.data.sight.elements;

import java.util.ArrayList;
import java.util.List;

import com.ruegnerlukas.simplemath.MathUtils;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.simplemath.vectors.vec3.Vector3d;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationAmmoData;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationData;
import com.ruegnerlukas.wtsights.data.sight.SightData;
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
	public LayoutLineObject layout(SightData sightData, CalibrationData calibData, CalibrationAmmoData ammoData, double canvasWidth, double canvasHeight) {
		
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
				sxPX = Conversion.get().mil2pixel(start.x, canvasHeight, sightData.envZoomedIn);
				syPX = Conversion.get().mil2pixel(start.y, canvasHeight, sightData.envZoomedIn);
				exPX = Conversion.get().mil2pixel(end.x, canvasHeight, sightData.envZoomedIn);
				eyPX = Conversion.get().mil2pixel(end.y, canvasHeight, sightData.envZoomedIn);
			} else {
				sxPX = Conversion.get().screenspace2pixel(start.x, canvasHeight, sightData.envZoomedIn);
				syPX = Conversion.get().screenspace2pixel(start.y, canvasHeight, sightData.envZoomedIn);
				exPX = Conversion.get().screenspace2pixel(end.x, canvasHeight, sightData.envZoomedIn);
				eyPX = Conversion.get().screenspace2pixel(end.y, canvasHeight, sightData.envZoomedIn);
			}
			
			
		} else if(movement == Movement.MOVE) {
			
			double rangeCorrectionMil = 0;
			
			if(ammoData != null) {
				List<Vector2d> fittingPoints = new ArrayList<Vector2d>();
				fittingPoints.add(new Vector2d(0, 0));
				for(int i=0; i<ammoData.markerRanges.size(); i++) {
					Vector2d p = new Vector2d(ammoData.markerRanges.get(i).y/100, ammoData.markerRanges.get(i).x);
					if(ammoData.zoomedIn) {
						p.y /= Conversion.get().zoomInMul;
					}
					fittingPoints.add(p);
				}
				Vector3d fittingParams = SightUtils.fitBallisticFunction(fittingPoints, 1);
				if(fittingParams == null) {
					return null;
				}
				final double rangeCorrectionResultPX = SightUtils.ballisticFunction(sightData.envRangeCorrection/100.0, fittingParams);
				rangeCorrectionMil = Conversion.get().pixel2mil(rangeCorrectionResultPX, canvasHeight, false);
				
			} else {
				// found values by testing  50m = 0.6875mil
				rangeCorrectionMil = sightData.envRangeCorrection * (0.6875/50.0);
			}
			
			final double rangeCorrectionPX = Conversion.get().mil2pixel(rangeCorrectionMil, canvasHeight, sightData.envZoomedIn);
			
			if(useThousandth) {
				sxPX = Conversion.get().mil2pixel(start.x, canvasHeight, sightData.envZoomedIn);
				syPX = Conversion.get().mil2pixel(start.y, canvasHeight, sightData.envZoomedIn);
				exPX = Conversion.get().mil2pixel(end.x, canvasHeight, sightData.envZoomedIn);
				eyPX = Conversion.get().mil2pixel(end.y, canvasHeight, sightData.envZoomedIn);
			} else {
				sxPX = Conversion.get().screenspace2pixel(start.x, canvasHeight, sightData.envZoomedIn);
				syPX = Conversion.get().screenspace2pixel(start.y, canvasHeight, sightData.envZoomedIn);
				exPX = Conversion.get().screenspace2pixel(end.x, canvasHeight, sightData.envZoomedIn);
				eyPX = Conversion.get().screenspace2pixel(end.y, canvasHeight, sightData.envZoomedIn);
			}
			
			
			if(sightData.gnrApplyCorrectionToGun) {
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
			final double radius = useThousandth ? centerOW.dist(radCenter) : Conversion.get().screenspace2mil(centerOW.dist(radCenter), sightData.envZoomedIn);
			if(MathUtils.isNearlyEqual(radius, 0)) {
				return null;
			}
			
			double rangeCorrAngleSMil = SightUtils.rangeCorrection_meters2sovmil(sightData.envRangeCorrection);
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
				sxPX = Conversion.get().mil2pixel(toStart.x, canvasHeight, sightData.envZoomedIn);
				syPX = Conversion.get().mil2pixel(toStart.y, canvasHeight, sightData.envZoomedIn);
				exPX = Conversion.get().mil2pixel(toEnd.x, canvasHeight, sightData.envZoomedIn);
				eyPX = Conversion.get().mil2pixel(toEnd.y, canvasHeight, sightData.envZoomedIn);
			} else {
				sxPX = Conversion.get().screenspace2pixel(toStart.x, canvasHeight, sightData.envZoomedIn);
				syPX = Conversion.get().screenspace2pixel(toStart.y, canvasHeight, sightData.envZoomedIn);
				exPX = Conversion.get().screenspace2pixel(toEnd.x, canvasHeight, sightData.envZoomedIn);
				eyPX = Conversion.get().screenspace2pixel(toEnd.y, canvasHeight, sightData.envZoomedIn);
			}
			
		}
		
		
		sxPX += canvasWidth/2;
		syPX += canvasHeight/2;
		exPX += canvasWidth/2;
		eyPX += canvasHeight/2;
		
		layoutData.start.set(sxPX, syPX);
		layoutData.end.set(exPX, eyPX);
		layoutData.lineSize = sightData.gnrLineSize*sightData.gnrFontScale;
		return layoutData;
	}
	
}
