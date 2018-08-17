package com.ruegnerlukas.wtsights.data;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2i;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationAmmoData;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationData;
import com.ruegnerlukas.wtsights.data.sight.BallisticsBlock;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.data.sight.SightData.ScaleMode;
import com.ruegnerlukas.wtsights.data.sight.objects.CircleObject;
import com.ruegnerlukas.wtsights.data.sight.objects.LineObject;
import com.ruegnerlukas.wtsights.data.sight.objects.QuadObject;
import com.ruegnerlukas.wtsights.data.sight.objects.SightObject;
import com.ruegnerlukas.wtsights.data.sight.objects.SightObject.Movement;
import com.ruegnerlukas.wtsights.data.sight.objects.TextObject;
import com.ruegnerlukas.wtutils.Config2;

public class DataWriter {


	public static boolean saveExternalCalibFile(CalibrationData data, File outputFile) throws Exception {

		if(data == null) {
			Logger.get().warn("Could not find calibration data: " + data);
			return false;
		}
		if(outputFile == null) {
			Logger.get().warn("Could not find file: " + outputFile);
			return false;
		}
		
		
		Logger.get().info("Writing calibration-file to " + outputFile);
		
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		Document doc = docBuilder.newDocument();
		
		Element rootElement = doc.createElement("calibrationdata");
		doc.appendChild(rootElement);
		
		Element elementVehicle = doc.createElement(data.vehicle.name);
		elementVehicle.setAttribute("fovOut", ""+data.vehicle.fovOut);
		elementVehicle.setAttribute("fovIn", ""+data.vehicle.fovIn);
		rootElement.appendChild(elementVehicle);
				
		Element elementAmmoGroup = doc.createElement("ammo");
		elementVehicle.appendChild(elementAmmoGroup);
		
		// ammo data
		for(CalibrationAmmoData ammoData : data.ammoData) {
			
			String ammoName =  ammoData.ammo.name;
			
			Element elementAmmo = doc.createElement(ammoName);
			elementAmmoGroup.appendChild(elementAmmo);
					
			elementAmmo.setAttribute("zoomedIn", ammoData.zoomedIn ? "true" : "false");
			elementAmmo.setAttribute("imageName", ammoData.imgName);
			elementAmmo.setAttribute("markerCenter", ammoData.markerCenter.x + "," + ammoData.markerCenter.y);

			Element elementMarkerRanges = doc.createElement("markerRanges");
			elementAmmo.appendChild(elementMarkerRanges);
			
			int i = 0;
			for(Vector2i m : ammoData.markerRanges) {
				elementMarkerRanges.setAttribute("marker_" + (i++), m.x + ", " + m.y);
			}
			
		}
		
		
		// images
		Element elementImageRoot = doc.createElement("images");
		elementVehicle.appendChild(elementImageRoot);
		
		for(Entry<String,BufferedImage> entry : data.images.entrySet()) {
			String imageName = entry.getKey();
			Element elementImage = doc.createElement(imageName);
			elementImageRoot.appendChild(elementImage);
			String encodedImage = endodeImage(entry.getValue(), "jpg");
			elementImage.setAttribute("encodedData", encodedImage);
		}
		
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(outputFile);
		
		transformer.transform(source, result);
		
		Logger.get().info("External calibration file saved");
		return true;
	}
	
	
	
	
	private static String endodeImage(BufferedImage img, String format) throws IOException {
	
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(img, format, baos);
		baos.flush();
		
		String strImage = Base64.getEncoder().encodeToString(baos.toByteArray());
		baos.close();
		
		return strImage;
	}
	
	
	
	
	
	public static boolean saveSight(SightData data, CalibrationData dataCalib, File outputFile) {
		
		if(data == null) {
			Logger.get().warn("Could not find sight-data: " + data);
			return false;
		}
		if(dataCalib == null) {
			Logger.get().warn("Could not find calibration-data: " + dataCalib);
			return false;
		}
		if(outputFile == null) {
			Logger.get().warn("Could not find file: " + outputFile);
			return false;
		}
		
		
		List<String> lines = new ArrayList<String>();
			
		// metadata
		lines.add("// created with WTSightEdit " + Config2.build_date);
		lines.add("// vehicle = " + (dataCalib == null ? "unknown" : dataCalib.vehicle.name));
		lines.add("");
		
		// general
		lines.add("// general");
		lines.add("thousandth:t = \"" + data.gnrThousandth.tag + "\"");
		lines.add("fontSizeMult:r = " + data.gnrFontScale);
		lines.add("lineSizeMult:r = " + data.gnrLineSize);
		lines.add("drawCentralLineVert:b = " + (data.gnrDrawCentralVertLine ? "yes" : "no") );
		lines.add("drawCentralLineHorz:b = " + (data.gnrDrawCentralHorzLine ? "yes" : "no") );
		lines.add("applyCorrectionToGun:b = " + (data.gnrApplyCorrectionToGun ? "yes" : "no") );
		lines.add("");
		
		// rangefinder
		lines.add("// rangefinder");
		lines.add("rangefinderHorizontalOffset:r = " + data.rfOffset.x);
		lines.add("rangefinderVerticalOffset:r = " + data.rfOffset.y);
		lines.add("rangefinderProgressBarColor1:c = " + (int)(data.rfColor1.getRed()*255) + "," + (int)(data.rfColor1.getGreen()*255) + "," + (int)(data.rfColor1.getBlue()*255) + "," + (int)(data.rfColor1.getOpacity()*255));
		lines.add("rangefinderProgressBarColor2:c = " + (int)(data.rfColor2.getRed()*255) + "," + (int)(data.rfColor2.getGreen()*255) + "," + (int)(data.rfColor2.getBlue()*255) + "," + (int)(data.rfColor2.getOpacity()*255));
		lines.add("rangefinderTextScale:r = " + data.rfTextScale);
		lines.add("rangefinderUseThousandth:b = " + (data.rfUseThousandth ? "yes" : "no") );
		lines.add("");
		
		// horz range indicators
		lines.add("// horizontal range indicators");
		lines.add("crosshairHorVertSize:p2 = " + data.hrSizeMajor + "," + data.hrSizeMinor);
		lines.add("crosshair_hor_ranges {");
		for(int i=0; i<data.hrMils.size(); i++) {
			int mil = data.hrMils.get(i);
			int label = data.hrMajors.get(i) ? Math.abs(mil) : 0;
			lines.add("  range:p2 = " + mil + "," + label);	
		}
		lines.add("}");
		lines.add("");
		
		
		// ballistic range indicators
		lines.add("// ballistic range indicators");
		lines.add("drawUpward:b = " + (data.brIndicators.bDrawUpward ? "yes" : "no") );
		lines.add("distancePos:p2 = " + data.brIndicators.bMainPos.x + "," + data.brIndicators.bMainPos.y);
		if(data.brIndicators.bMove) {
			lines.add("move:b = " + (data.brIndicators.bMove ? "yes" : "no") );
		}
		if(data.brIndicators.bScaleMode == ScaleMode.RADIAL) {
			lines.add("radial:b = " + (data.brIndicators.bScaleMode == ScaleMode.RADIAL ? "yes" : "no"));
		}
		if(data.brIndicators.bCircleMode) {
			lines.add("circleMode:b = " + (data.brIndicators.bCircleMode ? "yes" : "no"));
		}
		lines.add("crosshairDistHorSizeMain:p2 = " + data.brIndicators.bSizeMain.x + "," + data.brIndicators.bSizeMain.y);
		lines.add("textPos:p2 = " + data.brIndicators.bTextOffset.x + "," + data.brIndicators.bTextOffset.y);
		lines.add("textAlign:i = " + data.brIndicators.bTextAlign.id);
		lines.add("textShift:r = " + data.brIndicators.bTextShift);
		lines.add("drawAdditionalLines:b = " + (data.brIndicators.bDrawCenteredLines ? "yes" : "no") );
		lines.add("crosshairDistHorSizeAdditional:p2 = " + data.brIndicators.bSizeCentered.x + "," + data.brIndicators.bSizeCentered.y);
		if(data.brIndicators.bScaleMode == ScaleMode.RADIAL) {
			lines.add("radialStretch:r = " + data.brIndicators.bRadialStretch);
			lines.add("radialAngle:r = " + data.brIndicators.bRadialAngle);
			lines.add("radialRadius:p2 = " + data.brIndicators.bRadialRadius + "," + (data.brIndicators.bRadiusUseMils ? "1" : "0") );
		}
		lines.add("drawDistanceCorrection:b = " + (data.brIndicators.bDrawCorrection ? "yes" : "no") );
		if(data.brIndicators.bDrawCorrection) {
			lines.add("distanceCorrectionPos:p2 = " + data.brIndicators.bCorrectionPos.x + "," + data.brIndicators.bCorrectionPos.y);
		}
		lines.add("");

		lines.add("crosshair_distances {");
		for(int i=0; i<data.brIndicators.bDists.size(); i++) {
			int dist = data.brIndicators.bDists.get(i);
			boolean major = data.brIndicators.bMajors.get(i);
			int label = major ? Math.abs(dist/100) : 0;
			double extend = data.brIndicators.bExtensions.get(i);
			Vector2d textOff = data.brIndicators.bTextOffsets.get(i);
			lines.add("    distance { distance:p3="+dist + "," + label + "," + extend + "; textPos:p2=" + textOff.x + "," + textOff.y + "; }");
		}
		lines.add("}");
		lines.add("");
		
		
		
		// shell ballistics blocks
		if(!data.shellBlocks.isEmpty()) {
			lines.add("// shell ballistics blocks");
			lines.add("ballistics {");
			
			for(Entry<String,BallisticsBlock> entry : data.shellBlocks.entrySet()) {

				BallisticsBlock block = entry.getValue();
				
				lines.add("  //-- " + block.name + " (" + block.bBulletName + ")");
				lines.add("  bullet {");
				
				lines.add("    bulletType:t = \"" + block.bBulletType + "\"");
				lines.add("    speed:r = " + block.bBulletSpeed);
				lines.add("    triggerGroup:t = \"" + block.bTriggerGroup + "\"");
				lines.add("    thousandth:b = " + "no");
				lines.add("    drawUpward:b = " + (block.bDrawUpward ? "yes" : "no") );
				lines.add("    distancePos:p2 = " + block.bMainPos.x + "," + block.bMainPos.y);
				lines.add("    move:b = " + (block.bMove ? "yes" : "no") );
				if(block.bScaleMode == ScaleMode.RADIAL) {
					lines.add("    radial:b = " + (block.bScaleMode == ScaleMode.RADIAL ? "yes" : "no") );
				}
				if(block.bCircleMode) {
					lines.add("    circleMode:b = " + (block.bCircleMode ? "yes" : "no") );
				}
				lines.add("    crosshairDistHorSizeMain:p2 = " + block.bSizeMain.x + "," + block.bSizeMain.y);
				lines.add("    textPos:p2 = " + block.bTextOffset.x + "," + block.bTextOffset.y);
				lines.add("    textAlign:i = " + block.bTextAlign.id);
				lines.add("    textShift:r = " + block.bTextShift);
				
				lines.add("    drawAdditionalLines:b = " + (block.bDrawCenteredLines ? "yes" : "no") );
				lines.add("    crosshairDistHorSizeAdditional:p2 = " + block.bSizeCentered.x + "," + block.bSizeCentered.y);
				
				if(block.bScaleMode == ScaleMode.RADIAL) {
					lines.add("    radialStretch:r = " + block.bRadialStretch);
					lines.add("    radialAngle:r = " + block.bRadialAngle);
					lines.add("    radialRadius:p2 = " + block.bRadialRadius + "," + (block.bRadiusUseMils ? "1" : "0" ));
				}
				lines.add("    crosshair_distances {");
				for(int i=0; i<block.bDists.size(); i++) {
					int dist = block.bDists.get(i);
					boolean major = block.bMajors.get(i);
					int label = major ? Math.abs(dist/100) : 0;
					double extend = block.bExtensions.get(i);
					Vector2d textOff = block.bTextOffsets.get(i);
					lines.add("        distance { distance:p3="+dist + "," + label + "," + extend + "; textPos:p2=" + textOff.x + "," + textOff.y + "; }");
				}
				lines.add("    }");
				lines.add("  }");
			}
			
			lines.add("}");
			lines.add("");
		}
		
		
		// custom elements
		List<LineObject> objLines = new ArrayList<LineObject>();
		List<TextObject> objTexts = new ArrayList<TextObject>();
		List<CircleObject> objCicles = new ArrayList<CircleObject>();
		List<QuadObject> objQuads = new ArrayList<QuadObject>();

		for(Entry<String,SightObject> entry : data.objects.entrySet()) {
			SightObject obj = entry.getValue();

			if(obj instanceof LineObject) {
				objLines.add((LineObject)obj);
				continue;
			}
			if(obj instanceof TextObject) {
				objTexts.add((TextObject)obj);
				continue;
			}
			if(obj instanceof CircleObject) {
				objCicles.add((CircleObject)obj);
				continue;
			}
			if(obj instanceof QuadObject) {
				objQuads.add((QuadObject)obj);
				continue;
			}
			
		}
		
		// lines
		if(!objLines.isEmpty()) {
			lines.add("// lines");
			lines.add("drawLines {");
			for(LineObject lineObj : objLines) {
				lines.add("  //-- " + lineObj.name);
				lines.add("  line {");
				lines.add("    thousandth:b = " + (lineObj.cmnUseThousandth ? "yes" : "no") );
				if(lineObj.cmnMovement == Movement.MOVE) {
					lines.add("    move:b = " + (lineObj.cmnMovement == Movement.STATIC ? "no" : "yes") );
				}
				if(lineObj.cmnMovement == Movement.MOVE_RADIAL) {
					lines.add("    moveRadial:b = " + (lineObj.cmnMovement == Movement.MOVE_RADIAL ? "yes" : "no") );
					lines.add("    radialAngle:r = " + lineObj.cmnAngle);
					lines.add("    radialCenter:p2 = " + lineObj.cmnRadCenter.x + "," + lineObj.cmnRadCenter.y);
					lines.add("    radialMoveSpeed:r = " + lineObj.cmnSpeed);
					if(!lineObj.useAutoCenter) {
						lines.add("    center:p2 = " + lineObj.cmnCenter.x + "," + lineObj.cmnCenter.y);
					}
				}
				lines.add("    line:p4 = " + lineObj.start.x + "," + lineObj.start.y + ", " + lineObj.end.x + "," + lineObj.end.y);
				lines.add("  }");
			}
			lines.add("}");
		}
		
		// text
		if(!objTexts.isEmpty()) {
			lines.add("// text");
			lines.add("drawTexts {");
			for(TextObject textObj : objTexts) {
				lines.add("  //-- " +  textObj.name);
				lines.add("  text {");
				lines.add("    text:t = " + "\""+textObj.text + "\"");
				lines.add("    thousandth:b = " + (textObj.cmnUseThousandth ? "yes" : "no") );
				if(textObj.cmnMovement == Movement.MOVE) {
					lines.add("    move:b = " + (textObj.cmnMovement == Movement.STATIC ? "no" : "yes") );
				}
				if(textObj.cmnMovement == Movement.MOVE_RADIAL) {
					lines.add("    moveRadial:b = " + (textObj.cmnMovement == Movement.MOVE_RADIAL ? "yes" : "no") );
					lines.add("    radialAngle:r = " + textObj.cmnAngle);
					lines.add("    radialCenter:p2 = " + textObj.cmnRadCenter.x + "," + textObj.cmnRadCenter.y);
					lines.add("    radialMoveSpeed:r = " + textObj.cmnSpeed);
					if(!textObj.useAutoCenter) {
						lines.add("    center:p2 = " + textObj.cmnCenter.x + "," + textObj.cmnCenter.y);
					}
				}
				lines.add("    pos:p2 = " + textObj.pos.x + "," + textObj.pos.y);
				lines.add("    align:i = " + textObj.align.id);
				lines.add("    size:r = " + textObj.size);
				lines.add("  }");
			}
			lines.add("}");
		}
		
		// circles
		if(!objCicles.isEmpty()) {
			lines.add("// circles");
			lines.add("drawCircles {");
			for(CircleObject circleObj : objCicles) {
				lines.add("  //-- " + circleObj.name);
				lines.add("  circle {");
				lines.add("    thousandth:b = " + (circleObj.cmnUseThousandth ? "yes" : "no") );
				if(circleObj.cmnMovement == Movement.MOVE) {
					lines.add("    move:b = " + (circleObj.cmnMovement == Movement.STATIC ? "no" : "yes") );
				}
				if(circleObj.cmnMovement == Movement.MOVE_RADIAL) {
					lines.add("    moveRadial:b = " + (circleObj.cmnMovement == Movement.MOVE_RADIAL ? "yes" : "no") );
					lines.add("    radialAngle:r = " + circleObj.cmnAngle);
					lines.add("    radialCenter:p2 = " + circleObj.cmnRadCenter.x + "," + circleObj.cmnRadCenter.y);
					lines.add("    radialMoveSpeed:r = " + circleObj.cmnSpeed);
					if(!circleObj.useAutoCenter) {
						lines.add("    center:p2 = " + circleObj.cmnCenter.x + "," + circleObj.cmnCenter.y);
					}
				}
				lines.add("    segment:p2 = " + circleObj.segment.x + "," + circleObj.segment.y);
				lines.add("    pos:p2 = " + circleObj.pos.x + "," + circleObj.pos.y);
				lines.add("    diameter:r = " + circleObj.diameter);
				lines.add("    size:r = " + circleObj.size);
				lines.add("  }");
			}
			lines.add("}");
		}
		
		
		// quads
		if(!objLines.isEmpty()) {
			lines.add("// quads");
			lines.add("drawQuads {");
			for(QuadObject quadObj : objQuads) {
				lines.add("  //-- " + quadObj.name);
				lines.add("  quad {");
				lines.add("    thousandth:b = " + (quadObj.cmnUseThousandth ? "yes" : "no") );
				if(quadObj.cmnMovement == Movement.MOVE) {
					lines.add("    move:b = " + (quadObj.cmnMovement == Movement.STATIC ? "no" : "yes") );
				}
				if(quadObj.cmnMovement == Movement.MOVE_RADIAL) {
					lines.add("    moveRadial:b = " + (quadObj.cmnMovement == Movement.MOVE_RADIAL ? "yes" : "no") );
					lines.add("    radialAngle:r = " + quadObj.cmnAngle);
					lines.add("    radialCenter:p2 = " + quadObj.cmnRadCenter.x + "," + quadObj.cmnRadCenter.y);
					lines.add("    radialMoveSpeed:r = " + quadObj.cmnSpeed);
					if(!quadObj.useAutoCenter) {
						lines.add("    center:p2 = " + quadObj.cmnCenter.x + "," + quadObj.cmnCenter.y);
					}
				}
				lines.add("    tl:p2 = " + quadObj.pos1.x + "," + quadObj.pos1.y);
				lines.add("    tr:p2 = " + quadObj.pos2.x + "," + quadObj.pos2.y);
				lines.add("    br:p2 = " + quadObj.pos3.x + "," + quadObj.pos3.y);
				lines.add("    bl:p2 = " + quadObj.pos4.x + "," + quadObj.pos4.y);
				lines.add("  }");
			}
			lines.add("}");
		}
		
		
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
	
	
	
}
