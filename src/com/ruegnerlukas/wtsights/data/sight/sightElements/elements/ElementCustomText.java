package com.ruegnerlukas.wtsights.data.sight.sightElements.elements;

import com.ruegnerlukas.simplemath.MathUtils;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.layouts.LayoutTextObject;
import com.ruegnerlukas.wtutils.Conversion;
import com.ruegnerlukas.wtutils.SightUtils;
import com.ruegnerlukas.wtutils.SightUtils.TextAlign;

public class ElementCustomText extends ElementCustomObject {

	
	public String 		text 		= "text";
	public Vector2d 	position 	= new Vector2d(0.1,0);
	public double 		size 		= 1;
	public TextAlign 	align 		= TextAlign.LEFT;
	
	
	
	public ElementCustomText() {
		this(ElementType.CUSTOM_TEXT.defaultName);
	}
	
	
	public ElementCustomText(String name) {
		super(name, ElementType.CUSTOM_TEXT);
		this.setLayout(new LayoutTextObject());
	}
	
	

	
	@Override
	public LayoutTextObject layout(DataPackage data, double canvasWidth, double canvasHeight) {

		LayoutTextObject layout = (LayoutTextObject)getLayout();
		
		if(isDirty()) {
			setDirty(false);
			
			if(movement == Movement.MOVE_RADIAL) {
				
				if(autoCenter) {
					layout.center.set(position);
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
			
			double xPX = 0;
			double yPX = 0;
			
			if(movement == Movement.STATIC) {
			
				if(useThousandth) {
					xPX = Conversion.get().mil2pixel(position.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX = Conversion.get().mil2pixel(position.y, canvasHeight, data.dataSight.envZoomedIn);
				} else {
					xPX = Conversion.get().screenspace2pixel(position.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX = Conversion.get().screenspace2pixel(position.y, canvasHeight, data.dataSight.envZoomedIn);
				}
				
				
			} else if(movement == Movement.MOVE) {
				
				if(useThousandth) {
					xPX = Conversion.get().mil2pixel(position.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX = Conversion.get().mil2pixel(position.y, canvasHeight, data.dataSight.envZoomedIn);
				} else {
					xPX = Conversion.get().screenspace2pixel(position.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX = Conversion.get().screenspace2pixel(position.y, canvasHeight, data.dataSight.envZoomedIn);
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
				final double radius = useThousandth ? centerOW.dist(radCenter) : Conversion.get().screenspace2mil(centerOW.dist(radCenter), data.dataSight.envZoomedIn);
				if(MathUtils.isNearlyEqual(radius, 0)) {
					return null;
				}
				
				double rangeCorrAngleSMil = SightUtils.rangeCorrection_meters2sovmil(data.dataSight.envRangeCorrection);
				double rangeAngle = SightUtils.calcAngle_rad(rangeCorrAngleSMil, radius, speed);
				
				if(!data.dataSight.gnrApplyCorrectionToGun) {
					rangeAngle = -rangeAngle;
				}
				
				Vector2d toCenter = Vector2d.createVectorAB(radCenter, position);
				if(MathUtils.isNearlyEqual(toCenter.length2(), 0)) {
					toCenter.set(position);
				} else {
					toCenter.rotateDeg(-angle).rotateRad(rangeAngle).add(radCenter);
				}

				if(useThousandth) {
					xPX = Conversion.get().mil2pixel(toCenter.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX = Conversion.get().mil2pixel(toCenter.y, canvasHeight, data.dataSight.envZoomedIn);
				} else {
					xPX = Conversion.get().screenspace2pixel(toCenter.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX = Conversion.get().screenspace2pixel(toCenter.y, canvasHeight, data.dataSight.envZoomedIn);
				}
				
			}
			
			xPX += canvasWidth/2;
			yPX += canvasHeight/2;
		
			layout.fontSize = (data.dataSight.envZoomedIn?18.5:17.5) * data.dataSight.gnrFontScale * size * (data.dataSight.envZoomedIn ? Conversion.get().zoomInMul : 1);
			layout.pos.set(xPX, yPX);
		}
		
		return layout;
	}
	
}
