package com.ruegnerlukas.wtsights.data.sight.sightElements;

import java.util.ArrayList;
import java.util.List;

import com.ruegnerlukas.wtsights.data.DataPackage;

public abstract class ElementMulti extends Element {

	private List<ElementSingle> subElements = new ArrayList<ElementSingle>();
	private ILayoutData mainLayoutData;
	
	
	
	protected ElementMulti(String name, ElementType type) {
		super(name, type);
	}

	
	
	
	@Override
	public void setDirty(boolean dirty) {
		for(ElementSingle e : subElements) {
			e.setDirty(true);
		}
	}
	
	
	
	
	@Override
	public boolean isDirty() {
		for(ElementSingle e : subElements) {
			if(e.isDirty()) {
				return true;
			}
		}
		return false;
	}
	
	
	
	
	public List<ElementSingle> getSubElements() {
		return subElements;
	}
	
	
	
	
	protected void setMainLayout(ILayoutData layoutData) {
		this.mainLayoutData = layoutData;
	}
	
	
	
	
	public ILayoutData getMainLayout() {
		return this.mainLayoutData;
	}
	
	
	
	
	public abstract void layout(DataPackage data, double canvasWidth, double canvasHeight);
	

}
