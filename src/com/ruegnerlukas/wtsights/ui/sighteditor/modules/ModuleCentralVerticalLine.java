package com.ruegnerlukas.wtsights.ui.sighteditor.modules;

import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.sight.sightElements.BaseElement;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCentralVertLine;
import com.ruegnerlukas.wtsights.ui.sighteditor.SightEditorController;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

public class ModuleCentralVerticalLine implements Module {

	private ElementCentralVertLine element;
	
	@FXML private CheckBox cbShowLine;

	



	
	@Override
	public void create(DataPackage data) {
		ElementCentralVertLine elementDefault = new ElementCentralVertLine();
		cbShowLine.setSelected(elementDefault.drawCentralVertLine);
		cbShowLine.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				if(element == null) { return; }
				element.drawCentralVertLine = cbShowLine.isSelected();
				element.setDirty(true);
				((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
			}
		});
		setElement(null);
	}




	@Override
	public void setElement(BaseElement e) {
		if(e == null || e.type != ElementType.CENTRAL_VERT_LINE) {
			this.element = null;
		} else {
			this.element = (ElementCentralVertLine)e;
		}
		
		if(element != null) {
			cbShowLine.setSelected(element.drawCentralVertLine);
		}
	}


	
}
