package com.ruegnerlukas.wtsights.data.loading;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.FileVersion;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticData;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.data.vehicle.Vehicle;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class DataLoader implements IDataLoader {
	
	private static boolean initialized = false;
	private static Map<FileVersion,IDataLoader> loaders = new HashMap<FileVersion,IDataLoader>();
	
	
	public static IDataLoader get(FileVersion fileVersion) {
		if(!initialized) {
			loaders.put(FileVersion.AUTO_DETECT, new DataLoader());
			loaders.put(FileVersion.DUMMY, new IDataLoader() {
				@Override public List<Vehicle> loadVehicleDataFile(File file) throws Exception {
					return new ArrayList<Vehicle>();
				}
				@Override public SightData loadSightDataFile(File file, BallisticData dataBall) throws Exception {
					return new SightData(true);
				}
				@Override public BallisticData loadBallisticDataFile(File file) throws Exception {
					return new BallisticData();
				}
			});
			loaders.put(FileVersion.V_1_DEFAULT, new DataLoader_v1_default());
			loaders.put(FileVersion.V_2, new DataLoader_v2());
			loaders.put(FileVersion.V_3, new DataLoader_v3());
			initialized = true;
		}
		return loaders.get(fileVersion);
	}


	
	
	@Override
	public List<Vehicle> loadVehicleDataFile(File file) throws Exception {
		
		Logger.get().info("Extracting file-version (vehicles): " + file.getAbsolutePath());

		if(file == null || !file.exists()) {
			Logger.get().fatal("Error loading vehicles: Could not find " + file);
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText("Error loading vehicles: Could not find " + file);
			alert.showAndWait();
			return new ArrayList<Vehicle>();
		}
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(file);
		
		Element root = doc.getDocumentElement();
		if(root.hasAttribute("fileversion")) {
			FileVersion fileVersion = FileVersion.getFromString(root.getAttribute("fileversion"));
			return DataLoader.get(fileVersion).loadVehicleDataFile(file);
		} else {
			return DataLoader.get(FileVersion.V_1_DEFAULT).loadVehicleDataFile(file);
		}
	}


	
	
	@Override
	public BallisticData loadBallisticDataFile(File file) throws Exception {
		
	Logger.get().info("Extracting file-version (ballistics-ext)");
		
		if(file == null || !file.exists()) {
			Logger.get().error("Error loading file: " + file);
			return null;
		}
		
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(file);
		
		Element root = doc.getDocumentElement();
		if(root.hasAttribute("fileversion")) {
			FileVersion fileVersion = FileVersion.getFromString(root.getAttribute("fileversion"));
			return DataLoader.get(fileVersion).loadBallisticDataFile(file);
		} else {
			return DataLoader.get(FileVersion.V_1_DEFAULT).loadBallisticDataFile(file);
		}
		
	}


	
	
	@Override
	public SightData loadSightDataFile(File file, BallisticData dataBall) throws Exception {
		
		Logger.get().info("Extracting file-version (sight)");
		if(file == null || !file.exists()) {
			Logger.get().error("Error loading file: " + file);
			return new SightData(true);
		}
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String str;
			while( (str = reader.readLine()) != null) {
				String line = str.trim().replace(" ", "");
				
				if(line.startsWith("//fileversion=")) {
					String strFileVersion = line.split("=")[1];
					FileVersion fileVersion = FileVersion.getFromString(strFileVersion);
					reader.close();
					return DataLoader.get(fileVersion).loadSightDataFile(file, dataBall);
				}
				
			}
			reader.close();
		} catch (FileNotFoundException e) {
			Logger.get().error(e);
		} catch (IOException e) {
			Logger.get().error(e);
		}
		
		return DataLoader.get(FileVersion.V_1_DEFAULT).loadSightDataFile(file, dataBall);
	}
	
	
}








