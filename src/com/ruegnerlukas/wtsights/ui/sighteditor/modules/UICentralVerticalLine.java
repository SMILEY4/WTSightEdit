package com.ruegnerlukas.wtsights.ui.sighteditor.modules;

import com.ruegnerlukas.wtsights.data.sight.elements.Element;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCentralVertLine;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementRangefinder;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementType;
import com.ruegnerlukas.wtsights.ui.sighteditor.UISightEditor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

public class UICentralVerticalLine implements Module {

	private UISightEditor editor;
	private ElementCentralVertLine element;
	
	@FXML private CheckBox cbShowLine;

	
	

	@Override
	public void setEditor(UISightEditor editor) {
		this.editor = editor;
	}




	@Override
	public void create() {
		ElementCentralVertLine elementDefault = new ElementCentralVertLine();
		cbShowLine.setSelected(elementDefault.drawCentralVertLine);
		setElement(null);
	}




	@Override
	public void setElement(Element e) {
		if(e == null || e.type != ElementType.CENTRAL_VERT_LINE) {
			this.element = null;
		} else {
			this.element = (ElementCentralVertLine)e;
		}
		
		if(element != null) {
			cbShowLine.setSelected(element.drawCentralVertLine);
		}
	}


	
	
	@FXML
	void onShowLine(ActionEvent event) {
		if(element == null) { return; }
		element.drawCentralVertLine = cbShowLine.isSelected();
		editor.wtCanvas.repaint();
	}
}
