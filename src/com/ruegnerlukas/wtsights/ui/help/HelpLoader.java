package com.ruegnerlukas.wtsights.ui.help;

import java.util.Map;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.ui.view.IViewLoader;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.ParamKey;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.scene.Parent;
import javafx.stage.Stage;

public class HelpLoader implements IViewLoader {

	
	@Override
	public Parent openNew(Stage stage, Map<ParamKey,Object> parameter) {
		Logger.get().info("Loading 'Help'");
		FXUtils.openFXScene(View.HELP, null, "ui/help/help.fxml", 600, 700, "Help");
		ViewManager.getController(View.HELP).create(parameter);
		return null;
	}

}
