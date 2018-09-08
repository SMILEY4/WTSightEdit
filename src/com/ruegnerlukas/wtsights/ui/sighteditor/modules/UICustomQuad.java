package com.ruegnerlukas.wtsights.ui.sighteditor.modules;

import com.ruegnerlukas.wtsights.data.sight.elements.Element;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCustomLine;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementType;
import com.ruegnerlukas.wtsights.renderer.Conversion;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCustomObject.Movement;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCustomQuad;
import com.ruegnerlukas.wtsights.ui.sighteditor.UISightEditor;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;

public class UICustomQuad implements Module {

	private UISightEditor editor;
	private ElementCustomQuad element;

	@FXML private CheckBox cbUseThousandth;
	@FXML private ChoiceBox<String> choiceMovement;
	@FXML private Spinner<Double> spinnerAngle;
	@FXML private CheckBox cbUseAutoCenter;
	@FXML private Spinner<Double> spinnerCenterX;
	@FXML private Spinner<Double> spinnerCenterY;
	@FXML private Spinner<Double> spinnerOriginX;
	@FXML private Spinner<Double> spinnerOriginY;
	@FXML private Spinner<Double> spinnerSpeed;
	
	@FXML private Spinner<Double> spinnerPos1X;
	@FXML private Spinner<Double> spinnerPos1Y;
	@FXML private Spinner<Double> spinnerPos2X;
	@FXML private Spinner<Double> spinnerPos2Y;
	@FXML private Spinner<Double> spinnerPos3X;
	@FXML private Spinner<Double> spinnerPos3Y;
	@FXML private Spinner<Double> spinnerPos4X;
	@FXML private Spinner<Double> spinnerPos4Y;
	
	
	

	@Override
	public void setEditor(UISightEditor editor) {
		this.editor = editor;
	}
	
	
	
	
	@Override
	public void create() {
		
		// get element with default values
		ElementCustomQuad elementDefault = new ElementCustomQuad();
		
		cbUseThousandth.setSelected(elementDefault.useThousandth);
		
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
					editor.wtCanvas.repaint();
				}
			}
		});
		choiceMovement.getSelectionModel().select(elementDefault.movement.toString());
		
		FXUtils.initSpinner(spinnerAngle, elementDefault.angle, -360, 360, 0.5, 1, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.angle = newValue.doubleValue();
					editor.wtCanvas.repaint();
				}
			}
		});
		
		cbUseAutoCenter.setSelected(elementDefault.autoCenter);
		spinnerCenterX.setDisable(elementDefault.autoCenter);
		spinnerCenterY.setDisable(elementDefault.autoCenter);

		FXUtils.initSpinner(spinnerCenterX, elementDefault.center.x, -9999, 9999, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.center.x = newValue.doubleValue();
					editor.wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerCenterY, elementDefault.center.y, -9999, 9999, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.center.y = newValue.doubleValue();
					editor.wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerOriginX, elementDefault.radCenter.x, -9999, 9999, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.radCenter.x = newValue.doubleValue();
					editor.wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerOriginY, elementDefault.radCenter.y, -9999, 9999, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.radCenter.y = newValue.doubleValue();
					editor.wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerSpeed, elementDefault.speed, 0, 9999, 0.05, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.speed = newValue.doubleValue();
					editor.wtCanvas.repaint();
				}
			}
		});
		
		
		
		FXUtils.initSpinner(spinnerPos1X, elementDefault.pos1.x, -9999, 9999, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.pos1.x = newValue.doubleValue();
					editor.wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerPos1Y, elementDefault.pos1.y, -9999, 9999, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.pos1.y = newValue.doubleValue();
					editor.wtCanvas.repaint();
				}
			}
		});
		
		
		FXUtils.initSpinner(spinnerPos2X, elementDefault.pos2.x, -9999, 9999, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.pos2.x = newValue.doubleValue();
					editor.wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerPos2Y, elementDefault.pos2.y, -9999, 9999, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.pos2.y = newValue.doubleValue();
					editor.wtCanvas.repaint();
				}
			}
		});
		
		
		FXUtils.initSpinner(spinnerPos3X, elementDefault.pos3.x, -9999, 9999, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.pos3.x = newValue.doubleValue();
					editor.wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerPos3Y, elementDefault.pos3.y, -9999, 9999, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.pos3.y = newValue.doubleValue();
					editor.wtCanvas.repaint();
				}
			}
		});
		
		
		FXUtils.initSpinner(spinnerPos4X, elementDefault.pos4.x, -9999, 9999, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.pos4.x = newValue.doubleValue();
					editor.wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerPos4Y, elementDefault.pos4.y, -9999, 9999, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.pos4.y = newValue.doubleValue();
					editor.wtCanvas.repaint();
				}
			}
		});
		
		
		
		setElement(null);
	}

	
	
	
	public void setElement(Element e) {
		if(e == null || e.type != ElementType.CUSTOM_QUAD) {
			this.element = null;
		} else {
			this.element = (ElementCustomQuad)e;
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
			spinnerPos1X.getValueFactory().setValue(element.pos1.x);
			spinnerPos1Y.getValueFactory().setValue(element.pos1.y);
			spinnerPos2X.getValueFactory().setValue(element.pos2.x);
			spinnerPos2Y.getValueFactory().setValue(element.pos2.y);
			spinnerPos3X.getValueFactory().setValue(element.pos3.x);
			spinnerPos3Y.getValueFactory().setValue(element.pos3.y);
			spinnerPos4X.getValueFactory().setValue(element.pos4.x);
			spinnerPos4Y.getValueFactory().setValue(element.pos4.y);
		}
	}
	
	
	
	@FXML
	void onUseAutoCenter(ActionEvent event) {
		if(element != null) {
			element.useThousandth = cbUseThousandth.isSelected();
			spinnerCenterX.setDisable(element.autoCenter);
			spinnerCenterY.setDisable(element.autoCenter);
			editor.wtCanvas.repaint();
		}
	}
	
	
	
	@FXML
	void onUseThousandth(ActionEvent event) {
		if(element != null) {
			element.useThousandth = cbUseThousandth.isSelected();
			if(element.useThousandth) {
				FXUtils.initSpinner(spinnerPos1X, Conversion.get().screenspace2mil(spinnerPos1X.getValue(), editor.getSightData().envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);
				FXUtils.initSpinner(spinnerPos1Y, Conversion.get().screenspace2mil(spinnerPos1Y.getValue(), editor.getSightData().envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);
				FXUtils.initSpinner(spinnerPos2X, Conversion.get().screenspace2mil(spinnerPos2X.getValue(), editor.getSightData().envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);
				FXUtils.initSpinner(spinnerPos2Y, Conversion.get().screenspace2mil(spinnerPos2Y.getValue(), editor.getSightData().envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);
				FXUtils.initSpinner(spinnerPos3X, Conversion.get().screenspace2mil(spinnerPos3X.getValue(), editor.getSightData().envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);
				FXUtils.initSpinner(spinnerPos3Y, Conversion.get().screenspace2mil(spinnerPos3Y.getValue(), editor.getSightData().envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);
				FXUtils.initSpinner(spinnerPos4X, Conversion.get().screenspace2mil(spinnerPos4X.getValue(), editor.getSightData().envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);
				FXUtils.initSpinner(spinnerPos4Y, Conversion.get().screenspace2mil(spinnerPos4Y.getValue(), editor.getSightData().envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);
			} else {
				FXUtils.initSpinner(spinnerPos1X, Conversion.get().mil2screenspace(spinnerPos1X.getValue(), editor.getSightData().envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);
				FXUtils.initSpinner(spinnerPos1Y, Conversion.get().mil2screenspace(spinnerPos1Y.getValue(), editor.getSightData().envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);
				FXUtils.initSpinner(spinnerPos2X, Conversion.get().mil2screenspace(spinnerPos2X.getValue(), editor.getSightData().envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);
				FXUtils.initSpinner(spinnerPos2Y, Conversion.get().mil2screenspace(spinnerPos2Y.getValue(), editor.getSightData().envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);
				FXUtils.initSpinner(spinnerPos3X, Conversion.get().mil2screenspace(spinnerPos3X.getValue(), editor.getSightData().envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);
				FXUtils.initSpinner(spinnerPos3Y, Conversion.get().mil2screenspace(spinnerPos3Y.getValue(), editor.getSightData().envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);
				FXUtils.initSpinner(spinnerPos4X, Conversion.get().mil2screenspace(spinnerPos4X.getValue(), editor.getSightData().envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);
				FXUtils.initSpinner(spinnerPos4Y, Conversion.get().mil2screenspace(spinnerPos4Y.getValue(), editor.getSightData().envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);

			}
			editor.wtCanvas.repaint();
		}
	}
	
	
	
	
	
	
}
