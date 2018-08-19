package com.ruegnerlukas.wtsights.data.vehicle;

import java.util.ArrayList;
import java.util.List;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;


public class Vehicle {
	
	public String name;
	public List<Weapon> weaponsList = new ArrayList<Weapon>();

	public List<String> weapons = new ArrayList<String>();
	public List<String> triggerGroups = new ArrayList<String>();
	
	public float fovOut = -1;
	public float fovIn = -1;
	public float fovSight = -1;

	
	
	public void print(int level) {
		String indent = ""; for(int i=0; i<level; i++) { indent+="   ";};
		Logger.get().debug(indent+"vehicle = " + name);
		Logger.get().debug(indent+"fovOut = " + fovOut);
		Logger.get().debug(indent+"fovIn = " + fovIn);
		Logger.get().debug(indent+"fovSight = " + fovIn);
		for(Weapon w : weaponsList) {
			w.print(level+1);
		}
	}
	
	
}
