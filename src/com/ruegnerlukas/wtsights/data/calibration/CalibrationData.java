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

	
	public String vehicleName;
	public Vehicle vehicle;
	
	public float fovOut = -1;
	public float fovIn = -1;

	public List<CalibrationAmmoData> ammoData = new ArrayList<CalibrationAmmoData>();
	public Map<String, BufferedImage> images = new HashMap<String,BufferedImage>();
	

	
	
	public void print() {
		
		Logger.get().debug("==== CALIBRATION DATA ====");
		
		Logger.get().debug("vehicle = " + vehicleName);
		Logger.get().debug("fovOut = " + fovOut);
		Logger.get().debug("fovIn = " + fovIn);

		
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



