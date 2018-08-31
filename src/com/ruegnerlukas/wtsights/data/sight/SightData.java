package com.ruegnerlukas.wtsights.data.sight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.ruegnerlukas.wtsights.data.sight.elements.*;
import com.ruegnerlukas.wtsights.data.vehicle.Ammo;
import com.ruegnerlukas.wtutils.SightUtils.Thousandth;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

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
	public Image 	envBackground 		= null;
	
	
	// elements
	public HashMap<ElementType, List<Element>> elements = new HashMap<ElementType, List<Element>>();
	
	
	
	
	public SightData(boolean addDefaults) {
		if(addDefaults) {
			addElement(new ElementCentralHorzLine());
			addElement(new ElementCentralVertLine());
			addElement(new ElementRangefinder());
			addElement(new ElementHorzRangeIndicators());
			addElement(new ElementBallRangeIndicator());
		}
	}

	
	
	
	public void addElement(Element element) {
		List<Element> list = elements.get(element.type);
		if(list == null) {
			list = new ArrayList<Element>();
			elements.put(element.type, list);
		}
		list.add(element);
	}
	
	
	public List<Element> getElements(ElementType type) {
		List<Element> list = elements.get(type);
		if(list == null) {
			return new ArrayList<Element>(1);
		} else {
			return list;
		}
	}
	
	
	public List<Element> getElements(String name) {
		List<Element> list = new ArrayList<Element>();
		for(Entry<ElementType,List<Element>> entry : elements.entrySet()) {
			for(Element element : entry.getValue()) {
				if(element.name.equals(name)) {
					list.add(element);
				}
			}
		}
		return list;
	}
	
	
	public List<Element> getElements(ElementType type, String name) {
		List<Element> list = new ArrayList<Element>();
		if(elements.get(type) != null) {
			for(Element element : elements.get(type)) {
				if(element.name.equals(name)) {
					list.add(element);
				}
			}
		}
		return list;
	}
	
	
	public List<Element> collectElements() {
		List<Element> list = new ArrayList<Element>();
		for(Entry<ElementType,List<Element>> e : elements.entrySet()) {
			list.addAll(e.getValue());
		}
		return Collections.unmodifiableList(list);
	}
	
	
	
	public boolean removeElement(String name) {
		for(Entry<ElementType,List<Element>> entry : elements.entrySet()) {
			for(Element element : entry.getValue()) {
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
			for(Element element : elements.get(type)) {
				if(element.name.equals(name)) {
					elements.get(type).remove(element);
					return true;
				}
			}
		}
		return false;
	}
	
}

















