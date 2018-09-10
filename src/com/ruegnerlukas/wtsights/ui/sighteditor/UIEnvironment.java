package com.ruegnerlukas.wtsights.ui.sighteditor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationAmmoData;
import com.ruegnerlukas.wtsights.data.vehicle.Ammo;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

public class UIEnvironment {

	private UISightEditor editor;

	@FXML private ComboBox<Ammo> comboAmmo;
	@FXML private ChoiceBox<String> choiceZoomMode;
	
	@FXML private CheckBox cbShowRangefinder;
	
	@FXML private Slider sliderRangefinderProgress;
	@FXML private Slider sliderRangeCorrection;
	
	@FXML private CheckBox cbCrosshairLighting;
	
	@FXML private CheckBox cbDisplayGrid;
	@FXML private Spinner<Double> spinnerGridWidth;
	@FXML private Spinner<Double> spinnerGridHeight;

	
	@FXML private TextField pathBackground;
	
	@FXML private ChoiceBox<String> choiceResolution;

	@FXML private Label labelValueRFProgress;
	@FXML private Label labelValueRange;

	


	
	public void setEditor(UISightEditor editor) {
		this.editor = editor;
	}
	
	
	
	
	public void create() {
		
		// AMMO
		FXUtils.initComboboxAmmo(comboAmmo);
		if(editor.getCalibrationData().ammoData.isEmpty()) {
			Ammo ammo = new Ammo();
			ammo.type = "undefined";
			ammo.name = "No Ammunition available";
			comboAmmo.getItems().add(ammo);
		} else {
			for(CalibrationAmmoData ammoData : editor.getCalibrationData().ammoData) {
				comboAmmo.getItems().add(ammoData.ammo);
			}
		}
		comboAmmo.getSelectionModel().select(0);
		comboAmmo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Ammo>() {
			@Override
			public void changed(ObservableValue<? extends Ammo> observable, Ammo oldValue, Ammo newValue) {
				onAmmoSelected(newValue);
			}
		});
		onAmmoSelected(comboAmmo.getSelectionModel().getSelectedItem());
		
		
		
		// ZOOM MODE
		choiceZoomMode.getItems().add("Show Zoomed Out");
		choiceZoomMode.getItems().add("Show Zoomed In");
		choiceZoomMode.getSelectionModel().select(0);
		choiceZoomMode.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(newValue.equalsIgnoreCase("Show Zoomed Out")) {
					onZoomMode(false);
				} else {
					onZoomMode(true);
				}
			}
		});
		onZoomMode(editor.getSightData().envZoomedIn);
		
		
		// RANGEFINDER
		cbShowRangefinder.setSelected(editor.getSightData().envShowRangeFinder);
		sliderRangefinderProgress.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				onRangefinderProgress(newValue.intValue());
			}
		});
		sliderRangefinderProgress.setValue(editor.getSightData().envRFProgress);
		onRangefinderProgress(editor.getSightData().envRFProgress);
		
		// RANGE CORRECTION
		sliderRangeCorrection.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				onRangeCorrection(newValue.intValue());
			}
		});
		sliderRangeCorrection.setValue(editor.getSightData().envRangeCorrection);
		onRangeCorrection(editor.getSightData().envRangeCorrection);
		
		// CROSSHAIR LIGHTING
		cbCrosshairLighting.setSelected(false);
		
		
		// GRID OVERLAY
		cbDisplayGrid.setSelected(editor.getSightData().envDisplayGrid);
		cbDisplayGrid.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				editor.getSightData().envDisplayGrid = cbDisplayGrid.isSelected();
				editor.wtCanvas.repaint();
			}
		});


		FXUtils.initSpinner(spinnerGridWidth, editor.getSightData().envGridWidth, 2, 9999, 0.5, 1, true, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				editor.getSightData().envGridWidth = newValue;
				editor.wtCanvas.repaint();
			}
		});
		FXUtils.initSpinner(spinnerGridHeight, editor.getSightData().envGridHeight, 2, 9999, 0.5, 1, true, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				editor.getSightData().envGridHeight = newValue;
				editor.wtCanvas.repaint();
			}
		}); 
		
		
		
		// RESOLUTION
		choiceResolution.getItems().add("1024 x 768");
		choiceResolution.getItems().add("1152 x 864");
		choiceResolution.getItems().add("1280 x 720");
		choiceResolution.getItems().add("1280 x 768");
		choiceResolution.getItems().add("1280 x 800");
		choiceResolution.getItems().add("1280 x 960");
		choiceResolution.getItems().add("1280 x 1024");
		choiceResolution.getItems().add("1360 x 768");
		choiceResolution.getItems().add("1366 x 768");
		choiceResolution.getItems().add("1400 x 1050");
		choiceResolution.getItems().add("1440 x 900");
		choiceResolution.getItems().add("1600 x 900");
		choiceResolution.getItems().add("1680 x 1050");
		choiceResolution.getItems().add("1920 x 1080");
		choiceResolution.getSelectionModel().select("1920 x 1080");
		choiceResolution.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				int width = Integer.parseInt(newValue.split(" x ")[0]);
				int height = Integer.parseInt(newValue.split(" x ")[1]);
				onSelectResolution(width, height);
			}
		});
		
	}
	
	
	

	
	
	void onAmmoSelected(Ammo ammo) {
		editor.setAmmoData(null);
		for(int i=0; i<editor.getCalibrationData().ammoData.size(); i++) {
			CalibrationAmmoData ammoData = editor.getCalibrationData().ammoData.get(i);
			if(ammoData.ammo.name.equalsIgnoreCase(ammo.name)) {
				editor.setAmmoData(ammoData);
				break;
			}
		}
		Logger.get().debug("Selected ammo: " + (editor.getAmmoData() == null ? "null" : editor.getAmmoData().ammo.name) );
		editor.wtCanvas.repaint();
	}
	

	
	
	void onZoomMode(boolean zoomedIn) {
		editor.getSightData().envZoomedIn = zoomedIn;
		editor.wtCanvas.repaint();
	}
	
	
	
	
	@FXML
	void onShowRangefinder(ActionEvent event) {
		editor.getSightData().envShowRangeFinder = cbShowRangefinder.isSelected();
		editor.wtCanvas.repaint();
	}
	
	
	
	
	void onRangefinderProgress(double progress) {
		editor.getSightData().envRFProgress = progress;
		labelValueRFProgress.setText(progress+"%");
		editor.wtCanvas.repaint();
	}
	
	
	
	
	void onRangeCorrection(int range) {
		editor.getSightData().envRangeCorrection = range;//(range+49)/50 * 50;
		labelValueRange.setText(range+"m");
		editor.wtCanvas.repaint();
	}
	
	
	
	
	@FXML
	void onCrosshairLighting(ActionEvent event) {
		boolean enabled = cbCrosshairLighting.isSelected();
		if(enabled) {
			editor.getSightData().envSightColor = new Color(1.0, 75.0/255.0, 55.0/255.0, 1.0);
		} else {
			editor.getSightData().envSightColor = Color.BLACK;
		}
		editor.wtCanvas.repaint();
	}
	
	
	
	
	@FXML
	void onBrowseBackground(ActionEvent event) {
		FileChooser fc = new FileChooser();
		fc.setTitle("Select Background");
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image (*.jpg, *.png)", "*.jpg", "*.png"));
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image (*.png)", "*.png"));
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image (*.jpg)", "*.jpg"));
		File file = fc.showOpenDialog(((Button)event.getSource()).getScene().getWindow());
		if (file != null) {
			try {
				editor.getSightData().envBackground = new Image(new FileInputStream(file));
				pathBackground.setText(file.getAbsolutePath());
				Logger.get().info("Selected background: " + file);
				int width = (int)editor.getSightData().envBackground.getWidth();
				int height = (int)editor.getSightData().envBackground.getHeight();
				
				for(String res : choiceResolution.getItems()) {
					int w = Integer.parseInt(res.split(" x ")[0]);
					int h = Integer.parseInt(res.split(" x ")[1]);
					if(w == width && h == height) {
						choiceResolution.getSelectionModel().select(res);
						break;
					}
				}
				editor.wtCanvas.rebuildCanvas(width, height);
			} catch (FileNotFoundException e) {
				Logger.get().error(e);
			}
		}
	}




	@FXML
	void onResetBackground(ActionEvent event) {
		pathBackground.setText("");
		editor.getSightData().envBackground = null;
		choiceResolution.setDisable(false);
		int width = Integer.parseInt(choiceResolution.getValue().split(" x ")[0]);
		int height = Integer.parseInt(choiceResolution.getValue().split(" x ")[1]);
		editor.wtCanvas.rebuildCanvas(width, height);
	}

	
	

	void onSelectResolution(int width, int height) {
		Logger.get().info("Resolution selected: " + width  + "x" + height);
		editor.wtCanvas.rebuildCanvas(width, height);
	}
	


	
}
