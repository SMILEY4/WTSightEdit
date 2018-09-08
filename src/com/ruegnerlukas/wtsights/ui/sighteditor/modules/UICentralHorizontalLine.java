package com.ruegnerlukas.wtsights.ui.sighteditor.modules;

import com.ruegnerlukas.wtsights.data.sight.elements.Element;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCentralHorzLine;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCentralVertLine;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementType;
import com.ruegnerlukas.wtsights.ui.sighteditor.UISightEditor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

public class UICentralHorizontalLine implements Module {

	private UISightEditor editor;
	private ElementCentralHorzLine element;
	
	@FXML private CheckBox cbShowLine;

	
	

	@Override
	public void setEditor(UISightEditor editor) {
		this.editor = editor;
	}




	@Override
	public void create() {
		ElementCentralHorzLine elementDefault = new ElementCentralHorzLine();
		cbShowLine.setSelected(elementDefault.drawCentralHorzLine);
		setElement(null);
	}




	@Override
	public void setElement(Element e) {
		if(e == null || e.type != ElementType.CENTRAL_HORZ_LINE) {
			this.element = null;
		} else {
			this.element = (ElementCentralHorzLine)e;
		}
		
		if(element != null) {
			cbShowLine.setSelected(element.drawCentralHorzLine);
		}
	}


	
	
	@FXML
	void onShowLine(ActionEvent event) {
		if(element == null) { return; }
		element.drawCentralHorzLine = cbShowLine.isSelected();
		editor.wtCanvas.repaint();
	}
}
