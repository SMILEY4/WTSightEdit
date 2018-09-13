package com.ruegnerlukas.wtsights.ui.sighteditor.modules;

import com.ruegnerlukas.wtsights.data.sight.elements.Element;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementRangefinder;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementType;
import com.ruegnerlukas.wtsights.ui.sighteditor.UISightEditor;
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

public class UIRangefinder implements Module {

	private UISightEditor editor;
	private ElementRangefinder element;

	@FXML private Spinner<Integer> spinnerPosX;
	@FXML private Spinner<Double> spinnerPosY;
	@FXML private CheckBox cbUseThousandth;
	@FXML private Spinner<Double> spinnerTextScale;
	@FXML private ColorPicker colorPicker1;
	@FXML private ColorPicker colorPicker2;


	

	@Override
	public void setEditor(UISightEditor editor) {
		this.editor = editor;
	}
	
	
	
	@Override
	public void create() {
		
		// get element with default values
		ElementRangefinder elementDefault = new ElementRangefinder();
		
		// xpos
		FXUtils.initSpinner(spinnerPosX, elementDefault.position.x, -1000, 1000, 1, 0, new ChangeListener<Integer>() {
			@Override public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				if(element != null) {
					element.position.x = newValue.intValue();
					element.layoutData.dirty = true;
					editor.wtCanvas.repaint();
				}
			}
		});
		
		// ypos
		FXUtils.initSpinner(spinnerPosY, elementDefault.position.y, -1000, 1000, 1, 1, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.position.y = newValue.doubleValue();
					element.layoutData.dirty = true;
					editor.wtCanvas.repaint();
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
					FXUtils.initSpinner(spinnerPosY, Conversion.get().screenspace2mil(element.position.y, editor.getSightData().envZoomedIn), -1000, 1000, 1, 1, new ChangeListener<Double>() {
						@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
							if(element != null) {
								element.position.y = newValue.intValue();
							}
						}
					});
				} else {
					FXUtils.initSpinner(spinnerPosY, Conversion.get().mil2screenspace(element.position.y, editor.getSightData().envZoomedIn), -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
						@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
							if(element != null) {
								element.position.y = newValue.intValue();
							}
						}
					});
				}
				element.layoutData.dirty = true;
				editor.wtCanvas.repaint();
			}
		});

		// text scale
		FXUtils.initSpinner(spinnerTextScale, elementDefault.textScale, 0, 1000, 0.1, 1, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.textScale = newValue.doubleValue();
					element.layoutData.dirty = true;
					editor.wtCanvas.repaint();
				}
			}
		});
		
		// color
		colorPicker1.setValue(new Color(elementDefault.color1.getRed(), elementDefault.color1.getGreen(), elementDefault.color1.getBlue(), elementDefault.color1.getOpacity()));
		colorPicker1.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				if(element == null) { return; }
				element.color1 = new Color((int)(colorPicker1.getValue().getRed()), (int)(colorPicker1.getValue().getGreen()), (int)(colorPicker1.getValue().getBlue()), (int)(colorPicker1.getValue().getOpacity()));
				element.layoutData.dirty = true;
				editor.wtCanvas.repaint();
			}
		});
		
		colorPicker2.setValue(new Color(elementDefault.color2.getRed(), elementDefault.color2.getGreen(), elementDefault.color2.getBlue(), elementDefault.color2.getOpacity()));
		colorPicker2.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				if(element == null) { return; }
				element.color2 = new Color((int)(colorPicker2.getValue().getRed()), (int)(colorPicker2.getValue().getGreen()), (int)(colorPicker2.getValue().getBlue()), (int)(colorPicker2.getValue().getOpacity()));
				element.layoutData.dirty = true;
				editor.wtCanvas.repaint();
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
