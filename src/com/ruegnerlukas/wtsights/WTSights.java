package com.ruegnerlukas.wtsights;

import java.io.File;
import java.io.IOException;

import com.ruegnerlukas.simpleutils.JarLocation;
import com.ruegnerlukas.simpleutils.logging.LogLevel;
import com.ruegnerlukas.simpleutils.logging.builder.DefaultMessageBuilder;
import com.ruegnerlukas.simpleutils.logging.filter.FilterLevel;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.simpleutils.logging.target.LogFileTarget;
import com.ruegnerlukas.wtsights.data.Database;
import com.ruegnerlukas.wtsights.ui.main.UIMainMenu;
import com.ruegnerlukas.wtupdater.WTSightsStart;
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
		if(JarLocation.getJarLocation(WTSightsStart.class).endsWith("\\data")) {
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
			File logFile = new File(JarLocation.getJarLocation(WTSightsStart.class) + (wasStartedInsideData ? "" : "/data") + "/log.txt");
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
		Logger.get().blankLine();
		Logger.get().blankLine();
		Logger.get().blankLine();
		Logger.get().info("Starting Application (" + JarLocation.getJarLocation(WTSightsStart.class) + ") DEV_MODE=" + DEV_MODE + " PATH=" + JarLocation.getJarLocation(WTSightsStart.class) + "  inside=" + wasStartedInsideData);
		
		Config.load(new File(JarLocation.getJarLocation(WTSightsStart.class) + (wasStartedInsideData ? "" : "/data") + "/config.txt"));

		launch(args);
	}
	
	
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		WTSights.primaryStage = primaryStage;
		
		FXUtils.addIcons(primaryStage);
		
		Database.loadVehicles(new File(JarLocation.getJarLocation(WTSightsStart.class) + (wasStartedInsideData ? "" : "/data") + "/vehicle_data.xml"));
		
		UIMainMenu.openNew(primaryStage);
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				System.exit(0);
			} 
		});
		
		primaryStage.show();
	}

	
	
	
	public static Stage getPrimaryStage() {
		return primaryStage;
	}
	
	
	
}
