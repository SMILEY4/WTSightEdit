package com.ruegnerlukas.wtsights.data.ballisticdata;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ruegnerlukas.wtsights.data.vehicle.Vehicle;

public class BallisticData {

	public Vehicle vehicle;
	public List<BallisticElement> elements = new ArrayList<BallisticElement>();
	public Map<BallisticElement, Boolean> zoomedIn = new HashMap<BallisticElement, Boolean>();
	public double zoomModOut = 1.0;
	public double zoomModIn = 1.0;

	
	public Map<BallisticElement, BufferedImage> imagesBallistic = new HashMap<BallisticElement, BufferedImage>();
	public Map<Boolean, BufferedImage> imagesZoom = new HashMap<Boolean, BufferedImage>();
	
	
	
	
	
	public boolean isZoomedIn(BallisticElement element) {
		if(zoomedIn.containsKey(element)) {
			return zoomedIn.get(element);
		} else {
			return false;
		}
	}
	
	
	
}
