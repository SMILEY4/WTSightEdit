package com.ruegnerlukas.wtsights.ui.sighteditor.rendering;

import com.ruegnerlukas.simplemath.MathUtils;
import com.ruegnerlukas.simplemath.geometry.shapes.circle.Circlef;
import com.ruegnerlukas.simplemath.geometry.shapes.rectangle.Rectanglef;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.simplemath.vectors.vec4.Vector4d;
import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.sight.sightElements.Element;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementBallRangeIndicator;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCentralHorzLine;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCentralVertLine;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCustomCircleOutline;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCustomLine;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCustomQuadFilled;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCustomQuadOutline;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCustomText;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementHorzRangeIndicators;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementRangefinder;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementShellBlock;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.Movement;
import com.ruegnerlukas.wtsights.data.sight.sightElements.layouts.LayoutBallRangeIndicators;
import com.ruegnerlukas.wtsights.data.sight.sightElements.layouts.LayoutCentralHorzLine;
import com.ruegnerlukas.wtsights.data.sight.sightElements.layouts.LayoutCentralVertLine;
import com.ruegnerlukas.wtsights.data.sight.sightElements.layouts.LayoutCircleOutlineObject;
import com.ruegnerlukas.wtsights.data.sight.sightElements.layouts.LayoutHorzRangeIndicators;
import com.ruegnerlukas.wtsights.data.sight.sightElements.layouts.LayoutLineObject;
import com.ruegnerlukas.wtsights.data.sight.sightElements.layouts.LayoutQuadFilledObject;
import com.ruegnerlukas.wtsights.data.sight.sightElements.layouts.LayoutQuadOutlineObject;
import com.ruegnerlukas.wtsights.data.sight.sightElements.layouts.LayoutRangefinder;
import com.ruegnerlukas.wtsights.data.sight.sightElements.layouts.LayoutTextObject;
import com.ruegnerlukas.wtutils.Conversion;
import com.ruegnerlukas.wtutils.SightUtils.ScaleMode;
import com.ruegnerlukas.wtutils.canvas.WTCanvas;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;



public class OverlayRenderer {
	
	
	private static final Color COLOR_SELECTION_1 = Color.BLACK;
	private static final Color COLOR_SELECTION_2 = Color.WHITE;
	private static final Color COLOR_SELECTION_3 = new Color(0.2f, 0.2f, 0.2f, 1f);
	private static final Color COLOR_SELECTION_4 = new Color(0.8f, 0.8f, 0.8f, 1f);


	private static final Font font = new Font("Arial", 15);
	
	
	
	public static void draw(WTCanvas canvas, GraphicsContext g, DataPackage data) {
		
		if(data.dataSight.envDisplayGrid && !MathUtils.isNearlyEqual(0, data.dataSight.envGridWidth) && !MathUtils.isNearlyEqual(0, data.dataSight.envGridHeight)) {
			
			double pxWidth = Conversion.get().mil2pixel(data.dataSight.envGridWidth, canvas.getHeight(), data.dataSight.envZoomedIn);
			double pxHeight = Conversion.get().mil2pixel(data.dataSight.envGridHeight, canvas.getHeight(), data.dataSight.envZoomedIn);
			
			int nx = (int) (canvas.getWidth() / pxWidth);
			int ny = (int) (canvas.getHeight() / pxHeight);
			
			for(int x=-nx/2-1; x<=nx/2; x++) {
				for(int y=-ny/2-1; y<=ny/2; y++) {
					double cx = x * pxWidth + canvas.getWidth()/2;
					double cy = y * pxHeight + canvas.getHeight()/2;
					double cw = pxWidth;
					double ch = pxHeight;
					drawRect(data.dataSight.envColorGrid, data.dataSight.envColorGrid, canvas, g, cx, cy, cw, ch);
				}
			}
			
			
		}
		
		drawElementSelection(canvas, g, data);
	}
	
	
	
	public static void drawElementSelection(WTCanvas canvas, GraphicsContext g, DataPackage data) {

		Element selectedElement = data.dataSight.selectedElement;
		
		if(selectedElement == null) {
			return;
		}
		
		
		Conversion.get().initialize(
				canvas.getWidth(),
				canvas.getHeight(),
				data.dataBallistic.vehicle.fovOut*data.dataBallistic.zoomModOut,
				data.dataBallistic.vehicle.fovIn*data.dataBallistic.zoomModIn,
				data.dataSight.gnrThousandth);
		
		
		if(selectedElement.type == ElementType.CENTRAL_VERT_LINE) {
			ElementCentralVertLine element = (ElementCentralVertLine)selectedElement;
			LayoutCentralVertLine layout = element.layout(data, canvas.getWidth(), canvas.getHeight());
			drawRect(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.bounds.x, layout.bounds.y, layout.bounds.width, layout.bounds.height);
			
			
			
		} else if(selectedElement.type == ElementType.CENTRAL_HORZ_LINE) {
			ElementCentralHorzLine element = (ElementCentralHorzLine)selectedElement;
			LayoutCentralHorzLine layout = element.layout(data, canvas.getWidth(), canvas.getHeight());
			drawRect(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.bounds.x, layout.bounds.y, layout.bounds.width, layout.bounds.height);
			
			
			
		} else if(selectedElement.type == ElementType.RANGEFINDER) {
			ElementRangefinder element = (ElementRangefinder)selectedElement;
			LayoutRangefinder layout = element.layout(data, canvas.getWidth(), canvas.getHeight());
			drawRect(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.bounds.x, layout.bounds.y, layout.bounds.width, layout.bounds.height);
			drawCross(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.bounds.x, layout.bounds.y+layout.bounds.height, 6);
			
			
			
		} else if(selectedElement.type == ElementType.HORZ_RANGE_INDICATORS) {
			ElementHorzRangeIndicators element = (ElementHorzRangeIndicators)selectedElement;
			LayoutHorzRangeIndicators layout = element.layout(data, canvas.getWidth(), canvas.getHeight());

			for(int i=0; i<layout.bounds.length; i++) {
				Rectanglef bounds = layout.bounds[i];
				Vector2d textPos = layout.textPositions[i];
				boolean major = element.indicators.get(i).isMajor();
				drawRect(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, bounds.x, bounds.y, bounds.width, bounds.height);
				if(major) {
					drawCross(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, textPos.x, textPos.y, 6);
				}
				
			}
			
			
			
		} else if(selectedElement.type == ElementType.CUSTOM_LINE) {
			ElementCustomLine element = (ElementCustomLine)selectedElement;
			LayoutLineObject layout = element.layout(data, canvas.getWidth(), canvas.getHeight());
			drawLine(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.start.x, layout.start.y, layout.end.x, layout.end.y, layout.lineSize);
			
			Vector2d dir = Vector2d.createVectorAB(layout.start, layout.end).setLength(3);
			
			drawText(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.start.x-dir.x, layout.start.y-dir.y, "S");
			drawText(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.end.x+dir.x, layout.end.y+dir.y, "E");

			if(element.movement == Movement.MOVE_RADIAL) {
				drawCross(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.center.x, layout.center.y, 6);
				drawCross(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.radCenter.x, layout.radCenter.y, 6);
			}
			
			
		
		} else if(selectedElement.type == ElementType.CUSTOM_CIRCLE_OUTLINE) {
			ElementCustomCircleOutline element = (ElementCustomCircleOutline)selectedElement;
			LayoutCircleOutlineObject layout = element.layout(data, canvas.getWidth(), canvas.getHeight());
			if(layout.useLineSegments) {
				Vector2d v0 = new Vector2d(0, 1).rotateDeg(-element.segment.x).setLength(layout.circle.radius+layout.circle.radius*0.1);
				Vector2d v1 = new Vector2d(0, 1).rotateDeg(-element.segment.y).setLength(layout.circle.radius+layout.circle.radius*0.1);
				drawThinLine(COLOR_SELECTION_3, COLOR_SELECTION_4, canvas, g, layout.circle.cx, layout.circle.cy, layout.circle.cx+v0.x, layout.circle.cy+v0.y);
				drawThinLine(COLOR_SELECTION_3, COLOR_SELECTION_4, canvas, g, layout.circle.cx, layout.circle.cy, layout.circle.cx+v1.x, layout.circle.cy+v1.y);
			}
			drawCircle(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.circle.cx, layout.circle.cy, layout.circle.radius, layout.lineWidth);
			drawCross(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.circle.cx, layout.circle.cy, 6);
			
			if(element.movement == Movement.MOVE_RADIAL) {
				drawCross(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.center.x, layout.center.y, 6);
				drawCross(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.radCenter.x, layout.radCenter.y, 6);
			}
			
			
			
		} else if(selectedElement.type == ElementType.CUSTOM_TEXT) {
			ElementCustomText element = (ElementCustomText)selectedElement;
			LayoutTextObject layout = element.layout(data, canvas.getWidth(), canvas.getHeight());
			drawCross(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.pos.x, layout.pos.x, 6);
			
			if(element.movement == Movement.MOVE_RADIAL) {
				drawCross(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.center.x, layout.center.y, 6);
				drawCross(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.radCenter.x, layout.radCenter.y, 6);
			}
			
			
		} else if(selectedElement.type == ElementType.CUSTOM_QUAD_FILLED) {
			ElementCustomQuadFilled element = (ElementCustomQuadFilled)selectedElement;
			LayoutQuadFilledObject layout = element.layout(data, canvas.getWidth(), canvas.getHeight());
			drawQuad(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.p0.x, layout.p0.y, layout.p1.x, layout.p1.y, layout.p2.x, layout.p2.y, layout.p3.x, layout.p3.y);
			drawCross(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.p0.x, layout.p0.y, 4);
			drawCross(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.p1.x, layout.p1.y, 4);
			drawCross(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.p2.x, layout.p2.y, 4);
			drawCross(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.p3.x, layout.p3.y, 4);
			drawText(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.p0.x+5, layout.p0.y+5, "1");
			drawText(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.p1.x+5, layout.p1.y+5, "2");
			drawText(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.p2.x+5, layout.p2.y+5, "3");
			drawText(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.p3.x+5, layout.p3.y+5, "4");

			if(element.movement == Movement.MOVE_RADIAL) {
				drawCross(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.center.x, layout.center.y, 6);
				drawCross(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.radCenter.x, layout.radCenter.y, 6);
			}
			
			
		} else if(selectedElement.type == ElementType.CUSTOM_QUAD_OUTLINE) {
			ElementCustomQuadOutline element = (ElementCustomQuadOutline)selectedElement;
			element.layout(data, canvas.getWidth(), canvas.getHeight());
			for(int i=0; i<element.getSubElements().size(); i++) {
				ElementCustomLine lineObject = (ElementCustomLine)element.getSubElements().get(i);
				LayoutLineObject layout = lineObject.layout(data, canvas.getWidth(), canvas.getHeight());
				if(layout != null) {
					drawLine(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.start.x, layout.start.y, layout.end.x, layout.end.y, layout.lineSize);
					drawCross(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.start.x, layout.start.y, 4);
					drawText(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.start.x+5, layout.start.y+5, ""+(i+1));
				}
			}
			
			if(element.movement == Movement.MOVE_RADIAL) {
				LayoutQuadOutlineObject mainLayout = (LayoutQuadOutlineObject)element.getMainLayout();
				drawCross(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, mainLayout.center.x, mainLayout.center.y, 6);
				drawCross(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, mainLayout.radCenter.x, mainLayout.radCenter.y, 6);
			}
			
			
			
		} else if(selectedElement.type == ElementType.SHELL_BALLISTICS_BLOCK || selectedElement.type == ElementType.BALLISTIC_RANGE_INDICATORS) {
			ElementBallRangeIndicator element = (ElementBallRangeIndicator)selectedElement;
			
			DataPackage dataBlock = new DataPackage();
			dataBlock.dataSight = data.dataSight;
			dataBlock.dataBallistic = data.dataBallistic;
			dataBlock.elementBallistic = selectedElement.type == ElementType.BALLISTIC_RANGE_INDICATORS ? data.elementBallistic : ((ElementShellBlock)selectedElement).elementBallistic;
			LayoutBallRangeIndicators layout = element.layout(dataBlock, canvas.getWidth(), canvas.getHeight());
			
			if(element.drawCorrLabel && data.dataSight.envRangeCorrection > 0) {
				drawCross(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.corrLabel.x, layout.corrLabel.y, 6);
			}
			
			if(element.scaleMode == ScaleMode.VERTICAL) {
				for(int i=0; i<layout.vMainBounds.length; i++) {
					boolean major = element.indicators.get(i).isMajor();
					Rectanglef mainBounds = layout.vMainBounds[i];
					Rectanglef centerBounds = layout.vCenterBounds[i];
					Vector2d textPos = layout.vTextPositions[i];
					drawRect(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, mainBounds.x, mainBounds.y, mainBounds.width, mainBounds.height);
					if(element.drawAddLines) {
						drawRect(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, centerBounds.x, centerBounds.y, centerBounds.width, centerBounds.height);
					}
					if(major) {
						drawCross(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, textPos.x, textPos.y, 6);
					}
				}
				
			} else if(element.scaleMode == ScaleMode.RADIAL && !element.circleMode) {
				
				drawThinCircle(COLOR_SELECTION_3, COLOR_SELECTION_4, canvas, g, layout.rlCenter.x, layout.rlCenter.y, layout.rlRadius);
				drawThinCircle(COLOR_SELECTION_3, COLOR_SELECTION_4, canvas, g, layout.rlCenter.x, layout.rlCenter.y, layout.rlRadiusOutside);
				
				for(int i=0; i<layout.rlLines.length; i++) {
					boolean major = element.indicators.get(i).isMajor();
					Vector4d line = layout.rlLines[i];
					Vector2d textPos = layout.rlTextPositions[i];
					drawLine(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, line.x, line.y, line.z, line.w, layout.rlLineSize);
					drawCross(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.rlCenter.x, layout.rlCenter.y, 6);
					if(major) {
						drawCross(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, textPos.x, textPos.y, 6);
					}
				}
				
			} else if(element.scaleMode == ScaleMode.RADIAL && element.circleMode) {
				
				drawThinCircle(COLOR_SELECTION_3, COLOR_SELECTION_4, canvas, g, layout.rlCenter.x, layout.rlCenter.y, layout.rcRadius);

				for(int i=0; i<layout.rcCircles.length; i++) {
					boolean major = element.indicators.get(i).isMajor();
					Circlef circle = layout.rcCircles[i];
					Vector2d textPos = layout.rcTextPositions[i];
					drawCircle(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, circle.cx, circle.cy, circle.radius, layout.rcLineWidth);
					drawCross(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, layout.rcCenter.x, layout.rcCenter.y, 6);
					if(major) {
						drawCross(COLOR_SELECTION_1, COLOR_SELECTION_2, canvas, g, textPos.x, textPos.y, 6);
					}
				}
				
			}
			
		}

	
	}
	
	
	
	
	private static void drawText(Color color1, Color color2, WTCanvas canvas, GraphicsContext g, double x, double y, String str) {
	
		Point2D p = canvas.transformToOverlay(x, y);
		double px = ((int)p.getX())+0.5;
		double py = ((int)p.getY())+0.5;
		
		g.setFont(font);
		
		g.setLineDashes(null);

		g.setLineWidth(3);
		g.setStroke(color1);
		g.strokeText(str, px, py);

		g.setLineWidth(1);
		g.setStroke(color2);
		g.strokeText(str, px, py);

	}
	
	
	
	private static void drawThinCircle(Color color1, Color color2, WTCanvas canvas, GraphicsContext g, double x, double y, double radius) {
		
		Point2D p0 = canvas.transformToOverlay(x-radius, y-radius);
		Point2D p1 = canvas.transformToOverlay(x+radius, y+radius);
		
		double px0 = ((int)p0.getX())+0.5;
		double py0 = ((int)p0.getY())+0.5;
		double size = p1.getX()-p0.getX();
		
		
		g.setLineWidth(1);
		
		g.setStroke(color1);
		g.setLineDashes(null);
		g.strokeOval(px0, py0, size, size);

		g.setStroke(color2);
		g.setLineDashes(5, 8);
		g.setLineDashOffset(getDashOffset());
		g.strokeOval(px0, py0, size, size);
		
	}

	
	
	private static void drawCircle(Color color1, Color color2, WTCanvas canvas, GraphicsContext g, double x, double y, double radius, double lineSize) {
		
		double hls = lineSize/2;
		
		Point2D p0in = canvas.transformToOverlay(x-radius+hls, y-radius+hls);
		Point2D p1in = canvas.transformToOverlay(x+radius-hls, y+radius-hls);

		Point2D p0out = canvas.transformToOverlay(x-radius-hls, y-radius-hls);
		Point2D p1out = canvas.transformToOverlay(x+radius+hls, y+radius+hls);
		
		double px0in = ((int)p0in.getX())+0.5;
		double py0in = ((int)p0in.getY())+0.5;
		double sizeIn = p1in.getX()-p0in.getX();
		
		double px0out = ((int)p0out.getX())+0.5;
		double py0out = ((int)p0out.getY())+0.5;
		double sizeOut = p1out.getX()-p0out.getX();
		
		g.setLineWidth(1);
		
		g.setStroke(color1);
		g.setLineDashes(null);
		g.strokeOval(px0in, py0in, sizeIn, sizeIn);
		g.strokeOval(px0out, py0out, sizeOut, sizeOut);

		g.setStroke(color2);
		g.setLineDashes(5, 8);
		g.setLineDashOffset(getDashOffset());
		g.strokeOval(px0in, py0in, sizeIn, sizeIn);
		g.strokeOval(px0out, py0out, sizeOut, sizeOut);

	}
	
	
	
	
	static Vector2d dir = new Vector2d();
	static Vector2d cap = new Vector2d();
	static Vector2d perp = new Vector2d();
	static Vector2d start = new Vector2d();
	static Vector2d end = new Vector2d();
	static Vector2d a = new Vector2d();
	static Vector2d b = new Vector2d();
	static Vector2d c = new Vector2d();
	static Vector2d d = new Vector2d();
	

	private static void drawLine(Color color1, Color color2, WTCanvas canvas, GraphicsContext g, double x0, double y0, double x1, double y1, double lineSize) {
		

		
		if( (MathUtils.isNearlyEqual(x0, x1) && MathUtils.isNearlyEqual(y0, y1)) ) {
			return;
		}
		
		Point2D p0 = canvas.transformToOverlay(x0, y0);
		Point2D p1 = canvas.transformToOverlay(x1, y1);
		
		double px0 = ((int)p0.getX())+0.5;
		double py0 = ((int)p0.getY())+0.5;
		double px1 = ((int)p1.getX())+0.5;
		double py1 = ((int)p1.getY())+0.5;
		

		
		Vector2d.setVectorAB(px0, py0, px1, py1, dir);
		cap.set(dir).setLength(lineSize*canvas.canvas.getScaleX()/2);
		perp.set(dir).normalize().rotateDeg(90).setLength(lineSize*canvas.canvas.getScaleX()/2);
		
		start.set(px0, py0);
		end.set(px1, py1);

		a.set(start).add(perp).sub(cap);
		b.set(start).sub(perp).sub(cap);
		c.set(end).add(perp).add(cap);
		d.set(end).sub(perp).add(cap);

		g.setLineWidth(1);
		g.setStroke(color1);
		g.setLineDashes(null);
		g.strokeLine(a.x, a.y, b.x, b.y);
		g.strokeLine(c.x, c.y, d.x, d.y);
		g.strokeLine(a.x, a.y, c.x, c.y);
		g.strokeLine(b.x, b.y, d.x, d.y);

		g.setStroke(color2);
		g.setLineDashes(5, 8);
		double offset = getDashOffset();
		g.setLineDashOffset(offset);
		g.strokeLine(a.x, a.y, b.x, b.y);
		g.strokeLine(a.x, a.y, c.x, c.y);
		g.setLineDashOffset(offset+5);
		g.strokeLine(c.x, c.y, d.x, d.y);
		g.strokeLine(b.x, b.y, d.x, d.y);
		
		
	}

	
	
	
	private static void drawThinLine(Color color1, Color color2, WTCanvas canvas, GraphicsContext g, double x0, double y0, double x1, double y1) {
		
		if( !(MathUtils.isNearlyEqual(x0, x1) && MathUtils.isNearlyEqual(y0, y1)) ) {
			return;
		}
		
		Point2D p0 = canvas.transformToOverlay(x0, y0);
		Point2D p1 = canvas.transformToOverlay(x1, y1);

		double px0 = ((int)p0.getX())+0.5;
		double py0 = ((int)p0.getY())+0.5;
		double px1 = ((int)p1.getX())+0.5;
		double py1 = ((int)p1.getY())+0.5;
		
		g.setLineWidth(1);
		
		g.setStroke(color1);
		g.setLineDashes(null);
		g.strokeLine(px0, py0, px1, py1);
		
		g.setStroke(color2);
		g.setLineDashes(5, 8);
		g.setLineDashOffset(getDashOffset());
		g.strokeLine(px0, py0, px1, py1);
		
	}
	
	
	
	
	static Rectanglef view = new Rectanglef();
	
	private static void drawRect(Color color1, Color color2, WTCanvas canvas, GraphicsContext g, double x, double y, double width, double height) {
		
		view.set(0, 0, canvas.getWidth(), canvas.getHeight());
		
		Point2D p0 = canvas.transformToOverlay(x, y);
		Point2D p1 = canvas.transformToOverlay(x+width, y+height);

		if(!view.containsPoint(p0.getX(), p0.getY()) && !view.containsPoint(p1.getX(), p1.getY()) ) {
			return;
		}
		
		double rx = p0.getX();
		double ry = p0.getY();
		double rw = p1.getX()-p0.getX();
		double rh = p1.getY()-p0.getY();

		double px = ((int)rx)+0.5;
		double py = ((int)ry)+0.5;

		g.setLineWidth(1);
		
		g.setStroke(color1);
		g.setLineDashes(null);
		g.strokeRect(px, py, ((int)rw), ((int)rh));
		
		g.setStroke(color2);
		g.setLineDashes(5, 8);
		g.setLineDashOffset(getDashOffset());
		g.strokeRect(px, py, ((int)rw), ((int)rh));
		
	}
	
	
	
	
	private static void drawQuad(Color color1, Color color2, WTCanvas canvas, GraphicsContext g, double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3) {

		Point2D p0 = canvas.transformToOverlay(x0, y0);
		Point2D p1 = canvas.transformToOverlay(x1, y1);
		Point2D p2 = canvas.transformToOverlay(x2, y2);
		Point2D p3 = canvas.transformToOverlay(x3, y3);

		double px0 = ((int)p0.getX())+0.5;
		double py0 = ((int)p0.getY())+0.5;
		double px1 = ((int)p1.getX())+0.5;
		double py1 = ((int)p1.getY())+0.5;
		double px2 = ((int)p2.getX())+0.5;
		double py2 = ((int)p2.getY())+0.5;
		double px3 = ((int)p3.getX())+0.5;
		double py3 = ((int)p3.getY())+0.5;
		
		double[] xPoints = new double[] {px0, px1, px2, px3};
		double[] yPoints = new double[] {py0, py1, py2, py3};

		g.setLineWidth(1);
		
		g.setStroke(color1);
		g.setLineDashes(null);
		g.strokePolygon(xPoints, yPoints, 4);
		
		g.setStroke(color2);
		g.setLineDashes(5, 8);
		g.setLineDashOffset(getDashOffset());
		g.strokePolygon(xPoints, yPoints, 4);
		
	}
	
	
	
	
	private static void drawCross(Color color1, Color color2, WTCanvas canvas, GraphicsContext g, double x, double y, double radius) {
		
		radius = Math.max(radius, 0.01);
		
		Point2D p = canvas.transformToOverlay(x, y);
		double cx = p.getX();
		double cy = p.getY();
		
		g.setLineWidth(2);
		
		double px0 = ((int)cx-radius)+0.5;
		double py0 = ((int)cy-radius)+0.5;
		double px1 = ((int)cx+radius)+0.5;
		double py1 = ((int)cy+radius)+0.5;
		
		g.setStroke(color1);
		g.setLineDashes(null);
		
		g.strokeLine(px0, py0, px1, py1);
		g.strokeLine(px0, py1, px1, py0);

		g.setStroke(color2);
		g.setLineDashes(2, 5);
		g.setLineDashOffset(getDashOffset());
		
		g.strokeLine(px0, py0, px1, py1);
		g.strokeLine(px0, py1, px1, py0);
		
	}

	
	
	
	private static double getDashOffset() {
		return System.currentTimeMillis() / 100 % 10000;
	}
	
	
}








