package com.ruegnerlukas.wtsights.ui.sighteditor.modules;

import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.sight.sightElements.BaseElement;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCustomText;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.Movement;
import com.ruegnerlukas.wtsights.ui.sighteditor.SightEditorController;
import com.ruegnerlukas.wtsights.ui.sighteditor.StepSizes;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;
import com.ruegnerlukas.wtutils.Conversion;
import com.ruegnerlukas.wtutils.FXUtils;
import com.ruegnerlukas.wtutils.SightUtils.TextAlign;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

public class ModuleCustomText implements Module {

	private ElementCustomText element;

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
	@FXML private Spinner<Double> spinnerSize;
	@FXML private TextField textField;
	@FXML private ChoiceBox<String> choiceAlignment;
	
	
	
	
	
	
	@Override
	public void create(DataPackage data) {
		
		// get element with default values
		ElementCustomText elementDefault = new ElementCustomText();
		
		cbUseThousandth.setSelected(elementDefault.useThousandth);
		cbUseThousandth.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				if(element != null) {
					element.useThousandth = cbUseThousandth.isSelected();
					if(element.useThousandth) {
						FXUtils.initSpinner(spinnerPosX, Conversion.get().screenspace2mil(spinnerPosX.getValue(), data.dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, null);
						FXUtils.initSpinner(spinnerPosY, Conversion.get().screenspace2mil(spinnerPosY.getValue(), data.dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, null);
						FXUtils.initSpinner(spinnerOriginX, Conversion.get().screenspace2mil(spinnerOriginX.getValue(), data.dataSight.envZoomedIn), -9999, 9999, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, null);
						FXUtils.initSpinner(spinnerOriginY, Conversion.get().screenspace2mil(spinnerOriginY.getValue(), data.dataSight.envZoomedIn), -9999, 9999, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, null);
					} else {
						FXUtils.initSpinner(spinnerOriginX, Conversion.get().mil2screenspace(spinnerOriginX.getValue(), data.dataSight.envZoomedIn), -9999, 9999, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, null);
						FXUtils.initSpinner(spinnerOriginY, Conversion.get().mil2screenspace(spinnerOriginY.getValue(), data.dataSight.envZoomedIn), -9999, 9999, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, null);
						FXUtils.initSpinner(spinnerPosX, Conversion.get().screenspace2mil(spinnerPosX.getValue(), data.dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, null);
						FXUtils.initSpinner(spinnerPosY, Conversion.get().mil2screenspace(spinnerPosY.getValue(), data.dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, null);
					}
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
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
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		choiceMovement.getSelectionModel().select(elementDefault.movement.toString());
		
		FXUtils.initSpinner(spinnerAngle, elementDefault.angle, -360, 360, StepSizes.STEP_ANGLE, StepSizes.DECPLACES_ANGLE, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.angle = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		cbUseAutoCenter.setSelected(elementDefault.autoCenter);
		cbUseAutoCenter.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				if(element != null) {
					element.autoCenter = cbUseAutoCenter.isSelected();
					spinnerCenterX.setDisable(element.autoCenter);
					spinnerCenterY.setDisable(element.autoCenter);
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		spinnerCenterX.setDisable(elementDefault.autoCenter);
		spinnerCenterY.setDisable(elementDefault.autoCenter);

		FXUtils.initSpinner(spinnerCenterX, elementDefault.center.x, -9999, 9999, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.center.x = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerCenterY, elementDefault.center.y, -9999, 9999, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.center.y = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerOriginX, elementDefault.radCenter.x, -9999, 9999, (elementDefault.useThousandth ? StepSizes.STEP_MIL : StepSizes.STEP_SCREENSPACE), (elementDefault.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE), new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.radCenter.x = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerOriginY, elementDefault.radCenter.y, -9999, 9999, (elementDefault.useThousandth ? StepSizes.STEP_MIL : StepSizes.STEP_SCREENSPACE), (elementDefault.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE), new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.radCenter.y = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerSpeed, elementDefault.speed, 0, 9999, StepSizes.STEP_SPEED, StepSizes.DECPLACES_SPEED, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.speed = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		
		FXUtils.initSpinner(spinnerPosX, elementDefault.position.x, -9999, 9999, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.position.x = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerPosY, elementDefault.position.y, -9999, 9999, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.position.y = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerSize, elementDefault.size, 0, 9999, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.size = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		choiceAlignment.getItems().addAll(TextAlign.LEFT.toString(), TextAlign.CENTER.toString(), TextAlign.RIGHT.toString());
		choiceAlignment.getSelectionModel().select(elementDefault.align.toString());
		choiceAlignment.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(element != null) {
					element.align = TextAlign.get(newValue);
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		
		textField.setText(elementDefault.text);
		textField.textProperty().addListener(new ChangeListener<String>() {
			@Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(element != null) {
					element.text = newValue;
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		
		setElement(null);
	}

	
	
	
	public void setElement(BaseElement e) {
		if(e == null || e.type != ElementType.CUSTOM_TEXT) {
			this.element = null;
		} else {
			this.element = (ElementCustomText)e;
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
			spinnerSize.getValueFactory().setValue(element.size);
			textField.setText(element.text);
			choiceAlignment.getSelectionModel().select(element.align.toString());
		}
	}
	
	
	
}
