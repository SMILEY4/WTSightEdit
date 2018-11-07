package com.ruegnerlukas.wtsights.ui.sighteditor.modules;

import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.sight.sightElements.BaseElement;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCentralHorzLine;
import com.ruegnerlukas.wtsights.ui.sighteditor.SightEditorController;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

public class ModuleCentralHorizontalLine implements Module {

	private ElementCentralHorzLine element;
	
	@FXML private CheckBox cbShowLine;

	
	

	
	
	@Override
	public void create(DataPackage data) {
		ElementCentralHorzLine elementDefault = new ElementCentralHorzLine();
		cbShowLine.setSelected(elementDefault.drawCentralHorzLine);
		cbShowLine.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				if(element == null) { return; }
				element.drawCentralHorzLine = cbShowLine.isSelected();
				element.setDirty(true);
				((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
			}
		});
		setElement(null);
	}




	@Override
	public void setElement(BaseElement e) {
		if(e == null || e.type != ElementType.CENTRAL_HORZ_LINE) {
			this.element = null;
		} else {
			this.element = (ElementCentralHorzLine)e;
		}
		
		if(element != null) {
			cbShowLine.setSelected(element.drawCentralHorzLine);
		}
	}

	
}
