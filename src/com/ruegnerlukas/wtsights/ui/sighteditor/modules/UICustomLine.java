package com.ruegnerlukas.wtsights.ui.sighteditor.modules;

import com.ruegnerlukas.wtsights.data.sight.elements.Element;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCustomLine;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCustomObject.Movement;
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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;

public class UICustomLine implements Module {

	private UISightEditor editor;
	private ElementCustomLine element;

	@FXML private CheckBox cbUseThousandth;
	@FXML private ChoiceBox<String> choiceMovement;
	@FXML private Spinner<Double> spinnerAngle;
	@FXML private CheckBox cbUseAutoCenter;
	@FXML private Spinner<Double> spinnerCenterX;
	@FXML private Spinner<Double> spinnerCenterY;
	@FXML private Spinner<Double> spinnerOriginX;
	@FXML private Spinner<Double> spinnerOriginY;
	@FXML private Spinner<Double> spinnerSpeed;
	
	@FXML private Spinner<Double> spinnerStartX;
	@FXML private Spinner<Double> spinnerStartY;
	@FXML private Spinner<Double> spinnerEndX;
	@FXML private Spinner<Double> spinnerEndY;
	
	
	

	@Override
	public void setEditor(UISightEditor editor) {
		this.editor = editor;
	}
	
	
	
	
	@Override
	public void create() {
		
		// get element with default values
		ElementCustomLine elementDefault = new ElementCustomLine();
		
		cbUseThousandth.setSelected(elementDefault.useThousandth);
		cbUseThousandth.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				if(element != null) {
					element.useThousandth = cbUseThousandth.isSelected();
					if(element.useThousandth) {
						FXUtils.initSpinner(spinnerStartX, Conversion.get().screenspace2mil(spinnerStartX.getValue(), editor.getData().dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);
						FXUtils.initSpinner(spinnerStartY, Conversion.get().screenspace2mil(spinnerStartY.getValue(), editor.getData().dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);
						FXUtils.initSpinner(spinnerEndX, Conversion.get().screenspace2mil(spinnerEndX.getValue(), editor.getData().dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);
						FXUtils.initSpinner(spinnerEndY, Conversion.get().screenspace2mil(spinnerEndY.getValue(), editor.getData().dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);
					} else {
						FXUtils.initSpinner(spinnerStartX, Conversion.get().mil2screenspace(spinnerStartX.getValue(), editor.getData().dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);
						FXUtils.initSpinner(spinnerStartY, Conversion.get().mil2screenspace(spinnerStartY.getValue(), editor.getData().dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);
						FXUtils.initSpinner(spinnerEndX, Conversion.get().mil2screenspace(spinnerEndX.getValue(), editor.getData().dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);
						FXUtils.initSpinner(spinnerEndY, Conversion.get().mil2screenspace(spinnerEndY.getValue(), editor.getData().dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);
					}
					element.layoutData.dirty = true;
					editor.wtCanvas.repaint();
				}
			}
		});
		
		choiceMovement.getItems().addAll(Movement.STATIC.toString(), Movement.MOVE.toString(), Movement.MOVE_RADIAL.toString());
		choiceMovement.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(element != null) {
					element.movement = Movement.get(newValue);
					spinnerAngle.setDisable(element.movement != Movement.MOVE_RADIAL);
					spinnerCenterX.setDisable(element.movement != Movement.MOVE_RADIAL);
					spinnerCenterY.setDisable(element.movement != Movement.MOVE_RADIAL);
					spinnerOriginX.setDisable(element.movement != Movement.MOVE_RADIAL);
					spinnerOriginY.setDisable(element.movement != Movement.MOVE_RADIAL);
					spinnerSpeed.setDisable(element.movement != Movement.MOVE_RADIAL);
					cbUseAutoCenter.setDisable(element.movement != Movement.MOVE_RADIAL);
					spinnerCenterX.setDisable(elementDefault.autoCenter);
					spinnerCenterY.setDisable(elementDefault.autoCenter);
					element.layoutData.dirty = true;
					editor.wtCanvas.repaint();
				}
			}
		});
		choiceMovement.getSelectionModel().select(elementDefault.movement.toString());
		
		FXUtils.initSpinner(spinnerAngle, elementDefault.angle, -360, 360, 0.5, 1, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.angle = newValue.doubleValue();
					element.layoutData.dirty = true;
					editor.wtCanvas.repaint();
				}
			}
		});
		
		cbUseAutoCenter.setSelected(elementDefault.autoCenter);
		cbUseAutoCenter.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				if(element != null) {
					element.useThousandth = cbUseThousandth.isSelected();
					spinnerCenterX.setDisable(element.autoCenter);
					spinnerCenterY.setDisable(element.autoCenter);
					element.layoutData.dirty = true;
					editor.wtCanvas.repaint();
				}
			}
		});
		spinnerCenterX.setDisable(elementDefault.autoCenter);
		spinnerCenterY.setDisable(elementDefault.autoCenter);

		FXUtils.initSpinner(spinnerCenterX, elementDefault.center.x, -9999, 9999, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.center.x = newValue.doubleValue();
					element.layoutData.dirty = true;
					editor.wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerCenterY, elementDefault.center.y, -9999, 9999, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.center.y = newValue.doubleValue();
					element.layoutData.dirty = true;
					editor.wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerOriginX, elementDefault.radCenter.x, -9999, 9999, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.radCenter.x = newValue.doubleValue();
					element.layoutData.dirty = true;
					editor.wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerOriginY, elementDefault.radCenter.y, -9999, 9999, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.radCenter.y = newValue.doubleValue();
					element.layoutData.dirty = true;
					editor.wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerSpeed, elementDefault.speed, 0, 9999, 0.05, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.speed = newValue.doubleValue();
					element.layoutData.dirty = true;
					editor.wtCanvas.repaint();
				}
			}
		});
		
		
		
		FXUtils.initSpinner(spinnerStartX, elementDefault.start.x, -9999, 9999, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.start.x = newValue.doubleValue();
					element.layoutData.dirty = true;
					editor.wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerStartY, elementDefault.start.y, -9999, 9999, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.start.y = newValue.doubleValue();
					element.layoutData.dirty = true;
					editor.wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerEndX, elementDefault.end.x, -9999, 9999, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.end.x = newValue.doubleValue();
					element.layoutData.dirty = true;
					editor.wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerEndY, elementDefault.end.y, -9999, 9999, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.end.y = newValue.doubleValue();
					element.layoutData.dirty = true;
					editor.wtCanvas.repaint();
				}
			}
		});
		
		
		setElement(null);
	}

	
	
	
	public void setElement(Element e) {
		if(e == null || e.type != ElementType.CUSTOM_LINE) {
			this.element = null;
		} else {
			this.element = (ElementCustomLine)e;
		}
		if(this.element != null) {
			cbUseThousandth.setSelected(element.useThousandth);
			choiceMovement.getSelectionModel().select(element.movement.toString());
			spinnerAngle.getValueFactory().setValue(element.angle);
			cbUseAutoCenter.setSelected(element.autoCenter);
			spinnerCenterX.setDisable(element.autoCenter);
			spinnerCenterY.setDisable(element.autoCenter);
			spinnerCenterX.getValueFactory().setValue(element.center.x);
			spinnerCenterY.getValueFactory().setValue(element.center.y);
			spinnerOriginX.getValueFactory().setValue(element.radCenter.x);
			spinnerOriginY.getValueFactory().setValue(element.radCenter.y);
			spinnerSpeed.getValueFactory().setValue(element.speed);

			spinnerAngle.setDisable(element.movement != Movement.MOVE_RADIAL);
			spinnerCenterX.setDisable(element.movement != Movement.MOVE_RADIAL);
			spinnerCenterY.setDisable(element.movement != Movement.MOVE_RADIAL);
			spinnerOriginX.setDisable(element.movement != Movement.MOVE_RADIAL);
			spinnerOriginY.setDisable(element.movement != Movement.MOVE_RADIAL);
			spinnerSpeed.setDisable(element.movement != Movement.MOVE_RADIAL);
			cbUseAutoCenter.setDisable(element.movement != Movement.MOVE_RADIAL);

			spinnerStartX.getValueFactory().setValue(element.start.x);
			spinnerStartY.getValueFactory().setValue(element.start.y);
			spinnerEndX.getValueFactory().setValue(element.end.x);
			spinnerEndY.getValueFactory().setValue(element.end.y);

		}
	}
	
	
	
	
}
