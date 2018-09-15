package com.ruegnerlukas.wtsights;

import java.io.File;
import java.io.IOException;

import com.ruegnerlukas.simpleutils.JarLocation;
import com.ruegnerlukas.simpleutils.SystemUtils;
import com.ruegnerlukas.simpleutils.logging.LogLevel;
import com.ruegnerlukas.simpleutils.logging.builder.DefaultMessageBuilder;
import com.ruegnerlukas.simpleutils.logging.filter.FilterLevel;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.simpleutils.logging.target.LogFileTarget;
import com.ruegnerlukas.wtsights.data.Database;
import com.ruegnerlukas.wtsights.ui.AmmoIcons;
import com.ruegnerlukas.wtsights.ui.ElementIcons;
import com.ruegnerlukas.wtsights.ui.main.UIMainMenu;
import com.ruegnerlukas.wtutils.Config;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class WTSights extends Application {

	
	private static Stage primaryStage;
	
	public static boolean DEV_MODE = false;
	public static final boolean DARK_MODE = false;
	
	public static boolean wasStartedInsideData;

	
	
	public static void main(String[] args) {
		
		for(String arg : args) {
			if(arg.equalsIgnoreCase("dev")) {
				DEV_MODE = true;
			}
		}
		
		wasStartedInsideData = false;
		if(JarLocation.getJarLocation(WTSights.class).endsWith("\\data")) {
			wasStartedInsideData = true;
		}
		if(DEV_MODE) {
			wasStartedInsideData = false;
		}
		
		
		
		Logger.get().redirectStdOutput(LogLevel.DEBUG, LogLevel.ERROR);
		((DefaultMessageBuilder)Logger.get().getMessageBuilder()).setSourceNameSizeMin(23);
	    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
	        public void run() {
	    		Logger.get().info("=========================================");
	    		Logger.get().blankLine();
	    		Logger.get().blankLine();
	    		Logger.get().blankLine();
	    		Logger.get().blankLine();
	    		Logger.get().close();
	        }
	    }, "Shutdown-thread"));
		
	    
		if(DEV_MODE) {
			Logger.get().getFilterManager().addFilter(FilterLevel.only(LogLevel.values()));
			
		} else {
			Logger.get().getFilterManager().addFilter(FilterLevel.not(LogLevel.DEBUG));
			File logFile = new File(JarLocation.getJarLocation(WTSights.class) + (wasStartedInsideData ? "" : "/data") + "/log.txt");
			if(!logFile.exists()) {
				try {
					if(!logFile.getParentFile().exists()) {
						logFile.getParentFile().mkdir();
					}
					logFile.createNewFile();
				} catch (IOException e) {
					Logger.get().error(e);
				}
			}
			
			Logger.get().setLogTarget(new LogFileTarget(logFile, true));
		}
		
		Logger.get().blankLine();
		Logger.get().info("Starting Application (" + JarLocation.getJarLocation(WTSights.class) + ") DEV_MODE=" + DEV_MODE + " PATH=" + JarLocation.getJarLocation(WTSights.class) + "  inside=" + wasStartedInsideData);
		Logger.get().info("System information:   JAVA = " + SystemUtils.getJavaRuntimeName() +" "+ SystemUtils.getJavaVersion() + ",   OS = " + SystemUtils.getOSName());
		
		
		Config.load(new File(JarLocation.getJarLocation(WTSights.class) + (wasStartedInsideData ? "" : "/data") + "/config.json"));
		
		launch(args);
	}
	
	
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		WTSights.primaryStage = primaryStage;
		
		FXUtils.addIcons(primaryStage);
		
		if(DEV_MODE) {
			AmmoIcons.load("res/assets/ammo_icons_2.png", false);
			ElementIcons.load("res/assets/elementIcons.png", false);
		} else {
			AmmoIcons.load("/assets/ammo_icons_2.png", true);
			ElementIcons.load("/assets/elementIcons.png", true);
		}
		
		Database.loadVehicles(new File(JarLocation.getJarLocation(WTSights.class) + (wasStartedInsideData ? "" : "/data") + "/vehicle_data.xml"));
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				System.exit(0);
			} 
		});
		
		// STANDARD START
		UIMainMenu.openNew(primaryStage);
		
//		JUMP TO SIGHT EDITOR
//		CalibrationData dataCalib = DataLoader.loadExternalCalibFile(new File("C:\\Users\\LukasRuegner\\Desktop\\pz4f2\\calibration_pz4f2_v3.xml"));
//		SightData dataSight = DataLoader.loadSight(new File("C:\\Users\\LukasRuegner\\Desktop\\pz4f2\\sight_1.blk"), dataCalib);
//		UISightEditor.openNew(dataCalib, dataSight);
		
		
//		JUMP TO CALIB.EDITOR
//		ArrayList<Ammo> ammoList = new ArrayList<Ammo>();
//		Ammo ammo = new Ammo();
//		ammo.name = "ammo_75mm_pzgr_39";
//		ammo.type = "apcbc_tank";
//		ammo.speed = 740;
//		ammoList.add(ammo);
//		
//		HashMap<Ammo,File> imageMap = new HashMap<Ammo,File>();
//		imageMap.put(ammo, new File("C:\\Users\\LukasRuegner\\Desktop\\pz4f2\\apcbc.png"));
//		
//		UICalibrationEditor.openNew(Database.getVehicleByName("vehicle_germ_pzkpfw_iv_ausf_f2"), ammoList, imageMap);
	}

	
	
	
	public static Stage getPrimaryStage() {
		return primaryStage;
	}
	
	
	
}
