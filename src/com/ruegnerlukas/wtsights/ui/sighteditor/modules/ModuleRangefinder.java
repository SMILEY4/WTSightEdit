package com.ruegnerlukas.wtsights.ui.sighteditor.modules;

import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.sight.sightElements.Element;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementRangefinder;
import com.ruegnerlukas.wtsights.ui.sighteditor.SightEditorController;
import com.ruegnerlukas.wtsights.ui.sighteditor.StepSizes;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;
import com.ruegnerlukas.wtutils.Conversion;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Spinner;
import javafx.scene.paint.Color;

public class ModuleRangefinder implements Module {

	private ElementRangefinder element;

	@FXML private Spinner<Integer> spinnerPosX;
	@FXML private Spinner<Double> spinnerPosY;
	@FXML private CheckBox cbUseThousandth;
	@FXML private Spinner<Double> spinnerTextScale;
	@FXML private ColorPicker colorPicker1;
	@FXML private ColorPicker colorPicker2;


	
	
	
	
	@Override
	public void create(DataPackage data) {
		
		// get element with default values
		ElementRangefinder elementDefault = new ElementRangefinder();
		
		// xpos
		FXUtils.initSpinner(spinnerPosX, elementDefault.position.x, -1000, 1000, StepSizes.STEP_PIXEL, StepSizes.DECPLACES_PIXEL, new ChangeListener<Integer>() {
			@Override public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				if(element != null) {
					element.position.x = newValue.intValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		// ypos
		FXUtils.initSpinner(spinnerPosY, elementDefault.position.y, -1000, 1000, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.position.y = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		// thousandth
		cbUseThousandth.setSelected(elementDefault.useThousandth);
		cbUseThousandth.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				if(element == null) { return; }
				element.useThousandth = cbUseThousandth.isSelected();
				if(element.useThousandth) {
					FXUtils.initSpinner(spinnerPosY, Conversion.get().screenspace2mil(element.position.y, data.dataSight.envZoomedIn), -1000, 1000, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, new ChangeListener<Double>() {
						@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
							if(element != null) {
								element.position.y = newValue.intValue();
							}
						}
					});
				} else {
					FXUtils.initSpinner(spinnerPosY, Conversion.get().mil2screenspace(element.position.y, data.dataSight.envZoomedIn), -1000, 1000, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, new ChangeListener<Double>() {
						@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
							if(element != null) {
								element.position.y = newValue.intValue();
							}
						}
					});
				}
				element.setDirty(true);
				((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
			}
		});

		// text scale
		FXUtils.initSpinner(spinnerTextScale, elementDefault.textScale, 0, 1000, StepSizes.STEP_SCALE, StepSizes.DECPLACES_SCALE, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.textScale = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		// color
		colorPicker1.setValue(new Color(elementDefault.color1.getRed(), elementDefault.color1.getGreen(), elementDefault.color1.getBlue(), elementDefault.color1.getOpacity()));
		colorPicker1.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				if(element == null) { return; }
				element.color1 = new Color((int)(colorPicker1.getValue().getRed()), (int)(colorPicker1.getValue().getGreen()), (int)(colorPicker1.getValue().getBlue()), (int)(colorPicker1.getValue().getOpacity()));
				element.setDirty(true);
				((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
			}
		});
		
		colorPicker2.setValue(new Color(elementDefault.color2.getRed(), elementDefault.color2.getGreen(), elementDefault.color2.getBlue(), elementDefault.color2.getOpacity()));
		colorPicker2.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				if(element == null) { return; }
				element.color2 = new Color((int)(colorPicker2.getValue().getRed()), (int)(colorPicker2.getValue().getGreen()), (int)(colorPicker2.getValue().getBlue()), (int)(colorPicker2.getValue().getOpacity()));
				element.setDirty(true);
				((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
			}
		});
		
		setElement(null);
	}

	
	
	
	public void setElement(Element e) {
		if(e == null || e.type != ElementType.RANGEFINDER) {
			this.element = null;
		} else {
			this.element = (ElementRangefinder)e;
		}
		if(this.element != null) {
			spinnerPosX.getValueFactory().setValue((int)element.position.x);
			spinnerPosY.getValueFactory().setValue(element.position.y);
			cbUseThousandth.setSelected(element.useThousandth);
			spinnerTextScale.getValueFactory().setValue(element.textScale);
			colorPicker1.setValue(new Color(element.color1.getRed(), element.color1.getGreen(), element.color1.getBlue(), element.color1.getOpacity()));
			colorPicker2.setValue(new Color(element.color2.getRed(), element.color2.getGreen(), element.color2.getBlue(), element.color2.getOpacity()));
		}
	}
	
	
	
}
