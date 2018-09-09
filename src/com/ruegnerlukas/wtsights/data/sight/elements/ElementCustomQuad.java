package com.ruegnerlukas.wtsights.data.sight.elements;

import java.util.ArrayList;
import java.util.List;

import com.ruegnerlukas.simplemath.MathUtils;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.simplemath.vectors.vec3.Vector3d;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationAmmoData;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationData;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.data.sight.elements.layouts.LayoutQuadObject;
import com.ruegnerlukas.wtutils.Conversion;
import com.ruegnerlukas.wtutils.SightUtils;

public class ElementCustomQuad extends ElementCustomObject {

	
	public Vector2d pos1 = new Vector2d(-0.1, -0.1);
	public Vector2d pos2 = new Vector2d(+0.1, -0.1);
	public Vector2d pos3 = new Vector2d(+0.1, +0.1);
	public Vector2d pos4 = new Vector2d(-0.1, +0.1);
	
	public LayoutQuadObject layout = new LayoutQuadObject();
	
	
	
	
	public ElementCustomQuad(String name) {
		super(name, ElementType.CUSTOM_QUAD);
	}
	
	
	public ElementCustomQuad() {
		super(ElementType.CUSTOM_QUAD.defaultName, ElementType.CUSTOM_QUAD);
	}
	
	
	
	
	
	
	@Override
	public LayoutQuadObject layout(SightData sightData, CalibrationData calibData, CalibrationAmmoData ammoData, double canvasWidth, double canvasHeight) {

		double xPX1 = 0;
		double yPX1 = 0;
		double xPX2 = 0;
		double yPX2 = 0;
		double xPX3 = 0;
		double yPX3 = 0;
		double xPX4 = 0;
		double yPX4 = 0;
		
		if(movement == Movement.STATIC) {
			
			if(useThousandth) {
				xPX1 = Conversion.get().mil2pixel(pos1.x, canvasHeight, sightData.envZoomedIn);
				yPX1 = Conversion.get().mil2pixel(pos1.y, canvasHeight, sightData.envZoomedIn);
				xPX2 = Conversion.get().mil2pixel(pos2.x, canvasHeight, sightData.envZoomedIn);
				yPX2 = Conversion.get().mil2pixel(pos2.y, canvasHeight, sightData.envZoomedIn);
				xPX3 = Conversion.get().mil2pixel(pos3.x, canvasHeight, sightData.envZoomedIn);
				yPX3 = Conversion.get().mil2pixel(pos3.y, canvasHeight, sightData.envZoomedIn);
				xPX4 = Conversion.get().mil2pixel(pos4.x, canvasHeight, sightData.envZoomedIn);
				yPX4 = Conversion.get().mil2pixel(pos4.y, canvasHeight, sightData.envZoomedIn);
			} else {
				xPX1 = Conversion.get().screenspace2pixel(pos1.x, canvasHeight, sightData.envZoomedIn);
				yPX1 = Conversion.get().screenspace2pixel(pos1.y, canvasHeight, sightData.envZoomedIn);
				xPX2 = Conversion.get().screenspace2pixel(pos2.x, canvasHeight, sightData.envZoomedIn);
				yPX2 = Conversion.get().screenspace2pixel(pos2.y, canvasHeight, sightData.envZoomedIn);
				xPX3 = Conversion.get().screenspace2pixel(pos3.x, canvasHeight, sightData.envZoomedIn);
				yPX3 = Conversion.get().screenspace2pixel(pos3.y, canvasHeight, sightData.envZoomedIn);
				xPX4 = Conversion.get().screenspace2pixel(pos4.x, canvasHeight, sightData.envZoomedIn);
				yPX4 = Conversion.get().screenspace2pixel(pos4.y, canvasHeight, sightData.envZoomedIn);
			}
			
			
			
		} else if(movement == Movement.MOVE) {
			
			if(useThousandth) {
				xPX1 = Conversion.get().mil2pixel(pos1.x, canvasHeight, sightData.envZoomedIn);
				yPX1 = Conversion.get().mil2pixel(pos1.y, canvasHeight, sightData.envZoomedIn);
				xPX2 = Conversion.get().mil2pixel(pos2.x, canvasHeight, sightData.envZoomedIn);
				yPX2 = Conversion.get().mil2pixel(pos2.y, canvasHeight, sightData.envZoomedIn);
				xPX3 = Conversion.get().mil2pixel(pos3.x, canvasHeight, sightData.envZoomedIn);
				yPX3 = Conversion.get().mil2pixel(pos3.y, canvasHeight, sightData.envZoomedIn);
				xPX4 = Conversion.get().mil2pixel(pos4.x, canvasHeight, sightData.envZoomedIn);
				yPX4 = Conversion.get().mil2pixel(pos4.y, canvasHeight, sightData.envZoomedIn);
			} else {
				xPX1 = Conversion.get().screenspace2pixel(pos1.x, canvasHeight, sightData.envZoomedIn);
				yPX1 = Conversion.get().screenspace2pixel(pos1.y, canvasHeight, sightData.envZoomedIn);
				xPX2 = Conversion.get().screenspace2pixel(pos2.x, canvasHeight, sightData.envZoomedIn);
				yPX2 = Conversion.get().screenspace2pixel(pos2.y, canvasHeight, sightData.envZoomedIn);
				xPX3 = Conversion.get().screenspace2pixel(pos3.x, canvasHeight, sightData.envZoomedIn);
				yPX3 = Conversion.get().screenspace2pixel(pos3.y, canvasHeight, sightData.envZoomedIn);
				xPX4 = Conversion.get().screenspace2pixel(pos4.x, canvasHeight, sightData.envZoomedIn);
				yPX4 = Conversion.get().screenspace2pixel(pos4.y, canvasHeight, sightData.envZoomedIn);
			}
			
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
			
			if(sightData.gnrApplyCorrectionToGun) {
				yPX1 -= rangeCorrectionPX;
				yPX2 -= rangeCorrectionPX;
				yPX3 -= rangeCorrectionPX;
				yPX4 -= rangeCorrectionPX;
			} else {
				yPX1 += rangeCorrectionPX;
				yPX2 += rangeCorrectionPX;
				yPX3 += rangeCorrectionPX;
				yPX4 += rangeCorrectionPX;
			}
			
		} else if(movement == Movement.MOVE_RADIAL) {

			Vector2d centerOW = new Vector2d();
			
			if(autoCenter) {
				centerOW.add(pos1).add(pos2).add(pos3).add(pos4.x).scale(1.0/4.0);
			} else {
				centerOW = center;
			}
			final double radius = useThousandth ? centerOW.dist(radCenter) : Conversion.get().screenspace2mil(centerOW.dist(radCenter), sightData.envZoomedIn);
			if(MathUtils.isNearlyEqual(radius, 0)) {
				return null;
			}
			
			double rangeCorrAngleSMil = SightUtils.rangeCorrection_meters2sovmil(sightData.envRangeCorrection);
			double rangeAngle = SightUtils.calcAngle_rad(rangeCorrAngleSMil, radius, speed);
		
			Vector2d toPos1 = Vector2d.createVectorAB(radCenter, pos1);
			Vector2d toPos2 = Vector2d.createVectorAB(radCenter, pos2);
			Vector2d toPos3 = Vector2d.createVectorAB(radCenter, pos3);
			Vector2d toPos4 = Vector2d.createVectorAB(radCenter, pos4);

			if(MathUtils.isNearlyEqual(toPos1.length2(), 0) && MathUtils.isNearlyEqual(toPos2.length2(), 0) && MathUtils.isNearlyEqual(toPos3.length2(), 0) && MathUtils.isNearlyEqual(toPos4.length2(), 0)) {
				toPos1.set(pos1);
				toPos2.set(pos2);
				toPos3.set(pos3);
				toPos4.set(pos4);
			} else {
				toPos1.rotateDeg(-angle).rotateRad(-rangeAngle);
				toPos2.rotateDeg(-angle).rotateRad(-rangeAngle);
				toPos3.rotateDeg(-angle).rotateRad(-rangeAngle);
				toPos4.rotateDeg(-angle).rotateRad(-rangeAngle);
			}

			if(useThousandth) {
				xPX1 = Conversion.get().mil2pixel(toPos1.x, canvasHeight, sightData.envZoomedIn);
				yPX1 = Conversion.get().mil2pixel(toPos1.y, canvasHeight, sightData.envZoomedIn);
				xPX2 = Conversion.get().mil2pixel(toPos2.x, canvasHeight, sightData.envZoomedIn);
				yPX2 = Conversion.get().mil2pixel(toPos2.y, canvasHeight, sightData.envZoomedIn);
				xPX3 = Conversion.get().mil2pixel(toPos3.x, canvasHeight, sightData.envZoomedIn);
				yPX3 = Conversion.get().mil2pixel(toPos3.y, canvasHeight, sightData.envZoomedIn);
				xPX4 = Conversion.get().mil2pixel(toPos4.x, canvasHeight, sightData.envZoomedIn);
				yPX4 = Conversion.get().mil2pixel(toPos4.y, canvasHeight, sightData.envZoomedIn);
			} else {
				xPX1 = Conversion.get().screenspace2pixel(toPos1.x, canvasHeight, sightData.envZoomedIn);
				yPX1 = Conversion.get().screenspace2pixel(toPos1.y, canvasHeight, sightData.envZoomedIn);
				xPX2 = Conversion.get().screenspace2pixel(toPos2.x, canvasHeight, sightData.envZoomedIn);
				yPX2 = Conversion.get().screenspace2pixel(toPos2.y, canvasHeight, sightData.envZoomedIn);
				xPX3 = Conversion.get().screenspace2pixel(toPos3.x, canvasHeight, sightData.envZoomedIn);
				yPX3 = Conversion.get().screenspace2pixel(toPos3.y, canvasHeight, sightData.envZoomedIn);
				xPX4 = Conversion.get().screenspace2pixel(toPos4.x, canvasHeight, sightData.envZoomedIn);
				yPX4 = Conversion.get().screenspace2pixel(toPos4.y, canvasHeight, sightData.envZoomedIn);
			}
			
			
		}
		
		xPX1 += canvasWidth/2;
		yPX1 += canvasHeight/2;
		xPX2 += canvasWidth/2;
		yPX2 += canvasHeight/2;
		xPX3 += canvasWidth/2;
		yPX3 += canvasHeight/2;
		xPX4 += canvasWidth/2;
		yPX4 += canvasHeight/2;
		
		layout.p0.set(xPX1, yPX1);
		layout.p1.set(xPX2, yPX2);
		layout.p2.set(xPX3, yPX3);
		layout.p3.set(xPX4, yPX4);
	
		return layout;
	}
	
}
