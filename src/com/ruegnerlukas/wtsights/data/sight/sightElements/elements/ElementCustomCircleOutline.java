package com.ruegnerlukas.wtsights.data.sight.sightElements.elements;

import com.ruegnerlukas.simplemath.MathUtils;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.simplemath.vectors.vec4.Vector4d;
import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.layouts.LayoutCircleOutlineObject;
import com.ruegnerlukas.wtutils.Conversion;
import com.ruegnerlukas.wtutils.SightUtils;

public class ElementCustomCircleOutline extends ElementCustomObject {

	
	public Vector2d 	position 	= new Vector2d(0.1,0);
	public double 		diameter 	= 0.1;
	public Vector2d 	segment 	= new Vector2d(0, 360);
	public double 		size 		= 1;
	
	
	
	
	public ElementCustomCircleOutline() {
		this(ElementType.CUSTOM_CIRCLE_OUTLINE.defaultName);
	}
	
	
	public ElementCustomCircleOutline(String name) {
		super(name, ElementType.CUSTOM_CIRCLE_OUTLINE);
		this.setLayout(new LayoutCircleOutlineObject());
	}
	
	


	
	@Override
	public LayoutCircleOutlineObject layout(DataPackage data, double canvasWidth, double canvasHeight) {
		
		LayoutCircleOutlineObject layout = (LayoutCircleOutlineObject)getLayout();
		
		if(isDirty()) {
			setDirty(false);
			
			double xPX = 0;
			double yPX = 0;
			double dPX = 0;
			
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
			
			if(movement == Movement.STATIC) {
				
				if(useThousandth) {
					xPX = Conversion.get().mil2pixel(position.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX = Conversion.get().mil2pixel(position.y, canvasHeight, data.dataSight.envZoomedIn);
					dPX = Conversion.get().mil2pixel(diameter, canvasHeight, data.dataSight.envZoomedIn);
				} else {
					xPX = Conversion.get().screenspace2pixel(position.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX = Conversion.get().screenspace2pixel(position.y, canvasHeight, data.dataSight.envZoomedIn);
					dPX = Conversion.get().screenspace2pixel(diameter, canvasHeight, data.dataSight.envZoomedIn);
				}
				
				
			} else if(movement == Movement.MOVE) {
				
				if(useThousandth) {
					xPX = Conversion.get().mil2pixel(position.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX = Conversion.get().mil2pixel(position.y, canvasHeight, data.dataSight.envZoomedIn);
					dPX = Conversion.get().mil2pixel(diameter, canvasHeight, data.dataSight.envZoomedIn);
				} else {
					xPX = Conversion.get().screenspace2pixel(position.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX = Conversion.get().screenspace2pixel(position.y, canvasHeight, data.dataSight.envZoomedIn);
					dPX = Conversion.get().screenspace2pixel(diameter, canvasHeight, data.dataSight.envZoomedIn);
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
					dPX = Conversion.get().mil2pixel(diameter, canvasHeight, data.dataSight.envZoomedIn);
				} else {
					xPX = Conversion.get().screenspace2pixel(toCenter.x, canvasHeight, data.dataSight.envZoomedIn);
					yPX = Conversion.get().screenspace2pixel(toCenter.y, canvasHeight, data.dataSight.envZoomedIn);
					dPX = Conversion.get().screenspace2pixel(diameter, canvasHeight, data.dataSight.envZoomedIn);
				}
				
			}
			
			
			xPX += canvasWidth/2;
			yPX += canvasHeight/2;
			
			layout.lineWidth = size*data.dataSight.gnrFontScale;
			
			if(MathUtils.isNearlyEqual(segment.x, 0.0, false) && MathUtils.isNearlyEqual(segment.y, 360.0, true)) {
				layout.useLineSegments = false;
				layout.circle.set(xPX, yPX, dPX/2);
				
			} else {

				layout.circle.set(xPX, yPX, dPX/2);
				layout.useLineSegments = true;
				
				final double angleStart = segment.x;
				final double angleEnd = segment.y;
				if(angleStart > angleEnd) {
					return null;
				}
				
				int nLines = Math.min(360, Math.max((int) (((angleEnd-angleStart)/10.0) * (dPX/100.0)), 15));
				double angleStep = (angleEnd-angleStart)/nLines;
				
				if(layout.lines == null || layout.lines.length != nLines) {
					layout.lines = new Vector4d[nLines];
					for(int i=0; i<layout.lines.length; i++) {
						layout.lines[i] = new Vector4d();
					}
				}
				
				Vector2d pointer = new Vector2d(0,1).rotateDeg(-angleStart).setLength(dPX/2);
				for(int i=0; i<nLines; i++) {
					final double x0 = xPX + pointer.x;
					final double y0 = yPX + pointer.y;
					pointer.rotateDeg(-angleStep).setLength(dPX/2);
					final double x1 = xPX + pointer.x;
					final double y1 = yPX + pointer.y;
					layout.lines[i].set(x0, y0, x1, y1);
				}
				
			}
			
		}

		return layout;
	}
	
}
