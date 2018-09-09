package com.ruegnerlukas.wtsights.data.sight.elements;

import java.util.ArrayList;
import java.util.List;

import com.ruegnerlukas.simplemath.MathUtils;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.simplemath.vectors.vec3.Vector3d;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationAmmoData;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationData;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.data.sight.elements.layouts.LayoutTextObject;
import com.ruegnerlukas.wtutils.Conversion;
import com.ruegnerlukas.wtutils.SightUtils;
import com.ruegnerlukas.wtutils.SightUtils.TextAlign;

public class ElementCustomText extends ElementCustomObject {

	
	public String 		text 		= "text";
	public Vector2d 	position 	= new Vector2d(0,0);
	public double 		size 		= 1;
	public TextAlign 	align 		= TextAlign.LEFT;
	
	public LayoutTextObject layout = new LayoutTextObject();
	
	
	
	
	public ElementCustomText(String name) {
		super(name, ElementType.CUSTOM_TEXT);
	}
	
	
	public ElementCustomText() {
		super(ElementType.CUSTOM_TEXT.defaultName, ElementType.CUSTOM_TEXT);
	}
	
	
	
	
	
	
	@Override
	public LayoutTextObject layout(SightData sightData, CalibrationData calibData, CalibrationAmmoData ammoData, double canvasWidth, double canvasHeight) {

		double xPX = 0;
		double yPX = 0;
		
		if(movement == Movement.STATIC) {
		
			if(useThousandth) {
				xPX = Conversion.get().mil2pixel(position.x, canvasHeight, sightData.envZoomedIn);
				yPX = Conversion.get().mil2pixel(position.y, canvasHeight, sightData.envZoomedIn);
			} else {
				xPX = Conversion.get().screenspace2pixel(position.x, canvasHeight, sightData.envZoomedIn);
				yPX = Conversion.get().screenspace2pixel(position.y, canvasHeight, sightData.envZoomedIn);
			}
			
			
		} else if(movement == Movement.MOVE) {
			
			if(useThousandth) {
				xPX = Conversion.get().mil2pixel(position.x, canvasHeight, sightData.envZoomedIn);
				yPX = Conversion.get().mil2pixel(position.y, canvasHeight, sightData.envZoomedIn);
			} else {
				xPX = Conversion.get().screenspace2pixel(position.x, canvasHeight, sightData.envZoomedIn);
				yPX = Conversion.get().screenspace2pixel(position.y, canvasHeight, sightData.envZoomedIn);
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
				yPX -= rangeCorrectionPX;
			} else {
				yPX += rangeCorrectionPX;
			}
			
			
		} else if(movement == Movement.MOVE_RADIAL) {
			
			Vector2d centerOW = new Vector2d();
			if(autoCenter) {
				centerOW.set(position);
			} else {
				centerOW = center;
			}
			final double radius = useThousandth ? centerOW.dist(radCenter) : Conversion.get().screenspace2mil(centerOW.dist(radCenter), sightData.envZoomedIn);
			if(MathUtils.isNearlyEqual(radius, 0)) {
				return null;
			}
			
			double rangeCorrAngleSMil = SightUtils.rangeCorrection_meters2sovmil(sightData.envRangeCorrection);
			double rangeAngle = SightUtils.calcAngle_rad(rangeCorrAngleSMil, radius, speed);
			
			Vector2d toCenter = Vector2d.createVectorAB(radCenter, position);
			if(MathUtils.isNearlyEqual(toCenter.length2(), 0)) {
				toCenter.set(position);
			} else {
				toCenter.rotateDeg(-angle).rotateRad(-rangeAngle);
			}

			if(useThousandth) {
				xPX = Conversion.get().mil2pixel(toCenter.x, canvasHeight, sightData.envZoomedIn);
				yPX = Conversion.get().mil2pixel(toCenter.y, canvasHeight, sightData.envZoomedIn);
			} else {
				xPX = Conversion.get().screenspace2pixel(toCenter.x, canvasHeight, sightData.envZoomedIn);
				yPX = Conversion.get().screenspace2pixel(toCenter.y, canvasHeight, sightData.envZoomedIn);
			}
			
		}
		
		xPX += canvasWidth/2;
		yPX += canvasHeight/2;
	
		layout.fontSize = (sightData.envZoomedIn?18.5:17.5) * sightData.gnrFontScale * size * (sightData.envZoomedIn ? Conversion.get().zoomInMul : 1);
		layout.pos.set(xPX, yPX);
		return layout;
	}
	
}
