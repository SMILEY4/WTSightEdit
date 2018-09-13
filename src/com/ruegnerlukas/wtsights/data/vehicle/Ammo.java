package com.ruegnerlukas.wtsights.data.vehicle;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;

public class Ammo {
	
	public String type;
	public String name;
	public String namePretty;
	public int speed;
	
	
	public void print(int level) {
		String indent = ""; for(int i=0; i<level; i++) { indent+="   ";};
		Logger.get().debug(indent+"name=" + name);
		Logger.get().debug(indent+"  type=" + type);
		Logger.get().debug(indent+"  speed=" + speed);
	}
	
	
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Ammo) {
			Ammo other = (Ammo)obj;
			if(!other.type.equals(this.type)) { return false; }
			if(!other.name.equals(this.name)) { return false; }
			if(other.speed != this.speed) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
}
