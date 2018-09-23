package com.ruegnerlukas.wtsights.ui.sighteditor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticElement;
import com.ruegnerlukas.wtsights.data.ballisticdata.NullElement;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementType;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
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

	@FXML private ComboBox<BallisticElement> comboAmmo;
	@FXML private ChoiceBox<String> choiceZoomMode;
	
	@FXML private CheckBox cbShowRangefinder;
	
	@FXML private Slider sliderRangefinderProgress;
	@FXML private Slider sliderRangeCorrection;
	@FXML private Spinner<Integer> spinnerRange;
	
	@FXML private CheckBox cbCrosshairLighting;
	
	@FXML private CheckBox cbDisplayGrid;
	@FXML private Spinner<Double> spinnerGridWidth;
	@FXML private Spinner<Double> spinnerGridHeight;
	@FXML private ColorPicker colorGrid;

	
	@FXML private TextField pathBackground;
	
	@FXML private ChoiceBox<String> choiceResolution;

	@FXML private Label labelValueRFProgress;

	


	
	public void setEditor(UISightEditor editor) {
		this.editor = editor;
	}
	
	
	
	
	public void create() {
		
		// AMMO
		FXUtils.initComboboxBallistic(comboAmmo);
		comboAmmo.getItems().addAll(editor.getData().dataBallistic.elements);
		if(comboAmmo.getItems().isEmpty()) {
			comboAmmo.getItems().add(new NullElement());
		}
		
		comboAmmo.getSelectionModel().select(0);
		comboAmmo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BallisticElement>() {
			@Override
			public void changed(ObservableValue<? extends BallisticElement> observable, BallisticElement oldValue, BallisticElement newValue) {
				onBallElementSelected(newValue);
			}
		});
		onBallElementSelected(comboAmmo.getSelectionModel().getSelectedItem());
		
		
		
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
		onZoomMode(editor.getData().dataSight.envZoomedIn);
		
		
		// RANGEFINDER
		cbShowRangefinder.setSelected(editor.getData().dataSight.envShowRangeFinder);
		sliderRangefinderProgress.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				onRangefinderProgress(newValue.intValue());
			}
		});
		sliderRangefinderProgress.setValue(editor.getData().dataSight.envRFProgress);
		onRangefinderProgress(editor.getData().dataSight.envRFProgress);
		
		// RANGE CORRECTION
		FXUtils.initSpinner(spinnerRange, 0, 0, 4000, 10, 0, true, new ChangeListener<Integer>() {
			@Override public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				onRangeCorrection(newValue.intValue());
			}
		});
		sliderRangeCorrection.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				onRangeCorrection(newValue.intValue());
			}
		});
		sliderRangeCorrection.setValue(editor.getData().dataSight.envRangeCorrection);
		onRangeCorrection(editor.getData().dataSight.envRangeCorrection);
		
		// CROSSHAIR LIGHTING
		cbCrosshairLighting.setSelected(false);
		
		
		// GRID OVERLAY
		cbDisplayGrid.setSelected(editor.getData().dataSight.envDisplayGrid);
		cbDisplayGrid.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				editor.getData().dataSight.envDisplayGrid = cbDisplayGrid.isSelected();
				editor.wtCanvas.repaint();
			}
		});

		FXUtils.initSpinner(spinnerGridWidth, editor.getData().dataSight.envGridWidth, 2, 9999, 0.5, 1, true, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				editor.getData().dataSight.envGridWidth = newValue;
				editor.wtCanvas.repaint();
			}
		});
		FXUtils.initSpinner(spinnerGridHeight, editor.getData().dataSight.envGridHeight, 2, 9999, 0.5, 1, true, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				editor.getData().dataSight.envGridHeight = newValue;
				editor.wtCanvas.repaint();
			}
		}); 
		
		colorGrid.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				editor.getData().dataSight.envColorGrid = colorGrid.getValue();
				editor.wtCanvas.repaint();
			}
		});
		colorGrid.setValue(editor.getData().dataSight.envColorGrid);
		
		
		
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
	
	
	

	
	
	void onBallElementSelected(BallisticElement element) {
		editor.getData().elementBallistic = element;
		Logger.get().debug("Selected ballistic element: " + (editor.getData().elementBallistic == null ? "null" : editor.getData().elementBallistic) );
		editor.getData().dataSight.setElementsDirty();
		editor.wtCanvas.repaint();
	}
	

	
	
	void onZoomMode(boolean zoomedIn) {
		editor.getData().dataSight.envZoomedIn = zoomedIn;
		editor.getData().dataSight.setElementsDirty();
		editor.wtCanvas.repaint();
	}
	
	
	
	
	@FXML
	void onShowRangefinder(ActionEvent event) {
		editor.getData().dataSight.envShowRangeFinder = cbShowRangefinder.isSelected();
		editor.getData().dataSight.setElementsDirty(ElementType.RANGEFINDER);
		editor.wtCanvas.repaint();
	}
	
	
	
	
	void onRangefinderProgress(double progress) {
		editor.getData().dataSight.envRFProgress = progress;
		labelValueRFProgress.setText(progress+"%");
		editor.getData().dataSight.setElementsDirty(ElementType.RANGEFINDER);
		editor.wtCanvas.repaint();
	}
	
	
	
	
	void onRangeCorrection(int range) {
		editor.getData().dataSight.envRangeCorrection = range;
		editor.getData().dataSight.setElementsDirty();
		spinnerRange.getValueFactory().setValue(editor.getData().dataSight.envRangeCorrection);
		sliderRangeCorrection.setValue(editor.getData().dataSight.envRangeCorrection);
		editor.wtCanvas.repaint();
	}
	
	
	
	
	@FXML
	void onCrosshairLighting(ActionEvent event) {
		boolean enabled = cbCrosshairLighting.isSelected();
		if(enabled) {
			editor.getData().dataSight.envSightColor = new Color(1.0, 75.0/255.0, 55.0/255.0, 1.0);
		} else {
			editor.getData().dataSight.envSightColor = Color.BLACK;
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
				editor.getData().dataSight.envBackground = new Image(new FileInputStream(file));
				pathBackground.setText(file.getAbsolutePath());
				Logger.get().info("Selected background: " + file);
				int width = (int)editor.getData().dataSight.envBackground.getWidth();
				int height = (int)editor.getData().dataSight.envBackground.getHeight();
				
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
		editor.getData().dataSight.envBackground = null;
		choiceResolution.setDisable(false);
		int width = Integer.parseInt(choiceResolution.getValue().split(" x ")[0]);
		int height = Integer.parseInt(choiceResolution.getValue().split(" x ")[1]);
		editor.wtCanvas.rebuildCanvas(width, height);
	}

	
	

	void onSelectResolution(int width, int height) {
		Logger.get().info("Resolution selected: " + width  + "x" + height);
		editor.wtCanvas.rebuildCanvas(width, height);
		editor.getData().dataSight.setElementsDirty();
	}
	


	
}
