package com.ruegnerlukas.wtsights.data.sightfile;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;

public class ParamFloat extends Parameter {

	public float value;
	
	
	public ParamFloat(String name, String metadata,  float value) {
		super(name, metadata, ParameterType.FLOAT);
		this.value = value;
	}
	
	
	
	
	@Override
	public void prettyPrint(int level) {
		
		String str = "";
		for(int i=0; i<level; i++) {
			str += "  ";
		}
		
		str += this.type + ": " + this.name + " = " + value + (metadata == null ? "" : "  (" + metadata + ")");

		Logger.get().debug(str);
	}

	
	
	@Override
	public void resourcePrint(int level) {
		
		String str = "";
		for(int i=0; i<level; i++) {
			str += "  ";
		}
		
		Logger.get().debug(str + this.name + ":r=" + this.value + (metadata == null ? "" : "  (" + metadata + ")"));
	}
	
}
