package com.ruegnerlukas.wtsights.ui.about;

import java.util.Map;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.ui.view.IViewLoader;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.ParamKey;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.scene.Parent;
import javafx.stage.Stage;

public class AboutLoader implements IViewLoader {

	
	@Override
	public Parent openNew(Stage stage, Map<ParamKey,Object> parameters) {
		Logger.get().info("Loading 'About'");
		FXUtils.openFXScene(View.ABOUT, null, "ui/about/about.fxml", 600, 700, ViewManager.getResources().getString("ab_title"));
		ViewManager.getController(View.ABOUT).create(parameters);
		return null;
	}

}
