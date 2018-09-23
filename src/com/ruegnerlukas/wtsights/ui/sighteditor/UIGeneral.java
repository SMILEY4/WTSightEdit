package com.ruegnerlukas.wtsights.ui.sighteditor;

import com.ruegnerlukas.wtutils.FXUtils;
import com.ruegnerlukas.wtutils.SightUtils.Thousandth;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;

public class UIGeneral {

	private UISightEditor editor;
	
	@FXML private ChoiceBox<String> choiceThousandth;
	@FXML private Spinner<Float> fontSize;
	@FXML private Spinner<Float> lineSize;
	@FXML private CheckBox cbApplyCorrection;

	
	

	
	
	public void setEditor(UISightEditor editor) {
		this.editor = editor;
	}




	public void create() {
		
		for(Thousandth t : Thousandth.values()) {
			choiceThousandth.getItems().add(t.display);
		}
		choiceThousandth.getSelectionModel().select(editor.getData().dataSight.gnrThousandth.display);
		choiceThousandth.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				Thousandth thousandth = null;
				for(Thousandth t : Thousandth.values()) {
					if(t.display.equalsIgnoreCase(newValue)) {
						thousandth = t;
						break;
					}
				}
				if(thousandth != null) { selectThousandth(thousandth); }
			}
		});
		
		
		// font scale
		FXUtils.initSpinner(fontSize, editor.getData().dataSight.gnrFontScale, 0, 1000, 0.1, 1, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				setFontSize(newValue.floatValue());
			}
		});
		
		// line scale
		FXUtils.initSpinner(lineSize, editor.getData().dataSight.gnrLineSize, 0, 1000, 0.5, 1, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				setLineSize(newValue.floatValue());
			}
		});
		
		// apply correction
		cbApplyCorrection.setSelected(editor.getData().dataSight.gnrApplyCorrectionToGun);
	
	}
	
	
	
	
	@FXML
	void onApplyCorrection(ActionEvent event) {
		editor.getData().dataSight.gnrApplyCorrectionToGun = cbApplyCorrection.isSelected();
		editor.getData().dataSight.setElementsDirty();
		editor.wtCanvas.repaint();
	}
	
	
	
	
	void selectThousandth(Thousandth thousandth) {
		editor.getData().dataSight.gnrThousandth = thousandth;
		editor.getData().dataSight.setElementsDirty();
		editor.wtCanvas.repaint();
	}
	
	

	
	void setFontSize(float size) {
		editor.getData().dataSight.gnrFontScale = size;
		editor.getData().dataSight.setElementsDirty();
		editor.wtCanvas.repaint();
	}
	
	
	
	
	void setLineSize(float size) {
		editor.getData().dataSight.gnrLineSize = size;
		editor.getData().dataSight.setElementsDirty();
		editor.wtCanvas.repaint();
	}
	
	
	
}
