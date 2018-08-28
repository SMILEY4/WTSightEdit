package com.ruegnerlukas.wtsights.data.sightfile;



public abstract class BlockElement {

	public final String name;
	public final String metadata;
	
	public BlockElement(String name, String metadata) {
		this.name = name;
		this.metadata = metadata;
	}
	
	
	
	public abstract void prettyPrint(int level);
	
	
	public abstract void resourcePrint(int level);
}
