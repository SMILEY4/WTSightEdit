package com.ruegnerlukas.wtsights.ui.sighteditor;

import java.net.URL;
import java.util.ResourceBundle;

import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.data.sight.SightData.Thousandth;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;

public class UIGeneral {


	@FXML private ResourceBundle resources;
	@FXML private URL location;

	private UISightEditor editor;
	private SightData dataSight;

	@FXML private ComboBox<String> thousands;
	@FXML private Spinner<Double> fontScale;
	@FXML private Spinner<Double> lineScale;
	@FXML private CheckBox cbCentralVert;
	@FXML private CheckBox cbCentralHorz;
	@FXML private CheckBox cbApplyCorrection;


	
	
	public void setEditor(UISightEditor editor) {
		this.editor = editor;
	}




	public void setDataSight(SightData data) {
		this.dataSight = data;
	}




	public void create() {
		
		for(Thousandth t : SightData.THOUSANDTH_LIST) {
			thousands.getItems().add(t.display);
		}
		thousands.getSelectionModel().select(dataSight.gnrThousandth.display);
		thousands.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				onThousands(newValue);
			}
		});
		
		
		// font scale
		FXUtils.initSpinner(fontScale, dataSight.gnrFontScale, 0, 1000, 0.1, 1, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onFontScale(newValue.doubleValue());
			}
		});
		
		// line scale
		FXUtils.initSpinner(lineScale, dataSight.gnrLineSize, 0, 1000, 0.5, 1, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onLineScale(newValue.doubleValue());
			}
		});
		
		// central lines
		cbCentralVert.setSelected(dataSight.gnrDrawCentralVertLine);
		cbCentralHorz.setSelected(dataSight.gnrDrawCentralHorzLine);
	
		// apply correction
		cbApplyCorrection.setSelected(dataSight.gnrApplyCorrectionToGun);
	
	}




	void onThousands(String strThousands) {
		Thousandth thousands = null;
		for(Thousandth t : SightData.THOUSANDTH_LIST) {
			if(t.display.equalsIgnoreCase(strThousands)) {
				thousands = t;
				break;
			}
		}
		if(thousands != null) {
			dataSight.gnrThousandth = thousands;
		}
		editor.repaintCanvas();
	}
	

	
	
	void onFontScale(double scale) {
		dataSight.gnrFontScale = scale;
		editor.repaintCanvas();
	}
	
	
	
	
	void onLineScale(double scale) {
		dataSight.gnrLineSize = scale;
		editor.repaintCanvas();
	}
	
	
	
	
	@FXML
	void onDrawCentralHorz(ActionEvent event) {
		dataSight.gnrDrawCentralHorzLine = cbCentralHorz.isSelected();
		editor.repaintCanvas();
	}




	@FXML
	void onDrawCentralVert(ActionEvent event) {
		dataSight.gnrDrawCentralVertLine = cbCentralVert.isSelected();
		editor.repaintCanvas();
	}


	
	
	@FXML
	void onApplyCorrection(ActionEvent event) {
		dataSight.gnrApplyCorrectionToGun = cbApplyCorrection.isSelected();
		editor.repaintCanvas();
	}
	


	
	@FXML
	void initialize() {
		assert thousands != null : "fx:id=\"thousands\" was not injected: check your FXML file 'layout_sighteditor_general.fxml'.";
		assert fontScale != null : "fx:id=\"fontScale\" was not injected: check your FXML file 'layout_sighteditor_general.fxml'.";
		assert lineScale != null : "fx:id=\"lineScale\" was not injected: check your FXML file 'layout_sighteditor_general.fxml'.";
	}
	
	
}
