package com.ruegnerlukas.wtsights.ui.sighteditor.modules;

import com.ruegnerlukas.wtsights.data.sight.elements.Element;
import com.ruegnerlukas.wtsights.ui.sighteditor.UISightEditor;

public interface Module {
	public void setEditor(UISightEditor editor);
	public void create();
	public void setElement(Element e);
}
