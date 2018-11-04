package com.ruegnerlukas.wtsights.ui.sighteditor.createelement;

import java.util.ArrayList;
import java.util.List;

import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticElement;
import com.ruegnerlukas.wtsights.data.sight.sightElements.BaseElement;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementBallRangeIndicator;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCentralHorzLine;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCentralVertLine;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCustomCircleOutline;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCustomLine;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCustomPolygonFilled;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCustomPolygonOutline;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCustomQuadFilled;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCustomQuadOutline;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCustomText;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementHorzRangeIndicators;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementRangefinder;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementShellBlock;
import com.ruegnerlukas.wtsights.ui.view.IViewService;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;
import com.ruegnerlukas.wtutils.FXUtils;

public class ElementCreateService implements IViewService {

	
	private static BaseElement createdElement = null;
	
	public static BaseElement getCreatedElement() {
		return createdElement;
	}
	
	
	
	
	
	
	private List<BaseElement> existingElements;
	private DataPackage data;
	
	
	
	
	@Override
	public void initService() {
		existingElements = null;
		data = null;
	}
	
	
	
	
	public void init(List<BaseElement> existingElements, DataPackage data) {
		this.existingElements = existingElements;
		this.data = data;
	}
	
	
	
	
	public List<ElementType> getAvailableTypes() {
		
		List<ElementType> types = new ArrayList<ElementType>();
		
		for(ElementType type : ElementType.values()) {
			
			if(data.dataBallistic.elements.isEmpty() && (type == ElementType.SHELL_BALLISTICS_BLOCK || type == ElementType.BALLISTIC_RANGE_INDICATORS) ) {
				continue;
			}
			
			if(type == ElementType.SHELL_BALLISTICS_BLOCK) {
				boolean allRockets = true;
				for(BallisticElement element : data.dataBallistic.elements) {
					if(!element.isRocketElement) {
						allRockets = false;
						break;
					}
				}
				if(allRockets) {
					continue;
				}
			}
			
			int listCount = 0;
			for(BaseElement e : existingElements) {
				ElementType typeList = e.type;
				if(typeList == type) {
					listCount++;
				}
			}
			
			if(listCount < type.maxCount) {
				types.add(type);
			} else {
				continue;
			}
			
		}
		
		
		return types;
	}
	
	
	
	public void finish(ElementType type, String name) {
		
		if(type == null) {
			createdElement = null;
			
		} else {
			if(validateElementName(name) == 0) {
				
				if(type == ElementType.BALLISTIC_RANGE_INDICATORS) {
					createdElement = new ElementBallRangeIndicator(name);
				}
				if(type == ElementType.CENTRAL_HORZ_LINE) {
					createdElement = new ElementCentralHorzLine(name);
				}
				if(type == ElementType.CENTRAL_VERT_LINE) {
					createdElement = new ElementCentralVertLine(name);
				}
				if(type == ElementType.CUSTOM_CIRCLE_OUTLINE) {
					createdElement = new ElementCustomCircleOutline(name);
				}
				if(type == ElementType.CUSTOM_LINE) {
					createdElement = new ElementCustomLine(name);
				}
				if(type == ElementType.CUSTOM_QUAD_FILLED) {
					createdElement = new ElementCustomQuadFilled(name);
				}
				if(type == ElementType.CUSTOM_TEXT) {
					createdElement = new ElementCustomText(name);
				}
				if(type == ElementType.HORZ_RANGE_INDICATORS) {
					createdElement = new ElementHorzRangeIndicators(name);
				}
				if(type == ElementType.RANGEFINDER) {
					createdElement = new ElementRangefinder(name);
				}
				if(type == ElementType.SHELL_BALLISTICS_BLOCK) {
					createdElement = new ElementShellBlock(name);
				}
				if(type == ElementType.CUSTOM_QUAD_OUTLINE) {
					createdElement = new ElementCustomQuadOutline(name);
				}
				if(type == ElementType.CUSTOM_POLY_OUTLINE) {
					createdElement = new ElementCustomPolygonOutline(name);
				}
				if(type == ElementType.CUSTOM_POLY_FILLED) {
					createdElement = new ElementCustomPolygonFilled(name);
				}
				
			}
		}
		
		
		FXUtils.closeFXScene(View.ELEMENT_CREATE);
	}
	
	
	
	
	/**
	 * 0 = OK
	 * 1 = name is null
	 * 2 = name is empty
	 * 3 = name is not unique
	 * 4 = name contains forbidden characters
	 * */
	public int validateElementName(String name) {
		if(name == null) {
			return 1;
		}
		if(name.trim().length() == 0) {
			return 2;
		}
		for(BaseElement e : existingElements) {
			if(e.name.trim().equalsIgnoreCase(name.trim())) {
				return 3;
			}
		}
		if(name.contains("[") || name.contains("]") || name.contains("/") || name.contains("-") || name.contains("\"") || name.contains("=")|| name.contains(",")) {
			return 4;
		}
		return 0;
	}
	
	
}
