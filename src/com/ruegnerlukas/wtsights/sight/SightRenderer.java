package com.ruegnerlukas.wtsights.sight;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.ruegnerlukas.simplemath.MathUtils;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.simplemath.vectors.vec3.Vector3d;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationAmmoData;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationData;
import com.ruegnerlukas.wtsights.data.sight.BallisticsBlock;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.data.sight.SightData.ScaleMode;
import com.ruegnerlukas.wtsights.data.sight.SightData.SelectedElement;
import com.ruegnerlukas.wtsights.data.sight.SightData.TextAlign;
import com.ruegnerlukas.wtsights.data.sight.SightData.Thousandth;
import com.ruegnerlukas.wtsights.data.sight.objects.CircleObject;
import com.ruegnerlukas.wtsights.data.sight.objects.LineObject;
import com.ruegnerlukas.wtsights.data.sight.objects.QuadObject;
import com.ruegnerlukas.wtsights.data.sight.objects.SightObject;
import com.ruegnerlukas.wtsights.data.sight.objects.TextObject;
import com.ruegnerlukas.wtsights.data.sight.objects.SightObject.Movement;
import com.ruegnerlukas.wtsights.data.sight.objects.SightObject.Type;
import com.ruegnerlukas.wtutils.SightUtils;

import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
public class SightRenderer {

	private static final Color COLOR_DEBUG_0 = new Color(0, 0, 1, 0.0f);
	private static final Color COLOR_DEBUG_1 = new Color(1, 1, 0, 0.0f);
	
	private static Color COLOR_SELECTION = new Color(1, 0, 0, 0.8f);
	
	
	
	public static void draw(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData) {
		
		Conversion.get().initialize(canvas.getWidth(), canvas.getHeight(), dataCalib.fovOut, dataCalib.fovIn, dataSight.gnrThousandth);
		
		drawBackground(canvas, g, dataSight, dataCalib, currentAmmoData);
		
		drawCenteredLines(canvas, g, dataSight, dataCalib, currentAmmoData);
		
		if(dataSight.envShowRangeFinder) {
			drawRangefinder(canvas, g, dataSight, dataCalib, currentAmmoData);
		}
		
		drawHorzRangeIndicators(canvas, g, dataSight, dataCalib, currentAmmoData);
		
		drawBallisticsBlock(canvas, g, dataSight, dataCalib, currentAmmoData, dataSight.brIndicators);
		
		for(Entry<String,BallisticsBlock> entry : dataSight.shellBlocks.entrySet()) {
			CalibrationAmmoData ammoData = null;
			for(CalibrationAmmoData data : dataCalib.ammoData) {
				if(data.ammoName.equals(entry.getValue().bBulletName)) {
					ammoData = data;
					break;
				}
			}
			if(ammoData != null) {
				if(entry.getValue().isVisible) {
					drawBallisticsBlock(canvas, g, dataSight, dataCalib, ammoData, entry.getValue());
				}
			} else {
				Logger.get().warn(SightRenderer.class, "No CalibrationAmmoData found: " + entry.getValue().bBulletName);
			}
			
		}
		
		
		for(Entry<String,SightObject> entry : dataSight.objects.entrySet()) {
			drawObject(canvas, g, dataSight, dataCalib, currentAmmoData, entry.getValue());
		}
		
		
//		drawVignette(canvas, g, dataSight, dataCalib, currentAmmoData);
		
	}

	
	
	
	
	
	private static void drawBackground(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData) {
		if(dataSight.envBackground != null) {
			g.drawImage(dataSight.envBackground, 0, 0, canvas.getWidth(), canvas.getHeight());
		} else {
			g.setFill(Color.GRAY);
			g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		}
	}
	
	
	
	
	private static void drawVignette(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData) {
//		if(dataSight.envZoomedIn) {
//			g.drawImage(vignetteIn, 0, 0, canvas.getWidth(), canvas.getHeight());
//		} else {
//			g.drawImage(vignetteOut, 0, 0, canvas.getWidth(), canvas.getHeight());
//		}
	}
	
	
	
	
	
	
	private static void drawCenteredLines(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData) {
		
		final double lineSize = 1.0 * dataSight.gnrLineSize * dataSight.gnrFontScale;
		
		g.setFill(dataSight.envSightColor);
		if(dataSight.gnrDrawCentralHorzLine) {
			g.fillRect(0, canvas.getHeight()/2 - (lineSize/2f), canvas.getWidth(), lineSize);
		}
		if(dataSight.gnrDrawCentralVertLine) {
			g.fillRect(canvas.getWidth()/2 - (lineSize/2f), 0, lineSize, canvas.getHeight());
		}
	}
	
	
	
	
	private static void drawRangefinder(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData) {
		
		// font
		Font font = null;
		if(dataSight.envZoomedIn) {
			font = new Font("Arial", 18.0 * dataSight.gnrFontScale * dataSight.rfTextScale * Conversion.get().zoomInMul);
		} else {
			font = new Font("Arial", 17.5 * dataSight.gnrFontScale * dataSight.rfTextScale);
		}
		Text text = new Text();
		text.setFont(font);
		text.setText("Measuring range");
		text.setWrappingWidth(0);
		text.setLineSpacing(0);
		
		
		// x position
		double x = 0;
		if(dataSight.envZoomedIn) {
			x = dataSight.rfOffset.x * dataSight.gnrFontScale * Conversion.get().zoomInMul;
		} else {
			x = dataSight.rfOffset.x * dataSight.gnrFontScale;
		}
		
		
		// y position
		double y = 0;
		if(dataSight.rfUseThousandth) {
			y = Conversion.get().mil2pixel(dataSight.rfOffset.y+3, canvas.getHeight(), dataSight.envZoomedIn) * dataSight.gnrFontScale;
			
		} else {
			if(dataSight.envZoomedIn) {
				y = Conversion.get().screenspace2pixel(dataSight.rfOffset.y+0.002, canvas.getHeight(), dataSight.envZoomedIn) * dataSight.gnrFontScale;
			} else {
				y = Conversion.get().screenspace2pixel(dataSight.rfOffset.y, canvas.getHeight(), dataSight.envZoomedIn) * dataSight.gnrFontScale;
			}
		}
		
		x += canvas.getWidth()/2.0;
		y = canvas.getHeight()/2.0 - y;

		
		// size
		final double width  = text.getLayoutBounds().getWidth();
		final double height = text.getLayoutBounds().getHeight();

		// draw background
		g.setFill(dataSight.rfColor1);
		g.fillRect(x, y-height/2, width*(dataSight.envProgress/100f), height);
		
		g.setFill(dataSight.rfColor2);
		g.fillRect(x+(width*(dataSight.envProgress/100f)), y-height/2, width*(1f-(dataSight.envProgress/100f)), height);

		// draw text
		g.setFill(dataSight.envSightColor);
		g.setTextAlign(TextAlignment.CENTER);
		g.setTextBaseline(VPos.CENTER);
		g.setFont(font);
		g.fillText("Measuring range", x+width/2, y);
		g.setTextAlign(TextAlignment.LEFT);
		g.setTextBaseline(VPos.BASELINE);
		
		// draw selection
		if(dataSight.selectedElement == SelectedElement.RANGEFINDER) {
			g.setStroke(COLOR_SELECTION);
			g.setLineDashes(3, 3);
			g.strokeRect(x-1, y-height/2-1, width+2, height+2);
			g.strokeLine(canvas.getWidth()/2, canvas.getHeight()/2, canvas.getWidth()/2, y+height/2);
			g.strokeLine(canvas.getWidth()/2, y+height/2, x, y+height/2);
			g.setLineDashes(null);
		}
		
		
	}
	
	
	
	
	private static void drawHorzRangeIndicators(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData) {
		
		if(dataSight.hrMils.isEmpty()) {
			return;
		}
		
		// line size
		final double lineSize = dataSight.gnrLineSize * dataSight.gnrFontScale;
		
		for(int i=0; i<dataSight.hrMils.size(); i++) {
			final int mil = dataSight.hrMils.get(i);
			final boolean isMajor = dataSight.hrMajors.get(i);
			
			// length
			final double length = Conversion.get().mil2pixel(isMajor ? dataSight.hrSizeMajor : dataSight.hrSizeMinor, canvas.getHeight(), dataSight.envZoomedIn);
			
			// x pos
			final double x = canvas.getWidth()/2 + Conversion.get().mil2pixel(mil, canvas.getHeight(), dataSight.envZoomedIn);
			
			// y pos
			final double y = canvas.getHeight()/2;
			final double yLabel = y - length - Conversion.get().screenspace2pixel(0.013, canvas.getHeight(), dataSight.envZoomedIn) * dataSight.gnrFontScale;
			
			
			
			// draw line
			g.setFill(dataSight.envSightColor);
			g.fillRect(x-lineSize/2, y-length, lineSize, length*2);
			if(dataSight.selectedElement == SelectedElement.HORZ_RANGE) {
				g.setStroke(COLOR_SELECTION);
				g.setLineDashes(3, 3);
				g.strokeRect(x-lineSize/2, y-length, lineSize, length*2);
				g.setLineDashes(null);
			}
			
			// draw label
			if(isMajor) {
				
				Font font = new Font("Arial", 12.5 * dataSight.gnrFontScale * (dataSight.envZoomedIn ? Conversion.get().zoomInMul : 1) );
				
				g.setFill(dataSight.envSightColor);
				g.setTextAlign(TextAlignment.CENTER);
				g.setTextBaseline(VPos.CENTER);
				g.setFont(font);
				
				g.fillText(""+Math.abs(mil), x, yLabel);
				
				if(dataSight.selectedElement == SelectedElement.HORZ_RANGE) {
					g.setFill(COLOR_SELECTION);
					g.fillText(""+Math.abs(mil), x, yLabel);
				}
				
				g.setTextAlign(TextAlignment.LEFT);
				g.setTextBaseline(VPos.BASELINE);
			}
			

		}
		
	}
	
	
	
	
	private static void drawBallisticsBlock(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData, BallisticsBlock block) {
		if(block.bScaleMode == ScaleMode.VERTICAL) {
			drawBallisticsVertical(canvas, g, dataSight, dataCalib, currentAmmoData, block);
		} else {
			drawBallisticsRadial(canvas, g, dataSight, dataCalib, currentAmmoData, block);
		}
	}
	
	
	
	
	private static void drawRangeCorrectionLabel(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData, BallisticsBlock block) {
		
		if(block.bDrawCorrection && dataSight.envRangeCorrection > 0) {
			
			Font corrFont = new Font("Arial", 25.5*0.5*dataSight.gnrFontScale*(dataSight.envZoomedIn?Conversion.get().zoomInMul:1));
			Text corrHelper = new Text();
			corrHelper.setFont(corrFont);
			corrHelper.setWrappingWidth(0);
			corrHelper.setLineSpacing(0);
			corrHelper.setText("Distance:"+dataSight.envRangeCorrection);

			// x position
			double corrX = -block.bCorrectionPos.x;
			if(dataSight.envZoomedIn) {
				corrX = corrX * 1.025;
			}
			corrX = Conversion.get().screenspace2pixel(corrX, canvas.getHeight(), dataSight.envZoomedIn);
			corrX = corrX * dataSight.gnrFontScale;
			corrX = canvas.getWidth()/2 - corrX;
			
			// y position
			double corrY = block.bCorrectionPos.y;
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

			if((dataSight.selectedElement == SelectedElement.BALL_RANGE && dataSight.selectedSubElement.equals(dataSight.brIndicators.name))
					|| (dataSight.selectedElement == SelectedElement.SHELL_BLOCK && dataSight.selectedSubElement.equals(block.name))) {
				g.setFill(COLOR_SELECTION);
				g.fillText("Distance:"+dataSight.envRangeCorrection, corrX, corrY);
			}
			
			g.setTextAlign(TextAlignment.RIGHT);
			g.setTextBaseline(VPos.BASELINE);
			
			
		}
		
	}
	
	
	
	
	private static void drawBallisticsVertical(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData, BallisticsBlock block) {
		
		if(block.bDists.isEmpty()) {
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
		final double rangeCorrectionPX = block.bMove ? Conversion.get().mil2pixel(rangeCorrectionMil, canvas.getHeight(), dataSight.envZoomedIn) : 0;
		
		// font
		Font bIndFont = new Font("Arial", 25*dataSight.gnrFontScale*0.5f*(dataSight.envZoomedIn?Conversion.get().zoomInMul:1));
		
		// draw indicators
		for(int i=0; i<block.bDists.size(); i++) {
			int mil = block.bDists.get(i);
			boolean isMajor = block.bMajors.get(i);
			
			// range fitting
			double resultPX = SightUtils.ballisticFunction(mil/100.0, fittingParams);
			double rangeMil = Conversion.get().pixel2mil(resultPX, canvas.getHeight(), false);
			double rangePixel = Conversion.get().mil2pixel(rangeMil, canvas.getHeight(), dataSight.envZoomedIn) * (block.bDrawUpward ? -1 : +1);
			rangePixel -= block.bDrawUpward ? -rangeCorrectionPX : rangeCorrectionPX;

			
			// CENTRAL BLOCK
			if(block.bDrawCenteredLines) {
				
				// x position
				double xCentral = canvas.getWidth()/2;
				
				// y position
				
				double yCentral = canvas.getHeight()/2 + rangePixel;
				
				// length
				double lengthCentral = Conversion.get().screenspace2pixel(isMajor?block.bSizeCentered.x:block.bSizeCentered.y, canvas.getHeight(), dataSight.envZoomedIn) * dataSight.gnrFontScale;
				lengthCentral = Math.max(lengthCentral, 0);
asd

adsf
ad

// draw
				if(!MathUtils.isNearlyEqual(lengthCentral, 0)) {
					g.setFill(dataSight.envSightColor);
					g.fillRect(xCentral-lengthCentral, yCentral-lineSize/2, lengthCentral*2, lineSize);
				
					if((dataSight.selectedElement == SelectedElement.BALL_RANGE && dataSight.selectedSubElement.equals(dataSight.brIndicators.name))
							|| (dataSight.selectedElement == SelectedElement.SHELL_BLOCK && dataSight.selectedSubElement.equals(block.name))) {
						g.setStroke(COLOR_SELECTION);
						g.setLineDashes(3, 3);
						g.strokeRect(xCentral-lengthCentral, yCentral-lineSize/2, lengthCentral*2, lineSize);
						g.setLineDashes(null);
					}
				}
			}
			
			
			// MAIN BLOCK
			
			// length
			double mainLength = 0;
			if(isMajor) {
				mainLength = mainLength + block.bSizeMain.x;
				mainLength = mainLength + block.bTextShift*block.bSizeMain.x;
				mainLength = mainLength + (0.03 * block.bSizeMain.x);
			} else {
				mainLength = mainLength + block.bSizeMain.y;
				mainLength = mainLength + (0.03 * block.bSizeMain.y);
			}
			mainLength = mainLength + Math.abs(block.bExtensions.get(i));
			mainLength = mainLength * dataSight.gnrFontScale;
			mainLength = Conversion.get().screenspace2pixel(mainLength, canvas.getHeight(), dataSight.envZoomedIn);
			mainLength = Math.max(0, mainLength);
			
			
			// x position
			double mainX = block.bMainPos.x;
			if(dataSight.envZoomedIn) {
				mainX = mainX + 0.003 * (block.bMainPos.x / 0.1);
			}
			if(isMajor) {
				mainX = mainX + block.bTextShift*block.bSizeMain.x;
			}
			mainX = mainX + (block.bExtensions.get(i)>0 ? block.bExtensions.get(i) : 0);
			mainX = mainX * dataSight.gnrFontScale;
			mainX = Conversion.get().screenspace2pixel(mainX, canvas.getHeight(), dataSight.envZoomedIn);
			mainX = canvas.getWidth()/2 - mainX;

			
			// y position
			double mainY = block.bMainPos.y;
			if(dataSight.envZoomedIn) {
				mainY = mainY + 0.004 * (block.bMainPos.y / 0.13817484);
			}
			mainY = mainY * dataSight.gnrFontScale;
			mainY = Conversion.get().screenspace2pixel(mainY, canvas.getHeight(), dataSight.envZoomedIn);
			mainY = canvas.getHeight()/2+rangePixel + mainY;
			
			if(!MathUtils.isNearlyEqual(mainLength, 0)) {
				g.setFill(dataSight.envSightColor);
				g.fillRect(mainX, mainY, mainLength, lineSize);
				
				if((dataSight.selectedElement == SelectedElement.BALL_RANGE && dataSight.selectedSubElement.equals(dataSight.brIndicators.name))
						|| (dataSight.selectedElement == SelectedElement.SHELL_BLOCK && dataSight.selectedSubElement.equals(block.name))) {
					g.setStroke(COLOR_SELECTION);
					g.setLineDashes(3, 3);
					g.strokeRect(mainX, mainY, mainLength, lineSize);
					g.setLineDashes(null);
				}
			}
			
			// main labels
			if(isMajor) {
				
				double distLabel = Conversion.get().screenspace2pixel(0.004, canvas.getHeight(), dataSight.envZoomedIn);
				double textOffX = Conversion.get().screenspace2pixel(block.bTextOffsets.get(i).x+block.bTextOffset.x, canvas.getHeight(), dataSight.envZoomedIn);
				double textOffY = Conversion.get().screenspace2pixel(block.bTextOffsets.get(i).y+block.bTextOffset.y, canvas.getHeight(), dataSight.envZoomedIn);
				textOffY *= dataSight.gnrFontScale;

				g.setFill(dataSight.envSightColor);
				
				if(block.bTextAlign == TextAlign.LEFT)   {
					g.setTextAlign(TextAlignment.LEFT);
				}
				
				if(block.bTextAlign == TextAlign.CENTER) {
					g.setTextAlign(TextAlignment.CENTER);
					textOffX -= distLabel/2;
				}
				
				if(block.bTextAlign == TextAlign.RIGHT)  {
					g.setTextAlign(TextAlignment.RIGHT);
					textOffX -= distLabel;
				}
				
				textOffX *= dataSight.gnrFontScale;
				
				g.setTextBaseline(VPos.CENTER);
				g.setFont(bIndFont);
				
				g.fillText(""+Math.abs(mil/100), mainX+textOffX, mainY+textOffY);
				
				if((dataSight.selectedElement == SelectedElement.BALL_RANGE && dataSight.selectedSubElement.equals(dataSight.brIndicators.name))
						|| (dataSight.selectedElement == SelectedElement.SHELL_BLOCK && dataSight.selectedSubElement.equals(block.name))) {
					g.setFill(COLOR_SELECTION);
					g.fillText(""+Math.abs(mil/100), mainX+textOffX, mainY+textOffY);
				}
				
				g.setTextAlign(TextAlignment.LEFT);
				g.setTextBaseline(VPos.BASELINE);
				
			}
			
		}
		
		
		// draw range correction label
		if(block.bDrawCorrection) {
			drawRangeCorrectionLabel(canvas, g, dataSight, dataCalib, currentAmmoData, block);
		}
		
	}

	
	

	private static void drawBallisticsRadial(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData, BallisticsBlock block) {
		
		if(block.bDists.isEmpty()) {
			return;
		}
		
		if(block.bCircleMode) {
			drawBallisticsRadialCircle(canvas, g, dataSight, dataCalib, currentAmmoData, block);
		} else {
			drawBallisticsRadialLine(canvas, g, dataSight, dataCalib, currentAmmoData, block);
		}
		
		
		// draw range correction label
		if(block.bDrawCorrection) {
			drawRangeCorrectionLabel(canvas, g, dataSight, dataCalib, currentAmmoData, block);
		}
		
	}
	
	
	
	
	
	private static void drawBallisticsRadialLine(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData, BallisticsBlock block) {
		
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
		double originX = block.bMainPos.x;
		if (dataSight.envZoomedIn) {
			originX = originX + 0.0015 * (block.bMainPos.x / 0.05);
		}
		originX = originX * dataSight.gnrFontScale;
		originX = Conversion.get().screenspace2pixel(originX, canvas.getHeight(), dataSight.envZoomedIn);

		// origin y
		double originY = block.bMainPos.y;
		if (dataSight.envZoomedIn) {
			originY = originY + 0.0015 * (block.bMainPos.y / 0.05);
		}
		originY = originY * dataSight.gnrFontScale;
		originY = Conversion.get().screenspace2pixel(originY, canvas.getHeight(), dataSight.envZoomedIn);
	
		
		// radius
		double radiusMil = 0;
		if(block.bRadiusUseMils) {
			radiusMil = block.bRadialRadius;
		} else {
			radiusMil = Conversion.get().screenspace2mil(block.bRadialRadius, dataSight.envZoomedIn);
		}
		
		double radiusPX = 0;
		if(block.bRadiusUseMils) {
			radiusPX = Conversion.get().mil2pixel(block.bRadialRadius, canvas.getHeight(), dataSight.envZoomedIn);
		} else {
			radiusPX = Conversion.get().screenspace2pixel(block.bRadialRadius, canvas.getHeight(), dataSight.envZoomedIn);
		}
		
		
		Conversion conversionUSSR = new Conversion();
		conversionUSSR.initialize(canvas.getWidth(), canvas.getHeight(), dataCalib.fovOut, dataCalib.fovIn, Thousandth.USSR);
		
		
		// draw indicators
		for(int i=0; i<block.bDists.size(); i++) {
			int meters = block.bDists.get(i);
			boolean isMajor = block.bMajors.get(i);
			Vector2d dir = new Vector2d(0, radiusPX);

			// angle offset
			double angleOffset = block.bRadialAngle;
			dir.rotateDeg(-angleOffset);
			
			// angle range
			double resultPX = SightUtils.ballisticFunction(meters/100.0, fittingParams);
			double rangeMil = conversionUSSR.pixel2mil(resultPX, canvas.getHeight(), false);
			double rangeAngle     = ((rangeMil) / radiusMil) * block.bRadialStretch;

			// angle correction
			double rangeCorrAngleSMil = SightUtils.rangeCorrection_meters2sovmil(dataSight.envRangeCorrection);
			double angleCorrection = SightUtils.calcAngle_deg(rangeCorrAngleSMil, block.bRadialRadius, block.bRadialStretch);

			// rotate
			if(block.bDrawUpward) {
				dir.rotateRad(+rangeAngle).rotateDeg(-angleCorrection);
			} else {
				dir.rotateRad(-rangeAngle).rotateDeg(+angleCorrection);
			}
			
			// length
			double length = Conversion.get().screenspace2pixel(block.bSizeMain.x, canvas.getHeight(), dataSight.envZoomedIn);
			length = length * dataSight.gnrFontScale;

			// line position
			Vector2d posStart = dir.copy().setLength(dir.length()-length/2);
			Vector2d posEnd = dir.copy().setLength(dir.length()+length/2);

			
			// draw line
			g.setStroke(dataSight.envSightColor);
			g.setLineWidth(lineSize);
			g.strokeLine(
					canvas.getWidth()/2 - originX + posStart.x,
					canvas.getHeight()/2 - originY + posStart.y,
					canvas.getWidth()/2 - originX + posEnd.x,
					canvas.getHeight()/2 - originY + posEnd.y
					);
			g.setLineWidth(1);

			
			if((dataSight.selectedElement == SelectedElement.BALL_RANGE && dataSight.selectedSubElement.equals(dataSight.brIndicators.name))
					|| (dataSight.selectedElement == SelectedElement.SHELL_BLOCK && dataSight.selectedSubElement.equals(block.name))) {
				g.setStroke(COLOR_SELECTION);
				g.setLineDashes(3, 3);
				g.setLineWidth(lineSize);
				g.strokeLine(
						canvas.getWidth()/2 - originX + posStart.x,
						canvas.getHeight()/2 - originY + posStart.y,
						canvas.getWidth()/2 - originX + posEnd.x,
						canvas.getHeight()/2 - originY + posEnd.y
						);
				g.setLineDashes(null);
				g.setLineWidth(1);
			}
			
			
			// draw labels
			if(isMajor) {
				
				double textOffset = (Conversion.get().screenspace2pixel(block.bTextOffsets.get(i).x+block.bTextOffset.x, canvas.getHeight(), dataSight.envZoomedIn)*dataSight.gnrFontScale);
				
				if(block.bTextAlign == TextAlign.LEFT)   {
					g.setTextAlign(TextAlignment.LEFT);
				}
				
				if(block.bTextAlign == TextAlign.CENTER) {
					g.setTextAlign(TextAlignment.CENTER);
				}
				
				if(block.bTextAlign == TextAlign.RIGHT)  {
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
				
				if((dataSight.selectedElement == SelectedElement.BALL_RANGE && dataSight.selectedSubElement.equals(dataSight.brIndicators.name))
						|| (dataSight.selectedElement == SelectedElement.SHELL_BLOCK && dataSight.selectedSubElement.equals(block.name))) {
					g.setFill(COLOR_SELECTION);
					g.fillText(
							""+Math.abs(meters/100),
							canvas.getWidth()/2 - originX + posEnd.x + dir.copy().setLength(textOffset).x,
							canvas.getHeight()/2 - originY + posEnd.y + dir.copy().setLength(textOffset).y
							);
				}
				
				g.setTextAlign(TextAlignment.LEFT);
				g.setTextBaseline(VPos.BASELINE);
				
			}
			
			
		}
		
		
	}
	
	
	

	private static void drawBallisticsRadialCircle(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData, BallisticsBlock block) {
		
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
		double originX = block.bMainPos.x;
		if (dataSight.envZoomedIn) {
			originX = originX + 0.0015 * (block.bMainPos.x / 0.05);
		}
		originX = originX * dataSight.gnrFontScale;
		originX = Conversion.get().screenspace2pixel(originX, canvas.getHeight(), dataSight.envZoomedIn);

		// origin y
		double originY = block.bMainPos.y;
		if (dataSight.envZoomedIn) {
			originY = originY + 0.0015 * (block.bMainPos.y / 0.05);
		}
		originY = originY * dataSight.gnrFontScale;
		originY = Conversion.get().screenspace2pixel(originY, canvas.getHeight(), dataSight.envZoomedIn);
	
		
		// radius
		double radiusMil = 0;
		if(block.bRadiusUseMils) {
			radiusMil = block.bRadialRadius;
		} else {
			radiusMil = Conversion.get().screenspace2mil(block.bRadialRadius, dataSight.envZoomedIn);
		}
		
		double radiusPX = 0;
		if(block.bRadiusUseMils) {
			radiusPX = Conversion.get().mil2pixel(block.bRadialRadius, canvas.getHeight(), dataSight.envZoomedIn);
		} else {
			radiusPX = Conversion.get().screenspace2pixel(block.bRadialRadius, canvas.getHeight(), dataSight.envZoomedIn);
		}
		
		
		Conversion conversionUSSR = new Conversion();
		conversionUSSR.initialize(canvas.getWidth(), canvas.getHeight(), dataCalib.fovOut, dataCalib.fovIn, Thousandth.USSR);
		
		// draw indicators
		for(int i=0; i<block.bDists.size(); i++) {
			int meters = block.bDists.get(i);
			boolean isMajor = block.bMajors.get(i);
		
			Vector2d dir = new Vector2d(0, radiusPX);

			// angle offset
			double angleOffset = block.bRadialAngle;
			dir.rotateDeg(-angleOffset);
			
			// angle range
			double resultPX = SightUtils.ballisticFunction(meters/100.0, fittingParams);
			double rangeMil = conversionUSSR.pixel2mil(resultPX, canvas.getHeight(), false);
			double rangeAngle     = ((rangeMil) / radiusMil) * block.bRadialStretch;

			// angle correction
			double rangeCorrAngleSMil = SightUtils.rangeCorrection_meters2sovmil(dataSight.envRangeCorrection);
			double angleCorrection = SightUtils.calcAngle_deg(rangeCorrAngleSMil, block.bRadialRadius, block.bRadialStretch);

			// rotate
			if(block.bDrawUpward) {
				dir.rotateRad(+rangeAngle).rotateDeg(-angleCorrection);
			} else {
				dir.rotateRad(-rangeAngle).rotateDeg(+angleCorrection);
			}
			
			// circle radius
			double circleRadius = block.bSizeMain.y;
			circleRadius = circleRadius + block.bExtensions.get(i);
			circleRadius = circleRadius * 2;
			circleRadius = circleRadius * dataSight.gnrFontScale;
			circleRadius = Conversion.get().screenspace2pixel(circleRadius, canvas.getHeight(), dataSight.envZoomedIn);
						
			// circle width
			double circleWidth = block.bSizeMain.x;
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
			
			if((dataSight.selectedElement == SelectedElement.BALL_RANGE && dataSight.selectedSubElement.equals(dataSight.brIndicators.name))
					|| (dataSight.selectedElement == SelectedElement.SHELL_BLOCK && dataSight.selectedSubElement.equals(block.name))) {
				g.setStroke(COLOR_SELECTION);
				g.setLineDashes(3, 3);
				g.setLineWidth(circleWidth);
				g.strokeOval(
						canvas.getWidth()/2 - originX + dir.x - circleRadius/2,
						canvas.getHeight()/2 - originY + dir.y - circleRadius/2,
						circleRadius, circleRadius);
				g.setLineDashes(null);
				g.setLineWidth(1);
			}
			
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
				
				double textOffset = (Conversion.get().screenspace2pixel(block.bTextOffsets.get(i).x+block.bTextOffset.x, canvas.getHeight(), dataSight.envZoomedIn)*dataSight.gnrFontScale);
				
				if(block.bTextAlign == TextAlign.LEFT)   {
					g.setTextAlign(TextAlignment.LEFT);
				}
				
				if(block.bTextAlign == TextAlign.CENTER) {
					g.setTextAlign(TextAlignment.CENTER);
				}
				
				if(block.bTextAlign == TextAlign.RIGHT)  {
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
				
				if((dataSight.selectedElement == SelectedElement.BALL_RANGE && dataSight.selectedSubElement.equals(dataSight.brIndicators.name))
						|| (dataSight.selectedElement == SelectedElement.SHELL_BLOCK && dataSight.selectedSubElement.equals(block.name))) {
					g.setFill(COLOR_SELECTION);
					g.fillText(
							""+Math.abs(meters/100),
							canvas.getWidth()/2 - originX + dir.x + dir.copy().setLength(textOffset).x,
							canvas.getHeight()/2 - originY + dir.y + dir.copy().setLength(textOffset).y
							);
					}
				
				g.setTextAlign(TextAlignment.LEFT);
				g.setTextBaseline(VPos.BASELINE);
				
				
			}
			
			
		}
		
	}
	
	
	
	
	
	private static void drawObject(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData, SightObject obj) {
		
		if(obj.type == Type.LINE) {
			drawLineObject(canvas, g, dataSight, dataCalib, currentAmmoData, (LineObject)obj);
		}
		if(obj.type == Type.TEXT) {
			drawTextObject(canvas, g, dataSight, dataCalib, currentAmmoData, (TextObject)obj);
		}
		if(obj.type == Type.CIRCLE) {
			drawCircleObject(canvas, g, dataSight, dataCalib, currentAmmoData, (CircleObject)obj);
		}
		if(obj.type == Type.QUAD) {
			drawQuadObject(canvas, g, dataSight, dataCalib, currentAmmoData, (QuadObject)obj);
		}
	}
	
	
	
	
	private static void drawLineObject(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData, LineObject objLine) {
		
		double sxPX = 0;
		double syPX = 0;
		double exPX = 0;
		double eyPX = 0;

		
		if(objLine.cmnMovement == Movement.STATIC) {
			
			if(objLine.cmnUseThousandth) {
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
			
			
		} else if(objLine.cmnMovement == Movement.MOVE) {
			
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
			final double rangeCorrectionMil = Conversion.get().pixel2mil(rangeCorrectionResultPX, canvas.getHeight(), false);
			final double rangeCorrectionPX = Conversion.get().mil2pixel(rangeCorrectionMil, canvas.getHeight(), dataSight.envZoomedIn);
			
			if(objLine.cmnUseThousandth) {
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
			
			
			
		} else if(objLine.cmnMovement == Movement.MOVE_RADIAL) {
			
			Vector2d centerOW = new Vector2d();
			if(objLine.useAutoCenter) {
				centerOW.add(objLine.start).add(objLine.end).scale(0.5f);
			} else {
				centerOW = objLine.cmnCenter;
			}
			final double radius = centerOW.dist(objLine.cmnRadCenter);
			if(MathUtils.isNearlyEqual(radius, 0)) {
				return;
			}
			
			double rangeCorrAngleSMil = SightUtils.rangeCorrection_meters2sovmil(dataSight.envRangeCorrection);
			double rangeAngle = SightUtils.calcAngle_rad(rangeCorrAngleSMil, radius, objLine.cmnSpeed);
			
			Vector2d toStart = Vector2d.createVectorAB(objLine.cmnRadCenter, objLine.start);
			Vector2d toEnd = Vector2d.createVectorAB(objLine.cmnRadCenter, objLine.end);
			if(MathUtils.isNearlyEqual(toStart.length2(), 0) && MathUtils.isNearlyEqual(toEnd.length2(), 0)) {
				toStart.set(objLine.start);
				toEnd.set(objLine.end);
			} else {
				toStart.rotateDeg(-objLine.cmnAngle).rotateRad(-rangeAngle);
				toEnd.rotateDeg(-objLine.cmnAngle).rotateRad(-rangeAngle);
			}

			
			if(objLine.cmnUseThousandth) {
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
		
		if(dataSight.selectedElement == SelectedElement.CUSTOM_ELEMENT && dataSight.selectedSubElement.equals(objLine.name)) {
			g.setStroke(COLOR_SELECTION);
			g.setLineDashes(3, 3);
			g.setLineWidth(dataSight.gnrLineSize*dataSight.gnrFontScale);
			g.strokeLine(sxPX, syPX, exPX, eyPX);
			g.setLineDashes(null);
			g.setLineWidth(1);
		}
		
	}
	
	
	
	
	private static void drawTextObject(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData, TextObject objText) {
	
		double xPX = 0;
		double yPX = 0;
		
		if(objText.cmnMovement == Movement.STATIC) {
		
			if(objText.cmnUseThousandth) {
				xPX = Conversion.get().mil2pixel(objText.pos.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX = Conversion.get().mil2pixel(objText.pos.y, canvas.getHeight(), dataSight.envZoomedIn);
			} else {
				xPX = Conversion.get().screenspace2pixel(objText.pos.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX = Conversion.get().screenspace2pixel(objText.pos.y, canvas.getHeight(), dataSight.envZoomedIn);
			}
			
			
		} else if(objText.cmnMovement == Movement.MOVE) {
			
			if(objText.cmnUseThousandth) {
				xPX = Conversion.get().mil2pixel(objText.pos.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX = Conversion.get().mil2pixel(objText.pos.y, canvas.getHeight(), dataSight.envZoomedIn);
			} else {
				xPX = Conversion.get().screenspace2pixel(objText.pos.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX = Conversion.get().screenspace2pixel(objText.pos.y, canvas.getHeight(), dataSight.envZoomedIn);
			}
			
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
			final double rangeCorrectionMil = Conversion.get().pixel2mil(rangeCorrectionResultPX, canvas.getHeight(), false);
			final double rangeCorrectionPX = Conversion.get().mil2pixel(rangeCorrectionMil, canvas.getHeight(), dataSight.envZoomedIn);
			
			if(dataSight.gnrApplyCorrectionToGun) {
				yPX -= rangeCorrectionPX;
			} else {
				yPX += rangeCorrectionPX;
			}
			
			
		} else if(objText.cmnMovement == Movement.MOVE_RADIAL) {
			
			Vector2d centerOW = new Vector2d();
			if(objText.useAutoCenter) {
				centerOW.set(objText.pos);
			} else {
				centerOW = objText.cmnCenter;
			}
			final double radius = centerOW.dist(objText.cmnRadCenter);
			if(MathUtils.isNearlyEqual(radius, 0)) {
				return;
			}
			
			double rangeCorrAngleSMil = SightUtils.rangeCorrection_meters2sovmil(dataSight.envRangeCorrection);
			double rangeAngle = SightUtils.calcAngle_rad(rangeCorrAngleSMil, radius, objText.cmnSpeed);
			
			Vector2d toCenter = Vector2d.createVectorAB(objText.cmnRadCenter, objText.pos);
			if(MathUtils.isNearlyEqual(toCenter.length2(), 0)) {
				toCenter.set(objText.pos);
			} else {
				toCenter.rotateDeg(-objText.cmnAngle).rotateRad(-rangeAngle);
			}

			if(objText.cmnUseThousandth) {
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
		
		if(dataSight.selectedElement == SelectedElement.CUSTOM_ELEMENT && dataSight.selectedSubElement.equals(objText.name)) {
			g.setFill(COLOR_SELECTION);
			g.fillText(objText.text, xPX, yPX);
		}
		
		g.setTextAlign(TextAlignment.LEFT);
		g.setTextBaseline(VPos.BASELINE);
		
		
	}
	
	
	
	
	private static void drawCircleObject(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData, CircleObject objCircle) {
	
		double xPX = 0;
		double yPX = 0;
		double dPX = 0;
		
		if(objCircle.cmnMovement == Movement.STATIC) {
			
			if(objCircle.cmnUseThousandth) {
				xPX = Conversion.get().mil2pixel(objCircle.pos.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX = Conversion.get().mil2pixel(objCircle.pos.y, canvas.getHeight(), dataSight.envZoomedIn);
				dPX = Conversion.get().mil2pixel(objCircle.diameter, canvas.getHeight(), dataSight.envZoomedIn);
			} else {
				xPX = Conversion.get().screenspace2pixel(objCircle.pos.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX = Conversion.get().screenspace2pixel(objCircle.pos.y, canvas.getHeight(), dataSight.envZoomedIn);
				dPX = Conversion.get().screenspace2pixel(objCircle.diameter, canvas.getHeight(), dataSight.envZoomedIn);
			}
			
			
		} else if(objCircle.cmnMovement == Movement.MOVE) {
			
			if(objCircle.cmnUseThousandth) {
				xPX = Conversion.get().mil2pixel(objCircle.pos.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX = Conversion.get().mil2pixel(objCircle.pos.y, canvas.getHeight(), dataSight.envZoomedIn);
				dPX = Conversion.get().mil2pixel(objCircle.diameter, canvas.getHeight(), dataSight.envZoomedIn);
			} else {
				xPX = Conversion.get().screenspace2pixel(objCircle.pos.x, canvas.getHeight(), dataSight.envZoomedIn);
				yPX = Conversion.get().screenspace2pixel(objCircle.pos.y, canvas.getHeight(), dataSight.envZoomedIn);
				dPX = Conversion.get().screenspace2pixel(objCircle.diameter, canvas.getHeight(), dataSight.envZoomedIn);
			}
			
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
			final double rangeCorrectionMil = Conversion.get().pixel2mil(rangeCorrectionResultPX, canvas.getHeight(), false);
			final double rangeCorrectionPX = Conversion.get().mil2pixel(rangeCorrectionMil, canvas.getHeight(), dataSight.envZoomedIn);
			
			if(dataSight.gnrApplyCorrectionToGun) {
				yPX -= rangeCorrectionPX;
			} else {
				yPX += rangeCorrectionPX;
			}			
			
		} else if(objCircle.cmnMovement == Movement.MOVE_RADIAL) {

			Vector2d centerOW = new Vector2d();
			if(objCircle.useAutoCenter) {
				centerOW.set(objCircle.pos);
			} else {
				centerOW = objCircle.cmnCenter;
			}
			final double radius = centerOW.dist(objCircle.cmnRadCenter);
			if(MathUtils.isNearlyEqual(radius, 0)) {
				return;
			}
			
			double rangeCorrAngleSMil = SightUtils.rangeCorrection_meters2sovmil(dataSight.envRangeCorrection);
			double rangeAngle = SightUtils.calcAngle_rad(rangeCorrAngleSMil, radius, objCircle.cmnSpeed);
			
			Vector2d toCenter = Vector2d.createVectorAB(objCircle.cmnRadCenter, objCircle.pos);
			if(MathUtils.isNearlyEqual(toCenter.length2(), 0)) {
				toCenter.set(objCircle.pos);
			} else {
				toCenter.rotateDeg(-objCircle.cmnAngle).rotateRad(-rangeAngle);
			}
			
			if(objCircle.cmnUseThousandth) {
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
		
			if(dataSight.selectedElement == SelectedElement.CUSTOM_ELEMENT && dataSight.selectedSubElement.equals(objCircle.name)) {
				g.setStroke(COLOR_SELECTION);
				g.setLineDashes(3, 3);
				g.strokeOval(xPX-dPX/2, yPX-dPX/2, dPX, dPX);
				g.setLineDashes(null);
			}
			
		} else {
			
			final double angleStart = objCircle.segment.x;
			final double angleEnd = objCircle.segment.y;
			final double angleStep = 2 * (100/objCircle.diameter) * Math.log(objCircle.diameter+1)*0.5;
			
			double lastX = 0;
			double lastY = 0;
			
			g.setStroke(dataSight.envSightColor);
			g.setLineWidth(lineWidth);
			
			g.strokeLine(
					xPX + Math.sin(Math.toRadians(angleStart)) * dPX/2,
					yPX + Math.cos(Math.toRadians(angleStart)) * dPX/2,
					xPX + Math.sin(Math.toRadians(angleStart+angleStep)) * dPX/2,
					yPX + Math.cos(Math.toRadians(angleStart+angleStep)) * dPX/2
					);
			
			g.strokeLine(
					xPX + Math.sin(Math.toRadians(angleEnd-angleStep)) * dPX/2,
					yPX + Math.cos(Math.toRadians(angleEnd-angleStep)) * dPX/2,
					xPX + Math.sin(Math.toRadians(angleEnd)) * dPX/2,
					yPX + Math.cos(Math.toRadians(angleEnd)) * dPX/2
					);
			
			if(dataSight.selectedElement == SelectedElement.CUSTOM_ELEMENT && dataSight.selectedSubElement.equals(objCircle.name)) {
				g.setStroke(COLOR_SELECTION);
				g.setLineDashes(3, 3);
				g.strokeLine(
						xPX + Math.sin(Math.toRadians(angleStart)) * dPX/2,
						yPX + Math.cos(Math.toRadians(angleStart)) * dPX/2,
						xPX + Math.sin(Math.toRadians(angleStart+angleStep)) * dPX/2,
						yPX + Math.cos(Math.toRadians(angleStart+angleStep)) * dPX/2
						);
				
				g.strokeLine(
						xPX + Math.sin(Math.toRadians(angleEnd-angleStep)) * dPX/2,
						yPX + Math.cos(Math.toRadians(angleEnd-angleStep)) * dPX/2,
						xPX + Math.sin(Math.toRadians(angleEnd)) * dPX/2,
						yPX + Math.cos(Math.toRadians(angleEnd)) * dPX/2
						);
				g.setLineDashes(null);
			}
			
			int i=0;
			for(double deg = angleStart; deg<=angleEnd; deg+=angleStep) {
				
				double x = xPX + Math.sin(Math.toRadians(deg)) * dPX/2;
				double y = yPX + Math.cos(Math.toRadians(deg)) * dPX/2;

				if(i > 0) {
					
					g.setStroke(dataSight.envSightColor);
					g.setLineWidth(lineWidth);
					
					g.strokeLine(lastX, lastY, x, y);
					
					if(dataSight.selectedElement == SelectedElement.CUSTOM_ELEMENT && dataSight.selectedSubElement.equals(objCircle.name)) {
						g.setStroke(COLOR_SELECTION);
						g.setLineDashes(3, 3);
						g.strokeLine(lastX, lastY, x, y);
						g.setLineDashes(null);
					}
					
				}
				
				lastX = x;
				lastY = y;
				i++;
			}
			
		}
		
		g.setLineWidth(1);
		
	}
	
	
	
	
	private static void drawQuadObject(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData, QuadObject objQuad) {
		
		double xPX1 = 0;
		double yPX1 = 0;
		double xPX2 = 0;
		double yPX2 = 0;
		double xPX3 = 0;
		double yPX3 = 0;
		double xPX4 = 0;
		double yPX4 = 0;
		
		if(objQuad.cmnMovement == Movement.STATIC) {
			
			if(objQuad.cmnUseThousandth) {
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
			
			
			
		} else if(objQuad.cmnMovement == Movement.MOVE) {
			
			if(objQuad.cmnUseThousandth) {
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
			final double rangeCorrectionMil = Conversion.get().pixel2mil(rangeCorrectionResultPX, canvas.getHeight(), false);
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
			
		} else if(objQuad.cmnMovement == Movement.MOVE_RADIAL) {

			Vector2d centerOW = new Vector2d();
			
			if(objQuad.useAutoCenter) {
				centerOW.add(objQuad.pos1).add(objQuad.pos2).add(objQuad.pos3).add(objQuad.pos4.x).scale(1.0/4.0);
			} else {
				centerOW = objQuad.cmnCenter;
			}
			final double radius = centerOW.dist(objQuad.cmnRadCenter);
			if(MathUtils.isNearlyEqual(radius, 0)) {
				return;
			}
			
			double rangeCorrAngleSMil = SightUtils.rangeCorrection_meters2sovmil(dataSight.envRangeCorrection);
			double rangeAngle = SightUtils.calcAngle_rad(rangeCorrAngleSMil, radius, objQuad.cmnSpeed);
		
			Vector2d toPos1 = Vector2d.createVectorAB(objQuad.cmnRadCenter, objQuad.pos1);
			Vector2d toPos2 = Vector2d.createVectorAB(objQuad.cmnRadCenter, objQuad.pos2);
			Vector2d toPos3 = Vector2d.createVectorAB(objQuad.cmnRadCenter, objQuad.pos3);
			Vector2d toPos4 = Vector2d.createVectorAB(objQuad.cmnRadCenter, objQuad.pos4);

			if(MathUtils.isNearlyEqual(toPos1.length2(), 0) && MathUtils.isNearlyEqual(toPos2.length2(), 0) && MathUtils.isNearlyEqual(toPos3.length2(), 0) && MathUtils.isNearlyEqual(toPos4.length2(), 0)) {
				toPos1.set(objQuad.pos1);
				toPos2.set(objQuad.pos2);
				toPos3.set(objQuad.pos3);
				toPos4.set(objQuad.pos4);
			} else {
				toPos1.rotateDeg(-objQuad.cmnAngle).rotateRad(-rangeAngle);
				toPos2.rotateDeg(-objQuad.cmnAngle).rotateRad(-rangeAngle);
				toPos3.rotateDeg(-objQuad.cmnAngle).rotateRad(-rangeAngle);
				toPos4.rotateDeg(-objQuad.cmnAngle).rotateRad(-rangeAngle);
			}

			if(objQuad.cmnUseThousandth) {
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
		
		if(dataSight.selectedElement == SelectedElement.CUSTOM_ELEMENT && dataSight.selectedSubElement.equals(objQuad.name)) {
			g.setStroke(COLOR_SELECTION);
			g.setLineDashes(3, 3);
			g.strokePolygon(new double[] {xPX1, xPX2, xPX3, xPX4}, new double[] {yPX1, yPX2, yPX3, yPX4}, 4);
			g.setLineDashes(null);
		}
	}
	
	
}








