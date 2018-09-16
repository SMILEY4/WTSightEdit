package com.ruegnerlukas.wtsights.data.ballisticdata;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ruegnerlukas.wtsights.data.vehicle.Vehicle;
import com.ruegnerlukas.wtsights.data.vehicle.Weapon;
import com.ruegnerlukas.wtutils.SightUtils.TriggerGroup;

public class BallisticData {

	public Vehicle vehicle;
	public List<BallisticElement> elements = new ArrayList<BallisticElement>();
	
	public Map<BallisticElement, BufferedImage> images = new HashMap<BallisticElement, BufferedImage>();
	public Map<BallisticElement, Boolean> zoomedIn = new HashMap<BallisticElement, Boolean>();
	
	
	
	
	public static BallisticData createFromVehicle(Vehicle vehicle) {
		
		BallisticData dataBall = new BallisticData();
		dataBall.vehicle = vehicle;
		
		for(Weapon weapon : vehicle.weaponsList) {
			if(weapon.triggerGroup.isOr(TriggerGroup.PRIMARY, TriggerGroup.SECONDARY, TriggerGroup.COAXIAL, TriggerGroup.MACHINEGUN)) {
				
			}
			
		}
		
		return dataBall;
	}
	
}
