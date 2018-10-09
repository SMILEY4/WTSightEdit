package com.ruegnerlukas.wtsights.ui.screenshotupload;

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

public class ScreenshotUploadLoader implements IViewLoader {

	@Override
	public Parent openNew(Stage stage, Map<ParamKey,Object> parameters) {
		Logger.get().info("Loading 'ScreenshotUpload' (" + Workflow.toString(Workflow.steps) + ")");
		FXUtils.openFXScene(View.SCREENSHOT_UPLOAD, null, "ui/screenshotupload/screenshotupload.fxml", 650, 450, "Upload Screenshots");
		ViewManager.getController(View.SCREENSHOT_UPLOAD).create(parameters);
		return null;
	}
	
}
