package com.ruegnerlukas.wtsights.data.sight;



public abstract class BlockElement {

	public final String name;
	
	public BlockElement(String name) {
		this.name = name;
	}
	
	
	
	public abstract void prettyPrint(int level);
	
	
	public abstract void resourcePrint(int level);
}
