package com.ruegnerlukas.wtsights.ui.sighteditor.modules;

import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.sight.sightElements.Element;

public interface Module {
	public void create(DataPackage data);
	public void setElement(Element e);
}
