package com.ruegnerlukas.wtsights.renderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.ruegnerlukas.simplemath.MathUtils;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.simplemath.vectors.vec3.Vector3d;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationAmmoData;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationData;
import com.ruegnerlukas.wtsights.data.sight.BIndicator;
import com.ruegnerlukas.wtsights.data.sight.HIndicator;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.data.sight.elements.Element;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementBallRangeIndicator;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCentralHorzLine;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCentralVertLine;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCustomCircle;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCustomLine;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCustomText;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCustomObject.Movement;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCustomQuad;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementHorzRangeIndicators;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementRangefinder;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementShellBlock;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementType;
import com.ruegnerlukas.wtutils.SightUtils;
import com.ruegnerlukas.wtutils.SightUtils.ScaleMode;
import com.ruegnerlukas.wtutils.SightUtils.TextAlign;
import com.ruegnerlukas.wtutils.SightUtils.Thousandth;

import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;



public class SightRenderer {

	private static final Color COLOR_DEBUG_0 = new Color(0, 0, 1, 0.0f);
	private static final Color COLOR_DEBUG_1 = new Color(1, 1, 0, 0.0f);
	
	private static Color COLOR_SELECTION = new Color(1, 0, 0, 0.8f);
	
	
	
	
	public static void draw(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData) {
		
		Conversion.get().initialize(canvas.getWidth(), canvas.getHeight(), dataCalib.vehicle.fovOut, dataCalib.vehicle.fovIn, dataSight.gnrThousandth);
		
		// background
		drawBackground(canvas, g, dataSight);
		
		// centered lines
		drawCenteredLines(canvas, g, dataSight);
		
		// rangefinder
		if(dataSight.envShowRangeFinder) {
			drawRangefinder(canvas, g, dataSight);
		}
		
		// horz range indicators
		drawHorzRangeIndicators(canvas, g, dataSight);
		
		// ballistic range indicators
		if(dataSight.getElements(ElementType.SHELL_BALLISTICS_BLOCK).isEmpty() && currentAmmoData!=null) {
			drawBallisticsBlock(canvas, g, dataSight, dataCalib, currentAmmoData, (ElementBallRangeIndicator)dataSight.getElements(ElementType.BALLISTIC_RANGE_INDICATORS).get(0));
		}
		
		// shell block indicators
		if(currentAmmoData != null) {
			for(Element e : dataSight.getElements(ElementType.SHELL_BALLISTICS_BLOCK)) {
				ElementShellBlock shellBlock = (ElementShellBlock)e;
				drawBallisticsBlock(canvas, g, dataSight, dataCalib, shellBlock.dataAmmo, shellBlock);
			}
		}
		
		// custom elements
		for(Element e : dataSight.getElements(ElementType.CUSTOM_CIRCLE)) {
			drawCircleObject(canvas, g, dataSight, dataCalib, currentAmmoData, (ElementCustomCircle)e);
		}
		for(Element e : dataSight.getElements(ElementType.CUSTOM_LINE)) {
			drawLineObject(canvas, g, dataSight, dataCalib, currentAmmoData, (ElementCustomLine)e);
		}
		for(Element e : dataSight.getElements(ElementType.CUSTOM_QUAD)) {
			drawQuadObject(canvas, g, dataSight, dataCalib, currentAmmoData, (ElementCustomQuad)e);
		}
		for(Element e : dataSight.getElements(ElementType.CUSTOM_TEXT)) {
			drawTextObject(canvas, g, dataSight, dataCalib, currentAmmoData, (ElementCustomText)e);
		}
		
	}

	
	
	
	
	
	private static void drawBackground(Canvas canvas, GraphicsContext g, SightData dataSight) {
		if(dataSight.envBackground != null) {
			g.drawImage(dataSight.envBackground, 0, 0, canvas.getWidth(), canvas.getHeight());
		} else {
			g.setFill(Color.GRAY);
			g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		}
	}
	
	
	
	
	
	
	private static void drawCenteredLines(Canvas canvas, GraphicsContext g, SightData dataSight) {
		
		final double lineSize = 1.0 * dataSight.gnrLineSize * dataSight.gnrFontScale;

		ElementCentralHorzLine horzLine = (ElementCentralHorzLine)dataSight.getElements(ElementType.CENTRAL_HORZ_LINE).get(0);
		ElementCentralVertLine vertLine = (ElementCentralVertLine)dataSight.getElements(ElementType.CENTRAL_VERT_LINE).get(0);

		g.setFill(dataSight.envSightColor);
		if(horzLine.drawCentralHorzLine) {
			g.fillRect(0, canvas.getHeight()/2 - (lineSize/2f), canvas.getWidth(), lineSize);
		}
		if(vertLine.drawCentralVertLine) {
			g.fillRect(canvas.getWidth()/2 - (lineSize/2f), 0, lineSize, canvas.getHeight());
		}
	}
	
	
	
	
	private static void drawRangefinder(Canvas canvas, GraphicsContext g, SightData dataSight) {
		
		ElementRangefinder rangefinder = (ElementRangefinder)dataSight.getElements(ElementType.RANGEFINDER).get(0);
		
		// font
		Font font = null;
		if(dataSight.envZoomedIn) {
			font = new Font("Arial", 18.0 * dataSight.gnrFontScale * rangefinder.textScale * Conversion.get().zoomInMul);
		} else {
			font = new Font("Arial", 17.5 * dataSight.gnrFontScale * rangefinder.textScale);
		}
		Text text = new Text();
		text.setFont(font);
		text.setText("Measuring range");
		text.setWrappingWidth(0);
		text.setLineSpacing(0);
		
		
		// x position
		double x = 0;
		if(dataSight.envZoomedIn) {
			x = rangefinder.position.x * dataSight.gnrFontScale * Conversion.get().zoomInMul;
		} else {
			x = rangefinder.position.x * dataSight.gnrFontScale;
		}
		
		
		// y position
		double y = 0;
		if(rangefinder.useThousandth) {
			y = Conversion.get().mil2pixel(rangefinder.position.y+3, canvas.getHeight(), dataSight.envZoomedIn) * dataSight.gnrFontScale;
			
		} else {
			if(dataSight.envZoomedIn) {
				y = Conversion.get().screenspace2pixel(rangefinder.position.y+0.002, canvas.getHeight(), dataSight.envZoomedIn) * dataSight.gnrFontScale;
			} else {
				y = Conversion.get().screenspace2pixel(rangefinder.position.y, canvas.getHeight(), dataSight.envZoomedIn) * dataSight.gnrFontScale;
			}
		}
		
		x += canvas.getWidth()/2.0;
		y = canvas.getHeight()/2.0 - y;

		
		// size
		final double width  = text.getLayoutBounds().getWidth();
		final double height = text.getLayoutBounds().getHeight();

		// draw background
		g.setFill(rangefinder.color1);
		g.fillRect(x, y-height/2, width*(dataSight.envRFProgress/100f), height);
		
		g.setFill(rangefinder.color2);
		g.fillRect(x+(width*(dataSight.envRFProgress/100f)), y-height/2, width*(1f-(dataSight.envRFProgress/100f)), height);

		// draw text
		g.setFill(dataSight.envSightColor);
		g.setTextAlign(TextAlignment.CENTER);
		g.setTextBaseline(VPos.CENTER);
		g.setFont(font);
		g.fillText("Measuring range", x+width/2, y);
		g.setTextAlign(TextAlignment.LEFT);
		g.setTextBaseline(VPos.BASELINE);
		
	}
	
	
	
	
	private static void drawHorzRangeIndicators(Canvas canvas, GraphicsContext g, SightData dataSight) {
		
		ElementHorzRangeIndicators horRange = (ElementHorzRangeIndicators)dataSight.getElements(ElementType.HORZ_RANGE_INDICATORS).get(0);
		
		if(horRange.indicators.isEmpty()) {
			return;
		}
		
		// line size
		final double lineSize = dataSight.gnrLineSize * dataSight.gnrFontScale;
		
		for(int i=0; i<horRange.indicators.size(); i++) {
			HIndicator indicator = horRange.indicators.get(i);
			final int mil = indicator.getMil();
			final int label = Math.abs(indicator.getMil());
			final boolean isMajor = indicator.isMajor();
			
			// length
			final double length = Conversion.get().mil2pixel(isMajor ? horRange.sizeMajor : horRange.sizeMinor, canvas.getHeight(), dataSight.envZoomedIn);
			
			// x pos
			final double x = canvas.getWidth()/2 + Conversion.get().mil2pixel(mil, canvas.getHeight(), dataSight.envZoomedIn);
			
			// y pos
			final double y = canvas.getHeight()/2;
			final double yLabel = y - length - Conversion.get().screenspace2pixel(0.013, canvas.getHeight(), dataSight.envZoomedIn) * dataSight.gnrFontScale;
			
			
			
			// draw line
			g.setFill(dataSight.envSightColor);
			g.fillRect(x-lineSize/2, y-length, lineSize, length*2);
			
			// draw label
			if(isMajor) {
				
				Font font = new Font("Arial", 12.5 * dataSight.gnrFontScale * (dataSight.envZoomedIn ? Conversion.get().zoomInMul : 1) );
				
				g.setFill(dataSight.envSightColor);
				g.setTextAlign(TextAlignment.CENTER);
				g.setTextBaseline(VPos.CENTER);
				g.setFont(font);
				
				g.fillText(""+Math.abs(mil), x, yLabel);
				
				g.setTextAlign(TextAlignment.LEFT);
				g.setTextBaseline(VPos.BASELINE);
			}
			
		}
	}
	
	
	
	
	private static void drawBallisticsBlock(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData, ElementBallRangeIndicator block) {
		if(block.scaleMode == ScaleMode.VERTICAL) {
			drawBallisticsVertical(canvas, g, dataSight, dataCalib, currentAmmoData, block);
		} else {
			drawBallisticsRadial(canvas, g, dataSight, dataCalib, currentAmmoData, block);
		}
	}
	
	
	
	
	private static void drawRangeCorrectionLabel(Canvas canvas, GraphicsContext g, SightData dataSight, ElementBallRangeIndicator block) {
		
		if(block.drawCorrLabel && dataSight.envRangeCorrection > 0) {
			
			Font corrFont = new Font("Arial", 25.5*0.5*dataSight.gnrFontScale*(dataSight.envZoomedIn?Conversion.get().zoomInMul:1));
			Text corrHelper = new Text();
			corrHelper.setFont(corrFont);
			corrHelper.setWrappingWidth(0);
			corrHelper.setLineSpacing(0);
			corrHelper.setText("Distance:"+dataSight.envRangeCorrection);

			// x position
			double corrX = -block.posCorrLabel.x;
			if(dataSight.envZoomedIn) {
				corrX = corrX * 1.025;
			}
			corrX = Conversion.get().screenspace2pixel(corrX, canvas.getHeight(), dataSight.envZoomedIn);
			corrX = corrX * dataSight.gnrFontScale;
			corrX = canvas.getWidth()/2 - corrX;
			
			// y position
			double corrY = block.posCorrLabel.y;
			if(dataSight.envZoomedIn) {
				corrY = corrY * 1.025;
			}
			corrY = Conversion.get().screenspace2pixel(corrY, canvas.getHeight(), dataSight.envZoomedIn);
			corrY = corrY * dataSight.gnrFontScale;
			corrY = corrY + (canvas.getHeight()/2);
			corrY = corrY - corrHelper.getLayoutBounds().getHeight();
			
			// draw label
			g.setTextBaseline(VPos.CENTER);
			g.setFont(corrFont);
			g.setTextAlign(TextAlignment.LEFT);
			g.fillText("Distance:"+dataSight.envRangeCorrection, corrX, corrY);
			g.setTextAlign(TextAlignment.RIGHT);
			g.setTextBaseline(VPos.BASELINE);
			
		}
		
	}
	
	
	
	
	private static void drawBallisticsVertical(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData, ElementBallRangeIndicator block) {
		
		if(block.indicators.isEmpty()) {
			return;
		}
		
		// line size
		final double lineSize = 1.0 * dataSight.gnrLineSize * dataSight.gnrFontScale * (dataSight.envZoomedIn ? Conversion.get().zoomInMul : 1);
		
		// point fitting
		List<Vector2d> fittingPoints = new ArrayList<Vector2d>();
		fittingPoints.add(new Vector2d(0, 0));
		for(int i=0; i<currentAmmoData.markerRanges.size(); i++) {
			Vector2d p = new Vector2d(currentAmmoData.markerRanges.get(i).y/100, currentAmmoData.markerRanges.get(i).x);
			if(currentAmmoData.zoomedIn) {
				p.y /= Conversion.get().zoomInMul;
			}
			fittingPoints.add(p);
		}
		Vector3d fittingParams = SightUtils.fitBallisticFunction(fittingPoints, 1);
		if(fittingParams == null) {
			return;
		}
		
		// range correction
		final double rangeCorrectionResultPX = SightUtils.ballisticFunction(dataSight.envRangeCorrection/100.0, fittingParams);
		final double rangeCorrectionMil = Conversion.get().pixel2mil(rangeCorrectionResultPX, canvas.getHeight(), false);
		final double rangeCorrectionPX = block.move ? Conversion.get().mil2pixel(rangeCorrectionMil, canvas.getHeight(), dataSight.envZoomedIn) : 0;
		
		// font
		Font bIndFont = new Font("Arial", 25*dataSight.gnrFontScale*0.5f*(dataSight.envZoomedIn?Conversion.get().zoomInMul:1));
		
		// draw indicators
		for(int i=0; i<block.indicators.size(); i++) {
			BIndicator indicator = block.indicators.get(i);
			
			int mil = indicator.getDistance();
			boolean isMajor = indicator.isMajor();
			
			// range fitting
			double resultPX = SightUtils.ballisticFunction(mil/100.0, fittingParams);
			double rangeMil = Conversion.get().pixel2mil(resultPX, canvas.getHeight(), false);
			double rangePixel = Conversion.get().mil2pixel(rangeMil, canvas.getHeight(), dataSight.envZoomedIn) * (block.drawUpward ? -1 : +1);
			rangePixel -= block.drawUpward ? -rangeCorrectionPX : rangeCorrectionPX;

			
			// CENTRAL BLOCK
			if(block.drawAddLines) {
				
				// x position
				double xCentral = canvas.getWidth()/2;
				
				// y position
				double yCentral = canvas.getHeight()/2 + rangePixel;
				
				// length
				double lengthCentral = Conversion.get().screenspace2pixel(isMajor?block.sizeAddLine.x:block.sizeAddLine.y, canvas.getHeight(), dataSight.envZoomedIn) * dataSight.gnrFontScale;
				lengthCentral = Math.max(lengthCentral, 0);
				
				// draw
				if(!MathUtils.isNearlyEqual(lengthCentral, 0)) {
					g.setFill(dataSight.envSightColor);
					g.fillRect(xCentral-lengthCentral, yCentral-lineSize/2, lengthCentral*2, lineSize);
				}
			}
			
			
			// MAIN BLOCK
			
			// length
			double mainLength = 0;
			if(isMajor) {
				mainLength = mainLength + block.size.x;
				mainLength = mainLength + block.textShift*block.size.x;
				mainLength = mainLength + (0.03 * block.size.x);
			} else {
				mainLength = mainLength + block.size.y;
				mainLength = mainLength + (0.03 * block.size.y);
			}
			mainLength = mainLength + Math.abs(block.indicators.get(i).getExtend());
			mainLength = mainLength * dataSight.gnrFontScale;
			mainLength = Conversion.get().screenspace2pixel(mainLength, canvas.getHeight(), dataSight.envZoomedIn);
			mainLength = Math.max(0, mainLength);
			
			
			// x position
			double mainX = block.position.x;
			if(dataSight.envZoomedIn) {
				mainX = mainX + 0.003 * (block.position.x / 0.1);
			}
			if(isMajor) {
				mainX = mainX + block.textShift*block.size.x;
			}
			mainX = mainX + (block.indicators.get(i).getExtend()>0 ? block.indicators.get(i).getExtend() : 0);
			mainX = mainX * dataSight.gnrFontScale;
			mainX = Conversion.get().screenspace2pixel(mainX, canvas.getHeight(), dataSight.envZoomedIn);
			mainX = canvas.getWidth()/2 - mainX;

			
			// y position
			double mainY = block.position.y;
			if(dataSight.envZoomedIn) {
				mainY = mainY + 0.004 * (block.position.y / 0.13817484);
			}
			mainY = mainY * dataSight.gnrFontScale;
			mainY = Conversion.get().screenspace2pixel(mainY, canvas.getHeight(), dataSight.envZoomedIn);
			mainY = canvas.getHeight()/2+rangePixel + mainY;
			
			if(!MathUtils.isNearlyEqual(mainLength, 0)) {
				g.setFill(dataSight.envSightColor);
				g.fillRect(mainX, mainY, mainLength, lineSize);
			}
			
			// main labels
			if(isMajor) {
				
				double distLabel = Conversion.get().screenspace2pixel(0.004, canvas.getHeight(), dataSight.envZoomedIn);
				double textOffX = Conversion.get().screenspace2pixel(block.indicators.get(i).getTextX()+block.textPos.x, canvas.getHeight(), dataSight.envZoomedIn);
				double textOffY = Conversion.get().screenspace2pixel(block.indicators.get(i).getTextY()+block.textPos.y, canvas.getHeight(), dataSight.envZoomedIn);
				textOffY *= dataSight.gnrFontScale;

				g.setFill(dataSight.envSightColor);
				
				if(block.textAlign == TextAlign.LEFT)   {
					g.setTextAlign(TextAlignment.LEFT);
				}
				
				if(block.textAlign == TextAlign.CENTER) {
					g.setTextAlign(TextAlignment.CENTER);
					textOffX -= distLabel/2;
				}
				
				if(block.textAlign == TextAlign.RIGHT)  {
					g.setTextAlign(TextAlignment.RIGHT);
					textOffX -= distLabel;
				}
				
				textOffX *= dataSight.gnrFontScale;
				
				g.setTextBaseline(VPos.CENTER);
				g.setFont(bIndFont);
				
				g.fillText(""+Math.abs(mil/100), mainX+textOffX, mainY+textOffY);

				g.setTextAlign(TextAlignment.LEFT);
				g.setTextBaseline(VPos.BASELINE);
				
			}
			
		}
		
		
		// draw range correction label
		if(block.drawCorrLabel) {
			drawRangeCorrectionLabel(canvas, g, dataSight, block);
		}
		
	}

	
	

	private static void drawBallisticsRadial(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData, ElementBallRangeIndicator block) {
		
		if(block.indicators.isEmpty()) {
			return;
		}
		
		if(block.circleMode) {
			drawBallisticsRadialCircle(canvas, g, dataSight, dataCalib, currentAmmoData, block);
		} else {
			drawBallisticsRadialLine(canvas, g, dataSight, dataCalib, currentAmmoData, block);
		}
		
		
		// draw range correction label
		if(block.drawCorrLabel) {
			drawRangeCorrectionLabel(canvas, g, dataSight, block);
		}
		
	}
	
	
	
	
	
	private static void drawBallisticsRadialLine(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData, ElementBallRangeIndicator block) {
		
		// line size
		final double lineSize = 1.0 * dataSight.gnrLineSize * dataSight.gnrFontScale;
		
		// point fitting
		List<Vector2d> fittingPoints = new ArrayList<Vector2d>();
		fittingPoints.add(new Vector2d(0, 0));
		for(int i=0; i<currentAmmoData.markerRanges.size(); i++) {
			Vector2d p = new Vector2d(currentAmmoData.markerRanges.get(i).y/100, currentAmmoData.markerRanges.get(i).x);
			if(currentAmmoData.zoomedIn) {
				p.y /= Conversion.get().zoomInMul;
			}
			fittingPoints.add(p);
		}
		
		Vector3d fittingParams = SightUtils.fitBallisticFunction(fittingPoints, 1);
		if(fittingParams == null) {
			return;
		}
		
		// font
		Font bIndFont = new Font("Arial", 25*dataSight.gnrFontScale*0.5f*(dataSight.envZoomedIn?Conversion.get().zoomInMul:1));
		
		// origin x
		double originX = block.position.x;
		if (dataSight.envZoomedIn) {
			originX = originX + 0.0015 * (block.position.x / 0.05);
		}
		originX = originX * dataSight.gnrFontScale;
		originX = Conversion.get().screenspace2pixel(originX, canvas.getHeight(), dataSight.envZoomedIn);

		// origin y
		double originY = block.position.y;
		if (dataSight.envZoomedIn) {
			originY = originY + 0.0015 * (block.position.y / 0.05);
		}
		originY = originY * dataSight.gnrFontScale;
		originY = Conversion.get().screenspace2pixel(originY, canvas.getHeight(), dataSight.envZoomedIn);
	
		
		// radius
		double radiusMil = 0;
		if(block.radiusUseMils) {
			radiusMil = block.radialRadius;
		} else {
			radiusMil = Conversion.get().screenspace2mil(block.radialRadius, dataSight.envZoomedIn);
		}
		
		double radiusPX = 0;
		if(block.radiusUseMils) {
			radiusPX = Conversion.get().mil2pixel(block.radialRadius, canvas.getHeight(), dataSight.envZoomedIn);
		} else {
			radiusPX = Conversion.get().screenspace2pixel(block.radialRadius, canvas.getHeight(), dataSight.envZoomedIn);
		}
		
		
		Conversion conversionUSSR = new Conversion();
		conversionUSSR.initialize(canvas.getWidth(), canvas.getHeight(), dataCalib.vehicle.fovOut, dataCalib.vehicle.fovIn, Thousandth.USSR);
		
		
		// draw indicators
		for(int i=0; i<block.indicators.size(); i++) {
			BIndicator indicator = block.indicators.get(i);
			
			int meters = indicator.getDistance();
			boolean isMajor = indicator.isMajor();
			Vector2d dir = new Vector2d(0, radiusPX);

			// angle offset
			double angleOffset = block.radialAngle;
			dir.rotateDeg(-angleOffset);
			
			// angle range
			double resultPX = SightUtils.ballisticFunction(meters/100.0, fittingParams);
			double rangeMil = conversionUSSR.pixel2mil(resultPX, canvas.getHeight(), false);
			double rangeAngle     = ((rangeMil) / radiusMil) * block.radialStretch;

			// angle correction
			double rangeCorrAngleSMil = SightUtils.rangeCorrection_meters2sovmil(dataSight.envRangeCorrection);
			double angleCorrection = SightUtils.calcAngle_deg(rangeCorrAngleSMil, block.radialRadius, block.radialStretch);

			// rotate
			if(block.drawUpward) {
				dir.rotateRad(+rangeAngle).rotateDeg(-angleCorrection);
			} else {
				dir.rotateRad(-rangeAngle).rotateDeg(+angleCorrection);
			}
			
			// length
			double length = Conversion.get().screenspace2pixel(block.size.x, canvas.getHeight(), dataSight.envZoomedIn);
			length = length * dataSight.gnrFontScale;
			
			// line position
			Vector2d posStart = dir.copy().setLength(dir.length()-length/2);
			Vector2d posEnd = dir.copy().setLength(dir.length()+length/2);

			
			// draw line
			if(!MathUtils.isNearlyEqual(length, 0)) {
				g.setStroke(dataSight.envSightColor);
				g.setLineWidth(lineSize);
				g.strokeLine(
						canvas.getWidth()/2 - originX + posStart.x,
						canvas.getHeight()/2 - originY + posStart.y,
						canvas.getWidth()/2 - originX + posEnd.x,
						canvas.getHeight()/2 - originY + posEnd.y
						);
				g.setLineWidth(1);
				
			}
			
			
			
			// draw labels
			if(isMajor) {
				
				double textOffset = (Conversion.get().screenspace2pixel(block.indicators.get(i).getTextX()+block.textPos.x, canvas.getHeight(), dataSight.envZoomedIn)*dataSight.gnrFontScale);
				
				if(block.textAlign == TextAlign.LEFT)   {
					g.setTextAlign(TextAlignment.LEFT);
				}
				
				if(block.textAlign == TextAlign.CENTER) {
					g.setTextAlign(TextAlignment.CENTER);
				}
				
				if(block.textAlign == TextAlign.RIGHT)  {
					g.setTextAlign(TextAlignment.RIGHT);
				}
								
				g.setFill(dataSight.envSightColor);
				g.setTextBaseline(VPos.CENTER);
				g.setFont(bIndFont);
				
				g.fillText(
						""+Math.abs(meters/100),
						canvas.getWidth()/2 - originX + posEnd.x + dir.copy().setLength(textOffset).x,
						canvas.getHeight()/2 - originY + posEnd.y + dir.copy().setLength(textOffset).y
						);
				
				g.setTextAlign(TextAlignment.LEFT);
				g.setTextBaseline(VPos.BASELINE);
				
			}
			
			
		}
		
		
	}
	
	
	

	private static void drawBallisticsRadialCircle(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData, ElementBallRangeIndicator block) {
		
		// point fitting
		List<Vector2d> fittingPoints = new ArrayList<Vector2d>();
		fittingPoints.add(new Vector2d(0, 0));
		for(int i=0; i<currentAmmoData.markerRanges.size(); i++) {
			Vector2d p = new Vector2d(currentAmmoData.markerRanges.get(i).y/100, currentAmmoData.markerRanges.get(i).x);
			if(currentAmmoData.zoomedIn) {
				p.y /= Conversion.get().zoomInMul;
			}
			fittingPoints.add(p);
		}
		Vector3d fittingParams = SightUtils.fitBallisticFunction(fittingPoints, 1);
		if(fittingParams == null) {
			return;
		}
		
		// font
		Font bIndFont = new Font("Arial", 25*dataSight.gnrFontScale*0.5f*(dataSight.envZoomedIn?Conversion.get().zoomInMul:1));
		
		// origin x
		double originX = block.position.x;
		if (dataSight.envZoomedIn) {
			originX = originX + 0.0015 * (block.position.x / 0.05);
		}
		originX = originX * dataSight.gnrFontScale;
		originX = Conversion.get().screenspace2pixel(originX, canvas.getHeight(), dataSight.envZoomedIn);

		// origin y
		double originY = block.position.y;
		if (dataSight.envZoomedIn) {
			originY = originY + 0.0015 * (block.position.y / 0.05);
		}
		originY = originY * dataSight.gnrFontScale;
		originY = Conversion.get().screenspace2pixel(originY, canvas.getHeight(), dataSight.envZoomedIn);
	
		
		// radius
		double radiusMil = 0;
		if(block.radiusUseMils) {
			radiusMil = block.radialRadius;
		} else {
			radiusMil = Conversion.get().screenspace2mil(block.radialRadius, dataSight.envZoomedIn);
		}
		
		double radiusPX = 0;
		if(block.radiusUseMils) {
			radiusPX = Conversion.get().mil2pixel(block.radialRadius, canvas.getHeight(), dataSight.envZoomedIn);
		} else {
			radiusPX = Conversion.get().screenspace2pixel(block.radialRadius, canvas.getHeight(), dataSight.envZoomedIn);
		}
		
		
		Conversion conversionUSSR = new Conversion();
		conversionUSSR.initialize(canvas.getWidth(), canvas.getHeight(), dataCalib.vehicle.fovOut, dataCalib.vehicle.fovIn, Thousandth.USSR);
		
		// draw indicators
		for(int i=0; i<block.indicators.size(); i++) {
			BIndicator indicator = block.indicators.get(i);

			
			int meters = indicator.getDistance();
			boolean isMajor = indicator.isMajor();
		
			Vector2d dir = new Vector2d(0, radiusPX);

			// angle offset
			double angleOffset = block.radialAngle;
			dir.rotateDeg(-angleOffset);
			
			// angle range
			double resultPX = SightUtils.ballisticFunction(meters/100.0, fittingParams);
			double rangeMil = conversionUSSR.pixel2mil(resultPX, canvas.getHeight(), false);
			double rangeAngle     = ((rangeMil) / radiusMil) * block.radialStretch;

			// angle correction
			double rangeCorrAngleSMil = SightUtils.rangeCorrection_meters2sovmil(dataSight.envRangeCorrection);
			double angleCorrection = SightUtils.calcAngle_deg(rangeCorrAngleSMil, block.radialRadius, block.radialStretch);

			// rotate
			if(block.drawUpward) {
				dir.rotateRad(+rangeAngle).rotateDeg(-angleCorrection);
			} else {
				dir.rotateRad(-rangeAngle).rotateDeg(+angleCorrection);
			}
			
			// circle radius
			double circleRadius = block.size.y;
			circleRadius = circleRadius + indicator.getExtend();
			circleRadius = circleRadius * 2;
			circleRadius = circleRadius * dataSight.gnrFontScale;
			circleRadius = Conversion.get().screenspace2pixel(circleRadius, canvas.getHeight(), dataSight.envZoomedIn);
						
			// circle width
			double circleWidth = block.size.x;
			circleWidth = circleWidth / 2;
			circleWidth = circleWidth * dataSight.gnrFontScale;
			circleWidth = Conversion.get().screenspace2pixel(circleWidth, canvas.getHeight(), dataSight.envZoomedIn);

			
			
			// draw circle
			g.setLineWidth(circleWidth);
			g.setStroke(dataSight.envSightColor);
			g.strokeOval(
					canvas.getWidth()/2 - originX + dir.x - circleRadius/2,
					canvas.getHeight()/2 - originY + dir.y - circleRadius/2,
					circleRadius, circleRadius);
			g.setLineWidth(1);
			
			g.setLineDashes(5);
			if(i == 0) {
				g.setStroke(COLOR_DEBUG_1);
			} else {
				g.setStroke(COLOR_DEBUG_0);
			}
			g.strokeLine(
					canvas.getWidth()/2 - originX,
					canvas.getHeight()/2 - originY,
					canvas.getWidth()/2 - originX + dir.x,
					canvas.getHeight()/2 - originY + dir.y
					);
			g.setLineDashes(0);
			
			
			// draw labels
			if(isMajor) {
				
				double textOffset = (Conversion.get().screenspace2pixel(indicator.getTextX()+block.textPos.x, canvas.getHeight(), dataSight.envZoomedIn)*dataSight.gnrFontScale);
				
				if(block.textAlign == TextAlign.LEFT)   {
					g.setTextAlign(TextAlignment.LEFT);
				}
				
				if(block.textAlign == TextAlign.CENTER) {
					g.setTextAlign(TextAlignment.CENTER);
				}
				
				if(block.textAlign == TextAlign.RIGHT)  {
					g.setTextAlign(TextAlignment.RIGHT);
				}
								
				g.setFill(dataSight.envSightColor);
				g.setTextBaseline(VPos.CENTER);
				g.setFont(bIndFont);
				
				g.fillText(
						""+Math.abs(meters/100),
						canvas.getWidth()/2 - originX + dir.x + dir.copy().setLength(textOffset).x,
						canvas.getHeight()/2 - originY + dir.y + dir.copy().setLength(textOffset).y
						);

				g.setTextAlign(TextAlignment.LEFT);
				g.setTextBaseline(VPos.BASELINE);
				
				
			}
			
			
		}
		
	}
	
	
	
	
	
	private static void drawLineObject(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData, ElementCustomLine objLine) {
		
		double sxPX = 0;
		double syPX = 0;
		double exPX = 0;
		double eyPX = 0;

		
		if(objLine.movement == Movement.STATIC) {
			
			if(objLine.useThousandth) {
				sxPX = Conversion.get().mil2pixel(objLine.start.x, canvas.getHeight(), dataSight.envZoomedIn);
				syPX = Conversion.get().mil2pixel(objLine.start.y, canvas.getHeight(), dataSight.envZoomedIn);
				exPX = Conversion.get().mil2pixel(objLine.end.x, canvas.getHeight(), dataSight.envZoomedIn);
				eyPX = Conversion.get().mil2pixel(objLine.end.y, canvas.getHeight(), dataSight.envZoomedIn);
			} else {
				sxPX = Conversion.get().screenspace2pixel(objLine.start.x, canvas.getHeight(), dataSight.envZoomedIn);
				syPX = Conversion.get().screenspace2pixel(objLine.start.y, canvas.getHeight(), dataSight.envZoomedIn);
				exPX = Conversion.get().screenspace2pixel(objLine.end.x, canvas.getHeight(), dataSight.envZoomedIn);
				eyPX = Conversion.get().screenspace2pixel(objLine.end.y, canvas.getHeight(), dataSight.envZoomedIn);
			}
			
			
		} else if(objLine.movement == Movement.MOVE) {
			
			
			double rangeCorrectionMil = 0;
			
			if(currentAmmoData != null) {
				List<Vector2d> fittingPoints = new ArrayList<Vector2d>();
				fittingPoints.add(new Vector2d(0, 0));
				for(int i=0; i<currentAmmoData.markerRanges.size(); i++) {
					Vector2d p = new Vector2d(currentAmmoData.markerRanges.get(i).y/100, currentAmmoData.markerRanges.get(i).x);
					if(currentAmmoData.zoomedIn) {
						p.y /= Conversion.get().zoomInMul;
					}
					fittingPoints.add(p);
				}
				Vector3d fittingParams = SightUtils.fitBallisticFunction(fittingPoints, 1);
				if(fittingParams == null) {
					return;
				}
				final double rangeCorrectionResultPX = SightUtils.ballisticFunction(dataSight.envRangeCorrection/100.0, fittingParams);
				rangeCorrectionMil = Conversion.get().pixel2mil(rangeCorrectionResultPX, canvas.getHeight(), false);
				
			} else {
				// found values by testing  50m = 0.6875mil
				rangeCorrectionMil = dataSight.envRangeCorrection * (0.6875/50.0);
			}
			
			final double rangeCorrectionPX = Conversion.get().mil2pixel(rangeCorrectionMil, canvas.getHeight(), dataSight.envZoomedIn);
			
			if(objLine.useThousandth) {
				sxPX = Conversion.get().mil2pixel(objLine.start.x, canvas.getHeight(), dataSight.envZoomedIn);
				syPX = Conversion.get().mil2pixel(objLine.start.y, canvas.getHeight(), dataSight.envZoomedIn);
				exPX = Conversion.get().mil2pixel(objLine.end.x, canvas.getHeight(), dataSight.envZoomedIn);
				eyPX = Conversion.get().mil2pixel(objLine.end.y, canvas.getHeight(), dataSight.envZoomedIn);
			} else {
				sxPX = Conversion.get().screenspace2pixel(objLine.start.x, canvas.getHeight(), dataSight.envZoomedIn);
				syPX = Conversion.get().screenspace2pixel(objLine.start.y, canvas.getHeight(), dataSight.envZoomedIn);
				exPX = Conversion.get().screenspace2pixel(objLine.end.x, canvas.getHeight(), dataSight.envZoomedIn);
				eyPX = Conversion.get().screenspace2pixel(objLine.end.y, canvas.getHeight(), dataSight.envZoomedIn);
			}
			
			
			if(dataSight.gnrApplyCorrectionToGun) {
				syPX -= rangeCorrectionPX;
				eyPX -= rangeCorrectionPX;
			} else {
				syPX += rangeCorrectionPX;
				eyPX += rangeCorrectionPX;
			}
			
			
			
		} else if(objLine.movement == Movement.MOVE_RADIAL) {
			
			Vector2d centerOW = new Vector2d();
			if(objLine.autoCenter) {
				centerOW.add(objLine.start).add(objLine.end).scale(0.5f);
			} else {
				centerOW = objLine.center;
			}
			final double radius = objLine.useThousandth ? centerOW.dist(objLine.radCenter) : Conversion.get().screenspace2mil(centerOW.dist(objLine.radCenter), dataSight.envZoomedIn);
			if(MathUtils.isNearlyEqual(radius, 0)) {
				return;
			}
			
			double rangeCorrAngleSMil = SightUtils.rangeCorrection_meters2sovmil(dataSight.envRangeCorrection);
			double rangeAngle = SightUtils.calcAngle_rad(rangeCorrAngleSMil, radius, objLine.speed);
			
			Vector2d toStart = Vector2d.createVectorAB(objLine.radCenter, objLine.start);
			Vector2d toEnd = Vector2d.createVectorAB(objLine.radCenter, objLine.end);
			if(MathUtils.isNearlyEqual(toStart.length2(), 0) && MathUtils.isNearlyEqual(toEnd.length2(), 0)) {
				toStart.set(objLine.start);
				toEnd.set(objLine.end);
			} else {
				toStart.rotateDeg(-objLine.angle).rotateRad(-rangeAngle);
				toEnd.rotateDeg(-objLine.angle).rotateRad(-rangeAngle);
			}

			
			if(objLine.useThousandth) {
				sxPX = Conversion.get().mil2pixel(toStart.x, canvas.getHeight(), dataSight.envZoomedIn);
				syPX = Conversion.get().mil2pixel(toStart.y, canvas.getHeight(), dataSight.envZoomedIn);
				exPX = Conversion.get().mil2pixel(toEnd.x, canvas.getHeight(), dataSight.envZoomedIn);
				eyPX = Conversion.get().mil2pixel(toEnd.y, canvas.getHeight(), dataSight.envZoomedIn);
			} else {
				sxPX = Conversion.get().screenspace2pixel(toStart.x, canvas.getHeight(), dataSight.envZoomedIn);
				syPX = Conversion.get().screenspace2pixel(toStart.y, canvas.getHeight(), dataSight.envZoomedIn);
				exPX = Conversion.get().screenspace2pixel(toEnd.x, canvas.getHeight(), dataSight.envZoomedIn);
				eyPX = Conversion.get().screenspace2pixel(toEnd.y, canvas.getHeight(), dataSight.envZoomedIn);
			}
			
		}
		
		
		sxPX += canvas.getWidth()/2;
		syPX += canvas.getHeight()/2;
		exPX += canvas.getWidth()/2;
		eyPX += canvas.getHeight()/2;
		
		g.setStroke(dataSight.envSightColor);
		g.setLineWidth(dataSight.gnrLineSize*dataSight.gnrFontScale);
		g.strokeLine(sxPX, syPX, exPX, eyPX);
		g.setLineWidth(1);
		
	}
	
	
	
	
	private static void drawTextObject(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData, ElementCustomText objText) {
	
		double xPX = 0;
		double yPX = 0;
		
		if(objText.movement == Movement.STATIC) {
		
			if(objText.useThousandth) {
				xPX = Conversion.get().mil2pixel(objText.position.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX = Conversion.get().mil2pixel(objText.position.y, canvas.getHeight(), dataSight.envZoomedIn);
			} else {
				xPX = Conversion.get().screenspace2pixel(objText.position.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX = Conversion.get().screenspace2pixel(objText.position.y, canvas.getHeight(), dataSight.envZoomedIn);
			}
			
			
		} else if(objText.movement == Movement.MOVE) {
			
			if(objText.useThousandth) {
				xPX = Conversion.get().mil2pixel(objText.position.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX = Conversion.get().mil2pixel(objText.position.y, canvas.getHeight(), dataSight.envZoomedIn);
			} else {
				xPX = Conversion.get().screenspace2pixel(objText.position.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX = Conversion.get().screenspace2pixel(objText.position.y, canvas.getHeight(), dataSight.envZoomedIn);
			}
			
			double rangeCorrectionMil = 0;
			
			if(currentAmmoData != null) {
				List<Vector2d> fittingPoints = new ArrayList<Vector2d>();
				fittingPoints.add(new Vector2d(0, 0));
				for(int i=0; i<currentAmmoData.markerRanges.size(); i++) {
					Vector2d p = new Vector2d(currentAmmoData.markerRanges.get(i).y/100, currentAmmoData.markerRanges.get(i).x);
					if(currentAmmoData.zoomedIn) {
						p.y /= Conversion.get().zoomInMul;
					}
					fittingPoints.add(p);
				}
				Vector3d fittingParams = SightUtils.fitBallisticFunction(fittingPoints, 1);
				if(fittingParams == null) {
					return;
				}
				final double rangeCorrectionResultPX = SightUtils.ballisticFunction(dataSight.envRangeCorrection/100.0, fittingParams);
				rangeCorrectionMil = Conversion.get().pixel2mil(rangeCorrectionResultPX, canvas.getHeight(), false);
				
			} else {
				// found values by testing  50m = 0.6875mil
				rangeCorrectionMil = dataSight.envRangeCorrection * (0.6875/50.0);
			}
			
			final double rangeCorrectionPX = Conversion.get().mil2pixel(rangeCorrectionMil, canvas.getHeight(), dataSight.envZoomedIn);
			
			if(dataSight.gnrApplyCorrectionToGun) {
				yPX -= rangeCorrectionPX;
			} else {
				yPX += rangeCorrectionPX;
			}
			
			
		} else if(objText.movement == Movement.MOVE_RADIAL) {
			
			Vector2d centerOW = new Vector2d();
			if(objText.autoCenter) {
				centerOW.set(objText.position);
			} else {
				centerOW = objText.center;
			}
			final double radius = objText.useThousandth ? centerOW.dist(objText.radCenter) : Conversion.get().screenspace2mil(centerOW.dist(objText.radCenter), dataSight.envZoomedIn);
			if(MathUtils.isNearlyEqual(radius, 0)) {
				return;
			}
			
			double rangeCorrAngleSMil = SightUtils.rangeCorrection_meters2sovmil(dataSight.envRangeCorrection);
			double rangeAngle = SightUtils.calcAngle_rad(rangeCorrAngleSMil, radius, objText.speed);
			
			Vector2d toCenter = Vector2d.createVectorAB(objText.radCenter, objText.position);
			if(MathUtils.isNearlyEqual(toCenter.length2(), 0)) {
				toCenter.set(objText.position);
			} else {
				toCenter.rotateDeg(-objText.angle).rotateRad(-rangeAngle);
			}

			if(objText.useThousandth) {
				xPX = Conversion.get().mil2pixel(toCenter.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX = Conversion.get().mil2pixel(toCenter.y, canvas.getHeight(), dataSight.envZoomedIn);
			} else {
				xPX = Conversion.get().screenspace2pixel(toCenter.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX = Conversion.get().screenspace2pixel(toCenter.y, canvas.getHeight(), dataSight.envZoomedIn);
			}
			
		}
		
		
		xPX += canvas.getWidth()/2;
		yPX += canvas.getHeight()/2;
	
		
		
		Font font = new Font("Arial", (dataSight.envZoomedIn?18.5:17.5) * dataSight.gnrFontScale * objText.size * (dataSight.envZoomedIn ? Conversion.get().zoomInMul : 1) );
		Text helper = new Text();
		helper.setFont(font);
		helper.setText(objText.text);
		helper.setWrappingWidth(0);
		helper.setLineSpacing(0);
		
		// draw text
		g.setFill(dataSight.envSightColor);
		g.setTextAlign( (objText.align==TextAlign.LEFT ? TextAlignment.LEFT : (objText.align==TextAlign.CENTER ? TextAlignment.CENTER : TextAlignment.RIGHT)) );
		g.setTextBaseline(VPos.CENTER);
		g.setFont(font);
		g.fillText(objText.text, xPX, yPX);
		g.setTextAlign(TextAlignment.LEFT);
		g.setTextBaseline(VPos.BASELINE);
		
		
	}
	
	
	
	
	private static void drawCircleObject(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData, ElementCustomCircle objCircle) {
		
		double xPX = 0;
		double yPX = 0;
		double dPX = 0;
		
		if(objCircle.movement == Movement.STATIC) {
			
			if(objCircle.useThousandth) {
				xPX = Conversion.get().mil2pixel(objCircle.position.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX = Conversion.get().mil2pixel(objCircle.position.y, canvas.getHeight(), dataSight.envZoomedIn);
				dPX = Conversion.get().mil2pixel(objCircle.diameter, canvas.getHeight(), dataSight.envZoomedIn);
			} else {
				xPX = Conversion.get().screenspace2pixel(objCircle.position.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX = Conversion.get().screenspace2pixel(objCircle.position.y, canvas.getHeight(), dataSight.envZoomedIn);
				dPX = Conversion.get().screenspace2pixel(objCircle.diameter, canvas.getHeight(), dataSight.envZoomedIn);
			}
			
			
		} else if(objCircle.movement == Movement.MOVE) {
			
			if(objCircle.useThousandth) {
				xPX = Conversion.get().mil2pixel(objCircle.position.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX = Conversion.get().mil2pixel(objCircle.position.y, canvas.getHeight(), dataSight.envZoomedIn);
				dPX = Conversion.get().mil2pixel(objCircle.diameter, canvas.getHeight(), dataSight.envZoomedIn);
			} else {
				xPX = Conversion.get().screenspace2pixel(objCircle.position.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX = Conversion.get().screenspace2pixel(objCircle.position.y, canvas.getHeight(), dataSight.envZoomedIn);
				dPX = Conversion.get().screenspace2pixel(objCircle.diameter, canvas.getHeight(), dataSight.envZoomedIn);
			}
			
			double rangeCorrectionMil = 0;
			
			if(currentAmmoData != null) {
				List<Vector2d> fittingPoints = new ArrayList<Vector2d>();
				fittingPoints.add(new Vector2d(0, 0));
				for(int i=0; i<currentAmmoData.markerRanges.size(); i++) {
					Vector2d p = new Vector2d(currentAmmoData.markerRanges.get(i).y/100, currentAmmoData.markerRanges.get(i).x);
					if(currentAmmoData.zoomedIn) {
						p.y /= Conversion.get().zoomInMul;
					}
					fittingPoints.add(p);
				}
				Vector3d fittingParams = SightUtils.fitBallisticFunction(fittingPoints, 1);
				if(fittingParams == null) {
					return;
				}
				final double rangeCorrectionResultPX = SightUtils.ballisticFunction(dataSight.envRangeCorrection/100.0, fittingParams);
				rangeCorrectionMil = Conversion.get().pixel2mil(rangeCorrectionResultPX, canvas.getHeight(), false);
				
			} else {
				// found values by testing  50m = 0.6875mil
				rangeCorrectionMil = dataSight.envRangeCorrection * (0.6875/50.0);
			}
			
			final double rangeCorrectionPX = Conversion.get().mil2pixel(rangeCorrectionMil, canvas.getHeight(), dataSight.envZoomedIn);
			
			if(dataSight.gnrApplyCorrectionToGun) {
				yPX -= rangeCorrectionPX;
			} else {
				yPX += rangeCorrectionPX;
			}			
			
		} else if(objCircle.movement == Movement.MOVE_RADIAL) {

			Vector2d centerOW = new Vector2d();
			if(objCircle.autoCenter) {
				centerOW.set(objCircle.position);
			} else {
				centerOW = objCircle.center;
			}
			final double radius = objCircle.useThousandth ? centerOW.dist(objCircle.radCenter) : Conversion.get().screenspace2mil(centerOW.dist(objCircle.radCenter), dataSight.envZoomedIn);
			if(MathUtils.isNearlyEqual(radius, 0)) {
				return;
			}
			
			double rangeCorrAngleSMil = SightUtils.rangeCorrection_meters2sovmil(dataSight.envRangeCorrection);
			double rangeAngle = SightUtils.calcAngle_rad(rangeCorrAngleSMil, radius, objCircle.speed);
			
			Vector2d toCenter = Vector2d.createVectorAB(objCircle.radCenter, objCircle.position);
			if(MathUtils.isNearlyEqual(toCenter.length2(), 0)) {
				toCenter.set(objCircle.position);
			} else {
				toCenter.rotateDeg(-objCircle.angle).rotateRad(-rangeAngle);
			}
			
			if(objCircle.useThousandth) {
				xPX = Conversion.get().mil2pixel(toCenter.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX = Conversion.get().mil2pixel(toCenter.y, canvas.getHeight(), dataSight.envZoomedIn);
				dPX = Conversion.get().mil2pixel(objCircle.diameter, canvas.getHeight(), dataSight.envZoomedIn);
			} else {
				xPX = Conversion.get().screenspace2pixel(toCenter.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX = Conversion.get().screenspace2pixel(toCenter.y, canvas.getHeight(), dataSight.envZoomedIn);
				dPX = Conversion.get().screenspace2pixel(objCircle.diameter, canvas.getHeight(), dataSight.envZoomedIn);
			}
			
		}
		
		
		xPX += canvas.getWidth()/2;
		yPX += canvas.getHeight()/2;
		
		g.setStroke(dataSight.envSightColor);
		final double lineWidth = objCircle.size*dataSight.gnrFontScale;
		g.setLineWidth(lineWidth);
		
		if(MathUtils.isNearlyEqual(objCircle.segment.x, 0.0, false) && MathUtils.isNearlyEqual(objCircle.segment.y, 360.0, true)) {
			g.strokeOval(xPX-dPX/2, yPX-dPX/2, dPX, dPX);
			
		} else {
			
			final double angleStart = objCircle.segment.x;
			final double angleEnd = objCircle.segment.y;
			if(angleStart > angleEnd) {
				return;
			}
			
			int nLines = Math.min(360, Math.max((int) (((angleEnd-angleStart)/10.0) * (dPX/100.0)), 15));
			double angleStep = (angleEnd-angleStart)/nLines;
			
			g.setStroke(dataSight.envSightColor);
			g.setLineWidth(lineWidth);
			
			Vector2d pointer = new Vector2d(0,1).rotateDeg(-angleStart).setLength(dPX/2);
			for(int i=0; i<nLines; i++) {
				final double x0 = xPX + pointer.x;
				final double y0 = yPX + pointer.y;
				pointer.rotateDeg(-angleStep).setLength(dPX/2);
				final double x1 = xPX + pointer.x;
				final double y1 = yPX + pointer.y;
				g.strokeLine(x0, y0, x1, y1);
			}
			
		}
		
		g.setLineWidth(1);
		
	}
	
	
	
	
	private static void drawQuadObject(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData, ElementCustomQuad objQuad) {
		
		double xPX1 = 0;
		double yPX1 = 0;
		double xPX2 = 0;
		double yPX2 = 0;
		double xPX3 = 0;
		double yPX3 = 0;
		double xPX4 = 0;
		double yPX4 = 0;
		
		if(objQuad.movement == Movement.STATIC) {
			
			if(objQuad.useThousandth) {
				xPX1 = Conversion.get().mil2pixel(objQuad.pos1.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX1 = Conversion.get().mil2pixel(objQuad.pos1.y, canvas.getHeight(), dataSight.envZoomedIn);
				xPX2 = Conversion.get().mil2pixel(objQuad.pos2.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX2 = Conversion.get().mil2pixel(objQuad.pos2.y, canvas.getHeight(), dataSight.envZoomedIn);
				xPX3 = Conversion.get().mil2pixel(objQuad.pos3.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX3 = Conversion.get().mil2pixel(objQuad.pos3.y, canvas.getHeight(), dataSight.envZoomedIn);
				xPX4 = Conversion.get().mil2pixel(objQuad.pos4.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX4 = Conversion.get().mil2pixel(objQuad.pos4.y, canvas.getHeight(), dataSight.envZoomedIn);
			} else {
				xPX1 = Conversion.get().screenspace2pixel(objQuad.pos1.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX1 = Conversion.get().screenspace2pixel(objQuad.pos1.y, canvas.getHeight(), dataSight.envZoomedIn);
				xPX2 = Conversion.get().screenspace2pixel(objQuad.pos2.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX2 = Conversion.get().screenspace2pixel(objQuad.pos2.y, canvas.getHeight(), dataSight.envZoomedIn);
				xPX3 = Conversion.get().screenspace2pixel(objQuad.pos3.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX3 = Conversion.get().screenspace2pixel(objQuad.pos3.y, canvas.getHeight(), dataSight.envZoomedIn);
				xPX4 = Conversion.get().screenspace2pixel(objQuad.pos4.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX4 = Conversion.get().screenspace2pixel(objQuad.pos4.y, canvas.getHeight(), dataSight.envZoomedIn);
			}
			
			
			
		} else if(objQuad.movement == Movement.MOVE) {
			
			if(objQuad.useThousandth) {
				xPX1 = Conversion.get().mil2pixel(objQuad.pos1.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX1 = Conversion.get().mil2pixel(objQuad.pos1.y, canvas.getHeight(), dataSight.envZoomedIn);
				xPX2 = Conversion.get().mil2pixel(objQuad.pos2.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX2 = Conversion.get().mil2pixel(objQuad.pos2.y, canvas.getHeight(), dataSight.envZoomedIn);
				xPX3 = Conversion.get().mil2pixel(objQuad.pos3.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX3 = Conversion.get().mil2pixel(objQuad.pos3.y, canvas.getHeight(), dataSight.envZoomedIn);
				xPX4 = Conversion.get().mil2pixel(objQuad.pos4.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX4 = Conversion.get().mil2pixel(objQuad.pos4.y, canvas.getHeight(), dataSight.envZoomedIn);
			} else {
				xPX1 = Conversion.get().screenspace2pixel(objQuad.pos1.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX1 = Conversion.get().screenspace2pixel(objQuad.pos1.y, canvas.getHeight(), dataSight.envZoomedIn);
				xPX2 = Conversion.get().screenspace2pixel(objQuad.pos2.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX2 = Conversion.get().screenspace2pixel(objQuad.pos2.y, canvas.getHeight(), dataSight.envZoomedIn);
				xPX3 = Conversion.get().screenspace2pixel(objQuad.pos3.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX3 = Conversion.get().screenspace2pixel(objQuad.pos3.y, canvas.getHeight(), dataSight.envZoomedIn);
				xPX4 = Conversion.get().screenspace2pixel(objQuad.pos4.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX4 = Conversion.get().screenspace2pixel(objQuad.pos4.y, canvas.getHeight(), dataSight.envZoomedIn);
			}
			
			double rangeCorrectionMil = 0;
			
			if(currentAmmoData != null) {
				List<Vector2d> fittingPoints = new ArrayList<Vector2d>();
				fittingPoints.add(new Vector2d(0, 0));
				for(int i=0; i<currentAmmoData.markerRanges.size(); i++) {
					Vector2d p = new Vector2d(currentAmmoData.markerRanges.get(i).y/100, currentAmmoData.markerRanges.get(i).x);
					if(currentAmmoData.zoomedIn) {
						p.y /= Conversion.get().zoomInMul;
					}
					fittingPoints.add(p);
				}
				Vector3d fittingParams = SightUtils.fitBallisticFunction(fittingPoints, 1);
				if(fittingParams == null) {
					return;
				}
				final double rangeCorrectionResultPX = SightUtils.ballisticFunction(dataSight.envRangeCorrection/100.0, fittingParams);
				rangeCorrectionMil = Conversion.get().pixel2mil(rangeCorrectionResultPX, canvas.getHeight(), false);
				
			} else {
				// found values by testing  50m = 0.6875mil
				rangeCorrectionMil = dataSight.envRangeCorrection * (0.6875/50.0);
			}
			
			final double rangeCorrectionPX = Conversion.get().mil2pixel(rangeCorrectionMil, canvas.getHeight(), dataSight.envZoomedIn);
			
			if(dataSight.gnrApplyCorrectionToGun) {
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
			
		} else if(objQuad.movement == Movement.MOVE_RADIAL) {

			Vector2d centerOW = new Vector2d();
			
			if(objQuad.autoCenter) {
				centerOW.add(objQuad.pos1).add(objQuad.pos2).add(objQuad.pos3).add(objQuad.pos4.x).scale(1.0/4.0);
			} else {
				centerOW = objQuad.center;
			}
			final double radius = objQuad.useThousandth ? centerOW.dist(objQuad.radCenter) : Conversion.get().screenspace2mil(centerOW.dist(objQuad.radCenter), dataSight.envZoomedIn);
			if(MathUtils.isNearlyEqual(radius, 0)) {
				return;
			}
			
			double rangeCorrAngleSMil = SightUtils.rangeCorrection_meters2sovmil(dataSight.envRangeCorrection);
			double rangeAngle = SightUtils.calcAngle_rad(rangeCorrAngleSMil, radius, objQuad.speed);
		
			Vector2d toPos1 = Vector2d.createVectorAB(objQuad.radCenter, objQuad.pos1);
			Vector2d toPos2 = Vector2d.createVectorAB(objQuad.radCenter, objQuad.pos2);
			Vector2d toPos3 = Vector2d.createVectorAB(objQuad.radCenter, objQuad.pos3);
			Vector2d toPos4 = Vector2d.createVectorAB(objQuad.radCenter, objQuad.pos4);

			if(MathUtils.isNearlyEqual(toPos1.length2(), 0) && MathUtils.isNearlyEqual(toPos2.length2(), 0) && MathUtils.isNearlyEqual(toPos3.length2(), 0) && MathUtils.isNearlyEqual(toPos4.length2(), 0)) {
				toPos1.set(objQuad.pos1);
				toPos2.set(objQuad.pos2);
				toPos3.set(objQuad.pos3);
				toPos4.set(objQuad.pos4);
			} else {
				toPos1.rotateDeg(-objQuad.angle).rotateRad(-rangeAngle);
				toPos2.rotateDeg(-objQuad.angle).rotateRad(-rangeAngle);
				toPos3.rotateDeg(-objQuad.angle).rotateRad(-rangeAngle);
				toPos4.rotateDeg(-objQuad.angle).rotateRad(-rangeAngle);
			}

			if(objQuad.useThousandth) {
				xPX1 = Conversion.get().mil2pixel(toPos1.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX1 = Conversion.get().mil2pixel(toPos1.y, canvas.getHeight(), dataSight.envZoomedIn);
				xPX2 = Conversion.get().mil2pixel(toPos2.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX2 = Conversion.get().mil2pixel(toPos2.y, canvas.getHeight(), dataSight.envZoomedIn);
				xPX3 = Conversion.get().mil2pixel(toPos3.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX3 = Conversion.get().mil2pixel(toPos3.y, canvas.getHeight(), dataSight.envZoomedIn);
				xPX4 = Conversion.get().mil2pixel(toPos4.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX4 = Conversion.get().mil2pixel(toPos4.y, canvas.getHeight(), dataSight.envZoomedIn);
			} else {
				xPX1 = Conversion.get().screenspace2pixel(toPos1.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX1 = Conversion.get().screenspace2pixel(toPos1.y, canvas.getHeight(), dataSight.envZoomedIn);
				xPX2 = Conversion.get().screenspace2pixel(toPos2.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX2 = Conversion.get().screenspace2pixel(toPos2.y, canvas.getHeight(), dataSight.envZoomedIn);
				xPX3 = Conversion.get().screenspace2pixel(toPos3.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX3 = Conversion.get().screenspace2pixel(toPos3.y, canvas.getHeight(), dataSight.envZoomedIn);
				xPX4 = Conversion.get().screenspace2pixel(toPos4.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX4 = Conversion.get().screenspace2pixel(toPos4.y, canvas.getHeight(), dataSight.envZoomedIn);
			}
			
			
		}
		
		xPX1 += canvas.getWidth()/2;
		yPX1 += canvas.getHeight()/2;
		xPX2 += canvas.getWidth()/2;
		yPX2 += canvas.getHeight()/2;
		xPX3 += canvas.getWidth()/2;
		yPX3 += canvas.getHeight()/2;
		xPX4 += canvas.getWidth()/2;
		yPX4 += canvas.getHeight()/2;
		
		g.setFill(dataSight.envSightColor);
		g.fillPolygon(new double[] {xPX1, xPX2, xPX3, xPX4}, new double[] {yPX1, yPX2, yPX3, yPX4}, 4);

	}
	
	
}








