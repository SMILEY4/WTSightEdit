package com.ruegnerlukas.wtsights.ui.sighteditor;

import java.net.URL;
import java.util.ResourceBundle;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.sight.Conversion;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.paint.Color;

public class UIRangefinder {


	@FXML private ResourceBundle resources;
	@FXML private URL location;

	private UISightEditor editor;
	private SightData dataSight;
	
	@FXML private Spinner<Integer> positionX;
	@FXML private Spinner<Double> positionY;
	@FXML private CheckBox cbUseThousandth;
	
	@FXML private Spinner<Double> textScale;
	
	@FXML private ColorPicker color1;
	@FXML private Spinner<Integer> alpha1;
	@FXML private ColorPicker color2;
	@FXML private Spinner<Integer> alpha2;


	
	
	
	
	public void setEditor(UISightEditor editor) {
		this.editor = editor;
	}
	
	
	
	
	public void setDataSight(SightData data) {
		this.dataSight = data;
	}
	
	
	
	
	public void create() {
		
		// xpos
		FXUtils.initSpinner(positionX, dataSight.rfOffset.x, -1000, 1000, 1, 0, new ChangeListener<Integer>() {
			@Override public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				onPosition(newValue.doubleValue(), positionY.getValue().doubleValue());
			}
		});
		
		// ypos
		FXUtils.initSpinner(positionY, dataSight.rfOffset.y, -1000, 1000, 1, 1, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onPosition(positionX.getValue().doubleValue(), newValue);
			}
		});
		
		
		cbUseThousandth.setSelected(dataSight.rfUseThousandth);
		
		// text scale
		FXUtils.initSpinner(textScale, dataSight.rfTextScale, 0, 1000, 0.1, 1, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onScale(newValue.doubleValue());
			}
		});
		
		// color 1
		color1.setValue(dataSight.rfColor1);
		FXUtils.initSpinner(alpha1, dataSight.rfColor1.getOpacity()*255, 0, 255, 10, 0, new ChangeListener<Integer>() {
			@Override public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				onColor1(null);
			}
		});
		
		
		// color 2
		color2.setValue(dataSight.rfColor2);
		FXUtils.initSpinner(alpha2, dataSight.rfColor2.getOpacity()*255, 0, 255, 10, 0, new ChangeListener<Integer>() {
			@Override public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				onColor2(null);
			}
		});
	}

	
	
	
	void onPosition(double x, double y) {
		dataSight.rfOffset.x = x;
		dataSight.rfOffset.y = y;
		editor.repaintCanvas();
	}
	
	
	
	
	@FXML
	void onUseThousandth(ActionEvent event) {
		
		dataSight.rfUseThousandth = cbUseThousandth.isSelected();
		
		if(dataSight.rfUseThousandth) {
			FXUtils.initSpinner(positionY, Conversion.get().screenspace2mil(dataSight.rfOffset.y, dataSight.envZoomedIn), -1000, 1000, 1, 1, new ChangeListener<Double>() {
				@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
					onPosition(positionX.getValue().doubleValue(), newValue);
				}
			});
		} else {
			FXUtils.initSpinner(positionY, Conversion.get().mil2screenspace(dataSight.rfOffset.y, dataSight.envZoomedIn), -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
				@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
					onPosition(positionX.getValue().doubleValue(), newValue);
				}
			});
		}
		
		editor.repaintCanvas();
	}
	
	
	
	
	void onScale(double scale) {
		dataSight.rfTextScale = scale;
		editor.repaintCanvas();
	}
	
	
	
	
	@FXML
	void onColor1(ActionEvent event) {
		dataSight.rfColor1 = new Color(color1.getValue().getRed(), color1.getValue().getGreen(), color1.getValue().getBlue(), alpha1.getValue()/255.0);
		editor.repaintCanvas();
	}
	
	
	
	
	@FXML
	void onColor2(ActionEvent event) {
		dataSight.rfColor2 = new Color(color2.getValue().getRed(), color2.getValue().getGreen(), color2.getValue().getBlue(), alpha2.getValue()/255.0);
		editor.repaintCanvas();
	}
	

	
	
	@FXML
	void initialize() {
		assert positionX != null : "fx:id=\"positionX\" was not injected: check your FXML file 'layout_sighteditor_rangefinder.fxml'.";
		assert positionY != null : "fx:id=\"positionY\" was not injected: check your FXML file 'layout_sighteditor_rangefinder.fxml'.";
		assert textScale != null : "fx:id=\"textScale\" was not injected: check your FXML file 'layout_sighteditor_rangefinder.fxml'.";
		assert color1 != null : "fx:id=\"color1\" was not injected: check your FXML file 'layout_sighteditor_rangefinder.fxml'.";
		assert alpha1 != null : "fx:id=\"alpha1\" was not injected: check your FXML file 'layout_sighteditor_rangefinder.fxml'.";
		assert color2 != null : "fx:id=\"color2\" was not injected: check your FXML file 'layout_sighteditor_rangefinder.fxml'.";
		assert alpha2 != null : "fx:id=\"alpha2\" was not injected: check your FXML file 'layout_sighteditor_rangefinder.fxml'.";
	}

}
