package com.ruegnerlukas.wtsights.data;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
import com.ruegnerlukas.wtsights.data.sight.BIndicator;
import com.ruegnerlukas.wtsights.data.sight.HIndicator;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.data.sight.elements.*;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCustomObject.Movement;
import com.ruegnerlukas.wtsights.data.sightfile.BLKSightParser;
import com.ruegnerlukas.wtsights.data.sightfile.Block;
import com.ruegnerlukas.wtsights.data.sightfile.BlockElement;
import com.ruegnerlukas.wtsights.data.sightfile.ParamBool;
import com.ruegnerlukas.wtsights.data.sightfile.ParamColor;
import com.ruegnerlukas.wtsights.data.sightfile.ParamFloat;
import com.ruegnerlukas.wtsights.data.sightfile.ParamInteger;
import com.ruegnerlukas.wtsights.data.sightfile.ParamText;
import com.ruegnerlukas.wtsights.data.sightfile.ParamVec2;
import com.ruegnerlukas.wtsights.data.sightfile.ParamVec3;
import com.ruegnerlukas.wtsights.data.sightfile.ParamVec4;
import com.ruegnerlukas.wtsights.data.vehicle.Ammo;
import com.ruegnerlukas.wtsights.data.vehicle.Vehicle;
import com.ruegnerlukas.wtsights.data.vehicle.Weapon;
import com.ruegnerlukas.wtutils.SightUtils.ScaleMode;
import com.ruegnerlukas.wtutils.SightUtils.TextAlign;
import com.ruegnerlukas.wtutils.SightUtils.Thousandth;
import com.ruegnerlukas.wtutils.SightUtils.TriggerGroup;
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
			vehicle.name = elementVehicle.getTagName();
			vehicle.fovOut = Float.parseFloat(elementVehicle.getAttribute("fovOut"));
			vehicle.fovIn = Float.parseFloat(elementVehicle.getAttribute("fovIn"));
			vehicle.fovSight = Float.parseFloat(elementVehicle.getAttribute("fovSight"));
			if( !(vehicle.name.contains("tutorial") || vehicle.name.contains("dummy")) ) {
				vehiclesOut.add(vehicle);
			}

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
			return new SightData(true);
		}
		
		SightData dataSight = new SightData(true);
		ElementCentralVertLine centralVertLine = (ElementCentralVertLine)dataSight.getElements(ElementType.CENTRAL_VERT_LINE).get(0);
		ElementCentralHorzLine centralHorzLine = (ElementCentralHorzLine)dataSight.getElements(ElementType.CENTRAL_HORZ_LINE).get(0);
		ElementRangefinder rangefinder = (ElementRangefinder)dataSight.getElements(ElementType.RANGEFINDER).get(0);
		ElementHorzRangeIndicators horzRange = (ElementHorzRangeIndicators)dataSight.getElements(ElementType.HORZ_RANGE_INDICATORS).get(0);
		ElementBallRangeIndicator ballRange = (ElementBallRangeIndicator)dataSight.getElements(ElementType.BALLISTIC_RANGE_INDICATORS).get(0);

		horzRange.indicators.clear();
		ballRange.indicators.clear();
		
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
					centralVertLine.drawCentralVertLine = ((ParamBool)e).value;
					break;
				}
				case "drawCentralLineHorz": {
					centralHorzLine.drawCentralHorzLine = ((ParamBool)e).value;
					break;
				}
				
				// RANGEFINDER
				case "rangefinderHorizontalOffset": {
					rangefinder.position.x = ((ParamFloat)e).value;
					break;
				}
				case "rangefinderVerticalOffset": {
					rangefinder.position.y = ((ParamFloat)e).value;
					break;
				}
				case "rangefinderProgressBarColor1": {
					ParamColor vec = (ParamColor)e;
					vec.value.clampComponents(0, 255f).div(255f);
					rangefinder.color1 = new Color(vec.value.x, vec.value.y, vec.value.z, vec.value.w);
					break;
				}
				case "rangefinderProgressBarColor2": {
					ParamColor vec = (ParamColor)e;
					vec.value.clampComponents(0, 255f).div(255f);
					rangefinder.color2 = new Color(vec.value.x, vec.value.y, vec.value.z, vec.value.w);
					break;
				}
				case "rangefinderTextScale": {
					rangefinder.textScale = ((ParamFloat)e).value;
					break;
				}
				case "rangefinderUseThousandth": {
					rangefinder.useThousandth = ((ParamBool)e).value;
					break;
				}
				
				
				// HORZ RANGE INDICATORS
				case "crosshairHorVertSize": {
					horzRange.sizeMajor = ((ParamVec2)e).value.x;
					horzRange.sizeMinor = ((ParamVec2)e).value.y;
					break;
				}
				case "crosshair_hor_ranges": {
					for(BlockElement ehr : ((Block)e).elements) {
						switch(ehr.name) {
							case "range": {
								ParamVec2 p = (ParamVec2)ehr;
								horzRange.indicators.add(new HIndicator(p.value.getIntX(), p.value.getIntY()!=0));
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
									ballRange.indicators.add(new BIndicator(p.value.getIntX(), p.value.getIntY()!=0, 0, 0, 0));
									break;
								}
								if(ebr instanceof ParamVec3) {
									ParamVec3 p = (ParamVec3)ebr;
									ballRange.indicators.add(new BIndicator(p.value.getIntX(), p.value.getIntY()!=0, p.value.getFloatZ(), 0, 0));
									break;
								}
								if(ebr instanceof Block) {
									
									BIndicator indicator = new BIndicator(0, false, 0, 0, 0);
									ballRange.indicators.add(indicator);
									
									for(BlockElement ed : ((Block)ebr).elements) {
										
										switch(ed.name) {
											case "distance" : {
												if(ed instanceof ParamVec3) {
													ParamVec3 p = (ParamVec3)ed;
													indicator.setDistance(p.value.getIntX());
													indicator.setMajor(p.value.getIntY()!=0);
													indicator.setExtend(p.value.getFloatZ());
													break;
												}
												break;
											}
											case "textPos" : {
												if(ed instanceof ParamVec2) {
													indicator.setTextX(((ParamVec2)ed).value.x);
													indicator.setTextY(((ParamVec2)ed).value.y);
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
					ballRange.drawUpward = ((ParamBool)e).value;
					break;
				}
				case "distancePos": {
					ballRange.position = new Vector2d(((ParamVec2)e).value);
					break;
				}
				case "move": {
					ballRange.move = ((ParamBool)e).value;
					break;
				}
				case "radial": {
					ballRange.scaleMode = ((ParamBool)e).value ? ScaleMode.RADIAL : ScaleMode.VERTICAL;
					break;
				}
				case "circleMode": {
					ballRange.circleMode = ((ParamBool)e).value;
					break;
				}
				case "crosshairDistHorSizeMain": {
					ballRange.size = new Vector2d(((ParamVec2)e).value);
					break;
				}
				case "textPos": {
					ballRange.textPos = new Vector2d(((ParamVec2)e).value);
					break;
				}
				case "textAlign": {
					if(((ParamInteger)e).value == -1) {
						ballRange.textAlign = TextAlign.LEFT;
					}
					if(((ParamInteger)e).value == 0) {
						ballRange.textAlign = TextAlign.CENTER;
					}
					if(((ParamInteger)e).value == +1) {
						ballRange.textAlign = TextAlign.RIGHT;
					}
					break;
				}
				case "textShift": {
					ballRange.textShift = ((ParamFloat)e).value;
					break;
				}
				case "drawAdditionalLines": {
					ballRange.drawAddLines = ((ParamBool)e).value;
					break;
				}
				case "crosshairDistHorSizeAdditional": {
					ballRange.sizeAddLine = new Vector2d(((ParamVec2)e).value);
					break;
				}
				case "radialStretch": {
					ballRange.radialStretch = ((ParamFloat)e).value;
					break;
				}
				case "radialAngle": {
					ballRange.radialAngle = ((ParamFloat)e).value;
					break;
				}
				case "radialRadius": {
					ballRange.radialRadius = ((ParamVec2)e).value.x;
					ballRange.radiusUseMils = MathUtils.isNearlyEqual(((ParamVec2)e).value.y, 0.0) ? false : true;
					break;
				}
				case "drawDistanceCorrection": {
					ballRange.drawCorrLabel = ((ParamBool)e).value;
					break;
				}
				case "distanceCorrectionPos": {
					ballRange.posCorrLabel = new Vector2d(((ParamVec2)e).value);
					break;
				}
				
				
				// SHELL BALLISTICS BLOCK
				case "ballistics": {
					for(BlockElement eBullets : ((Block)e).elements) {
						switch(eBullets.name) {
							case "bullet" : {

								String name = null;
								if(eBullets.metadata != null) {
									name = eBullets.metadata.substring(0, eBullets.metadata.lastIndexOf('('));
								}
								
								ElementShellBlock shellBlock = new ElementShellBlock(name == null ? "block_"+(dataSight.getElements(ElementType.SHELL_BALLISTICS_BLOCK).size()+1) : name);
								shellBlock.indicators.clear();
								
								String bulletType = "";
								int bulletSpeed = 0;
								
								for(BlockElement eBullet : ((Block)eBullets).elements) {
									
									switch(eBullet.name) {
									
										case "bulletType" : {
											bulletType = ((ParamText)eBullet).text;
											break;
										}
										case "speed" : {
											bulletSpeed = (int)((ParamFloat)eBullet).value;
											break;
										}
										case "triggerGroup": {
											shellBlock.triggerGroup = TriggerGroup.get( ((ParamText)eBullet).text );
											break;
										}
										
										case "crosshair_distances": {
											
											for(BlockElement ebr : ((Block)eBullet).elements) {
												
												switch(ebr.name) {
													case "distance": {
														
														if(ebr instanceof ParamVec2) {
															ParamVec2 p = (ParamVec2)ebr;
															shellBlock.indicators.add(new BIndicator(p.value.getIntX(), p.value.getIntY()!=0, 0, 0, 0));
															break;
														}
														if(ebr instanceof ParamVec3) {
															ParamVec3 p = (ParamVec3)ebr;
															shellBlock.indicators.add(new BIndicator(p.value.getIntX(), p.value.getIntY()!=0, p.value.getFloatZ(), 0, 0));
															break;
														}
														if(ebr instanceof Block) {
															
															BIndicator indicator = new BIndicator(0, false, 0, 0, 0);
															shellBlock.indicators.add(indicator);
															
															for(BlockElement ed : ((Block)ebr).elements) {
																switch(ed.name) {
																	case "distance" : {
																		if(ed instanceof ParamVec3) {
																			ParamVec3 p = (ParamVec3)ed;
																			indicator.setDistance(p.value.getIntX());
																			indicator.setMajor(p.value.getIntY()!=0);
																			indicator.setExtend(p.value.getFloatZ());
																			break;
																		}
																		break;
																	}
																	case "textPos" : {
																		if(ed instanceof ParamVec2) {
																			indicator.setTextX(((ParamVec2)ed).value.x);
																			indicator.setTextY(((ParamVec2)ed).value.y);
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
											shellBlock.drawUpward = ((ParamBool)eBullet).value;
											break;
										}
										case "distancePos": {
											shellBlock.position = new Vector2d(((ParamVec2)eBullet).value);
											break;
										}
										case "move": {
											shellBlock.move = ((ParamBool)eBullet).value;
											break;
										}
										case "radial": {
											shellBlock.scaleMode = ((ParamBool)eBullet).value ? ScaleMode.RADIAL : ScaleMode.VERTICAL;
											break;
										}
										case "circleMode": {
											shellBlock.circleMode = ((ParamBool)eBullet).value;
											break;
										}
										case "crosshairDistHorSizeMain": {
											shellBlock.size = new Vector2d(((ParamVec2)eBullet).value);
											break;
										}
										case "textPos": {
											shellBlock.textPos = new Vector2d(((ParamVec2)eBullet).value);
											break;
										}
										case "textAlign": {
											if(((ParamInteger)eBullet).value == -1) {
												shellBlock.textAlign = TextAlign.LEFT;
											}
											if(((ParamInteger)eBullet).value == 0) {
												shellBlock.textAlign = TextAlign.CENTER;
											}
											if(((ParamInteger)eBullet).value == +1) {
												shellBlock.textAlign = TextAlign.RIGHT;
											}
											break;
										}
										case "textShift": {
											shellBlock.textShift = ((ParamFloat)eBullet).value;
											break;
										}
										case "drawAdditionalLines": {
											shellBlock.drawAddLines = ((ParamBool)eBullet).value;
											break;
										}
										case "crosshairDistHorSizeAdditional": {
											shellBlock.sizeAddLine = new Vector2d(((ParamVec2)eBullet).value);
											break;
										}
										case "radialStretch": {
											shellBlock.radialStretch = ((ParamFloat)eBullet).value;
											break;
										}
										case "radialAngle": {
											shellBlock.radialAngle = ((ParamFloat)eBullet).value;
											break;
										}
										case "radialRadius": {
											shellBlock.radialRadius = ((ParamVec2)eBullet).value.x;
											shellBlock.radiusUseMils = MathUtils.isNearlyEqual(((ParamVec2)eBullet).value.y, 0.0) ? false : true;
											break;
										}
										
									}

								}

								List<Ammo> ammoCanidates = Database.getAmmo(dataCalib.vehicle.name, bulletType, bulletSpeed);
								select: for(Ammo ammo : ammoCanidates) {
									for(CalibrationAmmoData dataAmmo : dataCalib.ammoData) {
										if(dataAmmo.ammo.name.equalsIgnoreCase(ammo.name)) {
											shellBlock.dataAmmo = dataAmmo;
											break select;
										}
									}
								}
								
								dataSight.addElement(shellBlock);
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
								
								ElementCustomLine objLine = new ElementCustomLine();
								objLine.name = "line_" + (dataSight.getElements(ElementType.CUSTOM_LINE).size()+1);
								if(eLines.metadata != null) {
									objLine.name = eLines.metadata;
								}
								
								for(BlockElement eLine : ((Block)eLines).elements) {
									switch(eLine.name) {
										case "thousandth": {
											objLine.useThousandth = ((ParamBool)eLine).value;
											break;
										}
										case "move": {
											objLine.movement = ((ParamBool)eLine).value ? Movement.MOVE : objLine.movement;
											break;
										}
										case "moveRadial": {
											objLine.movement = ((ParamBool)eLine).value ? Movement.MOVE_RADIAL : objLine.movement;
											break;
										}
										case "radialAngle": {
											objLine.angle = ((ParamFloat)eLine).value;
											break;
										}
										case "radialCenter": {
											objLine.radCenter = new Vector2d(((ParamVec2)eLine).value);
											break;
										}
										case "center": {
											objLine.center = new Vector2d(((ParamVec2)eLine).value);
											break;
										}
										case "radialMoveSpeed": {
											objLine.speed = ((ParamFloat)eLine).value;
											break;
										}
										case "line": {
											objLine.start = new Vector2d(((ParamVec4)eLine).value.x, ((ParamVec4)eLine).value.y);
											objLine.end = new Vector2d(((ParamVec4)eLine).value.z, ((ParamVec4)eLine).value.w);
											break;
										}
									}
								}
								dataSight.addElement(objLine);
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
								
								ElementCustomText objText = new ElementCustomText();
								objText.name = "text_" + (dataSight.getElements(ElementType.CUSTOM_TEXT).size()+1);
								if(eTexts.metadata != null) {
									objText.name = eTexts.metadata;
								}
								
								for(BlockElement eText : ((Block)eTexts).elements) {
									switch(eText.name) {
										case "thousandth": {
											objText.useThousandth = ((ParamBool)eText).value;
											break;
										}
										case "move": {
											objText.movement = ((ParamBool)eText).value ? Movement.MOVE : objText.movement;
											break;
										}
										case "moveRadial": {
											objText.movement = ((ParamBool)eText).value ? Movement.MOVE_RADIAL : objText.movement;
											break;
										}
										case "radialAngle": {
											objText.angle = ((ParamFloat)eText).value;
											break;
										}
										case "radialCenter": {
											objText.radCenter = new Vector2d(((ParamVec2)eText).value);
											break;
										}
										case "center": {
											objText.center = new Vector2d(((ParamVec2)eText).value);
											break;
										}
										case "radialMoveSpeed": {
											objText.speed = ((ParamFloat)eText).value;
											break;
										}
										case "text": {
											objText.text = ((ParamText)eText).text;
											break;
										}
										case "pos": {
											objText.position = new Vector2d(((ParamVec2)eText).value);
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
								dataSight.addElement(objText);
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
							
								ElementCustomCircle objCircle = new ElementCustomCircle();
								objCircle.name = "circle_" + (dataSight.getElements(ElementType.CUSTOM_CIRCLE).size()+1);
								if(eCircles.metadata != null) {
									objCircle.name = eCircles.metadata;
								}
								
								for(BlockElement eCircle : ((Block)eCircles).elements) {
									switch(eCircle.name) {
										case "thousandth": {
											objCircle.useThousandth = ((ParamBool)eCircle).value;
											break;
										}
										case "move": {
											objCircle.movement = ((ParamBool)eCircle).value ? Movement.MOVE : objCircle.movement;
											break;
										}
										case "moveRadial": {
											objCircle.movement = ((ParamBool)eCircle).value ? Movement.MOVE_RADIAL : objCircle.movement;
											break;
										}
										case "radialAngle": {
											objCircle.angle = ((ParamFloat)eCircle).value;
											break;
										}
										case "radialCenter": {
											objCircle.radCenter = new Vector2d(((ParamVec2)eCircle).value);
											break;
										}
										case "center": {
											objCircle.center = new Vector2d(((ParamVec2)eCircle).value);
											break;
										}
										case "radialMoveSpeed": {
											objCircle.speed = ((ParamFloat)eCircle).value;
											break;
										}
										case "segment": {
											objCircle.segment = new Vector2d(((ParamVec2)eCircle).value);
											break;
										}
										case "pos": {
											objCircle.position = new Vector2d(((ParamVec2)eCircle).value);
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
								dataSight.addElement(objCircle);
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
								
								ElementCustomQuad objQuad = new ElementCustomQuad();
								objQuad.name = "quad_" + (dataSight.getElements(ElementType.CUSTOM_QUAD).size()+1);
								if(eQuads.metadata != null) {
									objQuad.name = eQuads.metadata;
								}
								
								for(BlockElement eQuad : ((Block)eQuads).elements) {
									switch(eQuad.name) {
										case "thousandth": {
											objQuad.useThousandth = ((ParamBool)eQuad).value;
											break;
										}
										case "move": {
											objQuad.movement = ((ParamBool)eQuad).value ? Movement.MOVE : objQuad.movement;
											break;
										}
										case "moveRadial": {
											objQuad.movement = ((ParamBool)eQuad).value ? Movement.MOVE_RADIAL : objQuad.movement;
											break;
										}
										case "radialAngle": {
											objQuad.angle = ((ParamFloat)eQuad).value;
											break;
										}
										case "radialCenter": {
											objQuad.radCenter = new Vector2d(((ParamVec2)eQuad).value);
											break;
										}
										case "center": {
											objQuad.center = new Vector2d(((ParamVec2)eQuad).value);
											break;
										}
										case "radialMoveSpeed": {
											objQuad.speed = ((ParamFloat)eQuad).value;
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
								dataSight.addElement(objQuad);
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






















