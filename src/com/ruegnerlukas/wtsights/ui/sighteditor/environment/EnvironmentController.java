package com.ruegnerlukas.wtsights.ui.sighteditor.environment;

import java.io.File;
import java.util.Map;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticElement;
import com.ruegnerlukas.wtsights.ui.sighteditor.SightEditorController;
import com.ruegnerlukas.wtsights.ui.sighteditor.StepSizes;
import com.ruegnerlukas.wtsights.ui.view.IViewController;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.ParamKey;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;
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
import javafx.stage.FileChooser;

public class EnvironmentController implements IViewController {

	@FXML private ComboBox<BallisticElement> comboAmmo;
	@FXML private ChoiceBox<String> choiceZoomMode;
	
	@FXML private CheckBox cbShowRangefinder;
	@FXML private Label labelValueRFProgress;

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

	private EnvironmentService service;
	
	
	

	@Override
	public void create(Map<ParamKey, Object> parameters) {
		
		service = (EnvironmentService) ViewManager.getService(View.SEM_ENVIRONMENT, true);
		service.setDataPackage((DataPackage)parameters.get(ParamKey.DATA_PACKAGE));
		
		
		// AMMO
		FXUtils.initComboboxBallistic(comboAmmo);
		comboAmmo.getItems().addAll(service.getBallisticElements());
		comboAmmo.getSelectionModel().select(0);
		comboAmmo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BallisticElement>() {
			@Override
			public void changed(ObservableValue<? extends BallisticElement> observable, BallisticElement oldValue, BallisticElement newValue) {
				service.selectBallisticElement(newValue);
				((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
			}
		});
		service.selectBallisticElement(comboAmmo.getSelectionModel().getSelectedItem());
		
		
		// ZOOM MODE
		choiceZoomMode.getItems().add(ViewManager.getResources().getString("se_env_zoomed_out"));
		choiceZoomMode.getItems().add(ViewManager.getResources().getString("se_env_zoomed_in"));
		choiceZoomMode.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(newValue.equalsIgnoreCase(ViewManager.getResources().getString("se_env_zoomed_out"))) {
					service.setZoomMode(false);
				} else {
					service.setZoomMode(true);
				}
				((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
			}
		});
		choiceZoomMode.getSelectionModel().select(0);
		
		
		// RANGEFINDER
		cbShowRangefinder.setSelected(service.isRangefinderShown());
		sliderRangefinderProgress.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				onRangefinderProgress(newValue.intValue());
			}
		});
		sliderRangefinderProgress.setValue(service.getRangefinderProgress());
		onRangefinderProgress(service.getRangefinderProgress());
		
		
		// RANGE CORRECTION
		FXUtils.initSpinner(spinnerRange, 0, 0, 4000, StepSizes.STEP_RANGEMETER10, StepSizes.DECPLACES_RANGEMETER, true, new ChangeListener<Integer>() {
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
		sliderRangeCorrection.setValue(service.getRangeCorrection());
		onRangeCorrection(service.getRangeCorrection());
		
		
		// CROSSHAIR LIGHTING
		cbCrosshairLighting.setSelected(false);
		
		
		// GRID OVERLAY
		cbDisplayGrid.setSelected(service.isGridShown());
		cbDisplayGrid.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				service.showGrid(cbDisplayGrid.isSelected());
				((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
			}
		});

		FXUtils.initSpinner(spinnerGridWidth, service.getGridWidth(), 2, 9999, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, true, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				service.setGridWidth(newValue.doubleValue());
				((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
			}
		});
		FXUtils.initSpinner(spinnerGridHeight, service.getGridHeight(), 2, 9999, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, true, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				service.setGridHeight(newValue.doubleValue());
				((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
			}
		}); 
		
		colorGrid.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				service.setGridColor(colorGrid.getValue());
				((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
			}
		});
		colorGrid.setValue(service.getGridColor());
		
		
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
	
	
	
	
	@FXML
	void onShowRangefinder(ActionEvent event) {
		service.showRangefinder(cbShowRangefinder.isSelected());
		((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
	}
	
	
	
	
	void onRangefinderProgress(double progress) {
		service.setRangefinderProgress(progress);
		labelValueRFProgress.setText(progress+"%");
		((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
	}
	
	
	
	
	void onRangeCorrection(int range) {
		service.setRangeCorrection(range);
		spinnerRange.getValueFactory().setValue(service.getRangeCorrection());
		sliderRangeCorrection.setValue(service.getRangeCorrection());
		((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
	}
	
	
	
	
	@FXML
	void onCrosshairLighting(ActionEvent event) {
		service.setCrosshairLighting(cbCrosshairLighting.isSelected());
		((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
	}
	
	
	
	
	@FXML
	void onBrowseBackground(ActionEvent event) {
		FileChooser fc = new FileChooser();
		fc.setTitle("Select Background");
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(ViewManager.getResources().getString("se_env_background_file_type") + " (*.jpg, *.png)", "*.jpg", "*.png"));
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(ViewManager.getResources().getString("se_env_background_file_type") + " (*.png)", "*.png"));
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(ViewManager.getResources().getString("se_env_background_file_type") + " (*.jpg)", "*.jpg"));
		File file = fc.showOpenDialog(((Button)event.getSource()).getScene().getWindow());
		if (file != null) {
			service.setBackground(file);
			pathBackground.setText(file.getAbsolutePath());
			int width = (int)service.getBackground().getWidth();
			int height = (int)service.getBackground().getHeight();
			for(String res : choiceResolution.getItems()) {
				int w = Integer.parseInt(res.split(" x ")[0]);
				int h = Integer.parseInt(res.split(" x ")[1]);
				if(w == width && h == height) {
					choiceResolution.getSelectionModel().select(res);
					break;
				}
			}
			((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.rebuildCanvas(width, height);
		}
	}




	@FXML
	void onResetBackground(ActionEvent event) {
		pathBackground.setText("");
		service.setBackground(null);
		choiceResolution.setDisable(false);
		int width = Integer.parseInt(choiceResolution.getValue().split(" x ")[0]);
		int height = Integer.parseInt(choiceResolution.getValue().split(" x ")[1]);
		((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.rebuildCanvas(width, height);
	}

	
	

	void onSelectResolution(int width, int height) {
		service.setResolution(width, height);
		Logger.get().info("Resolution selected: " + width  + "x" + height);
		((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.rebuildCanvas(width, height);
	}
	
}
