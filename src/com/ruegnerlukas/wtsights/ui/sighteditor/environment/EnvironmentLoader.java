package com.ruegnerlukas.wtsights.ui.sighteditor.environment;

import java.io.IOException;
import java.util.Map;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.ui.view.IViewLoader;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.ParamKey;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class EnvironmentLoader implements IViewLoader {

	@Override
	public Parent openNew(Stage stage, Map<ParamKey, Object> parameters) {
		try {
			FXMLLoader loader = new FXMLLoader(EnvironmentLoader.class.getResource("environment.fxml"));
			Parent root = (Parent) loader.load();
			ViewManager.setController(View.SEM_ENVIRONMENT, loader.getController());
			AnchorPane.setTopAnchor(root, 0.0);
			AnchorPane.setBottomAnchor(root, 0.0);
			AnchorPane.setLeftAnchor(root, 0.0);
			AnchorPane.setRightAnchor(root, 0.0);
			ViewManager.getController(View.SEM_ENVIRONMENT).create(parameters);;
			return root;
		} catch (IOException e) {
			Logger.get().error(e);
			return null;
		}
	}

	

}
