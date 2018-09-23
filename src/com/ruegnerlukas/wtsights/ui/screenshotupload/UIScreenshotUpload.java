package com.ruegnerlukas.wtsights.ui.screenshotupload;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticElement;
import com.ruegnerlukas.wtsights.data.vehicle.Ammo;
import com.ruegnerlukas.wtsights.data.vehicle.Vehicle;
import com.ruegnerlukas.wtsights.data.vehicle.Weapon;
import com.ruegnerlukas.wtsights.ui.AmmoIcons;
import com.ruegnerlukas.wtsights.ui.Workflow;
import com.ruegnerlukas.wtsights.ui.Workflow.Step;
import com.ruegnerlukas.wtsights.ui.calibrationeditor.UICalibrationEditor;
import com.ruegnerlukas.wtutils.FXUtils;
import com.ruegnerlukas.wtutils.SightUtils.TriggerGroup;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

public class UIScreenshotUpload {


	
	private Stage stage;
	
	@FXML private ResourceBundle resources;
	@FXML private URL location;

	@FXML private Label labelTankName;
	@FXML private ListView<BallisticElement> listView;

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
		labelTankName.setText(vehicle.namePretty);
		
		// ITEMS
		for(int i=0; i<vehicle.weaponsList.size(); i++) {
			Weapon weapon = vehicle.weaponsList.get(i);
			
			// cannons
			if(weapon.triggerGroup == TriggerGroup.PRIMARY || weapon.triggerGroup == TriggerGroup.SECONDARY) {
				for(int j=0; j<weapon.ammo.size(); j++) {
					Ammo ammo = weapon.ammo.get(j);
					BallisticElement element = new BallisticElement();
					element.ammunition.add(ammo);
					if(element.ammunition.size() == 1) {
						if(element.ammunition.get(0).type.contains("rocket") || element.ammunition.get(0).type.contains("atgm")) {
							element.isRocketElement = true;
						}
					}
					listView.getItems().add(element);
				}
			
			// machineguns
			} else if(weapon.triggerGroup == TriggerGroup.MACHINEGUN || weapon.triggerGroup == TriggerGroup.COAXIAL) {
				BallisticElement element = new BallisticElement();
				element.ammunition.addAll(weapon.ammo);
				element.isRocketElement = false;
				listView.getItems().add(element);
				
			// other
			} else {
				continue;
			}
			
		}

		Logger.get().info("Loaded ammunition for " + vehicle.name + ": " + listView.getItems().size());
		
		listView.setCellFactory(new Callback<ListView<BallisticElement>, ListCell<BallisticElement>>() {
			@Override
			public ListCell<BallisticElement> call(ListView<BallisticElement> param) {
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

		Map<BallisticElement, File> imageMap = new HashMap<BallisticElement,File>();
		List<BallisticElement> dataList = new ArrayList<BallisticElement>();
		
		int nCells = 0;
		int nShells = 0;
		int nRockets = 0;
		
		for(Cell cell : cells) {
			if(cell.data != null) {
				nCells++;
				BallisticElement element = cell.data;
				File file = cell.fileImage;
				if(cell.isRocket) {
					nRockets++;
					dataList.add(element);
				} else {
					if(file != null) {
						nShells++;
						dataList.add(element);
						imageMap.put(element, file);
					}
				}
			}
		}
		
		boolean isValid = true;
		
		if(nShells+nRockets == 0) {
			isValid = false;
		}
		if(imageMap.size() != nShells) {
			isValid = false;
		}
		
		
		Logger.get().debug("onNext: " + "data="+dataList + "; imgs=" + imageMap + "; valid=" + isValid); 
		
		if(!isValid) {
			Logger.get().warn("(Alert) No Images selected. Select at least one image to continue.");
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText("No Images selected. Select at least one image to continue.");
			alert.showAndWait();
			return;
		}
		
		if(Workflow.is(Step.CREATE_CALIBRATION, Step.SELECT_VEHICLE)) {
			this.stage.close();
			Workflow.steps.add(Step.UPLOAD_SCREENSHOTS);
			UICalibrationEditor.openNew(vehicle, dataList, imageMap);
		}
		
		if(Workflow.is(Step.CREATE_SIGHT, Step.SELECT_CALIBRATION, Step.SELECT_VEHICLE)) {
			this.stage.close();
			Workflow.steps.add(Step.UPLOAD_SCREENSHOTS);
			UICalibrationEditor.openNew(vehicle, dataList, imageMap);
		}
		
		if(Workflow.is(Step.LOAD_SIGHT, Step.SELECT_CALIBRATION, Step.SELECT_VEHICLE)) {
			this.stage.close();
			Workflow.steps.add(Step.UPLOAD_SCREENSHOTS);
			UICalibrationEditor.openNew(vehicle, dataList, imageMap, fileSight);
		}
		
		
	}

	
	
	
	
	
	class Cell extends ListCell<BallisticElement> {
		
		public File fileImage;
		public Label label = new Label("null");
		public boolean isRocket = false;
		
		private HBox hbox = new HBox();
		private TextField textField = new TextField();
		private Button browse = new Button("Browse");
		private Button reset = new Button("X");
		private BallisticElement data;
		
		
		public Cell() {
			super();

			hbox.getChildren().addAll(label, textField, browse, reset);
			hbox.setPrefHeight(40+2);
			hbox.setAlignment(Pos.CENTER_LEFT);
			
			HBox.setHgrow(textField, Priority.ALWAYS);
			textField.setEditable(false);
		
			reset.setMinWidth(40);
			reset.setMaxWidth(40);
			reset.setDisable(true);
			
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
						reset.setDisable(false);
					}
					
					Logger.get().debug("Image selected: " + file);
					
				}
			});
			
			reset.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					fileImage = null;
					textField.setText("");
					reset.setDisable(true);
				}
			});
			
		}
		
		
		@Override
		protected void updateItem(BallisticElement item, boolean empty) {
			super.updateItem(item, empty);
			this.data = item;
			setText(null);
			
			if(empty || item == null || item.ammunition.isEmpty()) {
				setGraphic(null);
			} else {
				
				// multiple ammo
				if(item.ammunition.size() > 1) {
					
					label.setText(item.ammunition.get(0).parentWeapon.name);
					label.setTooltip(new Tooltip("TriggerGroup = " + item.ammunition.get(0).parentWeapon.triggerGroup.id));
					
					ImageView imgView = new ImageView(SwingFXUtils.toFXImage(AmmoIcons.getIcon("machinegun"), null));
					imgView.setSmooth(true);
					imgView.setPreserveRatio(true);
					imgView.setFitHeight(40);
					label.setGraphic(imgView);
					setGraphic(hbox);
					
					if(item.isRocketElement) {
						setDisable(true);
						isRocket = true;
					} else {
						setDisable(false);
						isRocket = false;
					}
					
					
				// single ammo
				} else if(item.ammunition.size() == 1) {
					label.setText(item.ammunition.get(0).namePretty);
					label.setTooltip(new Tooltip("Type = " + item.ammunition.get(0).type));
					ImageView imgView = new ImageView(SwingFXUtils.toFXImage(AmmoIcons.getIcon(item.ammunition.get(0).type), null));
					imgView.setSmooth(true);
					imgView.setPreserveRatio(true);
					imgView.setFitHeight(40);
					label.setGraphic(imgView);
					setGraphic(hbox);
					if(item.isRocketElement) {
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
	

}
