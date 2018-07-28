package com.ruegnerlukas.wtsights.data.sight;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2f;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;

public class ParamVec2 extends Parameter {

	public Vector2f value;
	
	
	public ParamVec2(String name, float x, float y) {
		this(name, new Vector2f(x, y));
	}
	
	
	public ParamVec2(String name, Vector2f value) {
		super(name, ParameterType.VEC2);
		this.value = value;
	}
	

	
	
	@Override
	public void prettyPrint(int level) {
		
		String str = "";
		for(int i=0; i<level; i++) {
			str += "  ";
		}
		
		str += this.type + ": " + this.name + " = " + value.toString();

		Logger.get().debug(str);
	}
	
	
	
	@Override
	public void resourcePrint(int level) {
		
		String str = "";
		for(int i=0; i<level; i++) {
			str += "  ";
		}
		
		Logger.get().debug(str + this.name + ":p2=" + this.value.x + ", " + this.value.y);
	}
}
