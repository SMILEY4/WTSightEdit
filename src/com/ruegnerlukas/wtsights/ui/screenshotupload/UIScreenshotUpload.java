package com.ruegnerlukas.wtsights.ui.screenshotupload;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.WTSights;
import com.ruegnerlukas.wtsights.data.Database;
import com.ruegnerlukas.wtsights.data.vehicle.Ammo;
import com.ruegnerlukas.wtsights.data.vehicle.Vehicle;
import com.ruegnerlukas.wtsights.data.vehicle.Weapon;
import com.ruegnerlukas.wtsights.ui.Workflow;
import com.ruegnerlukas.wtsights.ui.Workflow.Step;
import com.ruegnerlukas.wtsights.ui.calibrationeditor.UICalibrationEditor;
import com.ruegnerlukas.wtsights.ui.main.UIMainMenu;
import com.ruegnerlukas.wtsights.ui.sighteditor.UISightEditor;
import com.ruegnerlukas.wtsights.ui.vehicleselection.UIVehicleSelect;
import com.ruegnerlukas.wtutils.Config2;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class UIScreenshotUpload {


	private Stage stage;
	
	@FXML private ResourceBundle resources;
	@FXML private URL location;

	@FXML private Label labelTankName;
	@FXML private ListView<String> listView;

	private File fileSight = null;
	private Vehicle vehicle;
	private List<Cell> cells = new ArrayList<Cell>();
	
	private File lastDirectory = null;
	
	
	
	
	
	public static void openNew(Vehicle vehicle) {
		Logger.get().info("Navigate to 'ScreenshotUpload' (" + Workflow.toString(Workflow.steps) + ") vehicle=" + (vehicle == null ? "null" : vehicle.name) );
		
		Object[] sceneObjects = FXUtils.openFXScene(null, "/ui/layout_screenshotupload.fxml", 600, 400, "Upload Screenshots");
		UIScreenshotUpload controller = (UIScreenshotUpload)sceneObjects[0];
		Stage stage = (Stage)sceneObjects[1];
		
		controller.create(stage, null, vehicle);
	}
	
	
	public static void openNew(File fileSight, Vehicle vehicle) {
		Logger.get().info("Navigate to 'ScreenshotUpload' (" + Workflow.toString(Workflow.steps) + ") vehicle=" + (vehicle == null ? "null" : vehicle.name) + " fileSight=" + fileSight.getAbsolutePath() );
		
		Object[] sceneObjects = FXUtils.openFXScene(null, "/ui/layout_screenshotupload.fxml", 600, 400, "Upload Screenshots");
		UIScreenshotUpload controller = (UIScreenshotUpload)sceneObjects[0];
		Stage stage = (Stage)sceneObjects[1];
		
		controller.create(stage, fileSight, vehicle);
	}
	


	@FXML
	void initialize() {
		assert labelTankName != null : "fx:id=\"labelTankName\" was not injected: check your FXML file 'layout_screenshotupload.fxml'.";
		assert listView != null : "fx:id=\"listView\" was not injected: check your FXML file 'layout_screenshotupload.fxml'.";
	}



	
	private void create(Stage stage, File fileSight, Vehicle vehicle) {
		
		this.stage = stage;
		this.fileSight = fileSight;
		this.vehicle = vehicle;
		
		// VEHICLE NAME
		labelTankName.setText(vehicle.name);
		
		// LIST
		ObservableList<String> fxListAmmo = FXCollections.observableArrayList();
		for(int i=0; i<vehicle.weaponsList.size(); i++) {
			Weapon weapon = vehicle.weaponsList.get(i);
			for(int j=0; j<weapon.ammo.size(); j++) {
				Ammo ammo = weapon.ammo.get(j);
				fxListAmmo.add(ammo.name + ";" + ammo.type);
			}
		}
		
		Logger.get().info("Loaded ammunition for " + vehicle.name + ": " + fxListAmmo);
		
		listView.getItems().addAll(fxListAmmo);
		listView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override
			public ListCell<String> call(ListView<String> param) {
				Cell cell = new Cell();
				cells.add(cell);
				return cell;
			}
		});
	}

	
	
	
	@FXML
	void onCancel(ActionEvent event) {
		stage.close();
	}




	@FXML
	void onNext(ActionEvent event) {

		List<String> ammoNames = new ArrayList<String>();
		List<File> imgFiles = new ArrayList<File>();

		for(Cell c : cells) {
			if(!c.label.getText().equalsIgnoreCase("null") && c.fileImage != null) {
				ammoNames.add(c.label.getText());
				imgFiles.add(c.fileImage);
			}
		}
		
		boolean isValid = !ammoNames.isEmpty() && (ammoNames.size() == imgFiles.size());
		
		if(!isValid) {
			boolean onlyRockets = true;
			for(Cell c : cells) {
				if(c.label.getText() == null || "null".equals(c.label.getText())) {
					continue;
				}
				if(!c.isRocket) {
					onlyRockets = false;
					break;
				}
			}
			if(onlyRockets) {
				isValid = true;
			}
		}
		
		Logger.get().debug("onNext: " + "ammo="+ammoNames + "; imgs=" + imgFiles + "; valid=" + isValid); 
		
		if(!isValid) {
			Logger.get().warn("(Alert) No Images selected. Select at least one image to continue.");
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText("No Images selected. Select at least one image to continue.");
			alert.showAndWait();
			return;
		}
		
		
		
		List<Ammo> ammoList = new ArrayList<Ammo>();
		Map<Ammo, File> imageMap = new HashMap<Ammo,File>();
		
		for(int i=0; i<ammoNames.size(); i++) {
			String ammoName = ammoNames.get(i);
			File imgFile = imgFiles.get(i);
			
			for(Weapon w : vehicle.weaponsList) {
				for(Ammo a : w.ammo) {
					if(a.name.equals(ammoName)) {
						ammoList.add(a);
						imageMap.put(a, imgFile);
					}
				}
			}
			
		}
		
		
		if(Workflow.is(Step.CREATE_CALIBRATION, Step.SELECT_VEHICLE)) {
			this.stage.close();
			Workflow.steps.add(Step.UPLOAD_SCREENSHOTS);
			UICalibrationEditor.openNew(vehicle, ammoList, imageMap);
		}
		
		if(Workflow.is(Step.CREATE_SIGHT, Step.SELECT_CALIBRATION, Step.SELECT_VEHICLE)) {
			this.stage.close();
			Workflow.steps.add(Step.UPLOAD_SCREENSHOTS);
			UICalibrationEditor.openNew(vehicle, ammoList, imageMap);
		}
		
		if(Workflow.is(Step.EDIT_SIGHT, Step.SELECT_CALIBRATION, Step.SELECT_VEHICLE)) {
			this.stage.close();
			Workflow.steps.add(Step.UPLOAD_SCREENSHOTS);
			UICalibrationEditor.openNew(vehicle, ammoList, imageMap, fileSight);
		}
		
		
	}

	
	
	
	
	
	class Cell extends ListCell<String> {
		
		public File fileImage;
		public Label label = new Label("null");
		public boolean isRocket = false;
		
		private HBox hbox = new HBox();
		private TextField textField = new TextField();
		private Button browse = new Button("Browse");
		private Button reset = new Button("X");
		private String lastItem;
		
		
		public Cell() {
			super();

			hbox.getChildren().addAll(label, textField, browse, reset);
			hbox.setPrefHeight(31);
			hbox.setAlignment(Pos.CENTER_LEFT);
			
			HBox.setHgrow(textField, Priority.ALWAYS);
			textField.setEditable(false);
		
			reset.setMinWidth(40);
			reset.setMaxWidth(40);
			
			HBox.setMargin(label, new Insets(0, 10, 0, 0));
			label.setMinWidth(200);
			
			
			browse.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {

					Button source = (Button)event.getSource();
					
					FileChooser fc = new FileChooser();
					if(lastDirectory != null) {
						fc.setInitialDirectory(lastDirectory);
					}
					fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image (*.jpg, *.png)", "*.jpg", "*.png"));
					fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image (*.png)", "*.png"));
					fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image (*.jpg)", "*.jpg"));
					
					File file = fc.showOpenDialog(source.getScene().getWindow());
					if (file != null) {
						fileImage = file;
						textField.setText(file.getAbsolutePath());
						lastDirectory = file.getParentFile();
					}
					
					Logger.get().debug("Image selected: " + file);
					
				}
			});
			
			reset.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					fileImage = null;
					textField.setText("");
				}
			});
			
		}
		
		
		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			setText(null);
			if(empty) {
				lastItem = null;
				setGraphic(null);
			} else {
				lastItem = item;
				String name = item != null ? item.split(";")[0] : "<null>";
				String type = item != null ? item.split(";")[1] : "<null>";
				label.setText(name);
				label.setTooltip(new Tooltip("Type = " + type));
				setGraphic(hbox);
				if(type.contains("rocket") || type.contains("atgm")) {
					setDisable(true);
					isRocket = true;
				} else {
					setDisable(false);
					isRocket = false;
				}
			}
		}
		
	}
	

}
