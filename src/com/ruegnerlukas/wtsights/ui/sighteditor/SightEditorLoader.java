package com.ruegnerlukas.wtsights.ui.sighteditor;

import java.util.Map;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.ui.view.IViewLoader;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.ParamKey;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;
import com.ruegnerlukas.wtutils.Config;
import com.ruegnerlukas.wtutils.FXUtils;
import com.ruegnerlukas.wtutils.Workflow;

import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SightEditorLoader implements IViewLoader {

	
	@Override
	public Parent openNew(Stage stage, Map<ParamKey, Object> parameters) {
		Logger.get().info("Loading 'SightEditor' (" + Workflow.toString(Workflow.steps) + ")");
		int width = Config.app_window_size.x;
		int height = Config.app_window_size.y;
		FXUtils.openFXScene(View.SIGHT_EDITOR, null, "ui/sighteditor/sighteditor.fxml", width, height, "Edit Sight");
		((Stage)ViewManager.getStage(View.SIGHT_EDITOR)).setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override public void handle(WindowEvent event) {
				SightEditorController controller = (SightEditorController) ViewManager.getController(View.SIGHT_EDITOR);
				if(controller.wtCanvas != null && controller.wtCanvas.ses != null) {
					controller.wtCanvas.ses.shutdownNow();
				}
			}
		});
		ViewManager.getController(View.SIGHT_EDITOR).create(parameters);
		return null;
	}
	
	
}
