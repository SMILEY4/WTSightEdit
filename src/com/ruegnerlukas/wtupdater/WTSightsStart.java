package com.ruegnerlukas.wtupdater;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import com.ruegnerlukas.simpleutils.JarLocation;
import com.ruegnerlukas.simpleutils.logging.LogLevel;
import com.ruegnerlukas.simpleutils.logging.filter.FilterLevel;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.simpleutils.logging.target.LogFileTarget;
import com.ruegnerlukas.wtsights.WTSights;
import com.ruegnerlukas.wtsights.ui.main.UIMainMenu;
import com.ruegnerlukas.wtutils.Config;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class WTSightsStart extends Application {

	
	public static boolean DEV_MODE = false;
	public static boolean DEBUG_MODE = false;

	private static Stage primaryStage;
	

	
	
	public static void main(String[] args) {
		
		Config.load(new File(JarLocation.getJarLocation(WTSightsStart.class) + "/data/config.txt"));
		
		for(String arg : args) {
			if(arg.equalsIgnoreCase("dev")) {
				DEV_MODE = true;
			}
		}
		
		Logger.get().redirectStdOutput(LogLevel.DEBUG, LogLevel.ERROR);
	    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
	        public void run() {
	    		Logger.get().info("=========================================");
	    		Logger.get().close();
	        }
	    }, "Shutdown-thread"));
		
		if(DEV_MODE) {
			Logger.get().getFilterManager().addFilter(FilterLevel.only(LogLevel.values()));
			
		} else {
			
			Logger.get().getFilterManager().addFilter(FilterLevel.not(LogLevel.DEBUG));
			
			File logFile = new File(JarLocation.getJarLocation(WTSightsStart.class) + "/data/log.txt");
			if(!logFile.exists()) {
				try {
					logFile.createNewFile();
				} catch (IOException e) {
					Logger.get().error(e);
				}
			}
			
			Logger.get().setLogTarget(new LogFileTarget(logFile, true));
			
		}
		
		Logger.get().info("Starting AppStarter.");
		launch(args);
	}
	
	
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		WTSightsStart.primaryStage = primaryStage;
		
		FXUtils.addIcons(primaryStage);
		
		FXMLLoader loader = new FXMLLoader(UIMainMenu.class.getResource("/ui/layout_updater.fxml"));
		Parent root = (Parent) loader.load();
		UpdateController controller = (UpdateController)loader.getController();
		Scene scene = new Scene(root, 500, 110, true);
		primaryStage.setTitle("WT Sight Editor");
		primaryStage.setScene(scene);
		
		
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
