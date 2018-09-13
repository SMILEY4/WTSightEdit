package com.ruegnerlukas.wtsights.data.calibration;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.vehicle.Vehicle;

public class CalibrationData {

	
	public Vehicle vehicle;
	public List<CalibrationAmmoData> ammoData = new ArrayList<CalibrationAmmoData>();
	public Map<String, BufferedImage> images = new HashMap<String,BufferedImage>(); // key = "image_"+ammo.name
	

	
	
	public void print() {
		
		Logger.get().debug("==== CALIBRATION DATA ====");
		
		Logger.get().debug("vehicle = " + vehicle.name);
		
		for(CalibrationAmmoData data : ammoData) {
			Logger.get().debug("Ammo Data:");
			data.print(true);
		}

		Logger.get().debug("Images");
		for(Entry<String,BufferedImage> entry : images.entrySet()) {
			Logger.get().debug("    " + entry.getKey() + ":  " + entry.getValue());
		}
		
		Logger.get().debug("==========================");
	}
	
}



