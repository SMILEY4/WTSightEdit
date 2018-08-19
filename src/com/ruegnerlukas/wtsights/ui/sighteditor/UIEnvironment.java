package com.ruegnerlukas.wtsights.ui.sighteditor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.sight.SightData;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

public class UIEnvironment {


	@FXML private ResourceBundle resources;
	@FXML private URL location;
	
	private UISightEditor editor;
	private SightData dataSight;

	@FXML private TextField pathBackground;
	@FXML private ChoiceBox<String> choiceResolution;
	@FXML private ColorPicker colorSight;
	@FXML private CheckBox cbShowVignette;
	@FXML private CheckBox cbShowRangefinder;
	@FXML private Slider rangefinderProgress;
	@FXML private Slider rangeCorrection;

	
	
	
	public void setEditor(UISightEditor editor) {
		this.editor = editor;
	}
	
	
	
	
	public void setDataSight(SightData data) {
		this.dataSight = data;
	}
	
	
	
	
	public void create() {
		
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
		choiceResolution.getSelectionModel().select("1280 x 720");
		choiceResolution.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				int width = Integer.parseInt(newValue.split(" x ")[0]);
				int height = Integer.parseInt(newValue.split(" x ")[1]);
				onSelectResolution(width, height);
			}
		});
		
		// sight color
		colorSight.setValue(dataSight.envSightColor);
		
		// rangefinder
		cbShowRangefinder.setSelected(dataSight.envShowRangeFinder);
		rangefinderProgress.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				onRangefinderProgress(newValue.intValue());
			}
		});
		rangefinderProgress.setValue(dataSight.envProgress);
		
		// range correction
		rangeCorrection.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				onRangeCorrection(newValue.intValue());
			}
		});
		rangeCorrection.setValue(dataSight.envRangeCorrection);
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
				dataSight.envBackground = new Image(new FileInputStream(file));
				pathBackground.setText(file.getAbsolutePath());
				Logger.get().info("Selected background: " + file);
				int width = (int)dataSight.envBackground.getWidth();
				int height = (int)dataSight.envBackground.getHeight();
				
				System.out.println("search " + width + " " + height);
				
				for(String res : choiceResolution.getItems()) {
					int w = Integer.parseInt(res.split(" x ")[0]);
					int h = Integer.parseInt(res.split(" x ")[1]);
					System.out.println("TEST " + w + "  " + h);
					if(w == width && h == height) {
						System.out.println(" -> select "+ res);
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
		dataSight.envBackground = null;
		int width = Integer.parseInt(choiceResolution.getValue().split(" x ")[0]);
		int height = Integer.parseInt(choiceResolution.getValue().split(" x ")[1]);
		editor.rebuildCanvas(width, height);
	}

	
	

	void onSelectResolution(int width, int height) {
		editor.rebuildCanvas(width, height);
	}
	

	
	
	@FXML
	void onColorSight(ActionEvent event) {
		ColorPicker picker = (ColorPicker)event.getSource();
		Color color = picker.getValue();
		dataSight.envSightColor = color;
		editor.repaintCanvas();
	}


	
	
	@FXML
	void onShowVignette(ActionEvent event) {
		dataSight.envShowVignette = cbShowVignette.isSelected();
		editor.repaintCanvas();
	}
	
	
	
	
	@FXML
	void onShowRangefinder(ActionEvent event) {
		dataSight.envShowRangeFinder = cbShowRangefinder.isSelected();
		editor.repaintCanvas();
	}

	
	

	void onRangefinderProgress(double progress) {
		dataSight.envProgress = progress;
		editor.repaintCanvas();
	}


	
	
	void onRangeCorrection(int range) {
		dataSight.envRangeCorrection = range;//(range+49)/50 * 50;
		editor.repaintCanvas();
	}
	
	

	
	@FXML
	void initialize() {
		assert pathBackground != null : "fx:id=\"pathBackground\" was not injected: check your FXML file 'layout_sighteditor_environment.fxml'.";
		assert colorSight != null : "fx:id=\"colorSight\" was not injected: check your FXML file 'layout_sighteditor_environment.fxml'.";
		assert cbShowRangefinder != null : "fx:id=\"cbShow\" was not injected: check your FXML file 'layout_sighteditor_rangefinder.fxml'.";
		assert rangefinderProgress != null : "fx:id=\"progress\" was not injected: check your FXML file 'layout_sighteditor_rangefinder.fxml'.";
	}
	
}
