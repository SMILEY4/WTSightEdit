package com.ruegnerlukas.wtsights.data.loading;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.Database;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticData;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticElement;
import com.ruegnerlukas.wtsights.data.ballisticdata.Marker;
import com.ruegnerlukas.wtsights.data.ballisticdata.MarkerData;
import com.ruegnerlukas.wtsights.data.ballisticdata.ballfunctions.DefaultBallisticFuntion;
import com.ruegnerlukas.wtsights.data.sight.BIndicator;
import com.ruegnerlukas.wtsights.data.sight.HIndicator;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.data.sight.sightElements.BaseElement;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementBallRangeIndicator;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCentralHorzLine;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCentralVertLine;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCustomCircleFilled;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCustomCircleOutline;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCustomLine;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCustomPolygonFilled;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCustomPolygonOutline;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCustomQuadFilled;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCustomQuadOutline;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCustomText;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementFunnel;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementHorzRangeIndicators;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementRangefinder;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementShellBlock;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.Movement;
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


public class DataLoader_v2 implements IDataLoader {

	
	/**
	 * loads the files with merged data (vehicles+ammoData)
	 * */
	@Override
	public List<Vehicle> loadVehicleDataFile(File file) throws Exception {
		
		Logger.get().info("Loading vehicleData-file: " + file.getAbsolutePath());

		if(file == null || !file.exists()) {
			Logger.get().fatal("Error loading vehicles: Could not find " + file);
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText("Error loading vehicles: Could not find " + file);
			alert.showAndWait();
			return new ArrayList<Vehicle>();
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
				weapon.triggerGroup = TriggerGroup.get(elementWeapon.getAttribute("triggerGroup"));
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
					ammo.parentWeapon = weapon;
					weapon.ammo.add(ammo);
					ammo.type = elementAmmo.getAttribute("type");
					ammo.speed = Integer.parseInt(elementAmmo.getAttribute("speed"));
					ammo.name = elementAmmo.getTagName();
					
				}
				
			}
				
		}
		
		return vehiclesOut;
	}

	
	
	
	@Override
	public BallisticData loadBallisticDataFile(File file) {
		
		Logger.get().info("Loading ballistic-file (ext)");
		
		if(file == null || !file.exists()) {
			Logger.get().error("Error loading file: " + file);
			return null;
		}
		
		BallisticData data = new BallisticData();
		
		try {
		
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file);
			Element root = doc.getDocumentElement();
			if(!root.getTagName().equals("ballisticdata")) {
				Logger.get().error("Could not parse file: File is not a ballistic data file.");
				return null;
			}
			
			if(XMLUtils.getChildren(root).size() == 0) {
				return null;
			}
			
			Element elementVehicle = XMLUtils.getChildren(root).get(0);
			data.vehicle = Database.getVehicleByName(elementVehicle.getTagName());
			data.zoomModOut = elementVehicle.hasAttribute("zoomModOut") ? Double.parseDouble(elementVehicle.getAttribute("zoomModOut")) : 1.0;
			data.zoomModIn = elementVehicle.hasAttribute("zoomModIn") ? Double.parseDouble(elementVehicle.getAttribute("zoomModIn")) : 1.0;

			Element elementElements = XMLUtils.getElementByTagName(elementVehicle, "elements");
			
			int nElements = XMLUtils.getChildren(elementElements).size();
			for(int i=0; i<nElements; i++) {

				Element elementBall = null;
				for(Element e : XMLUtils.getChildren(elementElements)) {
					if(e.getTagName().equals("element_" + i)) {
						elementBall = e;
					}
				}
				if(elementBall == null) {
					continue;
				}
				
				Element elementAmmo = XMLUtils.getElementByTagName(elementBall, "ammunition");
				String strAmmoList = elementAmmo.getTextContent();
				String[] ammoNames = strAmmoList.split(";");
				
				BallisticElement ballElement = new BallisticElement();
				data.elements.add(ballElement);
				for(String name : ammoNames) {
					search: for(Weapon weapon : data.vehicle.weaponsList) {
						for(Ammo ammo : weapon.ammo) {
							if(ammo.name.equals(name)) {
								ballElement.ammunition.add(ammo);
								break search;
							}
						}
					}
				}
				if(ballElement.ammunition.size() == 1) {
					if(ballElement.ammunition.get(0).type.contains("rocket") || ballElement.ammunition.get(0).type.contains("atgm")) {
						ballElement.isRocketElement = true;
					}
				}
				
				Element elementMarkers = XMLUtils.getElementByTagName(elementBall, "markers");
				if(elementMarkers != null) {
					MarkerData dataMarkers = new MarkerData();
					ballElement.markerData = dataMarkers;
					
					if(!elementMarkers.getAttribute("zoomedIn").isEmpty()) {
						data.zoomedIn.put(ballElement, Boolean.parseBoolean(elementMarkers.getAttribute("zoomedIn")));
					}
					dataMarkers.yPosCenter = Double.parseDouble(elementMarkers.getAttribute("ycenter"));
					
					for(Element elementMarker : XMLUtils.getChildren(elementMarkers)) {
						int distMeters = Integer.parseInt(elementMarker.getAttribute("range"));
						double yPos = Double.parseDouble(elementMarker.getAttribute("ypos"));
						Marker marker = new Marker(distMeters, yPos);
						marker.id = dataMarkers.markers.size()+1;
						dataMarkers.markers.add(marker);
					}
					
					ballElement.function = DefaultBallisticFuntion.create(ballElement, data.vehicle, data.isZoomedIn(ballElement));
				}
				
			}
			
			Element elementImages = XMLUtils.getElementByTagName(elementVehicle, "images");
			for(Element elementImg : XMLUtils.getChildren(elementImages)) {
				if(elementImg.getTagName().startsWith("image_element_zoomMod")) {
					continue;
				}
				int elementIndex = Integer.parseInt(elementImg.getTagName().split("_")[2]);
				BallisticElement ballElement = data.elements.get(elementIndex);
				BufferedImage img = decodeImage(elementImg.getAttribute("encodedData"));
				data.imagesBallistic.put(ballElement, img);
			}
			if(XMLUtils.getElementByTagName(elementImages, "image_element_zoomModIn") != null) {
				Element elementImg = XMLUtils.getElementByTagName(elementImages, "image_element_zoomModIn");
				BufferedImage img = decodeImage(elementImg.getAttribute("encodedData"));
				data.imagesZoom.put(true, img);
			}
			if(XMLUtils.getElementByTagName(elementImages, "image_element_zoomModOut") != null) {
				Element elementImg = XMLUtils.getElementByTagName(elementImages, "image_element_zoomModOut");
				BufferedImage img = decodeImage(elementImg.getAttribute("encodedData"));
				data.imagesZoom.put(false, img);
			}
				
		} catch (ParserConfigurationException e) {
			Logger.get().error(e);
		} catch (SAXException e) {
			Logger.get().error(e);
		} catch (IOException e) {
			Logger.get().error(e);
		}
		
		return data;
	}
	
	
	
	
	private BufferedImage decodeImage(String encodedImage) throws IOException {
		byte[] bytes = Base64.getDecoder().decode(encodedImage);
		return ImageIO.read(new ByteArrayInputStream(bytes));
	}
	
	
	
	
	@Override
	public SightData loadSightDataFile(File file, BallisticData dataBall) {
		
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
		
		Map<BaseElement,Map<String,String>> attributeMap = new HashMap<BaseElement,Map<String,String>>();
		Map<String,List<BaseElement>> elements = new HashMap<String,List<BaseElement>>();
		
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
				case "applyCorrectionToGun": {
					dataSight.gnrApplyCorrectionToGun = ((ParamBool)e).value;
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

								
								ElementShellBlock shellBlock = new ElementShellBlock();
								attributeMap.put(shellBlock, parseAttributeString(eBullets.metadata));
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

								List<Ammo> ammoCanidates = Database.getAmmo(dataBall.vehicle.name, bulletType, bulletSpeed);
								select: for(Ammo ammo : ammoCanidates) {
									for(BallisticElement element : dataBall.elements) {
										for(Ammo ammoElement : element.ammunition) {
											if(ammoElement.name.equalsIgnoreCase(ammo.name)) {
												shellBlock.elementBallistic = element;
												break select;
											}
										}
									}
									
								}
								
								addElement(shellBlock, attributeMap.get(shellBlock).get("eid"), elements);
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
								attributeMap.put(objLine, parseAttributeString(eLines.metadata));
								
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
											objLine.autoCenter = false;
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
								
								addElement(objLine, attributeMap.get(objLine).get("eid"), elements);
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
								attributeMap.put(objText, parseAttributeString(eTexts.metadata));
								
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
											objText.autoCenter = false;
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
								
								addElement(objText, attributeMap.get(objText).get("eid"), elements);
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
							
								ElementCustomCircleOutline objCircle = new ElementCustomCircleOutline();
								attributeMap.put(objCircle, parseAttributeString(eCircles.metadata));
								
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
											objCircle.autoCenter = false;
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
								
								addElement(objCircle, attributeMap.get(objCircle).get("eid"), elements);
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
								
								ElementCustomQuadFilled objQuad = new ElementCustomQuadFilled();
								attributeMap.put(objQuad, parseAttributeString(eQuads.metadata));

								
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
											objQuad.autoCenter = false;
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
								
								addElement(objQuad, attributeMap.get(objQuad).get("eid"), elements);
								break;
							}
						}
					}
					
					break;
				}
				
				
			}
			
		}

		
		
		for(String eid : elements.keySet()) {
			List<BaseElement> elems = elements.get(eid);
			
			// combine elements to one multielement if neccessary
			BaseElement finalElement = null;
			if(elems.size() == 0) {
				continue;
				
			} else if(elems.size() == 1) {
				finalElement = elems.get(0);
				
			} else {
				ElementType type = ElementType.get(attributeMap.get(elems.get(0)).get("type"));
				if(type == ElementType.CUSTOM_POLY_FILLED) {
					// get num vertices
					int numVertices = 0;
					Map<String,String> attribsFirst = attributeMap.get(elems.get(0));
					if(attribsFirst == null || !attribsFirst.containsKey("size")) {
						continue;
					}
					numVertices = Integer.parseInt(attribsFirst.get("size"));
					// reconstruct vertices
					Vector2d[] vertices = new Vector2d[numVertices];
					for(BaseElement e : elems) {
						ElementCustomQuadFilled quad = (ElementCustomQuadFilled)e;
						Map<String,String> attribsQuad = attributeMap.get(e);
						if(attribsQuad.containsKey("indices")) {
							String[] strIndices = attribsQuad.get("indices").split(" ");
							if(strIndices.length == 3) {
								int i0 = Integer.parseInt(strIndices[0]);
								int i1 = Integer.parseInt(strIndices[1]);
								int i2 = Integer.parseInt(strIndices[2]);
								vertices[i0] = quad.pos1;
								vertices[i1] = quad.pos2;
								vertices[i2] = quad.pos3;
							}
						}
					}
					// set vertices / build polygon
					ElementCustomPolygonFilled poly = new ElementCustomPolygonFilled();
					poly.setVertices(new Vector2d[]{});
					for(int i=0; i<vertices.length; i++) {
						Vector2d vertex = vertices[i];
						if(vertex != null) {
							poly.addVertex(vertex);
						} else {
							poly.addVertex(0, 0);
						}
					}
					// set attributes
					ElementCustomQuadFilled quad = (ElementCustomQuadFilled)elems.get(0);
					poly.useThousandth = quad.useThousandth;
					poly.movement = quad.movement;
					poly.angle = quad.angle;
//					poly.autoCenter = false;
//					poly.center.set(quad.center);
					poly.radCenter.set(quad.radCenter);
					poly.speed = quad.speed;
					finalElement = poly;
				}
				if(type == ElementType.CUSTOM_CIRCLE_FILLED) {
					// get data from attribs
					Map<String,String> attribsFirst = attributeMap.get(elems.get(0));
					if(attribsFirst == null || !attribsFirst.containsKey("size")) {
						continue;
					}
					double quality = Double.parseDouble(attribsFirst.get("quality"));
					double segmentStart = Double.parseDouble(attribsFirst.get("segment").split(" ")[0]);
					double segmentEnd = Double.parseDouble(attribsFirst.get("segment").split(" ")[1]);
					// reconstruct center
					Vector2d avgCenter = new Vector2d();
					for(BaseElement e : elems) {
						ElementCustomQuadFilled quad = (ElementCustomQuadFilled)e;
						avgCenter.add(quad.pos1).add(quad.pos2).add(quad.pos3);
					}
					avgCenter.scale(1.0/(elems.size()*3));
					// reconstruct diameter
					double maxRadius = 0;
					for(BaseElement e : elems) {
						ElementCustomQuadFilled quad = (ElementCustomQuadFilled)e;
						final double dist0 = avgCenter.dist(quad.pos1);
						final double dist1 = avgCenter.dist(quad.pos1);
						final double dist2 = avgCenter.dist(quad.pos1);
						maxRadius = Math.max(maxRadius, dist0);
						maxRadius = Math.max(maxRadius, dist1);
						maxRadius = Math.max(maxRadius, dist2);
					}
					// set vertices / build circle
					ElementCustomCircleFilled circle = new ElementCustomCircleFilled();
					circle.position.set(avgCenter);
					circle.diameter = maxRadius*2;
					circle.segment.set(segmentStart, segmentEnd);
					circle.quality = quality;
					// set attributes
					ElementCustomQuadFilled quad = (ElementCustomQuadFilled)elems.get(0);
					circle.useThousandth = quad.useThousandth;
					circle.movement = quad.movement;
					circle.angle = quad.angle;
					circle.radCenter.set(quad.radCenter);
					circle.speed = quad.speed;
					finalElement = circle;
				}
				if(type == ElementType.CUSTOM_POLY_OUTLINE) {
					ElementCustomPolygonOutline poly = new ElementCustomPolygonOutline();
					List<Vector2d> vertices = new ArrayList<Vector2d>();
					for(BaseElement subElement : elems) {
						ElementCustomLine line = (ElementCustomLine)subElement;
						vertices.add(line.start);
					}
					poly.setVertices(vertices);
					// set attributes
					ElementCustomLine line = (ElementCustomLine)elems.get(0);
					poly.useThousandth = line.useThousandth;
					poly.movement = line.movement;
					poly.angle = line.angle;
					poly.radCenter.set(line.radCenter);
					poly.speed = line.speed;
					finalElement = poly;
				}
				if(type == ElementType.CUSTOM_QUAD_OUTLINE) {
					ElementCustomQuadOutline quad = new  ElementCustomQuadOutline();
					quad.pos1.set(((ElementCustomLine)elems.get(0)).start);
					quad.pos2.set(((ElementCustomLine)elems.get(1)).start);
					quad.pos3.set(((ElementCustomLine)elems.get(2)).start);
					quad.pos4.set(((ElementCustomLine)elems.get(3)).start);
					// set attributes
					ElementCustomLine line = (ElementCustomLine)elems.get(0);
					quad.useThousandth = line.useThousandth;
					quad.movement = line.movement;
					quad.angle = line.angle;
					quad.radCenter.set(line.radCenter);
					quad.speed = line.speed;
					finalElement = quad;
				}
				if(type == ElementType.FUNNEL) {
					Map<String,String> attribsFirst = attributeMap.get(elems.get(0));
					if(attribsFirst == null) {
						continue;
					}
					ElementFunnel funnel = new  ElementFunnel();
					funnel.movement = ((ElementCustomLine)elems.get(0)).movement;
					funnel.sizeTargetCM = Integer.parseInt(attribsFirst.get("tsize"));
					funnel.rangeStart = Integer.parseInt(attribsFirst.get("range").split(" ")[0]);
					funnel.rangeEnd = Integer.parseInt(attribsFirst.get("range").split(" ")[1]);
					funnel.rangeStep = Integer.parseInt(attribsFirst.get("range").split(" ")[2]);
					if(attribsFirst.containsKey("shell")) {
						String partialAmmoName = attribsFirst.get("shell");
						search: for(BallisticElement ballElement : dataBall.elements) {
							for(Ammo ammo : ballElement.ammunition) {
								if(ammo.name.equalsIgnoreCase(partialAmmoName)) {
									funnel.elementBallistic = ballElement;
									break search;
								}
							}
						}
					}
					finalElement = funnel;
				}
			}
			

			// add attributes to final element
			Map<String,String> attributes = attributeMap.get(elems.get(0));
			if(attributes != null && finalElement != null) {
				finalElement.name = attributes.get("name");
			}
			
			
			// add final element to sight data
			dataSight.addElement(finalElement);
			
		}
		
		Logger.get().info("Sight file loaded");
		return dataSight;
	}
	
	
	
	
	private void addElement(BaseElement element, String eid, Map<String,List<BaseElement>> elements) {
		if(!elements.containsKey(eid)) {
			elements.put(eid, new ArrayList<BaseElement>());
		}
		elements.get(eid).add(element);
	}
	
	
	
	
	private Map<String,String> parseAttributeString(String str) {
		
		Map<String,String> attributes = new HashMap<String,String>();
		
		String strAttributes = str.trim();
		if(!strAttributes.startsWith("[") || !strAttributes.endsWith("]")) {
			Logger.get().warn("Invalid Attribute String: " + strAttributes);
			return attributes;
		}
		strAttributes = strAttributes.substring(1, strAttributes.length()-1);
		
		String[] pairs = strAttributes.split(",");
		for(String pair : pairs) {
			String key = pair.split("=")[0].trim();
			String value = pair.split("=")[1].trim();
			value = value.substring(1, value.length()-1);
			attributes.put(key, value);
		}
		
		return attributes;
	}
	
	
	
}
