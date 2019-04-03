package com.ruegnerlukas.wtsights.data.sight;

import com.ruegnerlukas.wtsights.data.sight.sightElements.BaseElement;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.*;
import com.ruegnerlukas.wtsights.data.vehicle.Ammo;
import com.ruegnerlukas.wtutils.SightUtils.Thousandth;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class SightData {
	
	
	// general
	public Thousandth 	gnrThousandth 			= Thousandth.USSR;
	public double 		gnrFontScale 			= 1;
	public double 		gnrLineSize 			= 1;
	public boolean		gnrApplyCorrectionToGun = true;
	
	
	// environment
	public Ammo		envLoadedAmmo		= null;
	public boolean 	envZoomedIn 		= false;
	public boolean 	envShowRangeFinder 	= true;
	public double 	envRFProgress 		= 30;
	public int		envRangeCorrection 	= 0;
	public Color 	envSightColor 		= Color.BLACK;
	public Image	envBackground 		= null;
	public int		envBackgroundOffX	= 0;
	public int		envBackgroundOffY	= 0;
	public double	envBackgroundScale	= 1.0;
	public double	envBackgroundRotation = 0.0;
	public boolean	envDisplayGrid		= false;
	public double	envGridWidth 		= 20;
	public double	envGridHeight 		= 20;
	public Color	envColorGrid		= new Color(0.0f, 0.0f, 1f, 0.25f);

	
	// elements
	public HashMap<ElementType, List<BaseElement>> elements = new HashMap<ElementType, List<BaseElement>>();
	public BaseElement selectedElement = null;
	
	
	
	
	public SightData(boolean addDefaults) {
		if(addDefaults) {
			addElement(new ElementCentralHorzLine());
			addElement(new ElementCentralVertLine());
			addElement(new ElementRangefinder());
			addElement(new ElementHorzRangeIndicators());
			addElement(new ElementBallRangeIndicator());
		}
	}

	
	
	
	public void addElement(BaseElement element) {
		if(element == null) {
			return;
		}
		List<BaseElement> list = elements.get(element.type);
		if(list == null) {
			list = new ArrayList<BaseElement>();
			elements.put(element.type, list);
		}
		list.add(element);
	}
	
	
	public List<BaseElement> getElements(ElementType type) {
		List<BaseElement> list = elements.get(type);
		if(list == null) {
			return new ArrayList<BaseElement>(1);
		} else {
			return list;
		}
	}
	
	
	public List<BaseElement> getElements(String name) {
		List<BaseElement> list = new ArrayList<BaseElement>();
		for(Entry<ElementType,List<BaseElement>> entry : elements.entrySet()) {
			for(BaseElement element : entry.getValue()) {
				if(element.name.equals(name)) {
					list.add(element);
				}
			}
		}
		return list;
	}
	
	
	public List<BaseElement> getElements(ElementType type, String name) {
		List<BaseElement> list = new ArrayList<BaseElement>();
		if(elements.get(type) != null) {
			for(BaseElement element : elements.get(type)) {
				if(element.name.equals(name)) {
					list.add(element);
				}
			}
		}
		return list;
	}
	
	
	public List<BaseElement> collectElements() {
		List<BaseElement> list = new ArrayList<BaseElement>();
		for(Entry<ElementType,List<BaseElement>> e : elements.entrySet()) {
			list.addAll(e.getValue());
		}
		return Collections.unmodifiableList(list);
	}
	
	
	
	public boolean removeElement(BaseElement element) {
		for(Entry<ElementType,List<BaseElement>> entry : elements.entrySet()) {
			if(entry.getValue().remove(element)) {
				return true;
			}
		}
		return false;
	}
	
	
	public boolean removeElement(String name) {
		for(Entry<ElementType,List<BaseElement>> entry : elements.entrySet()) {
			for(BaseElement element : entry.getValue()) {
				if(element.name.equals(name)) {
					entry.getValue().remove(element);
					return true;
				}
			}
		}
		return false;
	}
	
	
	public boolean removeElement(String name, ElementType type) {
		if(elements.containsKey(type)) {
			for(BaseElement element : elements.get(type)) {
				if(element.name.equals(name)) {
					elements.get(type).remove(element);
					return true;
				}
			}
		}
		return false;
	}
	
	
	public void setElementsDirty() {
		for(Entry<ElementType,List<BaseElement>> entry : elements.entrySet()) {
			for(BaseElement e : entry.getValue()) {
				e.setDirty(true);
			}
		}
	}
	
	
	public void setElementsDirty(ElementType type) {
		for(BaseElement e : elements.get(type)) {
			e.setDirty(true);
		}
	}
	
}

















