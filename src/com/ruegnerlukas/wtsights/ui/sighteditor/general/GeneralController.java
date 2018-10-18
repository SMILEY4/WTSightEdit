package com.ruegnerlukas.wtsights.ui.sighteditor.general;

import java.util.Map;

import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.ui.sighteditor.SightEditorController;
import com.ruegnerlukas.wtsights.ui.sighteditor.StepSizes;
import com.ruegnerlukas.wtsights.ui.view.IViewController;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.ParamKey;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;
import com.ruegnerlukas.wtutils.FXUtils;
import com.ruegnerlukas.wtutils.SightUtils.Thousandth;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;

public class GeneralController implements IViewController {

	@FXML private ComboBox<Thousandth> comboThousandth;
	@FXML private Spinner<Float> fontSize;
	@FXML private Spinner<Float> lineSize;
	@FXML private CheckBox cbApplyCorrection;

	private GeneralService service;
	

	@Override
	public void create(Map<ParamKey,Object> parameters) {
		service = (GeneralService) ViewManager.getService(View.SEM_GENERAL, true);
		service.setDataPackage((DataPackage)parameters.get(ParamKey.DATA_PACKAGE));
	
		// THOUSANDTH
		FXUtils.initComboboxThousandth(comboThousandth);
		comboThousandth.getItems().addAll(Thousandth.values());
		comboThousandth.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Thousandth>() {
			@Override
			public void changed(ObservableValue<? extends Thousandth> observable, Thousandth oldValue, Thousandth newValue) {
				service.setThousandth(newValue);
				((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
			}
		});
		comboThousandth.getSelectionModel().select(service.getThousandth());

		// font scale
		FXUtils.initSpinner(fontSize, service.getFontScale(), 0, 1000, StepSizes.STEP_SCALE, StepSizes.DECPLACES_SCALE, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				service.setFontScale(newValue.doubleValue());
				((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();			}
		});
		
		// line scale
		FXUtils.initSpinner(lineSize, service.getLineSize(), 0, 1000, StepSizes.STEP_SCALE, StepSizes.DECPLACES_SCALE, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				service.setLineSize(newValue.doubleValue());
				((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();			}
		});
		
		// apply correction
		cbApplyCorrection.setSelected(service.applyCorrectionToGun());
	}



	
	
	
	
	@FXML
	void onApplyCorrection(ActionEvent event) {
		service.setApplyCorrectionToGun(cbApplyCorrection.isSelected());
		((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
	}

	
}
