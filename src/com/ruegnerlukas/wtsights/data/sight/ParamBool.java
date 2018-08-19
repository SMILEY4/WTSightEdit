package com.ruegnerlukas.wtsights.data.sight;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;

public class ParamBool extends Parameter {

	public boolean value;
	
	public ParamBool(String name, String metadata, boolean value) {
		super(name, metadata, ParameterType.BOOL);
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
		
		Logger.get().debug(str + this.name + ":b=" + this.value + (metadata == null ? "" : "  (" + metadata + ")"));
	}
}
