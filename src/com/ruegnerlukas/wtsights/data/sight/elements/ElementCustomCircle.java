package com.ruegnerlukas.wtsights.data.sight.elements;

import com.ruegnerlukas.simplemath.MathUtils;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.simplemath.vectors.vec4.Vector4d;
import com.ruegnerlukas.wtsights.data.WorkingData;
import com.ruegnerlukas.wtsights.data.sight.elements.layouts.LayoutCircleObject;
import com.ruegnerlukas.wtutils.Conversion;
import com.ruegnerlukas.wtutils.SightUtils;

public class ElementCustomCircle extends ElementCustomObject {

	
	public Vector2d 	position 	= new Vector2d(0,0);
	public double 		diameter 	= 0.1;
	public Vector2d 	segment 	= new Vector2d(0, 360);
	public double 		size 		= 1;
	
	public LayoutCircleObject layoutData = new LayoutCircleObject();
	
	
	
	
	public ElementCustomCircle(String name) {
		super(name, ElementType.CUSTOM_CIRCLE);
	}
	
	
	public ElementCustomCircle() {
		super(ElementType.CUSTOM_CIRCLE.defaultName, ElementType.CUSTOM_CIRCLE);
	}


	
	
	
	@Override
	public void setDirty() {
		this.layoutData.dirty = true;
	}
	
	
	
	@Override
	public LayoutCircleObject layout(WorkingData data, double canvasWidth, double canvasHeight) {
		
		if(!layoutData.dirty) {
			return layoutData;
		}
		layoutData.dirty = false;
		
		double xPX = 0;
		double yPX = 0;
		double dPX = 0;
		
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
			
			Vector2d toCenter = Vector2d.createVectorAB(radCenter, position);
			if(MathUtils.isNearlyEqual(toCenter.length2(), 0)) {
				toCenter.set(position);
			} else {
				toCenter.rotateDeg(-angle).rotateRad(-rangeAngle);
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
		
		layoutData.lineWidth = size*data.dataSight.gnrFontScale;
		
		if(MathUtils.isNearlyEqual(segment.x, 0.0, false) && MathUtils.isNearlyEqual(segment.y, 360.0, true)) {
			layoutData.useLineSegments = false;
			layoutData.circle.set(xPX, yPX, dPX/2);
			
		} else {

			layoutData.circle.set(xPX, yPX, dPX/2);
			layoutData.useLineSegments = true;
			
			final double angleStart = segment.x;
			final double angleEnd = segment.y;
			if(angleStart > angleEnd) {
				return null;
			}
			
			int nLines = Math.min(360, Math.max((int) (((angleEnd-angleStart)/10.0) * (dPX/100.0)), 15));
			double angleStep = (angleEnd-angleStart)/nLines;
			
			if(layoutData.lines == null || layoutData.lines.length != nLines) {
				layoutData.lines = new Vector4d[nLines];
				for(int i=0; i<layoutData.lines.length; i++) {
					layoutData.lines[i] = new Vector4d();
				}
			}
			
			Vector2d pointer = new Vector2d(0,1).rotateDeg(-angleStart).setLength(dPX/2);
			for(int i=0; i<nLines; i++) {
				final double x0 = xPX + pointer.x;
				final double y0 = yPX + pointer.y;
				pointer.rotateDeg(-angleStep).setLength(dPX/2);
				final double x1 = xPX + pointer.x;
				final double y1 = yPX + pointer.y;
				layoutData.lines[i].set(x0, y0, x1, y1);
			}
			
		}

		return layoutData;
		
	}
	
}
