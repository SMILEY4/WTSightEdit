package com.ruegnerlukas.wtsights.data.sight;



public class HIndicator {
	
	private int mil;
	private boolean major;
	
	
	public HIndicator(int mil, boolean major) {
		this.mil = mil;
		this.major = major;
	}
	
	
	public int getMil() {
		return mil;
	}
	
	public void setMil(int mil) {
		this.mil = mil;
	}
	
	public boolean isMajor() {
		return major;
	}
	
	public void setMajor(boolean major) {
		this.major = major;
	}
	

}
