package com.ruegnerlukas.wtsights.data.sight;



public class BIndicator {

	private int distance;
	private boolean major;
	private float extend;
	private float textX;
	private float textY;

	
	public BIndicator(int distance, boolean major, float extend, float textX, float textY) {
		this.distance = distance;
		this.major = major;
		this.extend = extend;
		this.textX = textX;
		this.textY = textY;
	}

	
	
	public int getDistance() {
		return distance;
	}

	
	
	public void setDistance(int distance) {
		this.distance = distance;
	}

	
	
	public boolean isMajor() {
		return major;
	}

	
	
	public void setMajor(boolean major) {
		this.major = major;
	}

	
	
	public float getExtend() {
		return extend;
	}

	
	
	public void setExtend(float extend) {
		this.extend = extend;
	}

	
	
	public float getTextX() {
		return textX;
	}

	
	
	public void setTextX(float textX) {
		this.textX = textX;
	}

	
	
	public float getTextY() {
		return textY;
	}

	
	
	public void setTextY(float textY) {
		this.textY = textY;
	}
	
}
