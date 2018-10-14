package com.ruegnerlukas.wtsights.ui.calibrationeditor;

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



public class CalibrationEditorLoader implements IViewLoader {


	@Override
	public Parent openNew(Stage stage, Map<ParamKey,Object> parameters) {

		Logger.get().info("Loading 'Metadata-Editor'");
		
		boolean styleDark = "dark".equals(Config.app_style);
		int width = Config.app_window_size.x;
		int height = Config.app_window_size.y;
		
		FXUtils.openFXScene(View.CALIBRATION_EDITOR, null, "ui/calibrationeditor/calibration"+(styleDark?"_dark":"")+".fxml", width, height, ViewManager.getResources().getString("ce_title"));
		ViewManager.getController(View.CALIBRATION_EDITOR).create(parameters);
		return null;
	}

	
}
