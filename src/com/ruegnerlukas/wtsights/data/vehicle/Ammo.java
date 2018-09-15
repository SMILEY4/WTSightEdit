package com.ruegnerlukas.wtsights.data.vehicle;


public class Ammo {
	
	public String type;
	public String name;
	public String namePretty;
	public int speed;
	
	
	
	
	public Ammo() {
	}

	
	
	public void print(int level) {
		String indent = ""; for(int i=0; i<level; i++) { indent+="   ";};
	}
	
	
}
