package com.ruegnerlukas.wtsights.ui.sighteditor;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.DataWriter;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationAmmoData;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationData;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.data.sight.SightData.SelectedElement;
import com.ruegnerlukas.wtsights.sight.SightRenderer;
import com.ruegnerlukas.wtsights.ui.Workflow;
import com.ruegnerlukas.wtsights.ui.vehicleselection.UIVehicleSelect;
import com.ruegnerlukas.wtutils.Config2;
import com.ruegnerlukas.wtutils.FXUtils;
import com.ruegnerlukas.wtutils.ZoomableScrollPane;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class UISightEditor {

	private Stage stage;
	
	@FXML private ResourceBundle resources;
	@FXML private URL location;

	// head
	@FXML private Label labelVehicleName;
	@FXML private ComboBox<String> comboAmmo;
	@FXML private ComboBox<String> comboZoomMode;

	
	// canvas
	@FXML private AnchorPane paneCanvas;
	private ZoomableScrollPane paneCanvasControl;
	private Canvas canvas;
	
	private boolean cursorVisible = false;
	private Vector2d cursorPosition = new Vector2d();
	
	
	// panes
	@FXML private Accordion accordion;
	@FXML private AnchorPane paneEnvironment;
	@FXML private AnchorPane paneGeneral;
	@FXML private AnchorPane paneRangefinder;
	@FXML private AnchorPane paneHorzRange;
	@FXML private AnchorPane paneBallRange;
	@FXML private AnchorPane paneShellBlocks;
	@FXML private AnchorPane paneCustomElements;
	
	// controllers
	private UIEnvironment controllerEnvironment;
	private UIGeneral controllerGeneral;
	private UIRangefinder controllerRangefinder;
	private UIHorzRange controllerHorzRange;
	private UIBallisticRange controllerBallisticRange;
	private UIShellBlocks controllerShellBlocks;
	private UIObjects controllerObjects;

	// misc
	private CalibrationData dataCalib;
	private CalibrationAmmoData currentAmmoData;
	private SightData dataSight;

	

	
	
	
	public static void openNew(CalibrationData dataCalib) {
		
		Logger.get().info("Navigate to 'SightEditor' (" + Workflow.toString(Workflow.steps) + ") vehicle=" + (dataCalib == null ? "null" : dataCalib.vehicle.name) );

		int width = Config2.app_window_size.x;
		int height = Config2.app_window_size.y;
		
		Object[] sceneObjects = FXUtils.openFXScene(null, "/ui/layout_sighteditor_light.fxml", width, height, "Edit Sight");
		UISightEditor controller = (UISightEditor)sceneObjects[0];
		Stage stage = (Stage)sceneObjects[1];
		
		controller.create(stage, dataCalib);
	}



	public static void openNew(CalibrationData dataCalib, SightData dataSight) {
		
		Logger.get().info("Navigate to 'SightEditor' (" + Workflow.toString(Workflow.steps) + ") vehicle=" + (dataCalib == null ? "null" : dataCalib.vehicle.name) + "; sightData="+dataSight);

		int width = Config2.app_window_size.x;
		int height = Config2.app_window_size.y;
		
		Object[] sceneObjects = FXUtils.openFXScene(null, "/ui/layout_sighteditor_light.fxml", width, height, "Edit Sight");
		UISightEditor controller = (UISightEditor)sceneObjects[0];
		Stage stage = (Stage)sceneObjects[1];
		
		controller.create(stage, dataCalib, dataSight);
	}


	

	@FXML
	void initialize() {
		assert labelVehicleName != null : "fx:id=\"labelVehicleName\" was not injected: check your FXML file 'layout_sighteditor.fxml'.";
		assert comboAmmo != null : "fx:id=\"comboAmmo\" was not injected: check your FXML file 'layout_sighteditor.fxml'.";
		assert comboZoomMode != null : "fx:id=\"comboZoomMode\" was not injected: check your FXML file 'layout_sighteditor.fxml'.";
		assert paneCanvas != null : "fx:id=\"paneCanvas\" was not injected: check your FXML file 'layout_sighteditor.fxml'.";
		assert paneEnvironment != null : "fx:id=\"paneEnvironment\" was not injected: check your FXML file 'layout_sighteditor.fxml'.";
		assert paneGeneral != null : "fx:id=\"paneGeneral\" was not injected: check your FXML file 'layout_sighteditor.fxml'.";
		assert paneRangefinder != null : "fx:id=\"paneRangefinder\" was not injected: check your FXML file 'layout_sighteditor.fxml'.";
		assert paneHorzRange != null : "fx:id=\"paneHorzRange\" was not injected: check your FXML file 'layout_sighteditor.fxml'.";
		assert paneBallRange != null : "fx:id=\"paneBallRange\" was not injected: check your FXML file 'layout_sighteditor.fxml'.";

	}

	
	
	
	private void create(Stage stage, CalibrationData dataCalib) {
		create(stage, dataCalib, new SightData());
	}
	
	
	
	
	private void create(Stage stage, CalibrationData dataCalib, SightData dataSight) {
		this.stage = stage;
		this.dataCalib = dataCalib;
		this.dataSight = dataSight;
		create();
	}

	
	
	
	private void create() {
		
		// VEHICLE NAME
		labelVehicleName.setText(dataCalib.vehicle.name);
		
		// AMMO
		for(CalibrationAmmoData ammoData : dataCalib.ammoData) {
			comboAmmo.getItems().add(ammoData.ammo.name);
		}
		comboAmmo.getSelectionModel().select(0);
		comboAmmo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				onAmmoSelected(newValue);
			}
		});
		onAmmoSelected(comboAmmo.getSelectionModel().getSelectedItem());
		
		
		// ZOOM MODE
		comboZoomMode.getItems().add("Show Zoomed Out");
		comboZoomMode.getItems().add("Show Zoomed In");
		comboZoomMode.getSelectionModel().select(0);
		comboZoomMode.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(newValue.equalsIgnoreCase("Show Zoomed Out")) {
					onZoomMode(false);
				} else {
					onZoomMode(true);
				}
			}
		});
		onZoomMode(dataSight.envZoomedIn);
		
		
		
		// SUB ELEMENTS
		
		accordion.expandedPaneProperty().addListener(new ChangeListener<TitledPane>() {
			@Override
			public void changed(ObservableValue<? extends TitledPane> observable, TitledPane oldValue, TitledPane newValue) {
				dataSight.selectedSubElement = "";
				dataSight.selectedElement = null;
				if(newValue != null) {
					if(newValue.getText().equals("Environment")) {
						dataSight.selectedElement = SelectedElement.ENVIRONMENT;
					}
					if(newValue.getText().equals("General")) {
						dataSight.selectedElement = SelectedElement.GENERAL;
					}
					if(newValue.getText().equals("Rangefinder")) {
						dataSight.selectedElement = SelectedElement.RANGEFINDER;
					}
					if(newValue.getText().equals("Horz. Range Indicators")) {
						dataSight.selectedElement = SelectedElement.HORZ_RANGE;
					}
					if(newValue.getText().equals("Ballistic Range Indicators")) {
						dataSight.selectedElement = SelectedElement.BALL_RANGE;
						dataSight.selectedSubElement = (dataSight != null ? dataSight.brIndicators.name : "");
					}
					if(newValue.getText().equals("Shell Ballistics Blocks")) {
						dataSight.selectedElement = SelectedElement.SHELL_BLOCK;
						dataSight.selectedSubElement = (controllerShellBlocks.selectedBlock != null ? controllerShellBlocks.selectedBlock.name : "");
					}
					if(newValue.getText().equals("Custom Elements")) {
						dataSight.selectedElement = SelectedElement.CUSTOM_ELEMENT;
						dataSight.selectedSubElement = (controllerObjects.selectedObject != null ? controllerObjects.selectedObject.name : "");
					}
				}
				repaintCanvas();
			}
			
		});
		
		// environment
		try {
			FXMLLoader loader = new FXMLLoader(UISightEditor.class.getResource("/ui/layout_sighteditor_environment.fxml"));
			Parent root = (Parent) loader.load();
			controllerEnvironment = (UIEnvironment) loader.getController();
			controllerEnvironment.setEditor(this);
			controllerEnvironment.setDataSight(dataSight);
			paneEnvironment.getChildren().add(root);
			AnchorPane.setTopAnchor(root, 0.0);
			AnchorPane.setBottomAnchor(root, 0.0);
			AnchorPane.setLeftAnchor(root, 0.0);
			AnchorPane.setRightAnchor(root, 0.0);
			controllerEnvironment.create();
		} catch (IOException e) {
			Logger.get().error(e);;
		}
		
		// general
		try {
			FXMLLoader loader = new FXMLLoader(UISightEditor.class.getResource("/ui/layout_sighteditor_general.fxml"));
			Parent root = (Parent) loader.load();
			controllerGeneral = (UIGeneral) loader.getController();
			controllerGeneral.setEditor(this);
			controllerGeneral.setDataSight(dataSight);
			paneGeneral.getChildren().add(root);
			AnchorPane.setTopAnchor(root, 0.0);
			AnchorPane.setBottomAnchor(root, 0.0);
			AnchorPane.setLeftAnchor(root, 0.0);
			AnchorPane.setRightAnchor(root, 0.0);
			controllerGeneral.create();
		} catch (IOException e) {
			Logger.get().error(e);;
		}
		
		// rangefinder
		try {
			FXMLLoader loader = new FXMLLoader(UISightEditor.class.getResource("/ui/layout_sighteditor_rangefinder.fxml"));
			Parent root = (Parent) loader.load();
			controllerRangefinder = (UIRangefinder) loader.getController();
			controllerRangefinder.setEditor(this);
			controllerRangefinder.setDataSight(dataSight);
			paneRangefinder.getChildren().add(root);
			AnchorPane.setTopAnchor(root, 0.0);
			AnchorPane.setBottomAnchor(root, 0.0);
			AnchorPane.setLeftAnchor(root, 0.0);
			AnchorPane.setRightAnchor(root, 0.0);
			controllerRangefinder.create();
		} catch (IOException e) {
			Logger.get().error(e);;
		}
		
		// horz range
		try {
			FXMLLoader loader = new FXMLLoader(UISightEditor.class.getResource("/ui/layout_sighteditor_horzRange.fxml"));
			Parent root = (Parent) loader.load();
			controllerHorzRange = (UIHorzRange) loader.getController();
			controllerHorzRange.setEditor(this);
			controllerHorzRange.setDataSight(dataSight);
			paneHorzRange.getChildren().add(root);
			AnchorPane.setTopAnchor(root, 0.0);
			AnchorPane.setBottomAnchor(root, 0.0);
			AnchorPane.setLeftAnchor(root, 0.0);
			AnchorPane.setRightAnchor(root, 0.0);
			controllerHorzRange.create();
		} catch (IOException e) {
			Logger.get().error(e);;
		}
		
		// ballistic range
		try {
			FXMLLoader loader = new FXMLLoader(UISightEditor.class.getResource("/ui/layout_sighteditor_ballRange_v2.fxml"));
			Parent root = (Parent) loader.load();
			controllerBallisticRange = (UIBallisticRange) loader.getController();
			controllerBallisticRange.setEditor(this);
			controllerBallisticRange.setDataSight(dataSight);
			paneBallRange.getChildren().add(root);
			AnchorPane.setTopAnchor(root, 0.0);
			AnchorPane.setBottomAnchor(root, 0.0);
			AnchorPane.setLeftAnchor(root, 0.0);
			AnchorPane.setRightAnchor(root, 0.0);
			controllerBallisticRange.create();
		} catch (IOException e) {
			Logger.get().error(e);;
		}
		
		
		// shell blocks
		try {
			FXMLLoader loader = new FXMLLoader(UISightEditor.class.getResource("/ui/layout_sighteditor_shellBlock.fxml"));
			Parent root = (Parent) loader.load();
			controllerShellBlocks = (UIShellBlocks) loader.getController();
			controllerShellBlocks.setEditor(this);
			controllerShellBlocks.setDataSight(dataSight);
			controllerShellBlocks.setDataCalib(dataCalib);
			paneShellBlocks.getChildren().add(root);
			AnchorPane.setTopAnchor(root, 0.0);
			AnchorPane.setBottomAnchor(root, 0.0);
			AnchorPane.setLeftAnchor(root, 0.0);
			AnchorPane.setRightAnchor(root, 0.0);
			controllerShellBlocks.create();
		} catch (IOException e) {
			Logger.get().error(e);;
		}
		
		
		// custom elements
		try {
			FXMLLoader loader = new FXMLLoader(UISightEditor.class.getResource("/ui/layout_sighteditor_objects.fxml"));
			Parent root = (Parent) loader.load();
			controllerObjects = (UIObjects) loader.getController();
			controllerObjects.setEditor(this);
			controllerObjects.setDataSight(dataSight);
			paneCustomElements.getChildren().add(root);
			AnchorPane.setTopAnchor(root, 0.0);
			AnchorPane.setBottomAnchor(root, 0.0);
			AnchorPane.setLeftAnchor(root, 0.0);
			AnchorPane.setRightAnchor(root, 0.0);
			controllerObjects.create();
		} catch (IOException e) {
			Logger.get().error(e);;
		}
		
		Logger.get().debug("SightEditor created");
		
		rebuildCanvas();
	}

	
	
	
	void onAmmoSelected(String ammoName) {
		currentAmmoData = null;
		for(int i=0; i<dataCalib.ammoData.size(); i++) {
			CalibrationAmmoData ammoData = dataCalib.ammoData.get(i);
			if(ammoData.ammo.name.equalsIgnoreCase(ammoName)) {
				currentAmmoData = ammoData;
				break;
			}
		}
		Logger.get().debug("Selected ammo: " + (currentAmmoData == null ? "null" : currentAmmoData.ammo.name) );
		repaintCanvas();
	}
	
	
	
	
	void onZoomMode(boolean zoomedIn) {
		dataSight.envZoomedIn = zoomedIn;
		repaintCanvas();
	}
	
	
	
	
	@FXML
	void onSave(ActionEvent event) {
		
		FileChooser fc = new FileChooser();
		fc.setTitle("Save Sight");
		
		File fileSelected = fc.showSaveDialog(stage);
		if(fileSelected == null) {
			return;
		}
		
		File file = new File(fileSelected.getAbsolutePath() + ".blk");
		
		try {
			if(!DataWriter.saveSight(dataSight, dataCalib, file)) {
				Logger.get().warn("(Alert) Sight could not be saved.");
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText(null);
				alert.setContentText("Calibration could not be saved.");
				alert.showAndWait();
			} else {
				Logger.get().info("Saved sight to " + file);
			}
			
		} catch (Exception e) {
			Logger.get().error(e);;
		}
		
	}
	
	
	
	
	public void rebuildCanvas() {
		
		
		if(dataSight.envBackground != null) {
			canvas = new Canvas(dataSight.envBackground.getWidth(), dataSight.envBackground.getHeight());
		} else {
			canvas = new Canvas(1920, 1080);
		}
		
		canvas.setOnMouseMoved(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				event.consume();
				cursorVisible = true;
				cursorPosition.set(event.getX(), event.getY());
			}
		});
		canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getButton() == MouseButton.PRIMARY) {
					event.consume();
					cursorVisible = true;
					cursorPosition.set(event.getX(), event.getY());
				}
			}
		});
		canvas.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				event.consume();
				cursorVisible = false;
			}
		});
		canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getButton() == MouseButton.PRIMARY) {
					event.consume();
					cursorVisible = true;
				}
			}
		});
		canvas.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getButton() == MouseButton.PRIMARY) {
					event.consume();
					cursorVisible = true;
				}
			}
		});
	
		
		paneCanvasControl = new ZoomableScrollPane(canvas);
		paneCanvas.getChildren().add(paneCanvasControl);
		AnchorPane.setLeftAnchor(paneCanvasControl, 0.0);
		AnchorPane.setRightAnchor(paneCanvasControl, 0.0);
		AnchorPane.setTopAnchor(paneCanvasControl, 0.0);
		AnchorPane.setBottomAnchor(paneCanvasControl, 0.0);

		repaintCanvas();
		
	}
	
	
	
	
	public void repaintCanvas() {
		if(canvas != null) {
			GraphicsContext g = canvas.getGraphicsContext2D();
			SightRenderer.draw(canvas, g, dataSight, dataCalib, currentAmmoData);
		}
	}


}
