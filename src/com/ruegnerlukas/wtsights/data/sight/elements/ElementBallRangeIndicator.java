package com.ruegnerlukas.wtsights.data.sight.elements;

import java.util.ArrayList;
import java.util.List;

import com.ruegnerlukas.simplemath.MathUtils;
import com.ruegnerlukas.simplemath.geometry.shapes.circle.Circlef;
import com.ruegnerlukas.simplemath.geometry.shapes.rectangle.Rectanglef;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.simplemath.vectors.vec3.Vector3d;
import com.ruegnerlukas.simplemath.vectors.vec4.Vector4d;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationAmmoData;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationData;
import com.ruegnerlukas.wtsights.data.sight.BIndicator;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.data.sight.elements.layouts.LayoutBallRangeIndicators;
import com.ruegnerlukas.wtutils.Conversion;
import com.ruegnerlukas.wtutils.SightUtils;
import com.ruegnerlukas.wtutils.SightUtils.ScaleMode;
import com.ruegnerlukas.wtutils.SightUtils.TextAlign;
import com.ruegnerlukas.wtutils.SightUtils.Thousandth;

import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ElementBallRangeIndicator extends Element {

	
	public boolean 		drawUpward 		= false;						// flip the scale and movement direction.
	public Vector2d 	position 		= new Vector2d(0.15, 0.0);		// position of the main indicator notches + labels relative to center
	public Vector2d		size			= new Vector2d(0.03, 0.02);		// size of the main indicators. vertical: length of major/minor notches; radial-lines: first value = length of lines; radial-circles: first value = strocke width, secodn value = circle radius
	public double		textShift		= 0.2;							// vertical: extends left side of major notches together with labels
	public Vector2d		textPos			= new Vector2d(0.0, 0.0);		// vertical: offset for all main indicator labels
	public TextAlign	textAlign		= TextAlign.RIGHT;				// label text alignment
	public ScaleMode	scaleMode		= ScaleMode.VERTICAL;			// vertical or radial
	public boolean		circleMode		= false;						// radial: lines or circles
	public boolean 		drawAddLines	= true;							// vertical: whether to draw the add. ball. range indicator lines alogn central vertical axis
	public Vector2d		sizeAddLine		= new Vector2d(0.005, 0.003);	// vertical: length of the add. ball. range indicator lines
	public boolean 		move 			= true;							// move or not when correcting for range
	public boolean 		drawCorrLabel	= true;							// whether to draw the range correction label
	public Vector2d		posCorrLabel	= new Vector2d(-0.2, -0.05);	// the position of the range correction label
	public double		radialStretch	= 1.0;							// radial: (for values > 1) stretches the notches/circles over longer arc
	public double		radialAngle		= 0.0;							// radial: angular displacement of the scales (in deg)
	public double		radialRadius	= 20.0;							// radial: the scales radius in screenspace or mils (see radiusUseMils)
	public boolean		radiusUseMils	= true;							// whether to use mils or screenspace for radialRadius
	public List<BIndicator> indicators = new ArrayList<BIndicator>();
	
	public LayoutBallRangeIndicators layoutData = new LayoutBallRangeIndicators();
	
	
	
	
	public ElementBallRangeIndicator(String name) {
		super(name, ElementType.BALLISTIC_RANGE_INDICATORS);
		resetIndicators();
	}
	
	
	public ElementBallRangeIndicator() {
		super(ElementType.BALLISTIC_RANGE_INDICATORS.defaultName, ElementType.BALLISTIC_RANGE_INDICATORS);
		resetIndicators();
	}
	
	
	
	
	
	
	public void resetIndicators() {
		indicators.clear();
		for(int i=200, j=1; i<=2800; i+=200, j++) {
			if(i == 0) { continue; }
			indicators.add(new BIndicator(i, (j%2==0), 0f, 0f, 0f));
		}
	}
	
	
	
	
	
	@Override
	public LayoutBallRangeIndicators layout(SightData sightData, CalibrationData calibData, CalibrationAmmoData ammoData, double canvasWidth, double canvasHeight) {
		if(scaleMode == ScaleMode.VERTICAL) {
			return layoutVertical(sightData, ammoData, canvasWidth, canvasHeight);
		} else {
			if(circleMode) {
				return layoutRadialCircles(sightData, calibData, ammoData, canvasWidth, canvasHeight);
			} else {
				return layoutRadialLines(sightData, calibData, ammoData, canvasWidth, canvasHeight);

			}
		}
	}
	
	
	
	
	public LayoutBallRangeIndicators layoutVertical(SightData sightData, CalibrationAmmoData ammoData, double canvasWidth, double canvasHeight) {
		
		if(layoutData.vCenterBounds == null || layoutData.vTextPositions == null || layoutData.vCenterBounds.length != indicators.size() ) {
			layoutData.vMainBounds = new Rectanglef[indicators.size()];
			layoutData.vCenterBounds = new Rectanglef[indicators.size()];
			layoutData.vTextPositions = new Vector2d[indicators.size()];
			for(int i=0; i<indicators.size(); i++) {
				layoutData.vMainBounds[i] = new Rectanglef();
				layoutData.vCenterBounds[i] = new Rectanglef();
				layoutData.vTextPositions[i] = new Vector2d();
			}
		}
		
		final double lineSize = 1.0 * sightData.gnrLineSize * sightData.gnrFontScale * (sightData.envZoomedIn ? Conversion.get().zoomInMul : 1);
		
		// point fitting
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
		
		// range correction
		final double rangeCorrectionResultPX = SightUtils.ballisticFunction(sightData.envRangeCorrection/100.0, fittingParams);
		final double rangeCorrectionMil = Conversion.get().pixel2mil(rangeCorrectionResultPX, canvasHeight, false);
		final double rangeCorrectionPX = move ? Conversion.get().mil2pixel(rangeCorrectionMil, canvasHeight, sightData.envZoomedIn) : 0;
		
		// draw indicators
		for(int i=0; i<indicators.size(); i++) {
			BIndicator indicator = indicators.get(i);
			Rectanglef boundsMain = layoutData.vMainBounds[i];
			Rectanglef boundsCenter = layoutData.vCenterBounds[i];
			Vector2d textPos = layoutData.vTextPositions[i];
			
			int mil = indicator.getDistance();
			boolean isMajor = indicator.isMajor();
			
			// range fitting
			double resultPX = SightUtils.ballisticFunction(mil/100.0, fittingParams);
			double rangeMil = Conversion.get().pixel2mil(resultPX, canvasHeight, false);
			double rangePixel = Conversion.get().mil2pixel(rangeMil, canvasHeight, sightData.envZoomedIn) * (drawUpward ? -1 : +1);
			rangePixel -= drawUpward ? -rangeCorrectionPX : rangeCorrectionPX;

			// CENTRAL BLOCK
			if(drawAddLines) {
				
				// x position
				double xCentral = canvasWidth/2;
				
				// y position
				double yCentral = canvasHeight/2 + rangePixel;
				
				// length
				double lengthCentral = Conversion.get().screenspace2pixel(isMajor ? sizeAddLine.x : sizeAddLine.y, canvasHeight, sightData.envZoomedIn) * sightData.gnrFontScale;
				lengthCentral = Math.max(lengthCentral, 0);
				
				// draw
				if(!MathUtils.isNearlyEqual(lengthCentral, 0)) {
					boundsCenter.set(xCentral-lengthCentral, yCentral-lineSize/2, lengthCentral*2, lineSize);
				} else {
					boundsCenter.set(-10000, -10000, 0, 0);
				}
			}
			
			
			// MAIN BLOCK
			
			// length
			double mainLength = 0;
			if(isMajor) {
				mainLength = mainLength + size.x;
				mainLength = mainLength + textShift*size.x;
				mainLength = mainLength + (0.03 * size.x);
			} else {
				mainLength = mainLength + size.y;
				mainLength = mainLength + (0.03 * size.y);
			}
			mainLength = mainLength + Math.abs(indicators.get(i).getExtend());
			mainLength = mainLength * sightData.gnrFontScale;
			mainLength = Conversion.get().screenspace2pixel(mainLength, canvasHeight, sightData.envZoomedIn);
			mainLength = Math.max(0, mainLength);
			
			
			// x position
			double mainX = position.x;
			if(sightData.envZoomedIn) {
				mainX = mainX + 0.003 * (position.x / 0.1);
			}
			if(isMajor) {
				mainX = mainX + textShift*size.x;
			}
			mainX = mainX + (indicators.get(i).getExtend()>0 ? indicators.get(i).getExtend() : 0);
			mainX = mainX * sightData.gnrFontScale;
			mainX = Conversion.get().screenspace2pixel(mainX, canvasHeight, sightData.envZoomedIn);
			mainX = canvasWidth/2 - mainX;

			
			// y position
			double mainY = position.y;
			if(sightData.envZoomedIn) {
				mainY = mainY + 0.004 * (position.y / 0.13817484);
			}
			mainY = mainY * sightData.gnrFontScale;
			mainY = Conversion.get().screenspace2pixel(mainY, canvasHeight, sightData.envZoomedIn);
			mainY = canvasHeight/2+rangePixel + mainY;
			
			if(!MathUtils.isNearlyEqual(mainLength, 0)) {
				boundsMain.set(mainX, mainY, mainLength, lineSize);
			} else {
				boundsMain.set(-10000, -10000, 0, 0);
			}
			
			// main labels
			if(isMajor) {
				double distLabel = Conversion.get().screenspace2pixel(0.004, canvasHeight, sightData.envZoomedIn);
				double textOffX = Conversion.get().screenspace2pixel(indicators.get(i).getTextX()+this.textPos.x, canvasHeight, sightData.envZoomedIn);
				double textOffY = Conversion.get().screenspace2pixel(indicators.get(i).getTextY()+this.textPos.y, canvasHeight, sightData.envZoomedIn);
				textOffY *= sightData.gnrFontScale;
				
				if(textAlign == TextAlign.LEFT)   { }
				if(textAlign == TextAlign.CENTER) { textOffX -= distLabel/2; }
				if(textAlign == TextAlign.RIGHT)  { textOffX -= distLabel; }
				
				textOffX *= sightData.gnrFontScale;
				
				textPos.set(mainX+textOffX, mainY+textOffY);
			}
			
		}
		
		return layoutData;
	}
	
	

	
	public LayoutBallRangeIndicators layoutRadialLines(SightData sightData, CalibrationData calibData, CalibrationAmmoData ammoData, double canvasWidth, double canvasHeight) {
		
		if(layoutData.rlLines == null || layoutData.rlLines.length != indicators.size() ) {
			layoutData.rlLines = new Vector4d[indicators.size()];
			layoutData.rlTextPositions = new Vector2d[indicators.size()];
			for(int i=0; i<indicators.size(); i++) {
				layoutData.rlLines[i] = new Vector4d();
				layoutData.rlTextPositions[i] = new Vector2d();
			}
		}
	
		// line size
		final double lineSize = 1.0 * sightData.gnrLineSize * sightData.gnrFontScale;
		layoutData.rlLineSize = lineSize;
		
		// point fitting
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
		
		// origin x
		double originX = position.x;
		if (sightData.envZoomedIn) {
			originX = originX + 0.0015 * (position.x / 0.05);
		}
		originX = originX * sightData.gnrFontScale;
		originX = Conversion.get().screenspace2pixel(originX, canvasHeight, sightData.envZoomedIn);

		// origin y
		double originY = position.y;
		if (sightData.envZoomedIn) {
			originY = originY + 0.0015 * (position.y / 0.05);
		}
		originY = originY * sightData.gnrFontScale;
		originY = Conversion.get().screenspace2pixel(originY, canvasHeight, sightData.envZoomedIn);
	
		
		// radius
		double radiusMil = 0;
		if(radiusUseMils) {
			radiusMil = radialRadius;
		} else {
			radiusMil = Conversion.get().screenspace2mil(radialRadius, sightData.envZoomedIn);
		}
		
		double radiusPX = 0;
		if(radiusUseMils) {
			radiusPX = Conversion.get().mil2pixel(radialRadius, canvasHeight, sightData.envZoomedIn);
		} else {
			radiusPX = Conversion.get().screenspace2pixel(radialRadius, canvasHeight, sightData.envZoomedIn);
		}
		
		
		Conversion conversionUSSR = new Conversion();
		conversionUSSR.initialize(canvasWidth, canvasHeight, calibData.vehicle.fovOut, calibData.vehicle.fovIn, Thousandth.USSR);
		
		
		// draw indicators
		for(int i=0; i<indicators.size(); i++) {
			Vector4d line = layoutData.rlLines[i];
			Vector2d textPosition = layoutData.rlTextPositions[i];
			BIndicator indicator = indicators.get(i);
			
			
			int meters = indicator.getDistance();
			boolean isMajor = indicator.isMajor();
			Vector2d dir = new Vector2d(0, radiusPX);

			// angle offset
			double angleOffset = radialAngle;
			dir.rotateDeg(-angleOffset);
			
			// angle range
			double resultPX = SightUtils.ballisticFunction(meters/100.0, fittingParams);
			double rangeMil = conversionUSSR.pixel2mil(resultPX, canvasHeight, false);
			double rangeAngle     = ((rangeMil) / radiusMil) * radialStretch;

			// angle correction
			double rangeCorrAngleSMil = SightUtils.rangeCorrection_meters2sovmil(sightData.envRangeCorrection);
			double angleCorrection = SightUtils.calcAngle_deg(rangeCorrAngleSMil, radialRadius, radialStretch);

			// rotate
			if(drawUpward) {
				dir.rotateRad(+rangeAngle).rotateDeg(-angleCorrection);
			} else {
				dir.rotateRad(-rangeAngle).rotateDeg(+angleCorrection);
			}
			
			// length
			double length = Conversion.get().screenspace2pixel(size.x, canvasHeight, sightData.envZoomedIn);
			length = length * sightData.gnrFontScale;
			
			// line position
			Vector2d posStart = dir.copy().setLength(dir.length()-length/2);
			Vector2d posEnd = dir.copy().setLength(dir.length()+length/2);

			
			// line
			if(!MathUtils.isNearlyEqual(length, 0)) {
				line.set(
						canvasWidth/2 - originX + posStart.x,
						canvasHeight/2 - originY + posStart.y,
						canvasWidth/2 - originX + posEnd.x,
						canvasHeight/2 - originY + posEnd.y
					);
			} else {
				line.set(-10000, -10000, -10000, -10000);
			}
			
			
			
			// labels
			if(isMajor) {
				double textOffset = (Conversion.get().screenspace2pixel(indicators.get(i).getTextX()+this.textPos.x, canvasHeight, sightData.envZoomedIn)*sightData.gnrFontScale);
				textPosition.set(canvasWidth/2 - originX + posEnd.x + dir.copy().setLength(textOffset).x, canvasHeight/2 - originY + posEnd.y + dir.copy().setLength(textOffset).y);
			}
			
		}
		
		return layoutData;
	}
	
	
	
	
	public LayoutBallRangeIndicators layoutRadialCircles(SightData sightData, CalibrationData calibData, CalibrationAmmoData ammoData, double canvasWidth, double canvasHeight) {
		if(layoutData == null || layoutData.rcCircles.length != indicators.size() ) {
			layoutData.rcCircles = new Circlef[indicators.size()];
			layoutData.rcTextPositions = new Vector2d[indicators.size()];
			for(int i=0; i<indicators.size(); i++) {
				layoutData.rcCircles[i] = new Circlef();
				layoutData.rcTextPositions[i] = new Vector2d();
			}
		}

		// point fitting
		List<Vector2d> fittingPoints = new ArrayList<Vector2d>();
		fittingPoints.add(new Vector2d(0, 0));
		for (int i = 0; i < ammoData.markerRanges.size(); i++) {
			Vector2d p = new Vector2d(ammoData.markerRanges.get(i).y / 100,
					ammoData.markerRanges.get(i).x);
			if (ammoData.zoomedIn) {
				p.y /= Conversion.get().zoomInMul;
			}
			fittingPoints.add(p);
		}
		Vector3d fittingParams = SightUtils.fitBallisticFunction(fittingPoints, 1);
		if (fittingParams == null) {
			return null;
		}

		// origin x
		double originX = position.x;
		if (sightData.envZoomedIn) {
			originX = originX + 0.0015 * (position.x / 0.05);
		}
		originX = originX * sightData.gnrFontScale;
		originX = Conversion.get().screenspace2pixel(originX, canvasHeight, sightData.envZoomedIn);

		// origin y
		double originY = position.y;
		if (sightData.envZoomedIn) {
			originY = originY + 0.0015 * (position.y / 0.05);
		}
		originY = originY * sightData.gnrFontScale;
		originY = Conversion.get().screenspace2pixel(originY, canvasHeight, sightData.envZoomedIn);

		// radius
		double radiusMil = 0;
		if (radiusUseMils) {
			radiusMil = radialRadius;
		} else {
			radiusMil = Conversion.get().screenspace2mil(radialRadius, sightData.envZoomedIn);
		}

		double radiusPX = 0;
		if (radiusUseMils) {
			radiusPX = Conversion.get().mil2pixel(radialRadius, canvasHeight, sightData.envZoomedIn);
		} else {
			radiusPX = Conversion.get().screenspace2pixel(radialRadius, canvasHeight,
					sightData.envZoomedIn);
		}

		Conversion conversionUSSR = new Conversion();
		conversionUSSR.initialize(canvasWidth, canvasHeight, calibData.vehicle.fovOut, calibData.vehicle.fovIn, Thousandth.USSR);

		// draw indicators
		for (int i = 0; i < indicators.size(); i++) {
			BIndicator indicator = indicators.get(i);

			int meters = indicator.getDistance();
			boolean isMajor = indicator.isMajor();

			Vector2d dir = new Vector2d(0, radiusPX);

			// angle offset
			double angleOffset = radialAngle;
			dir.rotateDeg(-angleOffset);

			// angle range
			double resultPX = SightUtils.ballisticFunction(meters / 100.0, fittingParams);
			double rangeMil = conversionUSSR.pixel2mil(resultPX, canvasHeight, false);
			double rangeAngle = ((rangeMil) / radiusMil) * radialStretch;

			// angle correction
			double rangeCorrAngleSMil = SightUtils.rangeCorrection_meters2sovmil(sightData.envRangeCorrection);
			double angleCorrection = SightUtils.calcAngle_deg(rangeCorrAngleSMil, radialRadius,
					radialStretch);

			// rotate
			if (drawUpward) {
				dir.rotateRad(+rangeAngle).rotateDeg(-angleCorrection);
			} else {
				dir.rotateRad(-rangeAngle).rotateDeg(+angleCorrection);
			}

			// circle radius
			double circleRadius = size.y;
			circleRadius = circleRadius + indicator.getExtend();
			circleRadius = circleRadius * 2;
			circleRadius = circleRadius * sightData.gnrFontScale;
			circleRadius = Conversion.get().screenspace2pixel(circleRadius, canvasHeight, sightData.envZoomedIn);

			// circle width
			double circleWidth = size.x;
			circleWidth = circleWidth / 2;
			circleWidth = circleWidth * sightData.gnrFontScale;
			circleWidth = Conversion.get().screenspace2pixel(circleWidth, canvasHeight, sightData.envZoomedIn);
			layoutData.rcLineWidth = circleWidth;
			
			// circle
			layoutData.rcCircles[i].set(canvasWidth / 2 - originX + dir.x, canvasHeight / 2 - originY + dir.y, circleRadius);
			

			// labels
			if (isMajor) {
				double textOffset = (Conversion.get().screenspace2pixel(indicator.getTextX() + this.textPos.x, canvasHeight, sightData.envZoomedIn) * sightData.gnrFontScale);
				layoutData.rcTextPositions[i].set(
						canvasWidth / 2 - originX + dir.x + dir.copy().setLength(textOffset).x,
						canvasHeight / 2 - originY + dir.y + dir.copy().setLength(textOffset).y);
			}

		}
		
		return layoutData;
	}
	
	
	
	
	public LayoutBallRangeIndicators layoutLabel(SightData sightData, double canvasWidth, double canvasHeight) {
		
		Font corrFont = new Font("Arial", 25.5*0.5*sightData.gnrFontScale*(sightData.envZoomedIn ? Conversion.get().zoomInMul:1));
		Text corrHelper = new Text();
		corrHelper.setFont(corrFont);
		corrHelper.setWrappingWidth(0);
		corrHelper.setLineSpacing(0);
		corrHelper.setText("Distance:"+sightData.envRangeCorrection);
		
		// x position
		double corrX = -posCorrLabel.x;
		if(sightData.envZoomedIn) {
			corrX = corrX * 1.025;
		}
		corrX = Conversion.get().screenspace2pixel(corrX, canvasHeight, sightData.envZoomedIn);
		corrX = corrX * sightData.gnrFontScale;
		corrX = canvasWidth/2 - corrX;
		
		// y position
		double corrY = posCorrLabel.y;
		if(sightData.envZoomedIn) {
			corrY = corrY * 1.025;
		}
		corrY = Conversion.get().screenspace2pixel(corrY, canvasHeight, sightData.envZoomedIn);
		corrY = corrY * sightData.gnrFontScale;
		corrY = corrY + (canvasHeight/2);
		corrY = corrY - corrHelper.getLayoutBounds().getHeight();
		
		layoutData.posLabel.set(corrX, corrY);
		
		return layoutData;
	}
	
	
}

