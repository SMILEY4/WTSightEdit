package com.ruegnerlukas.wtsights.ui.vehicleselection;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.Database;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationData;
import com.ruegnerlukas.wtsights.data.vehicle.Vehicle;
import com.ruegnerlukas.wtsights.ui.Workflow;
import com.ruegnerlukas.wtsights.ui.Workflow.Step;
import com.ruegnerlukas.wtsights.ui.calibrationselect.UICalibrationSelect;
import com.ruegnerlukas.wtsights.ui.screenshotupload.UIScreenshotUpload;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.application.Platform;
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

	private File fileSight = null;
	
	
	
	
	
	public static void openNew() {
		Logger.get().info("Navigate to 'VehicleSelect' (" + Workflow.toString(Workflow.steps) + ")");
		Object[] sceneObjects = FXUtils.openFXScene(null, "/ui/layout_vehicleselection.fxml", 600, 200, "Select Vehicle");
		UIVehicleSelect controller = (UIVehicleSelect)sceneObjects[0];
		Stage stage = (Stage)sceneObjects[1];
		controller.create(stage, null);
	}
	
	
	public static void openNew(File fileSight) {
		Logger.get().info("Navigate to 'VehicleSelect' (" + Workflow.toString(Workflow.steps) + "), fileSight=" + fileSight.getAbsolutePath());
		Object[] sceneObjects = FXUtils.openFXScene(null, "/ui/layout_vehicleselection.fxml", 600, 200, "Select Vehicle");
		UIVehicleSelect controller = (UIVehicleSelect)sceneObjects[0];
		Stage stage = (Stage)sceneObjects[1];
		controller.create(stage, fileSight);
	}
	
	
	
	
	
	
	@FXML
	void initialize() {
		assert comboVehicles != null : "fx:id=\"comboVehicles\" was not injected: check your FXML file 'layout_vehicleselection.fxml'.";
	}


	
	
	private void create(Stage stage, File fileSight) {
		this.stage = stage;
		this.fileSight = fileSight;
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
		
		Logger.get().debug(Workflow.steps + "  " + stage);
		
		
		if(Workflow.is(Step.CREATE_CALIBRATION)) {
			this.stage.close();
			Workflow.steps.add(Step.SELECT_VEHICLE);
			UIScreenshotUpload.openNew(vehicle);
		}
		
		if(Workflow.is(Step.CREATE_SIGHT, Step.SELECT_CALIBRATION)) {
			this.stage.close();
			Workflow.steps.add(Step.SELECT_VEHICLE);
			UIScreenshotUpload.openNew(vehicle);
		}
		
		if(Workflow.is(Step.LOAD_SIGHT, Step.SELECT_CALIBRATION)) {
			this.stage.close();
			Workflow.steps.add(Step.SELECT_VEHICLE);
			UIScreenshotUpload.openNew(fileSight, vehicle);
		}
		
	}

	
}
