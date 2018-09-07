package com.ruegnerlukas.wtsights.ui.sighteditor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationAmmoData;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.data.vehicle.Ammo;
import com.ruegnerlukas.wtsights.ui.AmmoIcons;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Callback;

public class UIEnvironment {

	private UISightEditor editor;

	@FXML private ComboBox<Ammo> comboAmmo;
	@FXML private ChoiceBox<String> choiceZoomMode;
	@FXML private CheckBox cbShowRangefinder;
	@FXML private Slider sliderRangefinderProgress;
	@FXML private Slider sliderRangeCorrection;
	@FXML private CheckBox cbCrosshairLighting;
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
		editor.repaintCanvas();
	}
	

	
	
	void onZoomMode(boolean zoomedIn) {
		editor.getSightData().envZoomedIn = zoomedIn;
		editor.repaintCanvas();
	}
	
	
	
	
	@FXML
	void onShowRangefinder(ActionEvent event) {
		editor.getSightData().envShowRangeFinder = cbShowRangefinder.isSelected();
		editor.repaintCanvas();
	}
	
	
	
	
	void onRangefinderProgress(double progress) {
		editor.getSightData().envRFProgress = progress;
		labelValueRFProgress.setText(progress+"%");
		editor.repaintCanvas();
	}
	
	
	
	
	void onRangeCorrection(int range) {
		editor.getSightData().envRangeCorrection = range;//(range+49)/50 * 50;
		labelValueRange.setText(range+"m");
		editor.repaintCanvas();
	}
	
	
	
	
	@FXML
	void onCrosshairLighting(ActionEvent event) {
		boolean enabled = cbCrosshairLighting.isSelected();
		if(enabled) {
			editor.getSightData().envSightColor = new Color(1.0, 75.0/255.0, 55.0/255.0, 1.0);
		} else {
			editor.getSightData().envSightColor = Color.BLACK;
		}
		editor.repaintCanvas();
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
				editor.rebuildCanvas(width, height);
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
		editor.rebuildCanvas(width, height);
	}

	
	

	void onSelectResolution(int width, int height) {
		Logger.get().info("Resolution selected: " + width  + "x" + height);
		editor.rebuildCanvas(width, height);
	}
	


	
}
