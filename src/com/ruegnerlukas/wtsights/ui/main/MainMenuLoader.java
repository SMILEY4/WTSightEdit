package com.ruegnerlukas.wtsights.ui.main;

import java.util.Map;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.ui.view.IViewLoader;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.ParamKey;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.scene.Parent;
import javafx.stage.Stage;

public class MainMenuLoader implements IViewLoader {

	
	@Override
	public Parent openNew(Stage stage, Map<ParamKey,Object> parameters) {
		Logger.get().info("Loading 'MainMenu'");
		FXUtils.openFXScene(View.MAIN_MENU, stage, "ui/main/mainMenu.fxml", 500, 600, "WT Sight Editor");
		ViewManager.getController(View.MAIN_MENU).create(parameters);
		return null;
	}
	
}
