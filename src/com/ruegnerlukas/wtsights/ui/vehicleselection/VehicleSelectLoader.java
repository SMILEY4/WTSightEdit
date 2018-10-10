package com.ruegnerlukas.wtsights.ui.vehicleselection;

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

public class VehicleSelectLoader implements IViewLoader {

	@Override
	public Parent openNew(Stage stage, Map<ParamKey,Object> parameters) {
		Logger.get().info("Loading 'VehicleSelect' (" + Workflow.toString(Workflow.steps) + ")");
		FXUtils.openFXScene(View.VEHICLE_SELECT, null, "ui/vehicleselection/vehicleselect.fxml", 600, 200, ViewManager.getResources().getString("vs_title"));
		ViewManager.getController(View.VEHICLE_SELECT).create(parameters);
		return null;
	}
	
}
