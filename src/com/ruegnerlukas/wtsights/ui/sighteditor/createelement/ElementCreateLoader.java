package com.ruegnerlukas.wtsights.ui.sighteditor.createelement;

import java.util.Map;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.ui.view.IViewLoader;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.ParamKey;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;
import com.ruegnerlukas.wtutils.Config;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.scene.Parent;
import javafx.stage.Stage;

public class ElementCreateLoader implements IViewLoader {

	@Override
	public Parent openNew(Stage stage, Map<ParamKey, Object> parameters) {
		Logger.get().info("Loading 'Create ELement'");
		FXUtils.openFXScene(View.ELEMENT_CREATE, stage, "ui/sighteditor/createelement/createelement.fxml", 500, 225, ViewManager.getResources().getString("se_ce_title"), "dark".equals(Config.app_style), true, parameters);
		return null;
	}

}
