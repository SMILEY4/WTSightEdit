package com.ruegnerlukas.wtsights.data.calibration;

import java.util.ArrayList;
import java.util.List;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2i;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.vehicle.Ammo;

public class CalibrationAmmoData {

	public Ammo ammo;
	
	public String imgName;

	public boolean zoomedIn = false;
	
	public Vector2i markerCenter = new Vector2i(-1);				// Marker = (yPX, 0)
	public List<Vector2i> markerRanges = new ArrayList<Vector2i>(); // Marker = (yPX, distance)
	
	
	
	public void print(boolean indent) {
		
		if(!indent) {
			Logger.get().debug("==== AMMO DATA ====");
		}
		
		
		String strIndent = indent ? "    " : "";
		
		Logger.get().debug(strIndent + "ammoName = " + ammo.name);
		Logger.get().debug(strIndent + "imageName = " + imgName);
		Logger.get().debug(strIndent + "markerCenter = " + markerCenter.x + "," + markerCenter.y);

		for(Vector2i marker : markerRanges) {
			Logger.get().debug("marker_" + markerRanges.indexOf(marker) + ": y=" + marker.x + ", d=" + marker.y);
		}
		
		if(!indent) {
			Logger.get().debug("===================");
		}
	}
	
}
