package com.ruegnerlukas.wtsights.data.sight;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;

public abstract class Parameter extends BlockElement {

	public static enum ParameterType {
		TEXT("t"),
		FLOAT("r"),
		INTEGER("i"),
		BOOL("b"),
		VEC2("p2"),
		VEC3("p3"),
		VEC4("p4"),
		COLOR("c");
		
		public final String token;
		private ParameterType(String token) {
			this.token = token;
		}
	}
	
	public final ParameterType type;
	
	
	public Parameter(String name, ParameterType type) {
		super(name);
		this.type = type;
	}
	
	
	
	@Override
	public void prettyPrint(int level) {
		
		String str = "";
		for(int i=0; i<level; i++) {
			str += "  ";
		}
		
		str += this.type + ": " + this.name;

		Logger.get().debug(str);
		
	}
	
	
	
}

