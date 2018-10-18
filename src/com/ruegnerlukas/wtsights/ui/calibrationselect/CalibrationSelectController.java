package com.ruegnerlukas.wtsights.ui.calibrationselect;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import com.ruegnerlukas.wtsights.ui.view.IViewController;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.ParamKey;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CalibrationSelectController implements IViewController {

	@FXML private ResourceBundle resources;
	@FXML private URL location;

	@FXML private CheckBox cbCreateNew;
	@FXML private ComboBox<String> comboInternalCalibration;
	@FXML private TextField textfieldPath;
	@FXML private VBox vboxSelectCalibration;
	@FXML private HBox hboxExternal;

	private CalibrationSelectService service;
	private Map<ParamKey,Object> parameters;
	
	
	@Override
	public void create(Map<ParamKey,Object> parameters) {
		this.parameters = parameters;
		this.service = (CalibrationSelectService) ViewManager.getService(View.CALIBRATION_SELECT, true);
		cbCreateNew.setSelected(false);
	}


	
	
	@FXML
	void onCreateNew(ActionEvent event) {
		CheckBox cb = (CheckBox) event.getSource();
		vboxSelectCalibration.setDisable(cb.isSelected());
		service.selectCreateNew(cb.isSelected());
	}
	
	
	

	@FXML
	void onBrowse(ActionEvent event) {
		service.browse();
	}




	@FXML
	void onCancel(ActionEvent event) {
		FXUtils.closeFXScene(View.CALIBRATION_SELECT);
	}




	@FXML
	void onNext(ActionEvent event) {
		service.next(parameters);
	}
	
	
	
	
	public void setTextPath(String path) {
		textfieldPath.setText(path);
	}
	

}
