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
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;
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
