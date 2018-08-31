package com.ruegnerlukas.wtsights.data.sightfile;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2f;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;

public class ParamVec2 extends Parameter {

	public Vector2f value;
	
	
	public ParamVec2(String name, String metadata, float x, float y) {
		this(name, metadata, new Vector2f(x, y));
	}
	
	
	public ParamVec2(String name, String metadata, Vector2f value) {
		super(name, metadata, ParameterType.VEC2);
		this.value = value;
	}
	

	
	
	@Override
	public void prettyPrint(int level) {
		
		String str = "";
		for(int i=0; i<level; i++) {
			str += "  ";
		}
		
		str += this.type + ": " + this.name + " = " + value.toString() + (metadata == null ? "" : "  (" + metadata + ")");

		Logger.get().debug(str);
	}
	
	
	
	@Override
	public void resourcePrint(int level) {
		
		String str = "";
		for(int i=0; i<level; i++) {
			str += "  ";
		}
		
		Logger.get().debug(str + this.name + ":p2=" + this.value.x + ", " + this.value.y + (metadata == null ? "" : "  (" + metadata + ")"));
	}
}
