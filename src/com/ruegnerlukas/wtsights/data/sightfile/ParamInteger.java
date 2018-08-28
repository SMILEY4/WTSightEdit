package com.ruegnerlukas.wtsights.data.sightfile;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;

public class ParamInteger extends Parameter {

	public int value;
	
	
	public ParamInteger(String name, String metadata, int value) {
		super(name, metadata, ParameterType.INTEGER);
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
		
		Logger.get().debug(str + this.name + ":i=" + this.value + (metadata == null ? "" : "  (" + metadata + ")"));
	}
	
}
