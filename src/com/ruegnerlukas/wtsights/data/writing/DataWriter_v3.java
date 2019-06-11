package com.ruegnerlukas.wtsights.data.writing;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.simplemath.vectors.vec3.Vector3i;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.FileVersion;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticData;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticElement;
import com.ruegnerlukas.wtsights.data.ballisticdata.Marker;
import com.ruegnerlukas.wtsights.data.sight.BIndicator;
import com.ruegnerlukas.wtsights.data.sight.HIndicator;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.data.sight.sightElements.BaseElement;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.*;
import com.ruegnerlukas.wtsights.data.vehicle.Ammo;
import com.ruegnerlukas.wtsights.ui.sighteditor.StepSizes;
import com.ruegnerlukas.wtutils.Config;
import com.ruegnerlukas.wtutils.SightUtils.ScaleMode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map.Entry;

public class DataWriter_v3 implements IDataWriter {

	
	@Override
	public boolean saveExternalBallisticFile(BallisticData data, File outputFile) throws Exception {

		if(data == null) {
			Logger.get().warn("Could not find ballistic data: " + data);
			return false;
		}
		if(outputFile == null) {
			Logger.get().warn("Could not find file: " + outputFile);
			return false;
		}
		
		
		Logger.get().info("Writing ballistic-data to " + outputFile);
		
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		Document doc = docBuilder.newDocument();
		
		
		Element rootElement = doc.createElement("ballisticdata");
		rootElement.setAttribute("fileversion", FileVersion.V_2.fileversion);
		doc.appendChild(rootElement);
		
		Element elementVehicle = doc.createElement(data.vehicle.name);
		rootElement.appendChild(elementVehicle);
		elementVehicle.setAttribute("zoomModOut", Double.toString(data.zoomModOut));
		elementVehicle.setAttribute("zoomModIn", Double.toString(data.zoomModIn));

		Element elementElements = doc.createElement("elements");
		elementVehicle.appendChild(elementElements);
		
		for(BallisticElement ballElement : data.elements) {
			
			Element elementBall = doc.createElement("element_" + data.elements.indexOf(ballElement));
			elementElements.appendChild(elementBall);
			
			Element elementAmmo = doc.createElement("ammunition");
			elementBall.appendChild(elementAmmo);
			String strAmmo = "";
			for(int i=0; i<ballElement.ammunition.size(); i++) {
				if(i == ballElement.ammunition.size()-1) {
					strAmmo += ballElement.ammunition.get(i).name;
				} else {
					strAmmo += ballElement.ammunition.get(i).name + ";";
				}
			}
			elementAmmo.setTextContent(strAmmo);
			
			if(ballElement.markerData != null) {
				
				Element elementMarkers = doc.createElement("markers");
				elementBall.appendChild(elementMarkers);
				boolean zoomedIn = false;
				if(data.zoomedIn.containsKey(ballElement)) {
					zoomedIn = data.zoomedIn.get(ballElement);
				}
				elementMarkers.setAttribute("zoomedIn", Boolean.toString(zoomedIn));
				elementMarkers.setAttribute("ycenter", ""+ballElement.markerData.yPosCenter);
				
				
				for(int i=0; i<ballElement.markerData.markers.size(); i++) {
					Marker marker = ballElement.markerData.markers.get(i);
					Element elementMarker = doc.createElement("marker_" + i);
					elementMarker.setAttribute("ypos", ""+marker.yPos);
					elementMarker.setAttribute("range", ""+marker.distMeters);
					elementMarkers.appendChild(elementMarker);
				}
			}
			
		}
		
		
		Element elementImages = doc.createElement("images");
		elementVehicle.appendChild(elementImages);
		
		for(Entry<BallisticElement,BufferedImage> entry : data.imagesBallistic.entrySet()) {
			Element elementImg = doc.createElement("image_element_" + data.elements.indexOf(entry.getKey()));
			String encodedImage = encodeImage(entry.getValue(), "jpg");
			elementImg.setAttribute("encodedData", encodedImage);
			elementImages.appendChild(elementImg);
		}
		if(data.imagesZoom.containsKey(true)) {
			Element elementImg = doc.createElement("image_element_zoomModIn");
			String encodedImage = encodeImage(data.imagesZoom.get(true), "jpg");
			elementImg.setAttribute("encodedData", encodedImage);
			elementImages.appendChild(elementImg);
		}
		if(data.imagesZoom.containsKey(false)) {
			Element elementImg = doc.createElement("image_element_zoomModOut");
			String encodedImage = encodeImage(data.imagesZoom.get(true), "jpg");
			elementImg.setAttribute("encodedData", encodedImage);
			elementImages.appendChild(elementImg);
		}
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(outputFile);
		
		transformer.transform(source, result);
		
		Logger.get().info("External ballistic data saved");
		return true;
	}

	
	
	
	private static String encodeImage(BufferedImage img, String format) throws IOException {
	
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(img, format, baos);
		baos.flush();
		
		String strImage = Base64.getEncoder().encodeToString(baos.toByteArray());
		baos.close();
		
		return strImage;
	}
	
	
	
	
	@Override
	public boolean saveSight(SightData data, BallisticData dataBall, File outputFile) {
		
		if(data == null) {
			Logger.get().warn("Could not find sight-data: " + data);
			return false;
		}
		if(dataBall == null) {
			Logger.get().warn("Could not find ballistic-data: " + dataBall);
			return false;
		}
		if(outputFile == null) {
			Logger.get().warn("Could not find file: " + outputFile);
			return false;
		}
		
		List<String> lines = new ArrayList<String>();
		
		// metadata
		lines.add("// created with WTSightEdit " + Config.build_version);
		lines.add("// fileversion = " + FileVersion.V_3.fileversion);
		lines.add("// vehicle = " + (dataBall == null ? "unknown" : dataBall.vehicle.name));
		lines.add("");
		
		// general
		lines.add("// general");
		lines.add("thousandth:t = \"" + data.gnrThousandth.tag + "\"");
		lines.add("fontSizeMult:r = " + asString(data.gnrFontScale, StepSizes.DECPLACES_SCALE));
		lines.add("lineSizeMult:r = " + asString(data.gnrLineSize, StepSizes.DECPLACES_SCALE));
		lines.add("applyCorrectionToGun:b = " + (data.gnrApplyCorrectionToGun ? "yes" : "no") );
		lines.add("drawCentralLineVert:b = " + (((ElementCentralVertLine)data.getElements(ElementType.CENTRAL_VERT_LINE).get(0)).drawCentralVertLine ? "yes" : "no") );
		lines.add("drawCentralLineHorz:b = " + (((ElementCentralHorzLine)data.getElements(ElementType.CENTRAL_HORZ_LINE).get(0)).drawCentralHorzLine ? "yes" : "no") );
		lines.add("");
		
		// rangefinder
		ElementRangefinder rangefinder = (ElementRangefinder)data.getElements(ElementType.RANGEFINDER).get(0);
		lines.add("// rangefinder");
		lines.add("rangefinderHorizontalOffset:r = " + asString(rangefinder.position.x, StepSizes.DECPLACES_PIXEL));
		lines.add("rangefinderVerticalOffset:r = " + asString(rangefinder.position.y, rangefinder.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE));
		lines.add("rangefinderProgressBarColor1:c = " + (int)(rangefinder.color1.getRed()*255) + "," + (int)(rangefinder.color1.getGreen()*255) + "," + (int)(rangefinder.color1.getBlue()*255) + "," + (int)(rangefinder.color1.getOpacity()*255));
		lines.add("rangefinderProgressBarColor2:c = " + (int)(rangefinder.color2.getRed()*255) + "," + (int)(rangefinder.color2.getGreen()*255) + "," + (int)(rangefinder.color2.getBlue()*255) + "," + (int)(rangefinder.color2.getOpacity()*255));
		lines.add("rangefinderTextScale:r = " + asString(rangefinder.textScale, StepSizes.DECPLACES_SCALE));
		lines.add("rangefinderUseThousandth:b = " + (rangefinder.useThousandth ? "yes" : "no") );
		lines.add("");
		
		// horz range indicators
		ElementHorzRangeIndicators horzRange = (ElementHorzRangeIndicators)data.getElements(ElementType.HORZ_RANGE_INDICATORS).get(0);
		lines.add("// horizontal range indicators");
		lines.add("crosshairHorVertSize:p2 = " + asString(horzRange.sizeMajor, StepSizes.DECPLACES_SCREENSPACE) + "," + asString(horzRange.sizeMinor, StepSizes.DECPLACES_SCREENSPACE));
		lines.add("crosshair_hor_ranges {");
		for(int i=0; i<horzRange.indicators.size(); i++) {
			HIndicator indicator = horzRange.indicators.get(i);
			int mil = indicator.getMil();
			int label = indicator.isMajor() ? Math.abs(indicator.getMil()) : 0;
			lines.add("  range:p2 = " + mil + "," + label);	
		}
		lines.add("}");
		lines.add("");
		
		
		// ballistic range indicators
		if(!data.getElements(ElementType.BALLISTIC_RANGE_INDICATORS).isEmpty()) {
			ElementBallRangeIndicator ballRange = (ElementBallRangeIndicator)data.getElements(ElementType.BALLISTIC_RANGE_INDICATORS).get(0);
			lines.add("// ballistic range indicators");
			lines.add("drawUpward:b = " + (ballRange.drawUpward ? "yes" : "no") );
			lines.add("distancePos:p2 = " + asString(ballRange.position.x, StepSizes.DECPLACES_SCREENSPACE) + "," + asString(ballRange.position.y, StepSizes.DECPLACES_SCREENSPACE));
			if(ballRange.move) {
				lines.add("move:b = " + (ballRange.move ? "yes" : "no") );
			}
			if(ballRange.scaleMode == ScaleMode.RADIAL) {
				lines.add("radial:b = " + (ballRange.scaleMode == ScaleMode.RADIAL ? "yes" : "no"));
			}
			if(ballRange.circleMode) {
				lines.add("circleMode:b = " + (ballRange.circleMode ? "yes" : "no"));
			}
			lines.add("crosshairDistHorSizeMain:p2 = " + asString(ballRange.size.x, StepSizes.DECPLACES_SCREENSPACE) + "," + asString(ballRange.size.y, StepSizes.DECPLACES_SCREENSPACE));
			lines.add("textPos:p2 = " + asString(ballRange.textPos.x, StepSizes.DECPLACES_SCREENSPACE) + "," + asString(ballRange.textPos.y, StepSizes.DECPLACES_SCREENSPACE));
			lines.add("textAlign:i = " + ballRange.textAlign.id);
			lines.add("textShift:r = " + asString(ballRange.textShift, 1));
			lines.add("drawAdditionalLines:b = " + (ballRange.drawAddLines ? "yes" : "no") );
			lines.add("crosshairDistHorSizeAdditional:p2 = " + asString(ballRange.sizeAddLine.x, StepSizes.DECPLACES_SCREENSPACE) + "," + asString(ballRange.sizeAddLine.y, StepSizes.DECPLACES_SCREENSPACE));
			if(ballRange.scaleMode == ScaleMode.RADIAL) {
				lines.add("radialStretch:r = " + asString(ballRange.radialStretch, 2));
				lines.add("radialAngle:r = " + asString(ballRange.radialAngle, StepSizes.DECPLACES_ANGLE));
				lines.add("radialRadius:p2 = " + asString(ballRange.radialRadius, ballRange.radiusUseMils ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + "," + (ballRange.radiusUseMils ? "1" : "0") );
			}
			lines.add("drawDistanceCorrection:b = " + (ballRange.drawCorrLabel ? "yes" : "no") );
			if(ballRange.drawCorrLabel) {
				lines.add("distanceCorrectionPos:p2 = " + asString(ballRange.posCorrLabel.x, StepSizes.DECPLACES_SCREENSPACE) + "," + asString(ballRange.posCorrLabel.y, StepSizes.DECPLACES_SCREENSPACE));
			}
			lines.add("");
			
			lines.add("crosshair_distances {");
			for(int i=0; i<ballRange.indicators.size(); i++) {
				BIndicator indicator = ballRange.indicators.get(i);
				int dist = indicator.getDistance();
				boolean major = indicator.isMajor();
				int label = major ? Math.abs(dist/100) : 0;
				String extend = asString(indicator.getExtend(), StepSizes.DECPLACES_SCREENSPACE);
				Vector2d textOff = new Vector2d(indicator.getTextX(), indicator.getTextY());
				lines.add("    distance { distance:p3="+dist + "," + label + "," + extend + "; textPos:p2=" + asString(textOff.x, StepSizes.DECPLACES_SCREENSPACE) + "," + asString(textOff.y, StepSizes.DECPLACES_SCREENSPACE) + "; }");
			}
			lines.add("}");
			lines.add("");
		}
		
		
		
		//  shell ballistics blocks
		if(!data.getElements(ElementType.SHELL_BALLISTICS_BLOCK).isEmpty()) {
			lines.add("// shell ballistics blocks");
			lines.add("ballistics {");
			
			for(BaseElement element : data.getElements(ElementType.SHELL_BALLISTICS_BLOCK)) {

				ElementShellBlock shellBlock = (ElementShellBlock)element;
				
				lines.add("  //-- eid=\"" + getElementIdentifier(shellBlock, null) + "\"");
				lines.add("  bullet {");
				
				for(Ammo ammo : shellBlock.elementBallistic.ammunition) {
					lines.add("    bulletType:t = \"" + ammo.type + "\"");
					lines.add("    speed:r = " + ammo.speed);
				}
				lines.add("    triggerGroup:t = \"" + shellBlock.triggerGroup + "\"");
				lines.add("    thousandth:b = " + "no");
				lines.add("    drawUpward:b = " + (shellBlock.drawUpward ? "yes" : "no") );
				lines.add("    distancePos:p2 = " + asString(shellBlock.position.x, StepSizes.DECPLACES_SCREENSPACE) + "," + asString(shellBlock.position.y, StepSizes.DECPLACES_SCREENSPACE));
				lines.add("    move:b = " + (shellBlock.move ? "yes" : "no") );
				if(shellBlock.scaleMode == ScaleMode.RADIAL) {
					lines.add("    radial:b = " + (shellBlock.scaleMode == ScaleMode.RADIAL ? "yes" : "no") );
				}
				if(shellBlock.circleMode) {
					lines.add("    circleMode:b = " + (shellBlock.circleMode ? "yes" : "no") );
				}
				lines.add("    crosshairDistHorSizeMain:p2 = " + asString(shellBlock.size.x, StepSizes.DECPLACES_SCREENSPACE) + "," + asString(shellBlock.size.y, StepSizes.DECPLACES_SCREENSPACE));
				lines.add("    textPos:p2 = " + asString(shellBlock.textPos.x, StepSizes.DECPLACES_SCREENSPACE) + "," + asString(shellBlock.textPos.y, StepSizes.DECPLACES_SCREENSPACE));
				lines.add("    textAlign:i = " + shellBlock.textAlign.id);
				lines.add("    textShift:r = " + asString(shellBlock.textShift, StepSizes.DECPLACES_SCREENSPACE));
				
				lines.add("    drawAdditionalLines:b = " + (shellBlock.drawAddLines ? "yes" : "no") );
				lines.add("    crosshairDistHorSizeAdditional:p2 = " + asString(shellBlock.sizeAddLine.x, StepSizes.DECPLACES_SCREENSPACE) + "," + asString(shellBlock.sizeAddLine.y, StepSizes.DECPLACES_SCREENSPACE));
				
				if(shellBlock.scaleMode == ScaleMode.RADIAL) {
					lines.add("radialStretch:r = " + asString(shellBlock.radialStretch, 2));
					lines.add("radialAngle:r = " + asString(shellBlock.radialAngle, StepSizes.DECPLACES_ANGLE));
					lines.add("radialRadius:p2 = " + asString(shellBlock.radialRadius, shellBlock.radiusUseMils ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + "," + (shellBlock.radiusUseMils ? "1" : "0") );
				}
				lines.add("    crosshair_distances {");
				
				for(int i=0; i<shellBlock.indicators.size(); i++) {
					BIndicator indicator = shellBlock.indicators.get(i);
					int dist = indicator.getDistance();
					boolean major = indicator.isMajor();
					int label = major ? Math.abs(dist/100) : 0;
					String extend = asString(indicator.getExtend(), StepSizes.DECPLACES_SCREENSPACE);
					Vector2d textOff = new Vector2d(indicator.getTextX(), indicator.getTextY());
					lines.add("        distance { distance:p3="+dist + "," + label + "," + extend + "; textPos:p2=" + asString(textOff.x, StepSizes.DECPLACES_SCREENSPACE) + "," + asString(textOff.y, StepSizes.DECPLACES_SCREENSPACE) + "; }");
				}
				lines.add("    }");
				lines.add("  }");
			}
			
			lines.add("}");
			lines.add("");
		}
		
		
		// custom elements
		
		// lines / polygon outlines / quad outlines
		if(!data.getElements(ElementType.CUSTOM_LINE).isEmpty()
				|| !data.getElements(ElementType.CUSTOM_POLY_OUTLINE).isEmpty()
				|| !data.getElements(ElementType.CUSTOM_QUAD_OUTLINE).isEmpty()
				|| !data.getElements(ElementType.FUNNEL).isEmpty()) {
			lines.add("// lines");
			lines.add("drawLines {");
			
			// lines
			for(BaseElement element : data.getElements(ElementType.CUSTOM_LINE)) {
				ElementCustomLine lineObj = (ElementCustomLine)element;
				lines.add("  //-- eid=\"" + getElementIdentifier(lineObj, null) + "\"");
				lines.add("  line {");
				lines.add("    thousandth:b = " + (lineObj.useThousandth ? "yes" : "no") );
				if(lineObj.movement == Movement.MOVE) {
					lines.add("    move:b = " + (lineObj.movement == Movement.STATIC ? "no" : "yes") );
				}
				if(lineObj.movement == Movement.MOVE_RADIAL) {
					lines.add("    moveRadial:b = " + (lineObj.movement == Movement.MOVE_RADIAL ? "yes" : "no") );
					lines.add("    radialAngle:r = " + asString(lineObj.angle, StepSizes.DECPLACES_ANGLE));
					lines.add("    radialCenter:p2 = " + asString(lineObj.radCenter.x, lineObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + "," + asString(lineObj.radCenter.y, lineObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE));
					lines.add("    radialMoveSpeed:r = " + asString(lineObj.speed, StepSizes.DECPLACES_SPEED));
					if(!lineObj.autoCenter) {
						lines.add("    center:p2 = " + asString(lineObj.center.x, StepSizes.DECPLACES_SCREENSPACE) + "," + asString(lineObj.center.y, StepSizes.DECPLACES_SCREENSPACE));
					}
				}
				lines.add("    line:p4 = " 
									+ asString(lineObj.start.x+lineObj.positionOffset.x, lineObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + ","
									+ asString(lineObj.start.y+lineObj.positionOffset.y, lineObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + ", "
									+ asString(lineObj.end.x+lineObj.positionOffset.x, lineObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + ","
									+ asString(lineObj.end.y+lineObj.positionOffset.y, lineObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE));
				lines.add("  }");
			}
			
			// polygon outlines
			for(BaseElement element : data.getElements(ElementType.CUSTOM_POLY_OUTLINE)) {
				ElementCustomPolygonOutline polyObj = (ElementCustomPolygonOutline)element;
				for(ElementCustomLine lineObj : polyObj.getLines()) {
					lines.add("  //-- eid=\"" + getElementIdentifier(polyObj, lineObj) + "\"");
					lines.add("  line {");
					lines.add("    thousandth:b = " + (lineObj.useThousandth ? "yes" : "no") );
					if(polyObj.movement == Movement.MOVE) {
						lines.add("    move:b = " + (polyObj.movement == Movement.STATIC ? "no" : "yes") );
					}
					if(polyObj.movement == Movement.MOVE_RADIAL) {
						lines.add("    moveRadial:b = " + (polyObj.movement == Movement.MOVE_RADIAL ? "yes" : "no") );
						lines.add("    radialAngle:r = " + asString(polyObj.angle, StepSizes.DECPLACES_ANGLE));
						lines.add("    radialCenter:p2 = " + asString(polyObj.radCenter.x, polyObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + "," + asString(polyObj.radCenter.y, polyObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE));
						lines.add("    radialMoveSpeed:r = " + asString(polyObj.speed, StepSizes.DECPLACES_SPEED));
						lines.add("    center:p2 = " + asString(polyObj.center.x, StepSizes.DECPLACES_SCREENSPACE) + "," + asString(polyObj.center.y, StepSizes.DECPLACES_SCREENSPACE));
					}
					lines.add("    line:p4 = " 
							+ asString(lineObj.start.x, lineObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + ","
							+ asString(lineObj.start.y, lineObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + ", "
							+ asString(lineObj.end.x, lineObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + ","
							+ asString(lineObj.end.y, lineObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE));					
					lines.add("  }");
				}
			}
			
			// quad outlines
			for(BaseElement element : data.getElements(ElementType.CUSTOM_QUAD_OUTLINE)) {
				ElementCustomQuadOutline quadObj = (ElementCustomQuadOutline)element;
				for(ElementCustomLine lineObj : quadObj.getLines()) {
					lines.add("  //-- eid=\"" + getElementIdentifier(quadObj, lineObj) + "\"");
					lines.add("  line {");
					lines.add("    thousandth:b = " + (lineObj.useThousandth ? "yes" : "no") );
					if(quadObj.movement == Movement.MOVE) {
						lines.add("    move:b = " + (quadObj.movement == Movement.STATIC ? "no" : "yes") );
					}
					if(quadObj.movement == Movement.MOVE_RADIAL) {
						lines.add("    moveRadial:b = " + (quadObj.movement == Movement.MOVE_RADIAL ? "yes" : "no") );
						lines.add("    radialAngle:r = " + asString(quadObj.angle, StepSizes.DECPLACES_ANGLE));
						lines.add("    radialCenter:p2 = " + asString(quadObj.radCenter.x, quadObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + "," + asString(quadObj.radCenter.y, quadObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE));
						lines.add("    radialMoveSpeed:r = " + asString(quadObj.speed, StepSizes.DECPLACES_SPEED));
						lines.add("    center:p2 = " + asString(quadObj.center.x, StepSizes.DECPLACES_SCREENSPACE) + "," + asString(quadObj.center.y, StepSizes.DECPLACES_SCREENSPACE));
					}
					lines.add("    line:p4 = " 
							+ asString(lineObj.start.x, lineObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + ","
							+ asString(lineObj.start.y, lineObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + ", "
							+ asString(lineObj.end.x, lineObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + ","
							+ asString(lineObj.end.y, lineObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE));				
					lines.add("  }");
				}
			}
			
			// funnel
			for(BaseElement element : data.getElements(ElementType.FUNNEL)) {
				ElementFunnel funnel = (ElementFunnel)element;
				for(ElementCustomLine lineObj : funnel.getLines()) {
					lines.add("  //-- eid=\"" + getElementIdentifier(funnel, lineObj) + "\"");
					lines.add("  line {");
					lines.add("    thousandth:b = " + funnel.useThousandth );
					if(funnel.movement == Movement.MOVE) {
						lines.add("    move:b = " + (funnel.movement == Movement.STATIC ? "no" : "yes") );
					}
					lines.add("    line:p4 = " 
							+ asString(lineObj.start.x, lineObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + ","
							+ asString(lineObj.start.y, lineObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + ", "
							+ asString(lineObj.end.x, lineObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + ","
							+ asString(lineObj.end.y, lineObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE));				
					lines.add("  }");
				}
			}
			
			lines.add("}");
			lines.add("");
		}
		
		// text
		if(!data.getElements(ElementType.CUSTOM_TEXT).isEmpty()) {
			lines.add("// text");
			lines.add("drawTexts {");
			for(BaseElement element : data.getElements(ElementType.CUSTOM_TEXT)) {
				ElementCustomText textObj = (ElementCustomText)element;
				lines.add("  //-- eid=\"" + getElementIdentifier(textObj, null) + "\"");
				lines.add("  text {");
				lines.add("    text:t = " + "\""+textObj.text + "\"");
				lines.add("    thousandth:b = " + (textObj.useThousandth ? "yes" : "no") );
				if(textObj.movement == Movement.MOVE) {
					lines.add("    move:b = " + (textObj.movement == Movement.STATIC ? "no" : "yes") );
				}
				if(textObj.movement == Movement.MOVE_RADIAL) {
					lines.add("    moveRadial:b = " + (textObj.movement == Movement.MOVE_RADIAL ? "yes" : "no") );
					lines.add("    radialAngle:r = " + asString(textObj.angle, StepSizes.DECPLACES_ANGLE));
					lines.add("    radialCenter:p2 = " + asString(textObj.radCenter.x, textObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + "," + asString(textObj.radCenter.y, textObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE));
					lines.add("    radialMoveSpeed:r = " + asString(textObj.speed, StepSizes.DECPLACES_SPEED));
					if(!textObj.autoCenter) {
						lines.add("    center:p2 = " + asString(textObj.center.x, StepSizes.DECPLACES_SCREENSPACE) + "," + asString(textObj.center.y, StepSizes.DECPLACES_SCREENSPACE));
					}
				}
				lines.add("    pos:p2 = " + asString(textObj.position.x, textObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + "," + asString(textObj.position.y, textObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE));
				lines.add("    align:i = " + textObj.align.id);
				lines.add("    size:r = " + asString(textObj.size, StepSizes.DECPLACES_SCREENSPACE));
				if(textObj.enableHighlight) {
					lines.add("    highlight:b = yes");
				}
				lines.add("  }");
			}
			lines.add("}");
			lines.add("");
		}
		
		// circles
		if(!data.getElements(ElementType.CUSTOM_CIRCLE_OUTLINE).isEmpty()) {
			lines.add("// circles");
			lines.add("drawCircles {");
			for(BaseElement element : data.getElements(ElementType.CUSTOM_CIRCLE_OUTLINE)) {
				ElementCustomCircleOutline circleObj = (ElementCustomCircleOutline)element;
				lines.add("  //-- eid=\"" + getElementIdentifier(circleObj, null) + "\"");
				lines.add("  circle {");
				lines.add("    thousandth:b = " + (circleObj.useThousandth ? "yes" : "no") );
				if(circleObj.movement == Movement.MOVE) {
					lines.add("    move:b = " + (circleObj.movement == Movement.STATIC ? "no" : "yes") );
				}
				if(circleObj.movement == Movement.MOVE_RADIAL) {
					lines.add("    moveRadial:b = " + (circleObj.movement == Movement.MOVE_RADIAL ? "yes" : "no") );
					lines.add("    radialAngle:r = " + asString(circleObj.angle, StepSizes.DECPLACES_ANGLE));
					lines.add("    radialCenter:p2 = " + asString(circleObj.radCenter.x, circleObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + "," + asString(circleObj.radCenter.y, circleObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE));
					lines.add("    radialMoveSpeed:r = " + asString(circleObj.speed, StepSizes.DECPLACES_SPEED));
					if(!circleObj.autoCenter) {
						lines.add("    center:p2 = " + asString(circleObj.center.x, StepSizes.DECPLACES_SCREENSPACE) + "," + asString(circleObj.center.y, StepSizes.DECPLACES_SCREENSPACE));
					}
				}
				lines.add("    segment:p2 = " + asString(circleObj.segment.x, StepSizes.DECPLACES_ANGLE) + "," + asString(circleObj.segment.y, StepSizes.DECPLACES_ANGLE));
				lines.add("    pos:p2 = " + asString(circleObj.position.x, circleObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + "," + asString(circleObj.position.y, circleObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE));
				lines.add("    diameter:r = " + asString(circleObj.diameter, circleObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE));
				lines.add("    size:r = " + asString(circleObj.size, StepSizes.DECPLACES_THICKNESS));
				lines.add("  }");
			}
			lines.add("}");
			lines.add("");
		}
		
		
		// quads
		if(!data.getElements(ElementType.CUSTOM_QUAD_FILLED).isEmpty() || !data.getElements(ElementType.CUSTOM_POLY_FILLED).isEmpty() || !data.getElements(ElementType.CUSTOM_CIRCLE_FILLED).isEmpty()) {
			lines.add("// quads");
			lines.add("drawQuads {");
			
			for(BaseElement element : data.getElements(ElementType.CUSTOM_QUAD_FILLED)) {
				ElementCustomQuadFilled quadObj = (ElementCustomQuadFilled)element;
				lines.add("  //-- eid=\"" + getElementIdentifier(quadObj, null) + "\"");
				lines.add("  quad {");
				lines.add("    thousandth:b = " + (quadObj.useThousandth ? "yes" : "no") );
				if(quadObj.movement == Movement.MOVE) {
					lines.add("    move:b = " + (quadObj.movement == Movement.STATIC ? "no" : "yes") );
				}
				if(quadObj.movement == Movement.MOVE_RADIAL) {
					lines.add("    moveRadial:b = " + (quadObj.movement == Movement.MOVE_RADIAL ? "yes" : "no") );
					lines.add("    radialAngle:r = " + asString(quadObj.angle, StepSizes.DECPLACES_ANGLE));
					lines.add("    radialCenter:p2 = " + asString(quadObj.radCenter.x, quadObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + "," + asString(quadObj.radCenter.y, quadObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE));
					lines.add("    radialMoveSpeed:r = " + asString(quadObj.speed, StepSizes.DECPLACES_SPEED));
					if(!quadObj.autoCenter) {
						lines.add("    center:p2 = " + asString(quadObj.center.x, StepSizes.DECPLACES_SCREENSPACE) + "," + asString(quadObj.center.y, StepSizes.DECPLACES_SCREENSPACE));
					}
				}
				lines.add("    tl:p2 = " + asString(quadObj.pos1.x+quadObj.positionOffset.x, quadObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + "," + asString(quadObj.pos1.y+quadObj.positionOffset.y, quadObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE));
				lines.add("    tr:p2 = " + asString(quadObj.pos2.x+quadObj.positionOffset.x, quadObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + "," + asString(quadObj.pos2.y+quadObj.positionOffset.y, quadObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE));
				lines.add("    br:p2 = " + asString(quadObj.pos3.x+quadObj.positionOffset.x, quadObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + "," + asString(quadObj.pos3.y+quadObj.positionOffset.y, quadObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE));
				lines.add("    bl:p2 = " + asString(quadObj.pos4.x+quadObj.positionOffset.x, quadObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + "," + asString(quadObj.pos4.y+quadObj.positionOffset.y, quadObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE));
				lines.add("  }");
			}
			
			for(BaseElement element : data.getElements(ElementType.CUSTOM_POLY_FILLED)) {
				ElementCustomPolygonFilled polyObj = (ElementCustomPolygonFilled)element;
				for(ElementCustomQuadFilled quadObj : polyObj.getQuads()) {
					lines.add("  //-- eid=\"" + getElementIdentifier(polyObj, quadObj) + "\"");
					lines.add("  quad {");
					lines.add("    thousandth:b = " + (polyObj.useThousandth ? "yes" : "no") );
					if(polyObj.movement == Movement.MOVE) {
						lines.add("    move:b = " + (polyObj.movement == Movement.STATIC ? "no" : "yes") );
					}
					if(polyObj.movement == Movement.MOVE_RADIAL) {
						lines.add("    moveRadial:b = " + (polyObj.movement == Movement.MOVE_RADIAL ? "yes" : "no") );
						lines.add("    radialAngle:r = " + asString(polyObj.angle, StepSizes.DECPLACES_ANGLE));
						lines.add("    radialCenter:p2 = " + asString(polyObj.radCenter.x, polyObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + "," + asString(polyObj.radCenter.y, polyObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE));
						lines.add("    radialMoveSpeed:r = " + asString(polyObj.speed, StepSizes.DECPLACES_SPEED));
						lines.add("    center:p2 = " + asString(polyObj.center.x, StepSizes.DECPLACES_SCREENSPACE) + "," + asString(polyObj.center.y, StepSizes.DECPLACES_SCREENSPACE));
					}
					lines.add("    tl:p2 = " + asString(quadObj.pos1.x, quadObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + "," + asString(quadObj.pos1.y, quadObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE));
					lines.add("    tr:p2 = " + asString(quadObj.pos2.x, quadObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + "," + asString(quadObj.pos2.y, quadObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE));
					lines.add("    br:p2 = " + asString(quadObj.pos3.x, quadObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + "," + asString(quadObj.pos3.y, quadObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE));
					lines.add("    bl:p2 = " + asString(quadObj.pos4.x, quadObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + "," + asString(quadObj.pos4.y, quadObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE));
					lines.add("  }");
				}
			}
			
			for(BaseElement element : data.getElements(ElementType.CUSTOM_CIRCLE_FILLED)) {
				ElementCustomCircleFilled circleObj = (ElementCustomCircleFilled)element;
				for(ElementCustomQuadFilled quadObj : circleObj.getQuads()) {
					lines.add("  //-- eid=\"" + getElementIdentifier(circleObj, quadObj) + "\"");
					lines.add("  quad {");
					lines.add("    thousandth:b = " + (circleObj.useThousandth ? "yes" : "no") );
					if(circleObj.movement == Movement.MOVE) {
						lines.add("    move:b = " + (circleObj.movement == Movement.STATIC ? "no" : "yes") );
					}
					if(circleObj.movement == Movement.MOVE_RADIAL) {
						lines.add("    moveRadial:b = " + (circleObj.movement == Movement.MOVE_RADIAL ? "yes" : "no") );
						lines.add("    radialAngle:r = " + asString(circleObj.angle, StepSizes.DECPLACES_ANGLE));
						lines.add("    radialCenter:p2 = " + asString(circleObj.radCenter.x, circleObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + "," + asString(circleObj.radCenter.y, circleObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE));
						lines.add("    radialMoveSpeed:r = " + asString(circleObj.speed, StepSizes.DECPLACES_SPEED));
						lines.add("    center:p2 = " + asString(circleObj.center.x, StepSizes.DECPLACES_SCREENSPACE) + "," + asString(circleObj.center.y, StepSizes.DECPLACES_SCREENSPACE));
					}
					lines.add("    tl:p2 = " + asString(quadObj.pos1.x, quadObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + "," + asString(quadObj.pos1.y, quadObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE));
					lines.add("    tr:p2 = " + asString(quadObj.pos2.x, quadObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + "," + asString(quadObj.pos2.y, quadObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE));
					lines.add("    br:p2 = " + asString(quadObj.pos3.x, quadObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + "," + asString(quadObj.pos3.y, quadObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE));
					lines.add("    bl:p2 = " + asString(quadObj.pos4.x, quadObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + "," + asString(quadObj.pos4.y, quadObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE));
					lines.add("  }");
				}
			}
			
			lines.add("}");
			lines.add("");
			lines.add("");
			lines.add("");
		}
		
		
		// write element metadata
		lines.add("//-- metadata start");
		for(BaseElement element : data.getElements(ElementType.SHELL_BALLISTICS_BLOCK)) {
			ElementShellBlock shellBlock = (ElementShellBlock)element;
			String strAmmo = "";
			for(int i=0; i<shellBlock.elementBallistic.ammunition.size(); i++) {
				strAmmo += shellBlock.elementBallistic.ammunition.get(i).name;
				if(i != shellBlock.elementBallistic.ammunition.size()-1) {
					strAmmo += ", ";
				}
			}
			StringBuilder sb = new StringBuilder().append("//-- ");
			buildAttribute(sb, "eid", getElementIdentifier(shellBlock, null)).append(", ");
			buildAttribute(sb, "name", shellBlock.name).append(", ");
			buildAttribute(sb, "type", shellBlock.type.toString()).append(", ");
			buildAttribute(sb, "ammo", strAmmo);
			lines.add(sb.toString());
		}
		for(BaseElement element : data.getElements(ElementType.CUSTOM_LINE)) {
			ElementCustomLine lineObj = (ElementCustomLine)element;
			StringBuilder sb = new StringBuilder().append("//-- ");
			buildAttribute(sb, "eid", getElementIdentifier(lineObj, null)).append(", ");
			buildAttribute(sb, "name", lineObj.name).append(", ");
			buildAttribute(sb, "type", lineObj.type.toString());
			lines.add(sb.toString());
		}
		for(BaseElement element : data.getElements(ElementType.CUSTOM_POLY_OUTLINE)) {
			ElementCustomPolygonOutline polyObj = (ElementCustomPolygonOutline)element;
			String strVertices = "";
			for(int i=0; i<polyObj.getLines().size(); i++) {
				ElementCustomLine lineObj = (ElementCustomLine)polyObj.getLines().get(i);
				strVertices += getElementIdentifier(lineObj, null);
				if(i != polyObj.getLines().size()-1) {
					strVertices += ",";
				}
			}
			StringBuilder sb = new StringBuilder().append("//-- ");
			buildAttribute(sb, "eid", getElementIdentifier(polyObj, null)).append(", ");
			buildAttribute(sb, "name", polyObj.name).append(", ");
			buildAttribute(sb, "type", polyObj.type.toString()).append(", ");
			buildAttribute(sb, "vertices", strVertices);
			lines.add(sb.toString());
		}
		for(BaseElement element : data.getElements(ElementType.CUSTOM_QUAD_OUTLINE)) {
			ElementCustomQuadOutline quadObj = (ElementCustomQuadOutline)element;
			String strVertices = "";
			for(int i=0; i<quadObj.getLines().size(); i++) {
				ElementCustomLine lineObj = (ElementCustomLine)quadObj.getLines().get(i);
				strVertices += getElementIdentifier(lineObj, null);
				if(i != quadObj.getLines().size()-1) {
					strVertices += ",";
				}
			}
			StringBuilder sb = new StringBuilder().append("//-- ");
			buildAttribute(sb, "eid", getElementIdentifier(quadObj, null)).append(", ");
			buildAttribute(sb, "name", quadObj.name).append(", ");
			buildAttribute(sb, "type", quadObj.type.toString()).append(", ");
			buildAttribute(sb, "vertices", strVertices);
			lines.add(sb.toString());
		}
		for(BaseElement element : data.getElements(ElementType.FUNNEL)) {
			ElementFunnel funnelObj = (ElementFunnel)element;
			StringBuilder sb = new StringBuilder().append("//-- ");
			buildAttribute(sb, "eid", getElementIdentifier(funnelObj, null)).append(", ");
			buildAttribute(sb, "name", funnelObj.name).append(", ");
			buildAttribute(sb, "type", funnelObj.type.toString()).append(", ");
			buildAttribute(sb, "sizetarget", funnelObj.sizeTargetCM+"").append(", ");
			buildAttribute(sb, "range", funnelObj.rangeStart+" "+funnelObj.rangeEnd+" "+funnelObj.rangeStep).append(", ");
			buildAttribute(sb, "shell", funnelObj.elementBallistic.ammunition.get(0).name).append(", ");
			buildAttribute(sb, "data", (funnelObj.showLeft?"l":"")
										+(funnelObj.showRight?"r":"")
										+(funnelObj.horz?"h":"")
										+(funnelObj.flip?"f":"")
										+(funnelObj.baseLine?"b":"")).append(", ");
			buildAttribute(sb, "offset", 
					asString(funnelObj.offset.x, funnelObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE) + " " +
					asString(funnelObj.offset.y, funnelObj.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE)).append(", ");
			lines.add(sb.toString());
		}
		for(BaseElement element : data.getElements(ElementType.CUSTOM_TEXT)) {
			ElementCustomText textObj = (ElementCustomText)element;
			StringBuilder sb = new StringBuilder().append("//-- ");
			buildAttribute(sb, "eid", getElementIdentifier(textObj, null)).append(", ");
			buildAttribute(sb, "name", textObj.name).append(", ");
			buildAttribute(sb, "type", textObj.type.toString());
			lines.add(sb.toString());
		}
		for(BaseElement element : data.getElements(ElementType.CUSTOM_CIRCLE_OUTLINE)) {
			ElementCustomCircleOutline circleObj = (ElementCustomCircleOutline)element;
			StringBuilder sb = new StringBuilder().append("//-- ");
			buildAttribute(sb, "eid", getElementIdentifier(circleObj, null)).append(", ");
			buildAttribute(sb, "name", circleObj.name).append(", ");
			buildAttribute(sb, "type", circleObj.type.toString());
			lines.add(sb.toString());
		}
		for(BaseElement element : data.getElements(ElementType.CUSTOM_QUAD_FILLED)) {
			ElementCustomQuadFilled quadObj = (ElementCustomQuadFilled)element;
			StringBuilder sb = new StringBuilder().append("//-- ");
			buildAttribute(sb, "eid", getElementIdentifier(quadObj, null)).append(", ");
			buildAttribute(sb, "name", quadObj.name).append(", ");
			buildAttribute(sb, "type", quadObj.type.toString());
			lines.add(sb.toString());
		}
		
		for(BaseElement element : data.getElements(ElementType.CUSTOM_POLY_FILLED)) {
			ElementCustomPolygonFilled polyObj = (ElementCustomPolygonFilled)element;
			String strTriangles = "";
			for(int i=0; i<polyObj.getQuads().size(); i++) {
				ElementCustomQuadFilled quadObj = (ElementCustomQuadFilled)polyObj.getQuads().get(i);
				Vector3i indices = polyObj.getTriangleIndices(quadObj);
				strTriangles += getElementIdentifier(quadObj, null) + "=[" + indices.x + " " + indices.y + " " + indices.z + "]";
				if(i != polyObj.getQuads().size()-1) {
					strTriangles += ",";
				}
			}
			StringBuilder sb = new StringBuilder().append("//-- ");
			buildAttribute(sb, "eid", getElementIdentifier(polyObj, null)).append(", ");
			buildAttribute(sb, "name", polyObj.name).append(", ");
			buildAttribute(sb, "type", polyObj.type.toString()).append(", ");
			buildAttribute(sb, "size", polyObj.getVertices().size()+"").append(", ");
			buildAttribute(sb, "tris", strTriangles);
			lines.add(sb.toString());
		}
		
		
		for(BaseElement element : data.getElements(ElementType.CUSTOM_CIRCLE_FILLED)) {
			ElementCustomCircleFilled circleObj = (ElementCustomCircleFilled)element;
			StringBuilder sb = new StringBuilder().append("//-- ");
			buildAttribute(sb, "eid", getElementIdentifier(circleObj, null)).append(", ");
			buildAttribute(sb, "name", circleObj.name).append(", ");
			buildAttribute(sb, "type", circleObj.type.toString()).append(", ");
			buildAttribute(sb, "segment", circleObj.segment.x+" "+circleObj.segment.y).append(", ");
			buildAttribute(sb, "quality", circleObj.quality+"");
			lines.add(sb.toString());
		}
		lines.add("//-- metadata end");


		try {
			Files.write(Paths.get(outputFile.getAbsolutePath()), lines, Charset.forName("UTF-8"));
			Logger.get().info("Sight file saved: " + outputFile.getAbsolutePath());
			return true;
		} catch (IOException e) {
			Logger.get().error("An error occured while saving sight");
			Logger.get().error(e);
			return false;
		}
		
		
	}
	
	
	
	private StringBuilder buildAttribute(StringBuilder sb, String key, String value) {
		return sb.append(key).append('=').append('"').append(value).append('"');
	}
	
	
	
	
	private String getElementIdentifier(BaseElement element, BaseElement subElement) {
		return Integer.toHexString(element.hashCode()) + (subElement != null ? "-" + Integer.toHexString(subElement.hashCode()) : "");
	}

	
	
	
	private static String asString(double value, int decPlaces) {
		String s = String.format("%." + decPlaces + "f", value).replaceAll(",", ".");
		s = s.indexOf(".") < 0 ? s : s.replaceAll("0*$", "").replaceAll("\\.$", "");
		return s;
	}
	
}


