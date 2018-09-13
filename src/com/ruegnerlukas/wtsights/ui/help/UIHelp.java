package com.ruegnerlukas.wtsights.ui.help;

import java.net.URL;
import java.util.ResourceBundle;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.fxml.FXML;
import javafx.stage.Stage;

public class UIHelp {

	@FXML private ResourceBundle resources;
	@FXML private URL location;
	
	
	public static void openNew() {
		Logger.get().info("Navigate to 'UIHelp'");
		
		Object[] sceneObjects = FXUtils.openFXScene(null, "/ui/layout_help.fxml", 600, 700, "Help");
		UIHelp controller = (UIHelp)sceneObjects[0];
		Stage stage = (Stage)sceneObjects[1];
		controller.create();
	}
	
	
	
	
	private void create() {
	}
	
	
}
