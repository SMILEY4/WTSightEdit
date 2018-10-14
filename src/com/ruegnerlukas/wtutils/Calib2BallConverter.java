package com.ruegnerlukas.wtutils;

import java.awt.image.BufferedImage;

import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticData;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticElement;
import com.ruegnerlukas.wtsights.data.ballisticdata.Marker;
import com.ruegnerlukas.wtsights.data.ballisticdata.MarkerData;
import com.ruegnerlukas.wtsights.data.ballisticdata.ballfunctions.DefaultBallisticFuntion;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationAmmoData;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationData;

public class Calib2BallConverter {

	
	public static BallisticData convert(CalibrationData dataCalib) {
		
		BallisticData dataBall = new BallisticData();
		dataBall.vehicle = dataCalib.vehicle;

		
		for(CalibrationAmmoData dataAmmo : dataCalib.ammoData) {
			
			BallisticElement element = new BallisticElement();
			element.ammunition.add(dataAmmo.ammo);
			
			if(dataAmmo.zoomedIn) {
				dataBall.zoomedIn.put(element, dataAmmo.zoomedIn);
			}
			
			if(!dataAmmo.markerRanges.isEmpty()) {

				double[] distances = new double[dataAmmo.markerRanges.size()+1];
				double[] yPositions = new double[dataAmmo.markerRanges.size()+1];
				
				distances[0] = 0;
				yPositions[0] = 0;
				
				for(int i=0; i<dataAmmo.markerRanges.size(); i++) {
					distances[i+1] = dataAmmo.markerRanges.get(i).y;
					yPositions[i+1] = dataAmmo.markerRanges.get(i).x;
					if(dataAmmo.zoomedIn) {
						yPositions[i+1] /= Conversion.get().zoomInMul;
					}
				}
				
				
				element.markerData = new MarkerData();
				element.markerData.yPosCenter = dataAmmo.markerCenter.x;
				for(int i=0; i<distances.length; i++) {
					Marker marker = new Marker((int)distances[i], yPositions[i]);
					marker.id = element.markerData.markers.size()+1;
					element.markerData.markers.add(marker);
				}

				element.function = DefaultBallisticFuntion.create(element, dataBall.vehicle, dataBall.isZoomedIn(element));
				
			}
			
			
			BufferedImage img = dataCalib.images.get("image_"+dataAmmo.ammo.name);
			if(img != null) {
				dataBall.imagesBallistic.put(element, img);
			}
			
			dataBall.elements.add(element);
			
		}
		
		
		return dataBall;
	}
	
}
