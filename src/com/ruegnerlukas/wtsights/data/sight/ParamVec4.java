package com.ruegnerlukas.wtsights.data.sight;

import com.ruegnerlukas.simplemath.vectors.vec4.Vector4f;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;

public class ParamVec4 extends Parameter {

	public Vector4f value;
	
	
	public ParamVec4(String name, float x, float y, float z, float w) {
		this(name, new Vector4f(x, y, z, w));
	}
	
	
	public ParamVec4(String name, Vector4f value) {
		super(name, ParameterType.VEC4);
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
		
		Logger.get().debug(str + this.name + ":p4=" + this.value.x + ", " + this.value.y + ", " + this.value.z + ", " + this.value.w);
	}
}
