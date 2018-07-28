package com.ruegnerlukas.wtsights.data.vehicle;

import java.util.ArrayList;
import java.util.List;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;


public class Weapon {
	
	public String name;
	public String triggerGroup;
	public List<Ammo> ammo = new ArrayList<Ammo>();
	
	public void print(int level) {
		String indent = ""; for(int i=0; i<level; i++) { indent+="   ";};
		Logger.get().debug(indent+"weapon=" + name);
		Logger.get().debug(indent+"   triggerGroup=" + triggerGroup);
		for(Ammo a : ammo) {
			a.print(level+1);
		}
	}
	
}
