package com.ruegnerlukas.wtsights.ui.calibrationselect;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.WTSights;
import com.ruegnerlukas.wtsights.data.DataLoader;
import com.ruegnerlukas.wtsights.data.Database;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationData;
import com.ruegnerlukas.wtsights.data.vehicle.Vehicle;
import com.ruegnerlukas.wtsights.ui.Workflow;
import com.ruegnerlukas.wtsights.ui.Workflow.Step;
import com.ruegnerlukas.wtsights.ui.calibrationeditor.UICalibrationEditor;
import com.ruegnerlukas.wtsights.ui.screenshotupload.UIScreenshotUpload;
import com.ruegnerlukas.wtsights.ui.sighteditor.UISightEditor;
import com.ruegnerlukas.wtsights.ui.vehicleselection.UIVehicleSelect;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class UICalibrationSelect {

	private Stage stage;

	@FXML private ResourceBundle resources;
	@FXML private URL location;

	@FXML private CheckBox cbCreateNew;
	@FXML private ComboBox<String> comboInternalCalibration;
	@FXML private TextField textfieldPath;
	@FXML private VBox vboxSelectCalibration;
	@FXML private HBox hboxExternal;

	private boolean useExternalCalibration = true;
	private Vehicle vehicle;
	private File fileSight = null;

	private File fileCalibExternal = null;
	private String nameCalibInternal = "";
	
	
	
	
	public static void openNew(Stage stage) {
		
		Logger.get().info("Opening CalibrationSelect (" + Workflow.toString(Workflow.steps) + ")");

		try {
			final Stage window = new Stage();
			window.initModality(Modality.WINDOW_MODAL);
			window.initOwner(WTSights.getPrimaryStage());
			FXUtils.addIcons(window);

			FXMLLoader loader = new FXMLLoader(UICalibrationSelect.class.getResource("/ui/layout_calibrationselect.fxml"));
			Parent root = (Parent) loader.load();
			UICalibrationSelect controller = (UICalibrationSelect) loader.getController();

			Scene scene = new Scene(root, 600, 230, true, SceneAntialiasing.DISABLED);
			if(WTSights.DARK_MODE) {
//				scene.getStylesheets().add("/ui/modena_dark.css");
			}
			window.setTitle("Select Calibration");
			window.setScene(scene);

			controller.create(window);
			window.show();

		} catch (IOException e) {
			Logger.get().error(e);
		}
		
	}
	

	
	
	public static void openNew(Stage stage, File fileSight) {

		Logger.get().info("Opening UICalibrationSelect (" + Workflow.toString(Workflow.steps) + ") file=" + fileSight);
		
		try {
			final Stage window = new Stage();
			window.initModality(Modality.WINDOW_MODAL);
			window.initOwner(WTSights.getPrimaryStage());
			FXUtils.addIcons(window);

			FXMLLoader loader = new FXMLLoader(UICalibrationSelect.class.getResource("/ui/layout_calibrationselect.fxml"));
			Parent root = (Parent) loader.load();
			UICalibrationSelect controller = (UICalibrationSelect) loader.getController();

			Scene scene = new Scene(root, 600, 230, true, SceneAntialiasing.DISABLED);
			if(WTSights.DARK_MODE) {
//				scene.getStylesheets().add("/ui/modena_dark.css");
			}
			window.setTitle("Select Calibration");
			window.setScene(scene);

			controller.create(window, fileSight);
			window.show();

		} catch (IOException e) {
			Logger.get().error(e);
		}

	}

	

	public static void openNew(Stage stage, Vehicle vehicle) {

		Logger.get().info("Opening UICalibrationSelect (" + Workflow.toString(Workflow.steps) + ")  vehicle=" + (vehicle == null ? "null" : vehicle.name) );
		
		try {
			final Stage window = new Stage();
			window.initModality(Modality.WINDOW_MODAL);
			window.initOwner(WTSights.getPrimaryStage());
			FXUtils.addIcons(window);

			FXMLLoader loader = new FXMLLoader(UICalibrationSelect.class.getResource("/ui/layout_calibrationselect.fxml"));
			Parent root = (Parent) loader.load();
			UICalibrationSelect controller = (UICalibrationSelect) loader.getController();

			Scene scene = new Scene(root, 600, 230, true, SceneAntialiasing.DISABLED);
			if(WTSights.DARK_MODE) {
//				scene.getStylesheets().add("/ui/modena_dark.css");
			}
			window.setTitle("Select Calibration");
			window.setScene(scene);

			controller.create(window, vehicle);
			window.show();

		} catch (IOException e) {
			Logger.get().error(e);
		}

	}
	



	@FXML
	void initialize() {
		assert cbCreateNew != null : "fx:id=\"cbCreateNew\" was not injected: check your FXML file 'layout_calibrationselect.fxml'.";
		assert comboInternalCalibration != null : "fx:id=\"comboInternalCalibration\" was not injected: check your FXML file 'layout_calibrationselect.fxml'.";
		assert textfieldPath != null : "fx:id=\"textfieldPath\" was not injected: check your FXML file 'layout_calibrationselect.fxml'.";
		assert hboxExternal != null : "fx:id=\"hboxExternal\" was not injected: check your FXML file 'layout_calibrationselect.fxml'.";
		assert vboxSelectCalibration != null : "fx:id=\"vboxSelectCalibration\" was not injected: check your FXML file 'layout_calibrationselect.fxml'.";
	}



	
	
	
	private void create(Stage stage) {
		
		this.fileSight = null;
		this.stage = stage;
		this.vehicle = null;
		
		comboInternalCalibration.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				onSelectedInternalCalibrationData(newValue);
			}
		});
		
		cbCreateNew.setSelected(false);
		cbCreateNew.setDisable(true);
		
	}
	
	
	private void create(Stage stage, File fileSight) {
		
		this.fileSight = fileSight;
		this.stage = stage;
		this.vehicle = null;
		
		comboInternalCalibration.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				onSelectedInternalCalibrationData(newValue);
			}
		});
		
		cbCreateNew.setSelected(false);
		cbCreateNew.setDisable(true);
		
	}
	
	
	
	private void create(Stage stage, Vehicle vehicle) {
		
		this.stage = stage;
		this.vehicle = vehicle;
		
		comboInternalCalibration.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				onSelectedInternalCalibrationData(newValue);
			}
		});
		
	}


	
	
	@FXML
	void onCreateNew(ActionEvent event) {
		CheckBox cb = (CheckBox) event.getSource();
		vboxSelectCalibration.setDisable(cb.isSelected());
	}
	
	
	
	
	@FXML
	void onUseExternal(ActionEvent event) {
		CheckBox cb = (CheckBox) event.getSource();
		hboxExternal.setDisable(!cb.isSelected());
		comboInternalCalibration.setDisable(cb.isSelected());
		useExternalCalibration = cb.isSelected();
	}
	
	
	
	
	void onSelectedInternalCalibrationData(String calibDataName) {
		this.nameCalibInternal = calibDataName;
	}
	
	
	

	@FXML
	void onBrowse(ActionEvent event) {
		FileChooser fc = new FileChooser();
		fc.setTitle("Open Calibration Data");
		fc.getExtensionFilters().add(new ExtensionFilter("Calibration File (*.xml)", "*.xml"));
		
		File file = fc.showOpenDialog(stage);
		
		if (file != null) {
			textfieldPath.setText(file.getAbsolutePath());
			this.fileCalibExternal = file;
		}
	}




	@FXML
	void onCancel(ActionEvent event) {
		stage.close();
	}




	@FXML
	void onNext(ActionEvent event) {
		
		if(Workflow.steps.size() == 2 && Workflow.steps.get(0) == Step.CREATE_SIGHT && Workflow.steps.get(1) == Step.SELECT_VEHICLE) {
			
			if(this.cbCreateNew.isSelected()) {
				this.stage.close();
				Workflow.steps.add(Step.SELECT_CALIBRATION);
				UIScreenshotUpload.openNew(stage, this.vehicle);
				
			} else {
				
				if(this.fileCalibExternal == null) {
					Logger.get().warn("(Alert) No calibration selected. Select calibration to continue.");
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText(null);
					alert.setContentText("No calibration selected. Select calibration to continue.");
					alert.showAndWait();
					return;
				}
				
				CalibrationData data = DataLoader.loadExternalCalibFile(this.fileCalibExternal);
				data.vehicle = vehicle;
				this.stage.close();
				Workflow.steps.add(Step.SELECT_CALIBRATION);
				UISightEditor.openNew(stage, data);
				
			}
			
			
		} else if(Workflow.steps.size() == 1 && Workflow.steps.get(0) == Step.LOAD_SIGHT) {

			if(this.fileCalibExternal == null) {
				Logger.get().warn("(Alert) No calibration selected. Select calibration to continue.");
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText(null);
				alert.setContentText("No calibration selected. Select calibration to continue.");
				alert.showAndWait();
				return;
			}
			
			CalibrationData data = DataLoader.loadExternalCalibFile(this.fileCalibExternal);
			data.vehicle = Database.getVehicleByName(data.vehicleName);
			this.stage.close();
			Workflow.steps.add(Step.SELECT_CALIBRATION);
			UISightEditor.openNew(stage, data, this.fileSight);
			
			
		} else if(Workflow.steps.size() == 1 && Workflow.steps.get(0) == Step.LOAD_CALIBRATION) {
			
			if(this.fileCalibExternal == null) {
				Logger.get().warn("(Alert) No calibration selected. Select calibration to continue.");
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText(null);
				alert.setContentText("No calibration selected. Select calibration to continue.");
				alert.showAndWait();
				return;
			}
			
			CalibrationData data = DataLoader.loadExternalCalibFile(this.fileCalibExternal);
			data.vehicle = Database.getVehicleByName(data.vehicleName);
			this.stage.close();
			Workflow.steps.add(Step.SELECT_CALIBRATION);
				
			UICalibrationEditor.openNew(stage, data, null);
			
		}
		
	}




	
	

}
