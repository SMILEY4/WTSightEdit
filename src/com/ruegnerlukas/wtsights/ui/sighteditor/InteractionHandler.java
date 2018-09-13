package com.ruegnerlukas.wtsights.ui.sighteditor;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationAmmoData;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationData;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.data.sight.elements.Element;
import com.ruegnerlukas.wtutils.canvas.WTCanvas;

public class InteractionHandler {

	
	
	
	public static void mouseDrag(Vector2d cursorPos, WTCanvas canvas, SightData dataSight, CalibrationData dataCalib, CalibrationAmmoData currentAmmoData) {
		
		Element selectedElement = dataSight.selectedElement;
		if(selectedElement == null) {
			return;
		}
		
		
//
//		if(selectedElement.type == ElementType.CENTRAL_VERT_LINE) {
//			ElementCentralVertLine element = (ElementCentralVertLine)selectedElement;
//			LayoutCentralVertLine layout = element.layout(dataSight, dataCalib, currentAmmoData, canvas.getWidth(), canvas.getHeight());
//			
//			
//			
//		} else if(selectedElement.type == ElementType.CENTRAL_HORZ_LINE) {
//			ElementCentralHorzLine element = (ElementCentralHorzLine)selectedElement;
//			LayoutCentralHorzLine layout = element.layout(dataSight, dataCalib, currentAmmoData, canvas.getWidth(), canvas.getHeight());
//			
//			
//			
//		} else if(selectedElement.type == ElementType.RANGEFINDER) {
//			ElementRangefinder element = (ElementRangefinder)selectedElement;
//			LayoutRangefinder layout = element.layout(dataSight, dataCalib, currentAmmoData, canvas.getWidth(), canvas.getHeight());
//			
//			
//			
//		} else if(selectedElement.type == ElementType.HORZ_RANGE_INDICATORS) {
//			ElementHorzRangeIndicators element = (ElementHorzRangeIndicators)selectedElement;
//			LayoutHorzRangeIndicators layout = element.layout(dataSight, dataCalib, currentAmmoData, canvas.getWidth(), canvas.getHeight());
//
//			for(int i=0; i<layout.bounds.length; i++) {
//				Rectanglef bounds = layout.bounds[i];
//				Vector2d textPos = layout.textPositions[i];
//				boolean major = element.indicators.get(i).isMajor();
//				if(major) {
//				}
//			}
//			
//			
//			
//		} else if(selectedElement.type == ElementType.CUSTOM_LINE) {
//			ElementCustomLine element = (ElementCustomLine)selectedElement;
//			LayoutLineObject layout = element.layout(dataSight, dataCalib, currentAmmoData, canvas.getWidth(), canvas.getHeight());
//		
//			
//		
//		} else if(selectedElement.type == ElementType.CUSTOM_CIRCLE) {
//			ElementCustomCircle element = (ElementCustomCircle)selectedElement;
//			LayoutCircleObject layout = element.layout(dataSight, dataCalib, currentAmmoData, canvas.getWidth(), canvas.getHeight());
//			
//			if(layout.useLineSegments) {
//				Vector2d v0 = new Vector2d(0, 1).rotateDeg(-element.segment.x).setLength(layout.circle.radius+layout.circle.radius*0.1);
//				Vector2d v1 = new Vector2d(0, 1).rotateDeg(-element.segment.y).setLength(layout.circle.radius+layout.circle.radius*0.1);
//			}
//			
//			
//			
//		} else if(selectedElement.type == ElementType.CUSTOM_TEXT) {
//			ElementCustomText element = (ElementCustomText)selectedElement;
//			LayoutTextObject layout = element.layout(dataSight, dataCalib, currentAmmoData, canvas.getWidth(), canvas.getHeight());
//			
//			
//			
//		} else if(selectedElement.type == ElementType.CUSTOM_QUAD) {
//			ElementCustomQuad element = (ElementCustomQuad)selectedElement;
//			LayoutQuadObject layout = element.layout(dataSight, dataCalib, currentAmmoData, canvas.getWidth(), canvas.getHeight());
//			
//			
//			
//		} else if(selectedElement.type == ElementType.SHELL_BALLISTICS_BLOCK || selectedElement.type == ElementType.BALLISTIC_RANGE_INDICATORS) {
//			ElementBallRangeIndicator element = (ElementBallRangeIndicator)selectedElement;
//			
//			CalibrationAmmoData ammoData = selectedElement.type == ElementType.BALLISTIC_RANGE_INDICATORS ? currentAmmoData : ((ElementShellBlock)selectedElement).dataAmmo;
//			LayoutBallRangeIndicators layout = element.layout(dataSight, dataCalib, ammoData, canvas.getWidth(), canvas.getHeight());
//			
//			if(element.drawCorrLabel && dataSight.envRangeCorrection > 0) {
//			}
//			
//			if(element.scaleMode == ScaleMode.VERTICAL) {
//				for(int i=0; i<layout.vMainBounds.length; i++) {
//					boolean major = element.indicators.get(i).isMajor();
//					Rectanglef mainBounds = layout.vMainBounds[i];
//					Rectanglef centerBounds = layout.vCenterBounds[i];
//					Vector2d textPos = layout.vTextPositions[i];
//					if(major) {
//					}
//				}
//				
//			} else if(element.scaleMode == ScaleMode.RADIAL && !element.circleMode) {
//				for(int i=0; i<layout.rlLines.length; i++) {
//					boolean major = element.indicators.get(i).isMajor();
//					Vector4d line = layout.rlLines[i];
//					Vector2d textPos = layout.rlTextPositions[i];
//					if(major) {
//					}
//				}
//				
//			} else if(element.scaleMode == ScaleMode.RADIAL && element.circleMode) {
//				for(int i=0; i<layout.rlLines.length; i++) {
//					boolean major = element.indicators.get(i).isMajor();
//					Circlef circle = layout.rcCircles[i];
//					Vector2d textPos = layout.rcTextPositions[i];
//					if(major) {
//					}
//				}
//				
//			}
//			
//		}

		
		
	}

	
	
	
}
