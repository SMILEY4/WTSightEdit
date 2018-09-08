package com.ruegnerlukas.wtsights.ui.sighteditor.modules;

import com.ruegnerlukas.wtsights.data.sight.elements.Element;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCustomCircle;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCustomLine;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementType;
import com.ruegnerlukas.wtsights.renderer.Conversion;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCustomObject.Movement;
import com.ruegnerlukas.wtsights.ui.sighteditor.UISightEditor;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;

public class UICustomCircle implements Module {

	private UISightEditor editor;
	private ElementCustomCircle element;

	@FXML private CheckBox cbUseThousandth;
	@FXML private ChoiceBox<String> choiceMovement;
	@FXML private Spinner<Double> spinnerAngle;
	@FXML private CheckBox cbUseAutoCenter;
	@FXML private Spinner<Double> spinnerCenterX;
	@FXML private Spinner<Double> spinnerCenterY;
	@FXML private Spinner<Double> spinnerOriginX;
	@FXML private Spinner<Double> spinnerOriginY;
	@FXML private Spinner<Double> spinnerSpeed;
	
	@FXML private Spinner<Double> spinnerPosX;
	@FXML private Spinner<Double> spinnerPosY;
	@FXML private Spinner<Double> spinnerSegment1;
	@FXML private Spinner<Double> spinnerSegment2;
	@FXML private Spinner<Double> spinnerThickness;
	@FXML private Spinner<Double> spinnerDiameter;
	
	

	@Override
	public void setEditor(UISightEditor editor) {
		this.editor = editor;
	}
	
	
	
	
	@Override
	public void create() {
		
		// get element with default values
		ElementCustomCircle elementDefault = new ElementCustomCircle();
		
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
		
		
		FXUtils.initSpinner(spinnerPosX, elementDefault.position.x, -9999, 9999, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.position.x = newValue.doubleValue();
					editor.wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerPosY, elementDefault.position.y, -9999, 9999, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.position.y = newValue.doubleValue();
					editor.wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerSegment1, elementDefault.segment.x, 0, 360, 1, 1, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.segment.x = newValue.doubleValue();
					editor.wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerSegment2, elementDefault.segment.y, 0, 360, 1, 1, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.segment.y = newValue.doubleValue();
					editor.wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerThickness, elementDefault.size, 0, 1000, 0.5, 1, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.size = newValue.doubleValue();
					editor.wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerDiameter, elementDefault.diameter, 0, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.diameter = newValue.doubleValue();
					editor.wtCanvas.repaint();
				}
			}
		});
		
		setElement(null);
	}

	
	
	
	public void setElement(Element e) {
		if(e == null || e.type != ElementType.CUSTOM_CIRCLE) {
			this.element = null;
		} else {
			this.element = (ElementCustomCircle)e;
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

			spinnerPosX.getValueFactory().setValue(element.position.x);
			spinnerPosY.getValueFactory().setValue(element.position.y);
			spinnerSegment1.getValueFactory().setValue(element.segment.x);
			spinnerSegment2.getValueFactory().setValue(element.segment.y);
			spinnerThickness.getValueFactory().setValue(element.size);
			spinnerDiameter.getValueFactory().setValue(element.diameter);

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
				FXUtils.initSpinner(spinnerPosX, Conversion.get().screenspace2mil(spinnerPosX.getValue(), editor.getSightData().envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);
				FXUtils.initSpinner(spinnerPosY, Conversion.get().screenspace2mil(spinnerPosY.getValue(), editor.getSightData().envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);
				FXUtils.initSpinner(spinnerDiameter, Conversion.get().screenspace2mil(spinnerDiameter.getValue(), editor.getSightData().envZoomedIn), 0, Integer.MAX_VALUE, 0.5, 1, null);
			} else {
				FXUtils.initSpinner(spinnerPosX, Conversion.get().mil2screenspace(spinnerPosX.getValue(), editor.getSightData().envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);
				FXUtils.initSpinner(spinnerPosY, Conversion.get().mil2screenspace(spinnerPosY.getValue(), editor.getSightData().envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);
				FXUtils.initSpinner(spinnerDiameter, Conversion.get().mil2screenspace(spinnerDiameter.getValue(), editor.getSightData().envZoomedIn), 0, Integer.MAX_VALUE, 0.01, 2, null);
			}
			editor.wtCanvas.repaint();
		}
	}
	
	
	
	
	
	
}
