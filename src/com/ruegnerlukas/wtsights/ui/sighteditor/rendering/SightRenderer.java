package com.ruegnerlukas.wtsights.ui.sighteditor.rendering;

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
import com.ruegnerlukas.wtsights.data.sight.HIndicator;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.data.sight.elements.Element;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementBallRangeIndicator;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCentralHorzLine;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCentralVertLine;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCustomCircle;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCustomLine;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCustomQuad;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCustomText;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementHorzRangeIndicators;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementRangefinder;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementShellBlock;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.elements.layouts.LayoutBallRangeIndicators;
import com.ruegnerlukas.wtsights.data.sight.elements.layouts.LayoutCircleObject;
import com.ruegnerlukas.wtsights.data.sight.elements.layouts.LayoutHorzRangeIndicators;
import com.ruegnerlukas.wtsights.data.sight.elements.layouts.LayoutLineObject;
import com.ruegnerlukas.wtsights.data.sight.elements.layouts.LayoutQuadObject;
import com.ruegnerlukas.wtsights.data.sight.elements.layouts.LayoutRangefinder;
import com.ruegnerlukas.wtsights.data.sight.elements.layouts.LayoutTextObject;
import com.ruegnerlukas.wtutils.Conversion;
import com.ruegnerlukas.wtutils.SightUtils.ScaleMode;
import com.ruegnerlukas.wtutils.SightUtils.TextAlign;

import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;



public class SightRenderer {

	private static final int MAX_FONT_CACHE_SIZE = 20;
	private static List<Font> fontCache = new ArrayList<Font>();
	
	
	public static Font getFont(double size) {
		
		for(int i=0, n=fontCache.size(); i<n; i++) {
			Font font = fontCache.get(i);
			
			if(MathUtils.isNearlyEqual(font.getSize(), size)) {
				fontCache.remove(font);
				fontCache.add(font);
				return font;
			}
			
		}
		
		Font font = new Font("Arial", size);
		fontCache.add(font);
		
		if(MAX_FONT_CACHE_SIZE < fontCache.size()) {
			fontCache.remove(fontCache.size()-1);
		}
		
		return font;
	}
	
	
	private static final Color COLOR_SELECTION_0 = Color.WHITE;
	private static final Color COLOR_SELECTION_1 = Color.WHITE;
	
	
	
	
	
	public static void draw(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData) {
		
		Conversion.get().initialize(canvas.getWidth(), canvas.getHeight(), dataCalib.vehicle.fovOut, dataCalib.vehicle.fovIn, dataSight.gnrThousandth);
		
		// background
		drawBackground(canvas, g, dataSight);
		
		// centered lines
		drawCenteredLines(canvas, g, dataSight, dataCalib, currentAmmoData);
		
		// rangefinder
		if(dataSight.envShowRangeFinder) {
			drawRangefinder(canvas, g, dataSight, dataCalib, currentAmmoData);
		}
		
		// horz range indicators
		drawHorzRangeIndicators(canvas, g, dataSight, dataCalib, currentAmmoData);
		
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
	
	
	
	
	private static void drawCenteredLines(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData ammoData) {
		
		ElementCentralHorzLine horzLine = (ElementCentralHorzLine)dataSight.getElements(ElementType.CENTRAL_HORZ_LINE).get(0);
		ElementCentralVertLine vertLine = (ElementCentralVertLine)dataSight.getElements(ElementType.CENTRAL_VERT_LINE).get(0);

		Rectanglef horzBounds = horzLine.layout(dataSight, dataCalib, ammoData, canvas.getWidth(), canvas.getHeight()).bounds;
		Rectanglef vertBounds = vertLine.layout(dataSight, dataCalib, ammoData, canvas.getWidth(), canvas.getHeight()).bounds;
		
		g.setFill(dataSight.envSightColor);
		if(horzLine.drawCentralHorzLine) {
			g.fillRect(horzBounds.x, horzBounds.y, horzBounds.width, horzBounds.height);
		}
		
		
		if(vertLine.drawCentralVertLine) {
			g.fillRect(vertBounds.x, vertBounds.y, vertBounds.width, vertBounds.height);
		}
		
	}
	
	
	
	
	private static void drawRangefinder(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData ammoData) {
		
		ElementRangefinder rangefinder = (ElementRangefinder)dataSight.getElements(ElementType.RANGEFINDER).get(0);
		
		LayoutRangefinder layout = rangefinder.layout(dataSight, dataCalib, ammoData, canvas.getWidth(), canvas.getHeight());
		Rectanglef bounds = layout.bounds;
		Vector2d textPos = layout.textPos;
		
		// font
		Font font = getFont(layout.fontSize);

		// draw background
		g.setFill(rangefinder.color1);
		g.fillRect(bounds.x, bounds.y, bounds.width*(dataSight.envRFProgress/100f), bounds.height);
		
		g.setFill(rangefinder.color2);
		g.fillRect(bounds.x+(bounds.width*(dataSight.envRFProgress/100f)), bounds.y, bounds.width*(1f-(dataSight.envRFProgress/100f)), bounds.height);

		// draw text
		g.setFill(dataSight.envSightColor);
		g.setTextAlign(TextAlignment.CENTER);
		g.setTextBaseline(VPos.CENTER);
		g.setFont(font);
		g.fillText("Measuring range", textPos.x, textPos.y);
		g.setTextAlign(TextAlignment.LEFT);
		g.setTextBaseline(VPos.BASELINE);
		
	}
	
	
	
	
	private static void drawHorzRangeIndicators(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData ammoData) {
		
		ElementHorzRangeIndicators horRange = (ElementHorzRangeIndicators)dataSight.getElements(ElementType.HORZ_RANGE_INDICATORS).get(0);
		if(horRange.indicators.isEmpty()) {
			return;
		}
		
		LayoutHorzRangeIndicators layoutData = horRange.layout(dataSight, dataCalib, ammoData, canvas.getWidth(), canvas.getHeight());
		
		for(int i=0; i<layoutData.bounds.length; i++) {
			Rectanglef bounds = layoutData.bounds[i];
			Vector2d textPos = layoutData.textPositions[i];
			HIndicator indicator = horRange.indicators.get(i);
			
			// draw line
			g.setFill(dataSight.envSightColor);
			g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
			
			// draw label
			if(indicator.isMajor()) {
				Font font = getFont(layoutData.fontSize );
				g.setFill(dataSight.envSightColor);
				g.setTextAlign(TextAlignment.CENTER);
				g.setTextBaseline(VPos.CENTER);
				g.setFont(font);
				g.fillText(""+Math.abs(indicator.getMil()), textPos.x, textPos.y);
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
			
			Vector3d layout = block.layoutLabel(dataSight, canvas.getWidth(), canvas.getHeight()).corrLabel;

			Font corrFont = getFont(layout.z);
			Text corrHelper = new Text();
			corrHelper.setFont(corrFont);
			corrHelper.setWrappingWidth(0);
			corrHelper.setLineSpacing(0);
			corrHelper.setText("Distance:"+dataSight.envRangeCorrection);
			
			// draw label
			g.setTextBaseline(VPos.CENTER);
			g.setFont(corrFont);
			g.setTextAlign(TextAlignment.LEFT);
			g.fillText("Distance:"+dataSight.envRangeCorrection, layout.x, layout.y);
			g.setTextAlign(TextAlignment.RIGHT);
			g.setTextBaseline(VPos.BASELINE);
			
		}
		
	}
	
	
	
	
	private static void drawBallisticsVertical(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData, ElementBallRangeIndicator block) {
		
		if(block.indicators.isEmpty()) {
			return;
		}

		LayoutBallRangeIndicators layout = block.layout(dataSight, dataCalib, currentAmmoData, canvas.getWidth(), canvas.getHeight());

		Font bIndFont = getFont(layout.fontSize);
		
		// draw indicators
		for(int i=0; i<block.indicators.size(); i++) {
			Rectanglef boundsMain = layout.vMainBounds[i];
			Rectanglef boundsCenter = layout.vCenterBounds[i];
			Vector2d textPos = layout.vTextPositions[i];
			BIndicator indicator = block.indicators.get(i);
			
			int mil = indicator.getDistance();
			boolean isMajor = indicator.isMajor();
			
			// CENTRAL BLOCK
			if(block.drawAddLines) {
				g.setFill(dataSight.envSightColor);
				g.fillRect(boundsCenter.x, boundsCenter.y, boundsCenter.width, boundsCenter.height);
			}
			
			// MAIN BLOCK
			g.setFill(dataSight.envSightColor);
			g.fillRect(boundsMain.x, boundsMain.y, boundsMain.width, boundsMain.height);
			
			// main labels
			if(isMajor) {
				g.setFill(dataSight.envSightColor);
				if(block.textAlign == TextAlign.LEFT)   { g.setTextAlign(TextAlignment.LEFT); }
				if(block.textAlign == TextAlign.CENTER) { g.setTextAlign(TextAlignment.CENTER); }
				if(block.textAlign == TextAlign.RIGHT)  { g.setTextAlign(TextAlignment.RIGHT); }
				g.setTextBaseline(VPos.CENTER);
				g.setFont(bIndFont);
				g.fillText(""+Math.abs(mil/100), textPos.x, textPos.y);
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
		
		LayoutBallRangeIndicators layout = block.layout(dataSight, dataCalib, currentAmmoData, canvas.getWidth(), canvas.getHeight());
		
		final double lineSize = layout.rlLineSize;
		Font bIndFont = getFont(layout.fontSize);
		
		// draw indicators
		for(int i=0; i<block.indicators.size(); i++) {
			Vector4d line = layout.rlLines[i];
			Vector2d textPos = layout.rlTextPositions[i];
			BIndicator indicator = block.indicators.get(i);
			
			int meters = indicator.getDistance();
			boolean isMajor = indicator.isMajor();
			
			// draw line
			g.setStroke(dataSight.envSightColor);
			g.setLineWidth(lineSize);
			g.strokeLine(line.x, line.y, line.z, line.w);
			g.setLineWidth(1);
			
			// draw labels
			if(isMajor) {
				if(block.textAlign == TextAlign.LEFT)   { g.setTextAlign(TextAlignment.LEFT); }
				if(block.textAlign == TextAlign.CENTER) { g.setTextAlign(TextAlignment.CENTER); }
				if(block.textAlign == TextAlign.RIGHT)  { g.setTextAlign(TextAlignment.RIGHT); }
				g.setFill(dataSight.envSightColor);
				g.setTextBaseline(VPos.CENTER);
				g.setFont(bIndFont);
				g.fillText(""+Math.abs(meters/100), textPos.x, textPos.y);
				g.setTextAlign(TextAlignment.LEFT);
				g.setTextBaseline(VPos.BASELINE);
				
			}
			
		}
		
	}
	
	
	

	private static void drawBallisticsRadialCircle(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData, ElementBallRangeIndicator block) {
		
		LayoutBallRangeIndicators layout = block.layout(dataSight, dataCalib, currentAmmoData, canvas.getWidth(), canvas.getHeight());
		
		// font
		Font bIndFont = getFont(layout.fontSize);
		double lineWidth = layout.rcLineWidth;
		
		// draw indicators
		for(int i=0; i<block.indicators.size(); i++) {
			Circlef circle = layout.rcCircles[i];
			Vector2d textPos = layout.rcTextPositions[i];
			BIndicator indicator = block.indicators.get(i);
			
			int meters = indicator.getDistance();
			boolean isMajor = indicator.isMajor();
			
			// draw circle
			g.setLineWidth(lineWidth);
			g.setStroke(dataSight.envSightColor);
			g.strokeOval(circle.cx-circle.radius, circle.cy-circle.radius, circle.radius*2, circle.radius*2);
			g.setLineWidth(1);
			
			// draw labels
			if(isMajor) {
				if(block.textAlign == TextAlign.LEFT)   { g.setTextAlign(TextAlignment.LEFT); }
				if(block.textAlign == TextAlign.CENTER) { g.setTextAlign(TextAlignment.CENTER); }
				if(block.textAlign == TextAlign.RIGHT)  { g.setTextAlign(TextAlignment.RIGHT); }
								
				g.setFill(dataSight.envSightColor);
				g.setTextBaseline(VPos.CENTER);
				g.setFont(bIndFont);
				
				g.fillText( ""+Math.abs(meters/100), textPos.x, textPos.y);

				g.setTextAlign(TextAlignment.LEFT);
				g.setTextBaseline(VPos.BASELINE);
				
			}
			
		}
		
	}
	
	
	
	
	
	private static void drawLineObject(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData, ElementCustomLine objLine) {
		LayoutLineObject layout = objLine.layout(dataSight, dataCalib, currentAmmoData, canvas.getWidth(), canvas.getHeight());
		g.setStroke(dataSight.envSightColor);
		g.setLineWidth(dataSight.gnrLineSize*dataSight.gnrFontScale);
		g.strokeLine(layout.start.x, layout.start.y, layout.end.x, layout.end.y);
		g.setLineWidth(1);
	}
	
	
	
	
	private static void drawTextObject(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData, ElementCustomText objText) {
	
		LayoutTextObject layout = objText.layout(dataSight, dataCalib, currentAmmoData, canvas.getWidth(), canvas.getHeight());
		Font font = getFont(layout.fontSize );
		
		// draw text
		g.setFill(dataSight.envSightColor);
		g.setTextAlign( (objText.align==TextAlign.LEFT ? TextAlignment.LEFT : (objText.align==TextAlign.CENTER ? TextAlignment.CENTER : TextAlignment.RIGHT)) );
		g.setTextBaseline(VPos.CENTER);
		g.setFont(font);
		g.fillText(objText.text, layout.pos.x, layout.pos.y);
		g.setTextAlign(TextAlignment.LEFT);
		g.setTextBaseline(VPos.BASELINE);
		
	}
	
	
	
	
	private static void drawCircleObject(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData, ElementCustomCircle objCircle) {
		
		LayoutCircleObject layout = objCircle.layout(dataSight, dataCalib, currentAmmoData, canvas.getWidth(), canvas.getHeight());
		
		g.setStroke(dataSight.envSightColor);
		g.setLineWidth(layout.lineWidth);
		
		if(layout.useLineSegments) {
			for (int i=0; i<layout.lines.length; i++) {
				Vector4d line = layout.lines[i];
				g.strokeLine(line.x, line.y, line.z, line.w);
			}
			
		} else {
			g.strokeOval(layout.circle.getMinXDouble(), layout.circle.getMinYDouble(), layout.circle.radius*2, layout.circle.radius*2);
		}
		
		g.setLineWidth(1);
		
	}
	
	
	
	
	private static void drawQuadObject(Canvas canvas, GraphicsContext g, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData, ElementCustomQuad objQuad) {
		
		LayoutQuadObject layout = objQuad.layout(dataSight, dataCalib, currentAmmoData, canvas.getWidth(), canvas.getHeight());
		
		g.setFill(dataSight.envSightColor);
		g.fillPolygon(
				new double[] {layout.p0.x, layout.p1.x, layout.p2.x, layout.p3.x},
				new double[] {layout.p0.y, layout.p1.y, layout.p2.y, layout.p3.y}, 4);

	}
	
	
}








