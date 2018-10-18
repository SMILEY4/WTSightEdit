package com.ruegnerlukas.wtsights.ui.about;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import com.ruegnerlukas.wtsights.ui.view.IViewController;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.ParamKey;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class AboutController implements IViewController {

	@FXML private ResourceBundle resources;
	@FXML private URL location;
	
	@FXML private TextArea textArea;
	
	
	
	
	@Override
	public void create(Map<ParamKey,Object> parameters) {
		AboutService service = (AboutService) ViewManager.getService(View.ABOUT, true);
		textArea.setText(service.getText(resources));
	}
	
	
}
