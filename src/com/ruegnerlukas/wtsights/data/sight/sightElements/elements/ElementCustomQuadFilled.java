package com.ruegnerlukas.wtsights.data.sight.sightElements.elements;

import com.ruegnerlukas.simplemath.MathUtils;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.layouts.LayoutQuadFilledObject;
import com.ruegnerlukas.wtutils.Conversion;
import com.ruegnerlukas.wtutils.SightUtils;

public class ElementCustomQuadFilled extends ElementCustomObject {

	
	public Vector2d pos1 = new Vector2d(+0.1, -0.1);
	public Vector2d pos2 = new Vector2d(+0.2, -0.1);
	public Vector2d pos3 = new Vector2d(+0.2, +0.1);
	public Vector2d pos4 = new Vector2d(+0.1, +0.1);
	
	
	
	public ElementCustomQuadFilled() {
		this(ElementType.CUSTOM_QUAD_FILLED.defaultName);
	}
	
	
	public ElementCustomQuadFilled(String name) {
		super(name, ElementType.CUSTOM_QUAD_FILLED);
		this.setLayout(new LayoutQuadFilledObject());
	}
	
	
	

	
	@Override
	public LayoutQuadFilledObject layout(DataPackage data, double canvasWidth, double canvasHeight) {

		LayoutQuadFilledObject layout = (LayoutQuadFilledObject)getLayout();
		
		if(isDirty()) {
			setDirty(false);
			
			if(movement == Movement.MOVE_RADIAL) {
				
				if(autoCenter) {
					layout.center.set(0).add(pos1).add(pos2).add(pos3).add(pos4.x).scale(1.0/4.0);
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
					xPX1 = Conversion.get().mil2pixel(pos1.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX1 = Conversion.get().mil2pixel(pos1.y, canvasHeight, data.dataSight.envZoomedIn);
					xPX2 = Conversion.get().mil2pixel(pos2.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX2 = Conversion.get().mil2pixel(pos2.y, canvasHeight, data.dataSight.envZoomedIn);
					xPX3 = Conversion.get().mil2pixel(pos3.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX3 = Conversion.get().mil2pixel(pos3.y, canvasHeight, data.dataSight.envZoomedIn);
					xPX4 = Conversion.get().mil2pixel(pos4.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX4 = Conversion.get().mil2pixel(pos4.y, canvasHeight, data.dataSight.envZoomedIn);
				} else {
					xPX1 = Conversion.get().screenspace2pixel(pos1.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX1 = Conversion.get().screenspace2pixel(pos1.y, canvasHeight, data.dataSight.envZoomedIn);
					xPX2 = Conversion.get().screenspace2pixel(pos2.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX2 = Conversion.get().screenspace2pixel(pos2.y, canvasHeight, data.dataSight.envZoomedIn);
					xPX3 = Conversion.get().screenspace2pixel(pos3.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX3 = Conversion.get().screenspace2pixel(pos3.y, canvasHeight, data.dataSight.envZoomedIn);
					xPX4 = Conversion.get().screenspace2pixel(pos4.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX4 = Conversion.get().screenspace2pixel(pos4.y, canvasHeight, data.dataSight.envZoomedIn);
				}
				
				
				
			} else if(movement == Movement.MOVE) {
				
				if(useThousandth) {
					xPX1 = Conversion.get().mil2pixel(pos1.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX1 = Conversion.get().mil2pixel(pos1.y, canvasHeight, data.dataSight.envZoomedIn);
					xPX2 = Conversion.get().mil2pixel(pos2.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX2 = Conversion.get().mil2pixel(pos2.y, canvasHeight, data.dataSight.envZoomedIn);
					xPX3 = Conversion.get().mil2pixel(pos3.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX3 = Conversion.get().mil2pixel(pos3.y, canvasHeight, data.dataSight.envZoomedIn);
					xPX4 = Conversion.get().mil2pixel(pos4.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX4 = Conversion.get().mil2pixel(pos4.y, canvasHeight, data.dataSight.envZoomedIn);
				} else {
					xPX1 = Conversion.get().screenspace2pixel(pos1.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX1 = Conversion.get().screenspace2pixel(pos1.y, canvasHeight, data.dataSight.envZoomedIn);
					xPX2 = Conversion.get().screenspace2pixel(pos2.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX2 = Conversion.get().screenspace2pixel(pos2.y, canvasHeight, data.dataSight.envZoomedIn);
					xPX3 = Conversion.get().screenspace2pixel(pos3.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX3 = Conversion.get().screenspace2pixel(pos3.y, canvasHeight, data.dataSight.envZoomedIn);
					xPX4 = Conversion.get().screenspace2pixel(pos4.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX4 = Conversion.get().screenspace2pixel(pos4.y, canvasHeight, data.dataSight.envZoomedIn);
				}
				
				double rangeCorrectionMil = 0;
				
				if(data.elementBallistic != null) {
					final double rangeCorrectionResultPX = data.elementBallistic.function.eval(data.dataSight.envRangeCorrection);
					rangeCorrectionMil = Conversion.get().pixel2mil(rangeCorrectionResultPX, canvasHeight, false);
					
				} else {
					// found values by testing  50m = 0.6875mil
					rangeCorrectionMil = data.dataSight.envRangeCorrection * (0.6875/50.0);
				}
				
				final double rangeCorrectionPX = Conversion.get().mil2pixel(rangeCorrectionMil, canvasHeight, data.dataSight.envZoomedIn);
				
				if(data.dataSight.gnrApplyCorrectionToGun) {
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
				
				final double radius = useThousandth ? centerOW.dist(radCenter) : Conversion.get().screenspace2mil(centerOW.dist(radCenter), data.dataSight.envZoomedIn);
				if(MathUtils.isNearlyEqual(radius, 0)) {
					return null;
				}
				
				double rangeCorrAngleSMil = SightUtils.rangeCorrection_meters2sovmil(data.dataSight.envRangeCorrection, data.dataSight.gnrThousandth);
				double rangeAngle = SightUtils.calcAngle_rad(rangeCorrAngleSMil, radius, speed);
			
				if(!data.dataSight.gnrApplyCorrectionToGun) {
					rangeAngle = -rangeAngle;
				}
			
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
					toPos1.rotateDeg(-angle).rotateRad(rangeAngle).add(radCenter);
					toPos2.rotateDeg(-angle).rotateRad(rangeAngle).add(radCenter);
					toPos3.rotateDeg(-angle).rotateRad(rangeAngle).add(radCenter);
					toPos4.rotateDeg(-angle).rotateRad(rangeAngle).add(radCenter);
				}

				if(useThousandth) {
					xPX1 = Conversion.get().mil2pixel(toPos1.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX1 = Conversion.get().mil2pixel(toPos1.y, canvasHeight, data.dataSight.envZoomedIn);
					xPX2 = Conversion.get().mil2pixel(toPos2.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX2 = Conversion.get().mil2pixel(toPos2.y, canvasHeight, data.dataSight.envZoomedIn);
					xPX3 = Conversion.get().mil2pixel(toPos3.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX3 = Conversion.get().mil2pixel(toPos3.y, canvasHeight, data.dataSight.envZoomedIn);
					xPX4 = Conversion.get().mil2pixel(toPos4.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX4 = Conversion.get().mil2pixel(toPos4.y, canvasHeight, data.dataSight.envZoomedIn);
				} else {
					xPX1 = Conversion.get().screenspace2pixel(toPos1.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX1 = Conversion.get().screenspace2pixel(toPos1.y, canvasHeight, data.dataSight.envZoomedIn);
					xPX2 = Conversion.get().screenspace2pixel(toPos2.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX2 = Conversion.get().screenspace2pixel(toPos2.y, canvasHeight, data.dataSight.envZoomedIn);
					xPX3 = Conversion.get().screenspace2pixel(toPos3.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX3 = Conversion.get().screenspace2pixel(toPos3.y, canvasHeight, data.dataSight.envZoomedIn);
					xPX4 = Conversion.get().screenspace2pixel(toPos4.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX4 = Conversion.get().screenspace2pixel(toPos4.y, canvasHeight, data.dataSight.envZoomedIn);
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
		}
		
		return layout;
	}
	
}
