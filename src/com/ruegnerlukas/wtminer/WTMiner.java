package com.ruegnerlukas.wtminer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.ruegnerlukas.simpleutils.SystemUtils;
import com.ruegnerlukas.simpleutils.logging.LogLevel;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.vehicle.Ammo;
import com.ruegnerlukas.wtsights.data.vehicle.Vehicle;
import com.ruegnerlukas.wtsights.data.vehicle.Weapon;
import com.ruegnerlukas.wtutils.JSONUtils;
import com.ruegnerlukas.wtutils.XMLUtils;


public class WTMiner {

	/*
	 * HOW TO:
	 * 
	 * how to unpack: open file with / drag file on correct unpacker.exe
	 * 
	 * 1. extract wt-tools_0.2.1.1.zip
	 * 2. goto warthunder game files
	 * 3. unpack aces.vromfs.bin (may take a while)
	 * 		tanks at: \\aces.vromfs.bin_u\\gamedata\\units\\tankmodels
	 *     ( tank guns at: \\aces.vromfs.bin_u\\gamedata\\units\\tankmodels\\weaponpresets )
	 *     tank guns at: \\aces.vromfs.bin_u\\gamedata\\waepons\\ground_weapons
	 * 4. set PATH_TO_BLKDIR
	 * 5. set PATH_UNPACKED_FILES
	 * 6. start
	 * 
	 */
	
	
	public static final String PATH_TO_BLKDIR_WEAPONS = "C:\\Users\\LukasRuegner\\Desktop\\groundmodels_weapons";
	public static final String PATH_UNPACKED_FILES_WEAPONS = "C:\\Users\\LukasRuegner\\Desktop\\groundmodels_weapons\\_unpacked";
	public static final String PATH_TO_OUTPUT_WEAPONS = "C:\\Users\\LukasRuegner\\Desktop\\groundmodels_weapons\\_unpacked\\weapons.xml";
	
	public static final String PATH_TO_BLKDIR_VEHICLES = "C:\\Users\\LukasRuegner\\Desktop\\tankmodels";
	public static final String PATH_UNPACKED_FILES_VEHICLES = "C:\\Users\\LukasRuegner\\Desktop\\tankmodels\\_unpacked";
	public static final String PATH_TO_OUTPUT_VEHICLES = "C:\\Users\\LukasRuegner\\Desktop\\tankmodels\\_unpacked\\tanks.xml";
	
	public static final String PATH_TO_OUTPUT_COMBINED = "C:\\Users\\LukasRuegner\\Desktop\\vehicle_data.xml";

	
	
	
	
	public static void main(String[] args) {
		Logger.get().redirectStdOutput(LogLevel.DEBUG, LogLevel.ERROR);
		new WTMiner();
	}
	
	
	
	
	
	public WTMiner() {
		
		
		// vehicles
		unpackBLKs(PATH_TO_BLKDIR_VEHICLES, PATH_UNPACKED_FILES_VEHICLES);
		List<Vehicle> vehicles = extractDataVehicles(PATH_UNPACKED_FILES_VEHICLES);
		saveVehiclesToFile(vehicles, PATH_TO_OUTPUT_VEHICLES);

		
		// ground weapons
		unpackBLKs(PATH_TO_BLKDIR_WEAPONS, PATH_UNPACKED_FILES_WEAPONS);
		List<Weapon> weapons = extractDataWeapons(PATH_UNPACKED_FILES_WEAPONS);
		saveWeaponsToFile(weapons, PATH_TO_OUTPUT_WEAPONS);
		
		// combine
		mergeData(PATH_TO_OUTPUT_WEAPONS, PATH_TO_OUTPUT_VEHICLES, PATH_TO_OUTPUT_COMBINED);
	}
	
	
	
	
	
	
	private void mergeData(String PATH_TO_OUTPUT_WEAPONS, String PATH_TO_OUTPUT_VEHICLES, String PATH_TO_OUTPUT_MERGED) {
		
		
		System.out.println("========================");
		System.out.println("|      MERGE DATA      |");
		System.out.println("========================");
	
		try {
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			Document docWeapons = builder.parse(new File(PATH_TO_OUTPUT_WEAPONS));
			Document docVehicles = builder.parse(new File(PATH_TO_OUTPUT_VEHICLES));
			
			Element rootVehicles = docVehicles.getDocumentElement();
			Element rootWeapons = docWeapons.getDocumentElement();

			Document docMerged = builder.newDocument();
			Element rootMerged = docMerged.createElement("data");
			docMerged.appendChild(rootMerged);
			Element elementVehicles = docMerged.createElement("vehicles");
			rootMerged.appendChild(elementVehicles);
			
			for(Element elementVehicle : XMLUtils.getChildren(rootVehicles)) {
				
				
				List<Element> listWeapons = XMLUtils.getChildren(elementVehicle);
				if(listWeapons.size() == 0) {
					System.out.println("no weapons: " + elementVehicle.getTagName());
					continue;
				}
				
				
				Element elementVehicleMerged = docMerged.createElement(elementVehicle.getTagName());
				elementVehicles.appendChild(elementVehicleMerged);
				
				String fovOut = elementVehicle.getAttribute("fovOut");
				String fovIn = elementVehicle.getAttribute("fovIn");
				elementVehicleMerged.setAttribute("fovOut", fovOut);
				elementVehicleMerged.setAttribute("fovIn", fovIn);
				
				Element elementWeapons = docMerged.createElement("weapons");
				elementVehicleMerged.appendChild(elementWeapons);
				
				for(Element elementWeaponVehicle : listWeapons) {
				
					Element elementWeaponMerged = docMerged.createElement("weapon_"+elementWeaponVehicle.getAttribute("name"));
					elementWeaponMerged.setAttribute("triggerGroup", elementWeaponVehicle.getAttribute("triggerGroup"));
					elementWeapons.appendChild(elementWeaponMerged);
					
					Element elementWeaponAmmo = docMerged.createElement("ammo");
					elementWeaponMerged.appendChild(elementWeaponAmmo);
						

					Element elementWeapon = XMLUtils.getElementByTagName(rootWeapons, "weapon_" + elementWeaponVehicle.getAttribute("name").toLowerCase());
					if (elementWeapon == null) {
						continue;
					}

					for(Element elementAmmo : XMLUtils.getChildren(elementWeapon)) {
						
						Element elementAmmoMerged = docMerged.createElement(elementAmmo.getTagName());
						elementAmmoMerged.setAttribute("type", elementAmmo.getAttribute("type"));
						elementAmmoMerged.setAttribute("speed", elementAmmo.getAttribute("speed"));
						elementAmmoMerged.setAttribute("mass", elementAmmo.getAttribute("mass"));
						elementWeaponAmmo.appendChild(elementAmmoMerged);
					}

				}
			}
			
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(docMerged);
			StreamResult result = new StreamResult(new File(PATH_TO_OUTPUT_MERGED));
			transformer.transform(source, result);
			
		
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		
		System.out.println("Data merged and file created");
		
	}
	

	
	
	
	
	private void unpackBLKs(String PATH_TO_BLKDIR, String PATH_UNPACKED_FILES) {
		
		System.out.println("========================");
		System.out.println("|     UNPACK FILES     |");
		System.out.println("========================");

		// get directory with all .blk files to unpack
		File dirBLKs = new File(PATH_TO_BLKDIR);
		System.out.println("BLKDIR: " + dirBLKs.getAbsolutePath() + "; exists=" + dirBLKs.exists());
		
		if(!dirBLKs.exists()) {
			System.err.println("FATAL: Cant find directory");
			System.exit(0);
		}
		
		
		// create new folder for unpacked files
		File destDir = new File(PATH_UNPACKED_FILES);
		destDir.mkdirs();
		System.out.println("Created output folder");

		
		// get all .blk files
		File[] blkFiles = dirBLKs.listFiles();
		System.out.println(blkFiles.length + " files found");
		
		
		System.out.println("Unpacking files ...");
		
		try {
			
			for(int i=0; i<blkFiles.length; i++) {
				
				File file = blkFiles[i];
				if(!file.getAbsolutePath().endsWith(".blk")) {
					System.err.println("Not a .blk file: " + file.getAbsolutePath());
					continue;
				}
				
				System.out.println("unpack " + file.getAbsolutePath());
				
				Process proc = Runtime.getRuntime().exec("cmd /c " + file.getAbsolutePath());
				int returnCode = proc.waitFor();
				
				if(returnCode != 0) {
					System.err.println("Error unpacking file");
					
				} else {
					
					File fileUnpacked = new File(file.getAbsolutePath() + "x");
					Files.move(Paths.get(fileUnpacked.getAbsolutePath()), Paths.get(PATH_UNPACKED_FILES + "\\" + fileUnpacked.getName()), StandardCopyOption.REPLACE_EXISTING);
					
					System.out.println("Unpacked file " + (i+1) + "/" + blkFiles.length);
				}
				
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("=======================");
		System.out.println();
		System.out.println();
	}
	
	
	
	
	private List<Vehicle> extractDataVehicles(String PATH_UNPACKED_FILES) {
	
		System.out.println("========================");
		System.out.println("|     EXTRACT DATA     |");
		System.out.println("========================");
		
		// get directory with all .blkx files
		File dirBLKXs = new File(PATH_UNPACKED_FILES);
		System.out.println("BLKDIR: " + dirBLKXs.getAbsolutePath() + "; exists=" + dirBLKXs.exists());
		
		if(!dirBLKXs.exists()) {
			System.err.println("FATAL: Cant find directory.");
			System.exit(0);
		}
		
		
		// get all .blkx files
		File[] blkxFiles = dirBLKXs.listFiles();
		System.out.println(blkxFiles.length + " files found");
		
		
		System.out.println("Extracting data...");
		
		List<Vehicle> vehicles = new ArrayList<Vehicle>();
		
		try {
			
			for(int i=0; i<blkxFiles.length; i++) {
				File file = blkxFiles[i];
				if(!file.getAbsolutePath().endsWith(".blkx")) {
					System.err.println("Not a .blkx file: " + file.getAbsolutePath());
					continue;
				}
			
				
				BufferedReader reader = new BufferedReader(new FileReader(file));
				StringBuilder sb = new StringBuilder();
				String str;
				while( (str = reader.readLine()) != null) {
					sb.append(str);
				}
				reader.close();
				String content = sb.toString();
				
				
				JsonParser parser = new JsonParser();
				JsonElement rootElement = parser.parse(content);
				
				// vehicle
				Vehicle vehicle = new Vehicle();
				vehicle.name = file.getName().replaceAll(".blkx", "");
				
				// weapons
				List<JsonObject> jsonWeapons = new ArrayList<JsonObject>();
				
				if(rootElement.isJsonObject()) {
					JsonObject root = rootElement.getAsJsonObject();
					JSONUtils.findObject(root, "Weapon", jsonWeapons);
					
				} else if(rootElement.isJsonArray()) {
					JsonArray root = rootElement.getAsJsonArray();
					JSONUtils.findObject(root, "Weapon", jsonWeapons);
				}
				
				for(JsonObject jsonWeapon : jsonWeapons) {
					
					if(jsonWeapon.has("blk") ) {
						
						JsonElement elementType = jsonWeapon.get("blk");
						if(elementType.isJsonPrimitive()) {
							JsonPrimitive primBLK = (JsonPrimitive)elementType;
							if(primBLK.isString()) {
								String strBLK = primBLK.getAsString().replaceAll(".blk", "");
								String[] pathElements = strBLK.split("/");
								String weaponName = pathElements[pathElements.length-1];
								if(vehicle.weapons.contains(weaponName)) {
									continue;
								}
								vehicle.weapons.add(weaponName);
							}
						}
						
						if(jsonWeapon.has("triggerGroup")) {
							JsonElement elementName = jsonWeapon.get("triggerGroup");
							if(elementName.isJsonPrimitive()) {
								JsonPrimitive primTriggerGroup = (JsonPrimitive)elementName;
								if(primTriggerGroup.isString()) {
									vehicle.triggerGroups.add(primTriggerGroup.getAsString());
								}
							}
						} else {
							vehicle.triggerGroups.add("primary");
						}
						
					}
					
				}
				
				// fov
				List<JsonObject> jsonCockpits = new ArrayList<JsonObject>();
				if(rootElement.isJsonObject()) {
					JsonObject root = rootElement.getAsJsonObject();
					JSONUtils.findObject(root, "cockpit", jsonCockpits);
					
				} else if(rootElement.isJsonArray()) {
					JsonArray root = rootElement.getAsJsonArray();
					JSONUtils.findObject(root, "cockpit", jsonCockpits);
				}
				
				if(jsonCockpits.size() > 0) {
					JsonObject objCockpit = jsonCockpits.get(0);
					
					if(objCockpit.has("zoomOutFov") && objCockpit.has("zoomInFov")) {
						JsonElement elementZoomOutFOV = objCockpit.get("zoomOutFov");
						JsonElement elementZoomInFOV = objCockpit.get("zoomInFov");
						vehicle.fovOut = elementZoomOutFOV.getAsFloat();
						vehicle.fovIn = elementZoomInFOV.getAsFloat();
					}
					
				} else {
					List<JsonArray> jsonArrCockpits = new ArrayList<JsonArray>();

					if(rootElement.isJsonObject()) {
						JsonObject root = rootElement.getAsJsonObject();
						JSONUtils.findArray(root, "cockpit", jsonArrCockpits);
						
					} else if(rootElement.isJsonArray()) {
						JsonArray root = rootElement.getAsJsonArray();
						JSONUtils.findArray(root, "cockpit", jsonArrCockpits);
					}
					
					if(jsonArrCockpits.size() > 0) {
						
						JsonArray arrCockpit = jsonArrCockpits.get(0);
						
						int nFOVsFound = 0;
						
						for(int j=0; j<arrCockpit.size(); j++) {
							JsonElement element = arrCockpit.get(j);
							
							if(element instanceof JsonObject) {
								JsonObject obj = (JsonObject)element;
								if(obj.has("zoomOutFov")) {
									vehicle.fovOut = ((JsonPrimitive)obj.get("zoomOutFov")).getAsFloat();
									nFOVsFound++;
								}
								if(obj.has("zoomInFov")) {
									vehicle.fovIn = ((JsonPrimitive)obj.get("zoomInFov")).getAsFloat();
									nFOVsFound++;
								}
							}
							
						}
						
						if(nFOVsFound != 2) {
							System.err.println("Invalid number of fovs found");
							continue;
						}
						
						
					} else {
						System.err.println("no cockpit found");
						continue;
					}
					
				}
				
				
				vehicles.add(vehicle);

				System.out.println("Extracted " + "(" + (i+1) + "/" + blkxFiles.length + "):  " + file.getName());
				
			}
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return vehicles;
		
	}
	
	
	
	
	private List<Weapon> extractDataWeapons(String PATH_UNPACKED_FILES) {
		
		System.out.println("========================");
		System.out.println("|     EXTRACT DATA     |");
		System.out.println("========================");
		
		// get directory with all .blkx files
		File dirBLKXs = new File(PATH_UNPACKED_FILES);
		System.out.println("BLKDIR: " + dirBLKXs.getAbsolutePath() + "; exists=" + dirBLKXs.exists());
		
		if(!dirBLKXs.exists()) {
			System.err.println("FATAL: Cant find directory.");
			System.exit(0);
		}
		
		
		// get all .blkx files
		File[] blkxFiles = dirBLKXs.listFiles();
		System.out.println(blkxFiles.length + " files found");
		
		
		
		System.out.println("Extracting data...");
		List<Weapon> cannons = new ArrayList<Weapon>();

		
		try {
			
			for(int i=0; i<blkxFiles.length; i++) {
				File file = blkxFiles[i];
				if(!file.getAbsolutePath().endsWith(".blkx")) {
					System.err.println("Not a .blkx file: " + file.getAbsolutePath());
					continue;
				}
				
				Weapon cannon = new Weapon();
				cannon.name = file.getName().replaceAll(".blkx", "");
				
				BufferedReader reader = new BufferedReader(new FileReader(file));
				StringBuilder sb = new StringBuilder();
				String str;
				while( (str = reader.readLine()) != null) {
					sb.append(str);
				}
				reader.close();
				String content = sb.toString();
				
				
				JsonParser parser = new JsonParser();
				JsonElement rootElement = parser.parse(content);
				
				List<JsonObject> jsonAmmo = new ArrayList<JsonObject>();
				
				if(rootElement.isJsonObject()) {
					JsonObject root = rootElement.getAsJsonObject();
					JSONUtils.findObject(root, "bullet", jsonAmmo);
					JSONUtils.findObject(root, "rocket", jsonAmmo);

				} else if(rootElement.isJsonArray()) {
					JsonArray root = rootElement.getAsJsonArray();
					JSONUtils.findObject(root, "bullet", jsonAmmo);
					JSONUtils.findObject(root, "rocket", jsonAmmo);
				}
				
				
				for(JsonObject jsonBullet : jsonAmmo) {
					
					if( jsonBullet.has("bulletName") && jsonBullet.has("bulletType") /* &&jsonBullet.has("speed") */ ) {
						
						Ammo ammo = new Ammo();
						
						JsonElement elementName = jsonBullet.get("bulletName");
						if(elementName.isJsonPrimitive()) {
							JsonPrimitive primName = (JsonPrimitive)elementName;
							if(primName.isString()) {
								ammo.name = primName.getAsString();
							}
						}
						
						JsonElement elementType = jsonBullet.get("bulletType");
						if(elementType.isJsonPrimitive()) {
							JsonPrimitive primType = (JsonPrimitive)elementType;
							if(primType.isString()) {
								ammo.type = primType.getAsString();
							}
						}
						
						if(jsonBullet.has("speed")) {
							JsonElement elementSpeed = jsonBullet.get("speed");
							if(elementSpeed.isJsonPrimitive()) {
								JsonPrimitive primSpeed = (JsonPrimitive)elementSpeed;
								if(primSpeed.isNumber()) {
									ammo.speed = primSpeed.getAsInt();
								}
							}
						} else {
							ammo.speed = 0;
						}
						
						JsonElement elementMass = jsonBullet.get("mass");
						if(elementMass.isJsonPrimitive()) {
							JsonPrimitive primSpeed = (JsonPrimitive)elementMass;
							if(primSpeed.isNumber()) {
								ammo.mass = primSpeed.getAsDouble();
							}
						}
						
						if(!cannon.ammo.contains(ammo)) {
							cannon.ammo.add(ammo);
						}

					}
					
				}
				
				if(!cannon.ammo.isEmpty()) {
					cannons.add(cannon);
				}
				
				System.out.println("Extracted " + "(" + (i+1) + "/" + blkxFiles.length + "):  " + file.getName());
				
			}
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		System.out.println("=======================");
		System.out.println();
		System.out.println();
		
		
		return cannons;
	}
	
	
	
	
	private void saveVehiclesToFile(List<Vehicle> vehicles, String PATH_TO_OUTPUT) {
		
		System.out.println("=========================");
		System.out.println("| SAVE VEHICLES TO FILE |");
		System.out.println("=========================");
		
		try {
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			
			Element rootElement = doc.createElement("vehicles");
			doc.appendChild(rootElement);
			
			
			for(Vehicle vehicle : vehicles) {
				
				Element elementVehicle = doc.createElement("vehicle_" + vehicle.name);
				rootElement.appendChild(elementVehicle);
				
				elementVehicle.setAttribute("fovOut", ""+vehicle.fovOut);
				elementVehicle.setAttribute("fovIn", ""+vehicle.fovIn);
				
				for(int i=0; i<vehicle.weapons.size(); i++) {
					String weaponName = vehicle.weapons.get(i);
					String triggerGroup = vehicle.triggerGroups.get(i);
					
					Element elementWeapon = doc.createElement("weapon_" + (i+1));
					elementVehicle.appendChild(elementWeapon);
					
					elementWeapon.setAttribute("name", weaponName);

					Attr attrTriggetGroup = doc.createAttribute("triggerGroup");
					attrTriggetGroup.setNodeValue(triggerGroup);
					elementWeapon.setAttributeNode(attrTriggetGroup);
					
				}
				
			}
			
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(PATH_TO_OUTPUT));
			transformer.transform(source, result);
			
			System.out.println("File saved");
			
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		
		
	}


	
	private void saveWeaponsToFile(List<Weapon> cannons, String PATH_TO_OUTPUT) {
		
		System.out.println("========================");
		System.out.println("| SAVE CANNONS TO FILE |");
		System.out.println("========================");
		
		try {
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			
			Element rootElement = doc.createElement("ground_weapons");
			doc.appendChild(rootElement);
			
			
			for(Weapon cannon : cannons) {
				
				Element elementCannon = doc.createElement("weapon_" + cannon.name);
				rootElement.appendChild(elementCannon);
				
				
				for(Ammo bullet : cannon.ammo) {
					
					Element elementBullet = doc.createElement("ammo_"+bullet.name);
					elementCannon.appendChild(elementBullet);
					
					Attr attrSpeed = doc.createAttribute("speed");
					attrSpeed.setNodeValue(""+(int)bullet.speed);
					elementBullet.setAttributeNode(attrSpeed);
					
					Attr attrMass = doc.createAttribute("mass");
					attrMass.setNodeValue(""+(double)bullet.mass);
					elementBullet.setAttributeNode(attrMass);
					
					Attr attrType = doc.createAttribute("type");
					attrType.setNodeValue(bullet.type);
					elementBullet.setAttributeNode(attrType);
					
				}
				
			}
			
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(PATH_TO_OUTPUT));
			
			transformer.transform(source, result);
			
			System.out.println("File saved");
			
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
}