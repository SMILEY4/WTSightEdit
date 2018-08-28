package com.ruegnerlukas.wtsights.data.sightfile;

import com.ruegnerlukas.simplemath.vectors.vec4.Vector4f;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;

public class ParamColor extends Parameter {

	public Vector4f value;
	
	
	public ParamColor(String name, String metadata, float r, float g, float b) {
		this(name, metadata, new Vector4f(r, g, b, 255));
	}
	
	
	public ParamColor(String name, String metadata, float r, float g, float b, float a) {
		this(name, metadata, new Vector4f(r, g, b, a));
	}
	
	
	public ParamColor(String name, String metadata, Vector4f value) {
		super(name, metadata, ParameterType.COLOR);
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
		
		Logger.get().debug(str + this.name + ":c=" + this.value.x + ", " + this.value.y + ", " + this.value.z + ", " + this.value.w + (metadata == null ? "" : "  (" + metadata + ")"));
	}
	
}
