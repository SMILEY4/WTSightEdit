package com.ruegnerlukas.wtsights.ui.sighteditor.modules;

import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.sight.sightElements.BaseElement;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCustomQuadFilled;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.Movement;
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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;

public class ModuleCustomQuadFilled implements Module {

	private ElementCustomQuadFilled element;

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
	
	@FXML private Spinner<Double> spinnerPosOffsetX;
	@FXML private Spinner<Double> spinnerPosOffsetY;

	
	
	
	
	
	@Override
	public void create(DataPackage data) {
		
		// get element with default values
		ElementCustomQuadFilled elementDefault = new ElementCustomQuadFilled();
		
		cbUseThousandth.setSelected(elementDefault.useThousandth);
		cbUseThousandth.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				if(element != null) {
					element.useThousandth = cbUseThousandth.isSelected();
					if(element.useThousandth) {
						FXUtils.initSpinner(spinnerPos1X, Conversion.get().screenspace2mil(spinnerPos1X.getValue(), data.dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, null);
						FXUtils.initSpinner(spinnerPos1Y, Conversion.get().screenspace2mil(spinnerPos1Y.getValue(), data.dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, null);
						FXUtils.initSpinner(spinnerPos2X, Conversion.get().screenspace2mil(spinnerPos2X.getValue(), data.dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, null);
						FXUtils.initSpinner(spinnerPos2Y, Conversion.get().screenspace2mil(spinnerPos2Y.getValue(), data.dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, null);
						FXUtils.initSpinner(spinnerPos3X, Conversion.get().screenspace2mil(spinnerPos3X.getValue(), data.dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, null);
						FXUtils.initSpinner(spinnerPos3Y, Conversion.get().screenspace2mil(spinnerPos3Y.getValue(), data.dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, null);
						FXUtils.initSpinner(spinnerPos4X, Conversion.get().screenspace2mil(spinnerPos4X.getValue(), data.dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, null);
						FXUtils.initSpinner(spinnerPos4Y, Conversion.get().screenspace2mil(spinnerPos4Y.getValue(), data.dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, null);
						FXUtils.initSpinner(spinnerOriginX, Conversion.get().screenspace2mil(spinnerOriginX.getValue(), data.dataSight.envZoomedIn), -9999, 9999, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, null);
						FXUtils.initSpinner(spinnerOriginY, Conversion.get().screenspace2mil(spinnerOriginY.getValue(), data.dataSight.envZoomedIn), -9999, 9999, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, null);
						FXUtils.initSpinner(spinnerPosOffsetX, Conversion.get().screenspace2mil(spinnerPosOffsetX.getValue(), data.dataSight.envZoomedIn), -9999, 9999, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, null);
						FXUtils.initSpinner(spinnerPosOffsetY, Conversion.get().screenspace2mil(spinnerPosOffsetY.getValue(), data.dataSight.envZoomedIn), -9999, 9999, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, null);
					} else {
						FXUtils.initSpinner(spinnerOriginX, Conversion.get().mil2screenspace(spinnerOriginX.getValue(), data.dataSight.envZoomedIn), -9999, 9999, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, null);
						FXUtils.initSpinner(spinnerOriginY, Conversion.get().mil2screenspace(spinnerOriginY.getValue(), data.dataSight.envZoomedIn), -9999, 9999, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, null);
						FXUtils.initSpinner(spinnerPos1X, Conversion.get().mil2screenspace(spinnerPos1X.getValue(), data.dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, null);
						FXUtils.initSpinner(spinnerPos1Y, Conversion.get().mil2screenspace(spinnerPos1Y.getValue(), data.dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, null);
						FXUtils.initSpinner(spinnerPos2X, Conversion.get().mil2screenspace(spinnerPos2X.getValue(), data.dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, null);
						FXUtils.initSpinner(spinnerPos2Y, Conversion.get().mil2screenspace(spinnerPos2Y.getValue(), data.dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, null);
						FXUtils.initSpinner(spinnerPos3X, Conversion.get().mil2screenspace(spinnerPos3X.getValue(), data.dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, null);
						FXUtils.initSpinner(spinnerPos3Y, Conversion.get().mil2screenspace(spinnerPos3Y.getValue(), data.dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, null);
						FXUtils.initSpinner(spinnerPos4X, Conversion.get().mil2screenspace(spinnerPos4X.getValue(), data.dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, null);
						FXUtils.initSpinner(spinnerPos4Y, Conversion.get().mil2screenspace(spinnerPos4Y.getValue(), data.dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, null);
						FXUtils.initSpinner(spinnerPosOffsetX, Conversion.get().mil2screenspace(spinnerPosOffsetX.getValue(),data.dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, null);
						FXUtils.initSpinner(spinnerPosOffsetY, Conversion.get().mil2screenspace(spinnerPosOffsetY.getValue(), data.dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, null);
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
		
		
		
		FXUtils.initSpinner(spinnerPos1X, elementDefault.pos1.x, -9999, 9999, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.pos1.x = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerPos1Y, elementDefault.pos1.y, -9999, 9999, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.pos1.y = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		
		FXUtils.initSpinner(spinnerPos2X, elementDefault.pos2.x, -9999, 9999, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.pos2.x = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerPos2Y, elementDefault.pos2.y, -9999, 9999, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.pos2.y = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		
		FXUtils.initSpinner(spinnerPos3X, elementDefault.pos3.x, -9999, 9999, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.pos3.x = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerPos3Y, elementDefault.pos3.y, -9999, 9999, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.pos3.y = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		
		FXUtils.initSpinner(spinnerPos4X, elementDefault.pos4.x, -9999, 9999, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.pos4.x = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerPos4Y, elementDefault.pos4.y, -9999, 9999, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.pos4.y = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		FXUtils.initSpinner(spinnerPosOffsetX, elementDefault.positionOffset.x, -9999, 9999, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.positionOffset.x = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerPosOffsetY, elementDefault.positionOffset.y, -9999, 9999, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.positionOffset.y = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		
		setElement(null);
	}

	
	
	
	public void setElement(BaseElement e) {
		if(e == null || e.type != ElementType.CUSTOM_QUAD_FILLED) {
			this.element = null;
		} else {
			this.element = (ElementCustomQuadFilled)e;
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
			spinnerPosOffsetX.getValueFactory().setValue(element.positionOffset.x);
			spinnerPosOffsetY.getValueFactory().setValue(element.positionOffset.y);
		}
	}
	
	
}
