package com.ruegnerlukas.wtsights.data.sightfile;

import com.ruegnerlukas.simplemath.vectors.vec3.Vector3f;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;

public class ParamVec3 extends Parameter {

	public Vector3f value;
	
	
	public ParamVec3(String name, String metadata, float x, float y, float z) {
		this(name, metadata, new Vector3f(x, y, z));
	}
	
	
	public ParamVec3(String name, String metadata, Vector3f value) {
		super(name, metadata, ParameterType.VEC3);
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
		
		Logger.get().debug(str + this.name + ":p3=" + this.value.x + ", " + this.value.y + ", " + this.value.z + (metadata == null ? "" : "  (" + metadata + ")"));
	}

}
