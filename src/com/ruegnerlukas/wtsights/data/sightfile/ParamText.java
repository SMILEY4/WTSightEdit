package com.ruegnerlukas.wtsights.data.sightfile;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;

public class ParamText extends Parameter {

	public String text;
	
	
	public ParamText(String name, String metadata,  String text) {
		super(name, metadata, ParameterType.TEXT);
		this.text = text.replace("\"", "");
	}
	
	
	
	
	@Override
	public void prettyPrint(int level) {
		
		String str = "";
		for(int i=0; i<level; i++) {
			str += "  ";
		}
		
		str += this.type + ": " + this.name + " = \"" + text + "\"" + (metadata == null ? "" : "  (" + metadata + ")");

		Logger.get().debug(str);
	}

	
	@Override
	public void resourcePrint(int level) {
		
		String str = "";
		for(int i=0; i<level; i++) {
			str += "  ";
		}
		
		Logger.get().debug(str + this.name + ":t=" + "\""+ this.text + "\"" + (metadata == null ? "" : "  (" + metadata + ")"));
	}
	
}
