package com.ruegnerlukas.wtsights.data.sight;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;

public class ParamInteger extends Parameter {

	public int value;
	
	
	public ParamInteger(String name, int value) {
		super(name, ParameterType.INTEGER);
		this.value = value;
	}
	
	
	
	
	@Override
	public void prettyPrint(int level) {
		
		String str = "";
		for(int i=0; i<level; i++) {
			str += "  ";
		}
		
		str += this.type + ": " + this.name + " = " + value;

		Logger.get().debug(str);
	}
	
	
	@Override
	public void resourcePrint(int level) {
		
		String str = "";
		for(int i=0; i<level; i++) {
			str += "  ";
		}
		
		Logger.get().debug(str + this.name + ":i=" + this.value);
	}
	
}
