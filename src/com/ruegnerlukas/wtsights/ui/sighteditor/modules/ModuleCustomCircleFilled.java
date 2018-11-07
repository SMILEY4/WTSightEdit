package com.ruegnerlukas.wtsights.ui.sighteditor.modules;

import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.sight.sightElements.BaseElement;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCustomCircleFilled;
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
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;

public class ModuleCustomCircleFilled implements Module {

	private ElementCustomCircleFilled element;

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
	@FXML private Spinner<Double> spinnerDiameter;
	@FXML private Slider sliderQuality;
	@FXML private Label labelQuality;
	
	
	
	
	@Override
	public void create(DataPackage data) {
		
		// get element with default values
		ElementCustomCircleFilled elementDefault = new ElementCustomCircleFilled();
		
		cbUseThousandth.setSelected(elementDefault.useThousandth);
		cbUseThousandth.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				if(element != null) {
					element.useThousandth = cbUseThousandth.isSelected();
					if(element.useThousandth) {
						FXUtils.initSpinner(spinnerPosX, Conversion.get().screenspace2mil(spinnerPosX.getValue(), data.dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, null);
						FXUtils.initSpinner(spinnerPosY, Conversion.get().screenspace2mil(spinnerPosY.getValue(), data.dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, null);
						FXUtils.initSpinner(spinnerDiameter, Conversion.get().screenspace2mil(spinnerDiameter.getValue(), data.dataSight.envZoomedIn), 0, Integer.MAX_VALUE, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, null);
						FXUtils.initSpinner(spinnerOriginX, Conversion.get().screenspace2mil(spinnerOriginX.getValue(), data.dataSight.envZoomedIn), -9999, 9999, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, null);
						FXUtils.initSpinner(spinnerOriginY, Conversion.get().screenspace2mil(spinnerOriginY.getValue(), data.dataSight.envZoomedIn), -9999, 9999, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, null);
					} else {
						FXUtils.initSpinner(spinnerPosX, Conversion.get().mil2screenspace(spinnerPosX.getValue(), data.dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, null);
						FXUtils.initSpinner(spinnerPosY, Conversion.get().mil2screenspace(spinnerPosY.getValue(), data.dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, null);
						FXUtils.initSpinner(spinnerDiameter, Conversion.get().mil2screenspace(spinnerDiameter.getValue(), data.dataSight.envZoomedIn), 0, Integer.MAX_VALUE, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, null);
						FXUtils.initSpinner(spinnerOriginX, Conversion.get().mil2screenspace(spinnerOriginX.getValue(), data.dataSight.envZoomedIn), -9999, 9999, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, null);
						FXUtils.initSpinner(spinnerOriginY, Conversion.get().mil2screenspace(spinnerOriginY.getValue(), data.dataSight.envZoomedIn), -9999, 9999, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, null);
					}
					element.setDirty(true);
					labelQuality.setText(ViewManager.getResources().getString("se_mccf_quality") + " (" + element.calcNumQuads(element.diameter, element.segment.x, element.segment.y) + " elements)");
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
		FXUtils.initSpinner(spinnerSegment1, elementDefault.segment.x, 0, 360, StepSizes.STEP_ANGLE, StepSizes.DECPLACES_ANGLE, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.segment.x = newValue.doubleValue();
					element.setDirty(true);
					labelQuality.setText(ViewManager.getResources().getString("se_mccf_quality") + " (" + element.calcNumQuads(element.diameter, element.segment.x, element.segment.y) + " elements)");
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerSegment2, elementDefault.segment.y, 0, 360, StepSizes.STEP_ANGLE, StepSizes.DECPLACES_ANGLE, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.segment.y = newValue.doubleValue();
					element.setDirty(true);
					labelQuality.setText(ViewManager.getResources().getString("se_mccf_quality") + " (" + element.calcNumQuads(element.diameter, element.segment.x, element.segment.y) + " elements)");
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerDiameter, elementDefault.diameter, 0, 1000, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.diameter = newValue.doubleValue();
					element.setDirty(true);
					labelQuality.setText(ViewManager.getResources().getString("se_mccf_quality") + " (" + element.calcNumQuads(element.diameter, element.segment.x, element.segment.y) + " elements)");
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		sliderQuality.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if(element != null) {
					element.quality = Math.max(1, newValue.intValue()) / 100.0;
					element.setDirty(true);
					labelQuality.setText(ViewManager.getResources().getString("se_mccf_quality") + " (" + element.calcNumQuads(element.diameter, element.segment.x, element.segment.y) + " elements)");
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		sliderQuality.setValue(elementDefault.quality*100);
		
		labelQuality.setText(ViewManager.getResources().getString("se_mccf_quality") + " (" + elementDefault.calcNumQuads(elementDefault.diameter, elementDefault.segment.x, elementDefault.segment.y) + " elements)");
		
		setElement(null);
	}

	
	
	
	public void setElement(BaseElement e) {
		if(e == null || e.type != ElementType.CUSTOM_CIRCLE_FILLED) {
			this.element = null;
		} else {
			this.element = (ElementCustomCircleFilled)e;
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
			spinnerDiameter.getValueFactory().setValue(element.diameter);
			sliderQuality.setValue(element.quality*100);
			labelQuality.setText(ViewManager.getResources().getString("se_mccf_quality") + " (" + element.calcNumQuads(element.diameter, element.segment.x, element.segment.y) + " elements)");
		}
	}
	
	
	
}
