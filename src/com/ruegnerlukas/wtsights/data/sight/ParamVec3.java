package com.ruegnerlukas.wtsights.data.sight;

import com.ruegnerlukas.simplemath.vectors.vec3.Vector3f;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;

public class ParamVec3 extends Parameter {

	public Vector3f value;
	
	
	public ParamVec3(String name, float x, float y, float z) {
		this(name, new Vector3f(x, y, z));
	}
	
	
	public ParamVec3(String name, Vector3f value) {
		super(name, ParameterType.VEC3);
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
		
		Logger.get().debug(str + this.name + ":p3=" + this.value.x + ", " + this.value.y + ", " + this.value.z);
	}

}
