package com.ruegnerlukas.wtsights.ui.vehicleselection;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.Database;
import com.ruegnerlukas.wtsights.data.vehicle.Vehicle;
import com.ruegnerlukas.wtsights.ui.Workflow;
import com.ruegnerlukas.wtsights.ui.Workflow.Step;
import com.ruegnerlukas.wtsights.ui.calibrationselect.UICalibrationSelect;
import com.ruegnerlukas.wtsights.ui.screenshotupload.UIScreenshotUpload;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class UIVehicleSelect {


	private Stage stage;

	@FXML private ResourceBundle resources;
	@FXML private URL location;

	@FXML private TextField textfieldFilter;
	@FXML private ComboBox<String> comboVehicles;

	
	
	
	
	
	public static void openNew() {
		Logger.get().info("Navigate to 'VehicleSelect' (" + Workflow.toString(Workflow.steps) + ")");
		Stage stage = null;
		UIVehicleSelect controller = (UIVehicleSelect)FXUtils.openFXScene(stage, "/ui/layout_vehicleselection.fxml", 600, 200, "Select Vehicle");
		controller.create(stage);
	}
	
	
	
	
	@FXML
	void initialize() {
		assert comboVehicles != null : "fx:id=\"comboVehicles\" was not injected: check your FXML file 'layout_vehicleselection.fxml'.";
	}


	
	
	private void create(Stage stage) {
		this.stage = stage;
		onFilter(null);
	}

	

	
	@FXML
	void onFilter(ActionEvent event) {
		
		String strFilter = "";
		if(event != null) {
			strFilter = textfieldFilter.getText();
		}
		
		List<String> vehicleNames = Database.getVehicleNames(strFilter.trim());
		if(vehicleNames.size() > 0) {
			comboVehicles.setItems(FXCollections.observableArrayList(vehicleNames));
			comboVehicles.getSelectionModel().select(vehicleNames.get(0));
		
		} else {
			comboVehicles.setItems(FXCollections.observableArrayList());
			comboVehicles.getSelectionModel().clearSelection();
		}
		
		Logger.get().info("Filter list by '" + strFilter + "' (" + vehicleNames.size() + ")");
		
	}




	@FXML
	void onCancel(ActionEvent event) {
		stage.close();
	}




	@FXML
	void onNext(ActionEvent event) {

		Vehicle vehicle = Database.getVehicleByName(comboVehicles.getSelectionModel().getSelectedItem());
		
		if(vehicle == null) {
			Logger.get().warn("(Alert) No vehicle selected. Select vehicle to continue.");
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText("No vehicle selected. Select vehicle to continue.");
			alert.showAndWait();
			return;
		}
		
		Logger.get().debug("Selected vehicle: " + vehicle.name);
		
		if(Workflow.steps.size() == 1 && Workflow.steps.get(0) == Step.CREATE_CALIBRATION) {
			this.stage.close();
			Workflow.steps.add(Step.SELECT_VEHICLE);
			UIScreenshotUpload.openNew(vehicle);
			
		} else if(Workflow.steps.size() == 1 && Workflow.steps.get(0) == Step.CREATE_SIGHT) {
			this.stage.close();
			Workflow.steps.add(Step.SELECT_VEHICLE);
			UICalibrationSelect.openNew(vehicle);
		
		}
	}

	
}
