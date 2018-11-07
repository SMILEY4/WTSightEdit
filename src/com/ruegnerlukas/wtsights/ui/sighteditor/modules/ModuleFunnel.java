package com.ruegnerlukas.wtsights.ui.sighteditor.modules;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticElement;
import com.ruegnerlukas.wtsights.data.ballisticdata.NullElement;
import com.ruegnerlukas.wtsights.data.sight.sightElements.BaseElement;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementFunnel;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;

public class ModuleFunnel implements Module {

	private ElementFunnel element;

	@FXML private ComboBox<BallisticElement> comboAmmo;
	
	@FXML private CheckBox cbMove;
	@FXML private CheckBox cbShowRight;
	@FXML private CheckBox cbShowLeft;
	@FXML private CheckBox cbHorz;
	@FXML private CheckBox cbBaseLine;
	@FXML private CheckBox cbFlip;
	
	@FXML private CheckBox cbUseThousandth;
	@FXML private Spinner<Double> spinnerOffX;
	@FXML private Spinner<Double> spinnerOffY;

	@FXML private Spinner<Integer> spinnerTargetSize;
	@FXML private Spinner<Integer> spinnerRangeStart;
	@FXML private Spinner<Integer> spinnerRangeEnd;
	

	
	
	
	@Override
	public void create(DataPackage data) {
		
		// get element with default values
		ElementFunnel elementDefault = new ElementFunnel();
		
		// AMMO
		FXUtils.initComboboxBallistic(comboAmmo);
		for(BallisticElement element : data.dataBallistic.elements) {
			if(!element.isRocketElement) {
				comboAmmo.getItems().add(element);
			}
		}
		comboAmmo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BallisticElement>() {
			@Override
			public void changed(ObservableValue<? extends BallisticElement> observable, BallisticElement oldValue, BallisticElement newValue) {
				BallisticElement selected = comboAmmo.getSelectionModel().getSelectedItem();
				if(element == null || selected == null || selected instanceof NullElement) {
					return;
				}
				element.elementBallistic = selected;
				element.setDirty(true);
				Logger.get().debug("Selected ballistic element: " + (element.elementBallistic == null ? "null" : element.elementBallistic) );
				((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
			}
		});
		comboAmmo.getSelectionModel().select(0);
		
		// movement
		cbMove.setSelected(elementDefault.movement == Movement.MOVE);
		cbMove.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				if(element != null) {
					element.movement = cbMove.isSelected() ? Movement.MOVE : Movement.STATIC;
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		// show sides
		cbShowRight.setSelected(elementDefault.showRight);
		cbShowRight.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				if(element != null) {
					element.showRight = cbShowRight.isSelected();
					element.setDirty(true);
					cbBaseLine.setDisable(element.showLeft && element.showRight);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		cbShowLeft.setSelected(elementDefault.showLeft);
		cbShowLeft.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				if(element != null) {
					element.showLeft = cbShowLeft.isSelected();
					element.setDirty(true);
					cbBaseLine.setDisable(element.showLeft && element.showRight);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		// horizontal
		cbHorz.setSelected(elementDefault.horz);
		cbHorz.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				if(element != null) {
					element.horz = cbHorz.isSelected();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		// baseline
		cbBaseLine.setSelected(elementDefault.baseLine);
		cbBaseLine.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				if(element != null) {
					element.baseLine = cbBaseLine.isSelected();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		cbBaseLine.setDisable(elementDefault.showLeft && elementDefault.showRight);
		
		// flip
		cbFlip.setSelected(elementDefault.flip);
		cbFlip.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				if(element != null) {
					element.flip = cbFlip.isSelected();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		// thousandth
		cbUseThousandth.setSelected(elementDefault.useThousandth);
		cbUseThousandth.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				if(element != null) {
					element.useThousandth = cbUseThousandth.isSelected();
					if(element.useThousandth) {
						FXUtils.initSpinner(spinnerOffX, Conversion.get().screenspace2mil(spinnerOffX.getValue(), data.dataSight.envZoomedIn), -9999, 9999, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, null);
						FXUtils.initSpinner(spinnerOffY, Conversion.get().screenspace2mil(spinnerOffY.getValue(), data.dataSight.envZoomedIn), -9999, 9999, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, null);
					} else {
						FXUtils.initSpinner(spinnerOffX, Conversion.get().mil2screenspace(spinnerOffX.getValue(), data.dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, null);
						FXUtils.initSpinner(spinnerOffY, Conversion.get().mil2screenspace(spinnerOffY.getValue(), data.dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, null);

					}
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		// offset
		FXUtils.initSpinner(spinnerOffX, elementDefault.offset.x, -9999, 9999, (elementDefault.useThousandth ? StepSizes.STEP_MIL : StepSizes.STEP_SCREENSPACE), (elementDefault.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE), new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.offset.x = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerOffY, elementDefault.offset.y, -9999, 9999, (elementDefault.useThousandth ? StepSizes.STEP_MIL : StepSizes.STEP_SCREENSPACE), (elementDefault.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE), new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.offset.y = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		// size target
		FXUtils.initSpinner(spinnerTargetSize, elementDefault.sizeTargetCM, 1, 1000, 10, 0, new ChangeListener<Integer>() {
			@Override public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				if(element != null) {
					element.sizeTargetCM = newValue.intValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		// range start
		FXUtils.initSpinner(spinnerRangeStart, elementDefault.rangeStart, 0, 10000, 100, 0, new ChangeListener<Integer>() {
			@Override public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				if(element != null) {
					element.rangeStart = newValue.intValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		// range end
		FXUtils.initSpinner(spinnerRangeEnd, elementDefault.rangeEnd, 0, 10000, 100, 0, new ChangeListener<Integer>() {
			@Override public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				if(element != null) {
					element.rangeEnd = newValue.intValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		setElement(null);
	}

	
	
	
	public void setElement(BaseElement e) {
		if(e == null || e.type != ElementType.FUNNEL) {
			this.element = null;
		} else {
			this.element = (ElementFunnel)e;
		}
		if(this.element != null) {
			if(element.elementBallistic == null) {
				element.elementBallistic = comboAmmo.getValue();
			} else {
				comboAmmo.getSelectionModel().select(element.elementBallistic);
			}
			spinnerTargetSize.getValueFactory().setValue(element.sizeTargetCM);
			spinnerRangeStart.getValueFactory().setValue(element.rangeStart);
			spinnerRangeEnd.getValueFactory().setValue(element.rangeEnd);
			cbMove.setSelected(element.movement == Movement.MOVE);
		}
	}
	
	
}
