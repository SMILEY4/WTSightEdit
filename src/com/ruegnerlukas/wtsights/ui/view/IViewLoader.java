package com.ruegnerlukas.wtsights.ui.view;

import java.util.Map;

import com.ruegnerlukas.wtsights.ui.view.ViewManager.ParamKey;

import javafx.scene.Parent;
import javafx.stage.Stage;

public interface IViewLoader {

	public Parent openNew(Stage stage, Map<ParamKey,Object> parameters);
	
}
