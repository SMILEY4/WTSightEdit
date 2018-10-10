package com.ruegnerlukas.wtsights.ui.calibrationselect;

import java.util.Map;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.ui.view.IViewLoader;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.ParamKey;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;
import com.ruegnerlukas.wtutils.FXUtils;
import com.ruegnerlukas.wtutils.Workflow;

import javafx.scene.Parent;
import javafx.stage.Stage;

public class CalibrationSelectLoader implements IViewLoader {

	
	@Override
	public Parent openNew(Stage stage, Map<ParamKey,Object> parameters) {
		Logger.get().info("Loading 'CalibrationSelect' (" + Workflow.toString(Workflow.steps) + ")");
		FXUtils.openFXScene(View.CALIBRATION_SELECT, null, "ui/calibrationselect/calibrationSelect.fxml", 600, 230, ViewManager.getResources().getString("cs_title"));
		ViewManager.getController(View.CALIBRATION_SELECT).create(parameters);
		return null;
	}

}
