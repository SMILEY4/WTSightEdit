package com.ruegnerlukas.wtutils;

import com.ruegnerlukas.wtutils.SightUtils.Thousandth;

public class Conversion {
	
	private static Conversion DEFAULT = null;
	
	public static Conversion get() {
		if(DEFAULT == null) {
			DEFAULT = new Conversion();
		}
		return DEFAULT;
	}
	
	
	
	
	
	
	
	
	public double conversionMilPx = 0;
	public double zoomInMul = 1;
	
	
	public void initialize(double screenWidth, double screenHeight, double fovOut, double fovIn, Thousandth thousanth) {
		final double x1 = (fovOut / 360.0) * thousanth.value;
		final double x2 = screenWidth / x1;
		conversionMilPx = x2 / screenHeight;
		zoomInMul = fovOut / fovIn;
	}
	
	
	

	public double screenspace2pixel(double ssUnits, double pxScreenHeight, boolean zoomedIn) {
		return (ssUnits * pxScreenHeight) * (zoomedIn ? zoomInMul : 1);
	}
	
	
	
	
	public double pixel2screenspace(double pxUnits, double pxScreenHeight, boolean zoomedIn) {
		return (pxUnits / pxScreenHeight) * (zoomedIn ? zoomInMul : 1);
	}
	
	
	
	
	public double screenspace2mil(double ssUnits, boolean zoomedIn) {
		return (ssUnits / conversionMilPx) * (zoomedIn ? zoomInMul : 1);
	}
	
	
	
	
	public double mil2screenspace(double milUnits, boolean zoomedIn) {
		return (milUnits * conversionMilPx) * (zoomedIn ? zoomInMul : 1);
	}
	
	
	
	
	public double mil2pixel(double milUnits, double pxScreenHeight, boolean zoomedIn) {
		return (milUnits * conversionMilPx * pxScreenHeight) * (zoomedIn ? zoomInMul : 1);
	}
	
	
	
	
	public double pixel2mil(double pxUnits, double pxScreenHeight, boolean zoomedIn) {
		return (pxUnits / (conversionMilPx * pxScreenHeight)) * (zoomedIn ? zoomInMul : 1);
	}
	
	
	
	
	
}



