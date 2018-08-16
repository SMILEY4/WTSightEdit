package com.ruegnerlukas.wtsights.data;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ruegnerlukas.simplemath.MathUtils;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2i;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationAmmoData;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationData;
import com.ruegnerlukas.wtsights.data.sight.Block;
import com.ruegnerlukas.wtsights.data.sight.BlockElement;
import com.ruegnerlukas.wtsights.data.sight.ParamBool;
import com.ruegnerlukas.wtsights.data.sight.ParamColor;
import com.ruegnerlukas.wtsights.data.sight.ParamFloat;
import com.ruegnerlukas.wtsights.data.sight.ParamInteger;
import com.ruegnerlukas.wtsights.data.sight.ParamText;
import com.ruegnerlukas.wtsights.data.sight.ParamVec2;
import com.ruegnerlukas.wtsights.data.sight.ParamVec3;
import com.ruegnerlukas.wtsights.data.sight.ParamVec4;
import com.ruegnerlukas.wtsights.data.sight.Parameter;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.data.sight.SightData.ScaleMode;
import com.ruegnerlukas.wtsights.data.sight.SightData.TextAlign;
import com.ruegnerlukas.wtsights.data.sight.SightData.Thousandth;
import com.ruegnerlukas.wtsights.data.sight.SightData.TriggerGroup;
import com.ruegnerlukas.wtsights.data.sight.objects.CircleObject;
import com.ruegnerlukas.wtsights.data.sight.objects.LineObject;
import com.ruegnerlukas.wtsights.data.sight.objects.QuadObject;
import com.ruegnerlukas.wtsights.data.sight.objects.SightObject;
import com.ruegnerlukas.wtsights.data.sight.objects.SightObject.Movement;
import com.ruegnerlukas.wtsights.data.sight.objects.TextObject;
import com.ruegnerlukas.wtsights.data.sight.BLKSightParser;
import com.ruegnerlukas.wtsights.data.sight.BallisticsBlock;
import com.ruegnerlukas.wtsights.data.vehicle.Ammo;
import com.ruegnerlukas.wtsights.data.vehicle.Vehicle;
import com.ruegnerlukas.wtsights.data.vehicle.Weapon;
import com.ruegnerlukas.wtutils.XMLUtils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;


public class DataLoader {

	
	
	
	/**
	 * loads the files with merged data (vehicles+ammoData)
	 * */
	public static List<Vehicle> loadVehicleDataFile(File file) throws Exception {
		
		Logger.get().info("Loading vehicleData-file");

		if(file == null || !file.exists()) {
			Logger.get().fatal("Error loading vehicles: Could not find " + file);
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText("Error loading vehicles: Could not find " + file);
			alert.showAndWait();
			System.exit(0);
		}
		
		
		List<Vehicle> vehiclesOut = new ArrayList<Vehicle>();
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		
		Document doc = builder.parse(file);
		
		Element elementData = doc.getDocumentElement();
		
		Element elementVehicles = null;
		for(int i=0; i<elementData.getElementsByTagName("vehicles").getLength(); i++) {
			Node nodeVehicle = elementData.getElementsByTagName("vehicles").item(i);
			if(nodeVehicle.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			elementVehicles = (Element)elementData.getElementsByTagName("vehicles").item(i);
			break;
		}
		
		NodeList listVehicles = elementVehicles.getChildNodes();
		for(int i=0; i<listVehicles.getLength(); i++) {
			Node nodeVehicle = listVehicles.item(i);
			if(nodeVehicle.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			Element elementVehicle = (Element)listVehicles.item(i);
			
			Vehicle vehicle = new Vehicle();
			vehiclesOut.add(vehicle);
			vehicle.name = elementVehicle.getTagName();
			vehicle.fovOut = Float.parseFloat(elementVehicle.getAttribute("fovOut"));
			vehicle.fovIn = Float.parseFloat(elementVehicle.getAttribute("fovIn"));

			Element elementWeaponsRoot = null;
			for(int j=0; j<elementVehicle.getElementsByTagName("weapons").getLength(); j++) {
				Node nodeWeapon = elementVehicle.getElementsByTagName("weapons").item(j);
				if(nodeWeapon.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				elementWeaponsRoot = (Element)elementVehicle.getElementsByTagName("weapons").item(j);
			}
			
			NodeList listWeapons = elementWeaponsRoot.getChildNodes();
			for(int j=0; j<listWeapons.getLength(); j++) {
				Node nodeWeapon = listWeapons.item(j);
				if(nodeWeapon.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				Element elementWeapon = (Element)listWeapons.item(j);
			
				
				Weapon weapon = new Weapon();
				weapon.name = elementWeapon.getTagName();
				weapon.triggerGroup = elementWeapon.getAttribute("triggerGroup");
				vehicle.weaponsList.add(weapon);
				
				Element elementAmmoRoot = null;
				NodeList listAmmoRoot = elementWeapon.getElementsByTagName("ammo");
				for(int k=0; k<listAmmoRoot.getLength(); k++) {
					Node nodeAmmoRoot = listAmmoRoot.item(k);
					if(nodeAmmoRoot.getNodeType() != Node.ELEMENT_NODE) {
						continue;
					}
					elementAmmoRoot = (Element)listAmmoRoot.item(k);
					break;
				}
				
				NodeList listAmmo = elementAmmoRoot.getChildNodes();
				for(int k=0; k<listAmmo.getLength(); k++) {
					Node nodeAmmo = listAmmo.item(k);
					if(nodeAmmo.getNodeType() != Node.ELEMENT_NODE) {
						continue;
					}
					Element elementAmmo = (Element)listAmmo.item(k);
					
					Ammo ammo = new Ammo();
					weapon.ammo.add(ammo);
					ammo.type = elementAmmo.getAttribute("type");
					ammo.speed = Integer.parseInt(elementAmmo.getAttribute("speed"));
					ammo.name = elementAmmo.getTagName();
					
				}
				
			}
				
		}
		
		return vehiclesOut;
	}

	
	

	
	
	public static CalibrationData loadExternalCalibFile(File file) {
		
		Logger.get().info("Loading calibration-file (ext)");
		
		if(file == null || !file.exists()) {
			Logger.get().error("Error loading file: " + file);
			return null;
		}
		
		CalibrationData data = new CalibrationData();
		String vehicleName = "-";
		
		try {
		
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file);
			Element root = doc.getDocumentElement();

			if(XMLUtils.getChildren(root).size() == 0) {
				return null;
			}
			
			Element elementVehicle = XMLUtils.getChildren(root).get(0);
			vehicleName = elementVehicle.getTagName();

			// ammo
			Element elementAmmoGroup = XMLUtils.getElementByTagName(elementVehicle, "ammo");
			
			for(Element elementAmmo : XMLUtils.getChildren(elementAmmoGroup)) {

				// load from file
				CalibrationAmmoData ammoData = new CalibrationAmmoData();
				ammoData.imgName = elementAmmo.getAttribute("imageName");
				ammoData.zoomedIn = elementAmmo.getAttribute("zoomedIn").equalsIgnoreCase("true");
				
				String[] strMarkerCenter = elementAmmo.getAttribute("markerCenter").split(",");
				ammoData.markerCenter.set(Integer.parseInt(strMarkerCenter[0].trim()), Integer.parseInt(strMarkerCenter[1].trim()));
				
				Element elementRangeMarkers = XMLUtils.getElementByTagName(elementAmmo, "markerRanges");
				
				for(int i=0; i<elementRangeMarkers.getAttributes().getLength(); i++) {
					String[] strMarker = elementRangeMarkers.getAttributes().item(i).getNodeValue().split(",");
					ammoData.markerRanges.add(new Vector2i(Integer.parseInt(strMarker[0].trim()), Integer.parseInt(strMarker[1].trim())));
				}

				// load ammo from vehicle db
				Vehicle vehicle = Database.getVehicleByName(vehicleName);
				for(Weapon w : vehicle.weaponsList) {
					for(Ammo a : w.ammo) {
						if(a.name.equals(elementAmmo.getTagName())) {
							ammoData.ammo = a;
							break;
						}
					}
				}
				
				data.ammoData.add(ammoData);
				
			}
			
			
			// images
			Element elementImageGroup = XMLUtils.getElementByTagName(elementVehicle, "images");
			for(Element elementImage : XMLUtils.getChildren(elementImageGroup)) {
				String imgName = elementImage.getTagName();
				BufferedImage img = decodeImage(elementImage.getAttribute("encodedData"));
				data.images.put(imgName, img);
			}
			
			
			// vehicle
			data.vehicle = Database.getVehicleByName(vehicleName);
				
		} catch (ParserConfigurationException e) {
			Logger.get().error(e);
		} catch (SAXException e) {
			Logger.get().error(e);
		} catch (IOException e) {
			Logger.get().error(e);
		}
		
		return data;
	}
	
	
	
	
	public static CalibrationData loadInternalCalibFile(String name) {
		if(name.isEmpty()) {
			return null;
		}
		// load data
		return null;
	}
	
	
	
	
	
	private static BufferedImage decodeImage(String encodedImage) throws IOException {
		byte[] bytes = Base64.getDecoder().decode(encodedImage);
		return ImageIO.read(new ByteArrayInputStream(bytes));
	}
	
	
	
	public static SightData loadSight(File file, CalibrationData dataCalib) {
		
		Logger.get().info("Loading sight-file");
		if(file == null || !file.exists()) {
			Logger.get().error("Error loading file: " + file);
			return new SightData();
		}
		
		SightData dataSight = new SightData();
		
		dataSight.hrMils.clear();
		dataSight.hrMajors.clear();
		
		dataSight.brIndicators.bDists.clear();
		dataSight.brIndicators.bMajors.clear();
		dataSight.brIndicators.bExtensions.clear();
		dataSight.brIndicators.bTextOffsets.clear();
		
		Block rootBlock = BLKSightParser.parse(file);
		
		for(BlockElement e : rootBlock.elements) {
			
			switch(e.name) {
			
				// GENERAL
				case "thousandth": {
					dataSight.gnrThousandth = Thousandth.get(((ParamText)e).text);
					break;
				}
				case "fontSizeMult": {
					dataSight.gnrFontScale = ((ParamFloat)e).value;
					break;
				}
				case "lineSizeMult": {
					dataSight.gnrLineSize = ((ParamFloat)e).value;
					break;
				}
				case "drawCentralLineVert": {
					dataSight.gnrDrawCentralVertLine = ((ParamBool)e).value;
					break;
				}
				case "drawCentralLineHorz": {
					dataSight.gnrDrawCentralHorzLine = ((ParamBool)e).value;
					break;
				}
				
				
				// RANGEFINDER
				case "rangefinderHorizontalOffset": {
					dataSight.rfOffset.x = ((ParamFloat)e).value;
					break;
				}
				case "rangefinderVerticalOffset": {
					dataSight.rfOffset.y = ((ParamFloat)e).value;
					break;
				}
				case "rangefinderProgressBarColor1": {
					ParamColor vec = (ParamColor)e;
					dataSight.rfColor1 = new Color(vec.value.x/255.0, vec.value.y/255.0, vec.value.z/255.0, vec.value.w/255.0);
					break;
				}
				case "rangefinderProgressBarColor2": {
					ParamColor vec = (ParamColor)e;
					dataSight.rfColor2 = new Color(vec.value.x/255.0, vec.value.y/255.0, vec.value.z/255.0, vec.value.w/255.0);
					break;
				}
				case "rangefinderTextScale": {
					dataSight.rfTextScale = ((ParamFloat)e).value;
					break;
				}
				case "rangefinderUseThousandth": {
					dataSight.rfUseThousandth = ((ParamBool)e).value;
					break;
				}
				
				
				// HORZ RANGE INDICATORS
				case "crosshairHorVertSize": {
					dataSight.hrSizeMajor = ((ParamVec2)e).value.x;
					dataSight.hrSizeMinor = ((ParamVec2)e).value.y;
					break;
				}
				case "crosshair_hor_ranges": {
					
					for(BlockElement ehr : ((Block)e).elements) {
						switch(ehr.name) {
							case "range": {
								ParamVec2 p = (ParamVec2)ehr;
								dataSight.hrMils.add(p.value.getIntX());
								dataSight.hrMajors.add(p.value.getIntY()!=0);
								break;
							}
						}
					}
				}
				
				// BALLISTIC RANGE INDICATORS
				case "crosshair_distances": {
					
					for(BlockElement ebr : ((Block)e).elements) {
						
						switch(ebr.name) {
							case "distance": {
								
								if(ebr instanceof ParamVec2) {
									ParamVec2 p = (ParamVec2)ebr;
									dataSight.brIndicators.bDists.add(p.value.getIntX());
									dataSight.brIndicators.bMajors.add(p.value.getIntY()!=0);
									dataSight.brIndicators.bExtensions.add(0.0);
									dataSight.brIndicators.bTextOffsets.add(new Vector2d(0.0,0.0));
									break;
								}
								if(ebr instanceof ParamVec3) {
									ParamVec3 p = (ParamVec3)ebr;
									dataSight.brIndicators.bDists.add(p.value.getIntX());
									dataSight.brIndicators.bMajors.add(p.value.getIntY()!=0);
									dataSight.brIndicators.bExtensions.add(p.value.getDoubleZ());
									dataSight.brIndicators.bTextOffsets.add(new Vector2d(0.0,0.0));
									break;
								}
								if(ebr instanceof Block) {
									
									for(BlockElement ed : ((Block)ebr).elements) {
										switch(ed.name) {
											case "distance" : {
												if(ed instanceof ParamVec3) {
													ParamVec3 p = (ParamVec3)ed;
													dataSight.brIndicators.bDists.add(p.value.getIntX());
													dataSight.brIndicators.bMajors.add(p.value.getIntY()!=0);
													dataSight.brIndicators.bExtensions.add(p.value.getDoubleZ());
													break;
												}
												break;
											}
											case "textPos" : {
												if(ed instanceof ParamVec2) {
													dataSight.brIndicators.bTextOffsets.add(new Vector2d(((ParamVec2)ed).value));
													break;
												}
												break;
											}
										}
									}
									
									
								}
								break;
								
							}
						}
						
					}
					break;
				}
				case "drawUpward": {
					dataSight.brIndicators.bDrawUpward = ((ParamBool)e).value;
					break;
				}
				case "distancePos": {
					dataSight.brIndicators.bMainPos = new Vector2d(((ParamVec2)e).value);
					break;
				}
				case "move": {
					dataSight.brIndicators.bMove = ((ParamBool)e).value;
					break;
				}
				case "radial": {
					dataSight.brIndicators.bScaleMode = ((ParamBool)e).value ? ScaleMode.RADIAL : ScaleMode.VERTICAL;
					break;
				}
				case "circleMode": {
					dataSight.brIndicators.bCircleMode = ((ParamBool)e).value;
					break;
				}
				case "crosshairDistHorSizeMain": {
					dataSight.brIndicators.bSizeMain = new Vector2d(((ParamVec2)e).value);
					break;
				}
				case "textPos": {
					dataSight.brIndicators.bTextOffset = new Vector2d(((ParamVec2)e).value);
					break;
				}
				case "textAlign": {
					if(((ParamInteger)e).value == -1) {
						dataSight.brIndicators.bTextAlign = TextAlign.LEFT;
					}
					if(((ParamInteger)e).value == 0) {
						dataSight.brIndicators.bTextAlign = TextAlign.CENTER;
					}
					if(((ParamInteger)e).value == +1) {
						dataSight.brIndicators.bTextAlign = TextAlign.RIGHT;
					}
					break;
				}
				case "textShift": {
					dataSight.brIndicators.bTextShift = ((ParamFloat)e).value;
					break;
				}
				case "drawAdditionalLines": {
					dataSight.brIndicators.bDrawCenteredLines = ((ParamBool)e).value;
					break;
				}
				case "crosshairDistHorSizeAdditional": {
					dataSight.brIndicators.bSizeCentered = new Vector2d(((ParamVec2)e).value);
					break;
				}
				case "radialStretch": {
					dataSight.brIndicators.bRadialStretch = ((ParamFloat)e).value;
					break;
				}
				case "radialAngle": {
					dataSight.brIndicators.bRadialAngle = ((ParamFloat)e).value;
					break;
				}
				case "radialRadius": {
					dataSight.brIndicators.bRadialRadius = ((ParamVec2)e).value.x;
					dataSight.brIndicators.bRadiusUseMils = MathUtils.isNearlyEqual(((ParamVec2)e).value.y, 0.0) ? false : true;
					break;
				}
				case "drawDistanceCorrection": {
					dataSight.brIndicators.bDrawCorrection = ((ParamBool)e).value;
					break;
				}
				case "distanceCorrectionPos": {
					dataSight.brIndicators.bCorrectionPos = new Vector2d(((ParamVec2)e).value);
					break;
				}
				
				
				// SHELL BALLISTICS BLOCK
				case "ballistics": {
					for(BlockElement eBullets : ((Block)e).elements) {
						switch(eBullets.name) {
							case "bullet" : {

								BallisticsBlock ballistics = new BallisticsBlock(false, "block_"+(dataSight.shellBlocks.size()+1));
								ballistics.bDists.clear();
								ballistics.bMajors.clear();
								ballistics.bExtensions.clear();
								ballistics.bTextOffsets.clear();
								
								
								for(BlockElement eBullet : ((Block)eBullets).elements) {
									
									switch(eBullet.name) {
									
										case "bulletType" : {
											ballistics.bBulletType = ((ParamText)eBullet).text;
											break;
										}
										case "speed" : {
											ballistics.bBulletSpeed = (int)((ParamFloat)eBullet).value;
											break;
										}
										case "triggerGroup": {
											ballistics.bTriggerGroup = TriggerGroup.get( ((ParamText)eBullet).text );
											break;
										}
										
										case "crosshair_distances": {
											
											for(BlockElement ebr : ((Block)eBullet).elements) {
												
												switch(ebr.name) {
													case "distance": {
														
														if(ebr instanceof ParamVec2) {
															ParamVec2 p = (ParamVec2)ebr;
															ballistics.bDists.add(p.value.getIntX());
															ballistics.bMajors.add(p.value.getIntY()!=0);
															ballistics.bExtensions.add(0.0);
															ballistics.bTextOffsets.add(new Vector2d(0.0,0.0));
															break;
														}
														if(ebr instanceof ParamVec3) {
															ParamVec3 p = (ParamVec3)ebr;
															ballistics.bDists.add(p.value.getIntX());
															ballistics.bMajors.add(p.value.getIntY()!=0);
															ballistics.bExtensions.add(p.value.getDoubleZ());
															ballistics.bTextOffsets.add(new Vector2d(0.0,0.0));
															break;
														}
														if(ebr instanceof Block) {
															
															for(BlockElement ed : ((Block)ebr).elements) {
																switch(ed.name) {
																	case "distance" : {
																		if(ed instanceof ParamVec3) {
																			ParamVec3 p = (ParamVec3)ed;
																			ballistics.bDists.add(p.value.getIntX());
																			ballistics.bMajors.add(p.value.getIntY()!=0);
																			ballistics.bExtensions.add(p.value.getDoubleZ());
																			break;
																		}
																		break;
																	}
																	case "textPos" : {
																		if(ed instanceof ParamVec2) {
																			ballistics.bTextOffsets.add(new Vector2d(((ParamVec2)ed).value));
																			break;
																		}
																		break;
																	}
																}
															}
															
															
														}
														break;
														
													}
												}
												
											}
											break;
										}
										case "drawUpward": {
											ballistics.bDrawUpward = ((ParamBool)eBullet).value;
											break;
										}
										case "distancePos": {
											ballistics.bMainPos = new Vector2d(((ParamVec2)eBullet).value);
											break;
										}
										case "move": {
											ballistics.bMove = ((ParamBool)eBullet).value;
											break;
										}
										case "radial": {
											ballistics.bScaleMode = ((ParamBool)eBullet).value ? ScaleMode.RADIAL : ScaleMode.VERTICAL;
											break;
										}
										case "circleMode": {
											ballistics.bCircleMode = ((ParamBool)eBullet).value;
											break;
										}
										case "crosshairDistHorSizeMain": {
											ballistics.bSizeMain = new Vector2d(((ParamVec2)eBullet).value);
											break;
										}
										case "textPos": {
											ballistics.bTextOffset = new Vector2d(((ParamVec2)eBullet).value);
											break;
										}
										case "textAlign": {
											if(((ParamInteger)eBullet).value == -1) {
												ballistics.bTextAlign = TextAlign.LEFT;
											}
											if(((ParamInteger)eBullet).value == 0) {
												ballistics.bTextAlign = TextAlign.CENTER;
											}
											if(((ParamInteger)eBullet).value == +1) {
												ballistics.bTextAlign = TextAlign.RIGHT;
											}
											break;
										}
										case "textShift": {
											ballistics.bTextShift = ((ParamFloat)eBullet).value;
											break;
										}
										case "drawAdditionalLines": {
											ballistics.bDrawCenteredLines = ((ParamBool)eBullet).value;
											break;
										}
										case "crosshairDistHorSizeAdditional": {
											ballistics.bSizeCentered = new Vector2d(((ParamVec2)eBullet).value);
											break;
										}
										case "radialStretch": {
											ballistics.bRadialStretch = ((ParamFloat)eBullet).value;
											break;
										}
										case "radialAngle": {
											ballistics.bRadialAngle = ((ParamFloat)eBullet).value;
											break;
										}
										case "radialRadius": {
											ballistics.bRadialRadius = ((ParamVec2)eBullet).value.x;
											ballistics.bRadiusUseMils = MathUtils.isNearlyEqual(((ParamVec2)eBullet).value.y, 0.0) ? false : true;
											break;
										}
										
									}

								}

								List<Ammo> ammoCanidates = Database.getAmmo(dataCalib.vehicle.name, ballistics.bBulletType, ballistics.bBulletSpeed);
								select: for(Ammo ammo : ammoCanidates) {
									for(CalibrationAmmoData dataAmmo : dataCalib.ammoData) {
										if(dataAmmo.ammo.name.equalsIgnoreCase(ammo.name)) {
											ballistics.bBulletName = ammo.name;
											break select;
										}
									}
								}
								dataSight.shellBlocks.put(ballistics.name, ballistics);
								break;
							}
						}
					}
					break;
				}
				
				// LINES
				case "drawLines": {
					
					for(BlockElement eLines : ((Block)e).elements) {
						switch(eLines.name) {
							case "line": {
								LineObject objLine = new LineObject();
								objLine.name = "line_" + (dataSight.objects.size()+1);
								for(BlockElement eLine : ((Block)eLines).elements) {
									switch(eLine.name) {
										case "thousandth": {
											objLine.cmnUseThousandth = ((ParamBool)eLine).value;
											break;
										}
										case "move": {
											objLine.cmnMovement = ((ParamBool)eLine).value ? Movement.MOVE : objLine.cmnMovement;
											break;
										}
										case "moveRadial": {
											objLine.cmnMovement = ((ParamBool)eLine).value ? Movement.MOVE_RADIAL : objLine.cmnMovement;
											break;
										}
										case "radialAngle": {
											objLine.cmnAngle = ((ParamFloat)eLine).value;
											break;
										}
										case "radialCenter": {
											objLine.cmnRadCenter = new Vector2d(((ParamVec2)eLine).value);
											break;
										}
										case "center": {
											objLine.cmnCenter = new Vector2d(((ParamVec2)eLine).value);
											break;
										}
										case "radialMoveSpeed": {
											objLine.cmnSpeed = ((ParamFloat)eLine).value;
											break;
										}
										case "line": {
											objLine.start = new Vector2d(((ParamVec4)eLine).value.x, ((ParamVec4)eLine).value.y);
											objLine.end = new Vector2d(((ParamVec4)eLine).value.z, ((ParamVec4)eLine).value.w);
											break;
										}
									}
								}
								dataSight.objects.put(objLine.name, objLine);
								break;
							}
						}
					}
					
					break;
				}
				
				
				// TEXT
				case "drawTexts": {
					
					for(BlockElement eTexts : ((Block)e).elements) {
						switch(eTexts.name) {
							case "text": {
								TextObject objText = new TextObject();
								objText.name = "text_" + (dataSight.objects.size()+1);
								for(BlockElement eText : ((Block)eTexts).elements) {
									switch(eText.name) {
										case "thousandth": {
											objText.cmnUseThousandth = ((ParamBool)eText).value;
											break;
										}
										case "move": {
											objText.cmnMovement = ((ParamBool)eText).value ? Movement.MOVE : objText.cmnMovement;
											break;
										}
										case "moveRadial": {
											objText.cmnMovement = ((ParamBool)eText).value ? Movement.MOVE_RADIAL : objText.cmnMovement;
											break;
										}
										case "radialAngle": {
											objText.cmnAngle = ((ParamFloat)eText).value;
											break;
										}
										case "radialCenter": {
											objText.cmnRadCenter = new Vector2d(((ParamVec2)eText).value);
											break;
										}
										case "center": {
											objText.cmnCenter = new Vector2d(((ParamVec2)eText).value);
											break;
										}
										case "radialMoveSpeed": {
											objText.cmnSpeed = ((ParamFloat)eText).value;
											break;
										}
										case "text": {
											objText.text = ((ParamText)eText).text;
											break;
										}
										case "pos": {
											objText.pos = new Vector2d(((ParamVec2)eText).value);
											break;
										}
										case "align": {
											if(((ParamInteger)eText).value == -1) {
												objText.align = TextAlign.LEFT;
											}
											if(((ParamInteger)eText).value == 0) {
												objText.align = TextAlign.CENTER;
											}
											if(((ParamInteger)eText).value == +1) {
												objText.align = TextAlign.RIGHT;
											}
											break;
										}
										case "size": {
											objText.size = ((ParamFloat)eText).value;
											break;
										}
									}
								}
								dataSight.objects.put(objText.name, objText);
								break;
							}
						}
					}
					
					break;
				}
				
				
				
				
				// CIRCLES
				case "drawCircles": {
					
					for(BlockElement eCircles : ((Block)e).elements) {
						switch(eCircles.name) {
							case "circle": {
								CircleObject objCircle = new CircleObject();
								objCircle.name = "circle_" + (dataSight.objects.size()+1);
								for(BlockElement eCircle : ((Block)eCircles).elements) {
									switch(eCircle.name) {
										case "thousandth": {
											objCircle.cmnUseThousandth = ((ParamBool)eCircle).value;
											break;
										}
										case "move": {
											objCircle.cmnMovement = ((ParamBool)eCircle).value ? Movement.MOVE : objCircle.cmnMovement;
											break;
										}
										case "moveRadial": {
											objCircle.cmnMovement = ((ParamBool)eCircle).value ? Movement.MOVE_RADIAL : objCircle.cmnMovement;
											break;
										}
										case "radialAngle": {
											objCircle.cmnAngle = ((ParamFloat)eCircle).value;
											break;
										}
										case "radialCenter": {
											objCircle.cmnRadCenter = new Vector2d(((ParamVec2)eCircle).value);
											break;
										}
										case "center": {
											objCircle.cmnCenter = new Vector2d(((ParamVec2)eCircle).value);
											break;
										}
										case "radialMoveSpeed": {
											objCircle.cmnSpeed = ((ParamFloat)eCircle).value;
											break;
										}
										case "segment": {
											objCircle.segment = new Vector2d(((ParamVec2)eCircle).value);
											break;
										}
										case "pos": {
											objCircle.pos = new Vector2d(((ParamVec2)eCircle).value);
											break;
										}
										case "diameter": {
											objCircle.diameter = ((ParamFloat)eCircle).value;
											break;
										}
										case "size": {
											objCircle.size = ((ParamFloat)eCircle).value;
											break;
										}
									}
								}
								dataSight.objects.put(objCircle.name, objCircle);
								break;
							}
						}
					}
					
					break;
				}
				
				
				
				// QUAD
				case "drawQuads": {
					
					for(BlockElement eQuads : ((Block)e).elements) {
						switch(eQuads.name) {
							case "quad": {
								QuadObject objQuad = new QuadObject();
								objQuad.name = "quad_" + (dataSight.objects.size()+1);
								for(BlockElement eQuad : ((Block)eQuads).elements) {
									switch(eQuad.name) {
										case "thousandth": {
											objQuad.cmnUseThousandth = ((ParamBool)eQuad).value;
											break;
										}
										case "move": {
											objQuad.cmnMovement = ((ParamBool)eQuad).value ? Movement.MOVE : objQuad.cmnMovement;
											break;
										}
										case "moveRadial": {
											objQuad.cmnMovement = ((ParamBool)eQuad).value ? Movement.MOVE_RADIAL : objQuad.cmnMovement;
											break;
										}
										case "radialAngle": {
											objQuad.cmnAngle = ((ParamFloat)eQuad).value;
											break;
										}
										case "radialCenter": {
											objQuad.cmnRadCenter = new Vector2d(((ParamVec2)eQuad).value);
											break;
										}
										case "center": {
											objQuad.cmnCenter = new Vector2d(((ParamVec2)eQuad).value);
											break;
										}
										case "radialMoveSpeed": {
											objQuad.cmnSpeed = ((ParamFloat)eQuad).value;
											break;
										}
										case "tl": {
											objQuad.pos1 = new Vector2d(((ParamVec2)eQuad).value);
											break;
										}
										case "tr": {
											objQuad.pos2 = new Vector2d(((ParamVec2)eQuad).value);
											break;
										}
										case "br": {
											objQuad.pos3 = new Vector2d(((ParamVec2)eQuad).value);
											break;
										}
										case "bl": {
											objQuad.pos4 = new Vector2d(((ParamVec2)eQuad).value);
											break;
										}
									}
								}
								dataSight.objects.put(objQuad.name, objQuad);
								break;
							}
						}
					}
					
					break;
				}
				
				
			}
			
		}
		
		
		Logger.get().info("Sight file loaded");
		return dataSight;
	}
	
	
}






















