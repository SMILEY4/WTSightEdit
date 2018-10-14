package com.ruegnerlukas.wtsights.data.sight.sightElements.elements;

import java.util.ArrayList;
import java.util.List;

import com.ruegnerlukas.simplemath.MathUtils;
import com.ruegnerlukas.simplemath.geometry.shapes.circle.Circlef;
import com.ruegnerlukas.simplemath.geometry.shapes.rectangle.Rectanglef;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.simplemath.vectors.vec4.Vector4d;
import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.sight.BIndicator;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementSingle;
import com.ruegnerlukas.wtsights.data.sight.sightElements.layouts.LayoutBallRangeIndicators;
import com.ruegnerlukas.wtsights.ui.sighteditor.rendering.SightRenderer;
import com.ruegnerlukas.wtutils.Conversion;
import com.ruegnerlukas.wtutils.SightUtils;
import com.ruegnerlukas.wtutils.SightUtils.ScaleMode;
import com.ruegnerlukas.wtutils.SightUtils.TextAlign;
import com.ruegnerlukas.wtutils.SightUtils.Thousandth;

import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ElementBallRangeIndicator extends ElementSingle {

	
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
	
	private Text corrHelper = new Text();
	
	
	
	public ElementBallRangeIndicator() {
		this(ElementType.BALLISTIC_RANGE_INDICATORS.defaultName);
	}
	
	
	public ElementBallRangeIndicator(String name) {
		super(name, ElementType.BALLISTIC_RANGE_INDICATORS);
		resetIndicators();
		this.setLayout(new LayoutBallRangeIndicators());
	}
	
	
	
	
	
	
	
	
	public void resetIndicators() {
		indicators.clear();
		for(int i=200, j=1; i<=2800; i+=200, j++) {
			if(i == 0) { continue; }
			indicators.add(new BIndicator(i, (j%2==0), 0f, 0f, 0f));
		}
	}
	

	
	
	@Override
	public LayoutBallRangeIndicators layout(DataPackage data, double canvasWidth, double canvasHeight) {
		
		LayoutBallRangeIndicators layout = (LayoutBallRangeIndicators)getLayout();
		
		if(isDirty()) {
			setDirty(false);
			if(scaleMode == ScaleMode.VERTICAL) {
				return layoutVertical(data, canvasWidth, canvasHeight);
			} else {
				if(circleMode) {
					return layoutRadialCircles(data, canvasWidth, canvasHeight);
				} else {
					return layoutRadialLines(data, canvasWidth, canvasHeight);
				}
			}
		}
		
		return layout;
	}
	
	
	
	
	private LayoutBallRangeIndicators layoutVertical(DataPackage data, double canvasWidth, double canvasHeight) {
		
		LayoutBallRangeIndicators layout = (LayoutBallRangeIndicators)getLayout();
		
		if(layout.vCenterBounds == null || layout.vTextPositions == null || layout.vCenterBounds.length != indicators.size() ) {
			layout.vMainBounds = new Rectanglef[indicators.size()];
			layout.vCenterBounds = new Rectanglef[indicators.size()];
			layout.vTextPositions = new Vector2d[indicators.size()];
			for(int i=0; i<indicators.size(); i++) {
				layout.vMainBounds[i] = new Rectanglef();
				layout.vCenterBounds[i] = new Rectanglef();
				layout.vTextPositions[i] = new Vector2d();
			}
		}
		
		final double lineSize = 1.0 * data.dataSight.gnrLineSize * data.dataSight.gnrFontScale * (data.dataSight.envZoomedIn ? Conversion.get().zoomInMul : 1);
		
		layout.fontSize = 25 * data.dataSight.gnrFontScale * 0.5f * (data.dataSight.envZoomedIn ? Conversion.get().zoomInMul : 1);
		
		// range correction
		final double rangeCorrectionResultPX = data.elementBallistic.function.eval(data.dataSight.envRangeCorrection);
		final double rangeCorrectionMil = Conversion.get().pixel2mil(rangeCorrectionResultPX, canvasHeight, false);
		final double rangeCorrectionPX = move ? Conversion.get().mil2pixel(rangeCorrectionMil, canvasHeight, data.dataSight.envZoomedIn) : 0;
		
		// draw indicators
		for(int i=0; i<indicators.size(); i++) {
			BIndicator indicator = indicators.get(i);
			Rectanglef boundsMain = layout.vMainBounds[i];
			Rectanglef boundsCenter = layout.vCenterBounds[i];
			Vector2d textPos = layout.vTextPositions[i];
			
			int mil = indicator.getDistance();
			boolean isMajor = indicator.isMajor();
			
			// range fitting
			double resultPX = data.elementBallistic.function.eval(mil);
			double rangeMil = Conversion.get().pixel2mil(resultPX, canvasHeight, false);
			double rangePixel = Conversion.get().mil2pixel(rangeMil, canvasHeight, data.dataSight.envZoomedIn) * (drawUpward ? -1 : +1);
			rangePixel -= drawUpward ? -rangeCorrectionPX : rangeCorrectionPX;

			// CENTRAL BLOCK
			if(drawAddLines) {
				
				// x position
				double xCentral = canvasWidth/2;
				
				// y position
				double yCentral = canvasHeight/2 + rangePixel;
				
				// length
				double lengthCentral = Conversion.get().screenspace2pixel(isMajor ? sizeAddLine.x : sizeAddLine.y, canvasHeight, data.dataSight.envZoomedIn) * data.dataSight.gnrFontScale;
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
			mainLength = mainLength * data.dataSight.gnrFontScale;
			mainLength = Conversion.get().screenspace2pixel(mainLength, canvasHeight, data.dataSight.envZoomedIn);
			mainLength = Math.max(0, mainLength);
			
			
			// x position
			double mainX = position.x;
			if(data.dataSight.envZoomedIn) {
				mainX = mainX + 0.003 * (position.x / 0.1);
			}
			if(isMajor) {
				mainX = mainX + textShift*size.x;
			}
			mainX = mainX + (indicators.get(i).getExtend()>0 ? indicators.get(i).getExtend() : 0);
			mainX = mainX * data.dataSight.gnrFontScale;
			mainX = Conversion.get().screenspace2pixel(mainX, canvasHeight, data.dataSight.envZoomedIn);
			mainX = canvasWidth/2 - mainX;

			
			// y position
			double mainY = position.y;
			if(data.dataSight.envZoomedIn) {
				mainY = mainY + 0.004 * (position.y / 0.13817484);
			}
			mainY = mainY * data.dataSight.gnrFontScale;
			mainY = Conversion.get().screenspace2pixel(mainY, canvasHeight, data.dataSight.envZoomedIn);
			mainY = canvasHeight/2+rangePixel + mainY;
			
			if(!MathUtils.isNearlyEqual(mainLength, 0)) {
				boundsMain.set(mainX, mainY, mainLength, lineSize);
			} else {
				boundsMain.set(-10000, -10000, 0, 0);
			}
			
			// main labels
			if(isMajor) {
				double distLabel = Conversion.get().screenspace2pixel(0.004, canvasHeight, data.dataSight.envZoomedIn);
				double textOffX = Conversion.get().screenspace2pixel(indicators.get(i).getTextX()+this.textPos.x, canvasHeight, data.dataSight.envZoomedIn);
				double textOffY = Conversion.get().screenspace2pixel(indicators.get(i).getTextY()+this.textPos.y, canvasHeight, data.dataSight.envZoomedIn);
				textOffY *= data.dataSight.gnrFontScale;
				
				if(textAlign == TextAlign.LEFT)   { }
				if(textAlign == TextAlign.CENTER) { textOffX -= distLabel/2; }
				if(textAlign == TextAlign.RIGHT)  { textOffX -= distLabel; }
				
				textOffX *= data.dataSight.gnrFontScale;
				
				textPos.set(mainX+textOffX, mainY+textOffY);
			}
			
		}
		
		return layout;
	}
	
	

	
	private LayoutBallRangeIndicators layoutRadialLines(DataPackage data, double canvasWidth, double canvasHeight) {
		
		LayoutBallRangeIndicators layout = (LayoutBallRangeIndicators)getLayout();
		
		if(layout.rlLines == null || layout.rlLines.length != indicators.size() ) {
			layout.rlLines = new Vector4d[indicators.size()];
			layout.rlTextPositions = new Vector2d[indicators.size()];
			for(int i=0; i<indicators.size(); i++) {
				layout.rlLines[i] = new Vector4d();
				layout.rlTextPositions[i] = new Vector2d();
			}
		}
	
		// line size
		final double lineSize = 1.0 * data.dataSight.gnrLineSize * data.dataSight.gnrFontScale;
		layout.rlLineSize = lineSize;
		
		layout.fontSize = 25 * data.dataSight.gnrFontScale * 0.5f * (data.dataSight.envZoomedIn ? Conversion.get().zoomInMul : 1);

		// origin x
		double originX = position.x;
		if (data.dataSight.envZoomedIn) {
			originX = originX + 0.0015 * (position.x / 0.05);
		}
		originX = originX * data.dataSight.gnrFontScale;
		originX = Conversion.get().screenspace2pixel(originX, canvasHeight, data.dataSight.envZoomedIn);

		// origin y
		double originY = position.y;
		if (data.dataSight.envZoomedIn) {
			originY = originY + 0.0015 * (position.y / 0.05);
		}
		originY = originY * data.dataSight.gnrFontScale;
		originY = Conversion.get().screenspace2pixel(originY, canvasHeight, data.dataSight.envZoomedIn);
	
		layout.rlCenter.set(canvasWidth/2 - originX, canvasHeight/2 - originY);
		
		// radius
		double radiusMil = 0;
		if(radiusUseMils) {
			radiusMil = radialRadius;
		} else {
			radiusMil = Conversion.get().screenspace2mil(radialRadius, data.dataSight.envZoomedIn);
		}
		
		
		double radiusPX = 0;
		if(radiusUseMils) {
			radiusPX = Conversion.get().mil2pixel(radialRadius, canvasHeight, data.dataSight.envZoomedIn);
		} else {
			radiusPX = Conversion.get().screenspace2pixel(radialRadius, canvasHeight, data.dataSight.envZoomedIn);
		}
		layout.rlRadius = radiusPX;
		
		Conversion conversionUSSR = new Conversion();
		conversionUSSR.initialize(
				canvasWidth,
				canvasHeight,
				data.dataBallistic.vehicle.fovOut*data.dataBallistic.zoomModOut,
				data.dataBallistic.vehicle.fovIn*data.dataBallistic.zoomModIn,
				Thousandth.USSR);
		
		// length
		double length = Conversion.get().screenspace2pixel(size.x, canvasHeight, data.dataSight.envZoomedIn);
		length = length * data.dataSight.gnrFontScale;
		layout.rlRadius = layout.rlRadius-length/2;
		layout.rlRadiusOutside = layout.rlRadius+length;
		
		// draw indicators
		for(int i=0; i<indicators.size(); i++) {
			Vector4d line = layout.rlLines[i];
			Vector2d textPosition = layout.rlTextPositions[i];
			BIndicator indicator = indicators.get(i);
			
			
			int meters = indicator.getDistance();
			boolean isMajor = indicator.isMajor();
			Vector2d dir = new Vector2d(0, radiusPX);

			// angle offset
			double angleOffset = radialAngle;
			dir.rotateDeg(-angleOffset);
			
			// angle range
			double resultPX = data.elementBallistic.function.eval(meters);
			double rangeMil = conversionUSSR.pixel2mil(resultPX, canvasHeight, false);
			double rangeAngle     = ((rangeMil) / radiusMil) * radialStretch;

			// angle correction
			double rangeCorrAngleSMil = SightUtils.rangeCorrection_meters2sovmil(data.dataSight.envRangeCorrection);
			double angleCorrection = SightUtils.calcAngle_deg(rangeCorrAngleSMil, radialRadius, radialStretch);

			// rotate
			if(drawUpward) {
				dir.rotateRad(+rangeAngle).rotateDeg(-angleCorrection);
			} else {
				dir.rotateRad(-rangeAngle).rotateDeg(+angleCorrection);
			}
			
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
				double textOffset = (Conversion.get().screenspace2pixel(indicators.get(i).getTextX()+this.textPos.x, canvasHeight, data.dataSight.envZoomedIn)*data.dataSight.gnrFontScale);
				textPosition.set(canvasWidth/2 - originX + posEnd.x + dir.copy().setLength(textOffset).x, canvasHeight/2 - originY + posEnd.y + dir.copy().setLength(textOffset).y);
			}
			
		}
		
		return layout;
	}
	
	
	
	
	private LayoutBallRangeIndicators layoutRadialCircles(DataPackage data, double canvasWidth, double canvasHeight) {
		
		LayoutBallRangeIndicators layout = (LayoutBallRangeIndicators)getLayout();
		
		if(layout.rcCircles == null || layout.rcCircles.length != indicators.size() ) {
			layout.rcCircles = new Circlef[indicators.size()];
			layout.rcTextPositions = new Vector2d[indicators.size()];
			for(int i=0; i<indicators.size(); i++) {
				layout.rcCircles[i] = new Circlef();
				layout.rcTextPositions[i] = new Vector2d();
			}
		}

		layout.fontSize = 25 * data.dataSight.gnrFontScale * 0.5f * (data.dataSight.envZoomedIn ? Conversion.get().zoomInMul : 1);
		
		// origin x
		double originX = position.x;
		if (data.dataSight.envZoomedIn) {
			originX = originX + 0.0015 * (position.x / 0.05);
		}
		originX = originX * data.dataSight.gnrFontScale;
		originX = Conversion.get().screenspace2pixel(originX, canvasHeight, data.dataSight.envZoomedIn);

		// origin y
		double originY = position.y;
		if (data.dataSight.envZoomedIn) {
			originY = originY + 0.0015 * (position.y / 0.05);
		}
		originY = originY * data.dataSight.gnrFontScale;
		originY = Conversion.get().screenspace2pixel(originY, canvasHeight, data.dataSight.envZoomedIn);

		layout.rcCenter.set(canvasWidth/2 - originX, canvasHeight/2 - originY);

		
		// radius
		double radiusMil = 0;
		if (radiusUseMils) {
			radiusMil = radialRadius;
		} else {
			radiusMil = Conversion.get().screenspace2mil(radialRadius, data.dataSight.envZoomedIn);
		}

		double radiusPX = 0;
		if (radiusUseMils) {
			radiusPX = Conversion.get().mil2pixel(radialRadius, canvasHeight, data.dataSight.envZoomedIn);
		} else {
			radiusPX = Conversion.get().screenspace2pixel(radialRadius, canvasHeight,
					data.dataSight.envZoomedIn);
		}
		layout.rcRadius = radiusPX;

		Conversion conversionUSSR = new Conversion();
		conversionUSSR.initialize(
				canvasWidth,
				canvasHeight,
				data.dataBallistic.vehicle.fovOut*data.dataBallistic.zoomModOut,
				data.dataBallistic.vehicle.fovIn*data.dataBallistic.zoomModIn,
				Thousandth.USSR);

		
		
		// calc indicators
		for (int i = 0; i < indicators.size(); i++) {
			BIndicator indicator = indicators.get(i);

			int meters = indicator.getDistance();
			boolean isMajor = indicator.isMajor();

			Vector2d dir = new Vector2d(0, radiusPX);

			// angle offset
			double angleOffset = radialAngle;
			dir.rotateDeg(-angleOffset);

			// angle range
			double resultPX = data.elementBallistic.function.eval(meters);
			double rangeMil = conversionUSSR.pixel2mil(resultPX, canvasHeight, false);
			double rangeAngle = ((rangeMil) / radiusMil) * radialStretch;

			// angle correction
			double rangeCorrAngleSMil = SightUtils.rangeCorrection_meters2sovmil(data.dataSight.envRangeCorrection);
			double angleCorrection = SightUtils.calcAngle_deg(rangeCorrAngleSMil, radialRadius, radialStretch);

			// rotate
			if (drawUpward) {
				dir.rotateRad(+rangeAngle).rotateDeg(-angleCorrection);
			} else {
				dir.rotateRad(-rangeAngle).rotateDeg(+angleCorrection);
			}

			// circle radius
			double circleDiameter = size.y;
			circleDiameter = circleDiameter + indicator.getExtend();
			circleDiameter = circleDiameter * 2;
			circleDiameter = circleDiameter * data.dataSight.gnrFontScale;
			circleDiameter = Conversion.get().screenspace2pixel(circleDiameter, canvasHeight, data.dataSight.envZoomedIn);

			// circle width
			double circleWidth = size.x;
			circleWidth = circleWidth / 2;
			circleWidth = circleWidth * data.dataSight.gnrFontScale;
			circleWidth = Conversion.get().screenspace2pixel(circleWidth, canvasHeight, data.dataSight.envZoomedIn);
			layout.rcLineWidth = circleWidth;
			
			// circle
			layout.rcCircles[i].set(canvasWidth / 2 - originX + dir.x, canvasHeight / 2 - originY + dir.y, circleDiameter/2);
			

			// labels
			if (isMajor) {
				double textOffset = (Conversion.get().screenspace2pixel(indicator.getTextX() + this.textPos.x, canvasHeight, data.dataSight.envZoomedIn) * data.dataSight.gnrFontScale);
				layout.rcTextPositions[i].set(
						canvasWidth / 2 - originX + dir.x + dir.copy().setLength(textOffset).x,
						canvasHeight / 2 - originY + dir.y + dir.copy().setLength(textOffset).y);
			}

		}
		
		return layout;
	}
	
	
	
	
	public LayoutBallRangeIndicators layoutLabel(DataPackage data, double canvasWidth, double canvasHeight) {
		
		LayoutBallRangeIndicators layout = (LayoutBallRangeIndicators)getLayout();
		
		final double fontSize = 25.5*0.5*data.dataSight.gnrFontScale*(data.dataSight.envZoomedIn?Conversion.get().zoomInMul:1);
		
		Font corrFont = SightRenderer.getFont(fontSize);
		corrHelper.setFont(corrFont);
		corrHelper.setWrappingWidth(0);
		corrHelper.setLineSpacing(0);
		corrHelper.setText("Distance:"+data.dataSight.envRangeCorrection);
		
		// x position
		double corrX = -posCorrLabel.x;
		if(data.dataSight.envZoomedIn) {
			corrX = corrX * 1.025;
		}
		corrX = Conversion.get().screenspace2pixel(corrX, canvasHeight, data.dataSight.envZoomedIn);
		corrX = corrX * data.dataSight.gnrFontScale;
		corrX = canvasWidth/2 - corrX;
		
		// y position
		double corrY = posCorrLabel.y;
		if(data.dataSight.envZoomedIn) {
			corrY = corrY * 1.025;
		}
		corrY = Conversion.get().screenspace2pixel(corrY, canvasHeight, data.dataSight.envZoomedIn);
		corrY = corrY * data.dataSight.gnrFontScale;
		corrY = corrY + (canvasHeight/2);
		corrY = corrY - corrHelper.getLayoutBounds().getHeight();
		
		layout.corrLabel.set(corrX, corrY, fontSize);
		
		return layout;
	}
	
	
}

