package com.ruegnerlukas.wtsights.data.sightfile;

import java.util.ArrayList;
import java.util.List;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;

public class Block extends BlockElement {

	public List<BlockElement> elements = new ArrayList<BlockElement>();
	
	
	
	
	public Block(String name, String metadata) {
		super(name, metadata);
	}
	
	
	
	
	
	
	public BlockElement getElementByName(String name) {
		for(int i=0; i<elements.size(); i++) {
			BlockElement e = elements.get(i);
			if(e.name.equalsIgnoreCase(name)) {
				return e;
			}
		}
		return null;
	}
	
	
	
	
	public Parameter getParameterByName(String name) {
		for(int i=0; i<elements.size(); i++) {
			BlockElement e = elements.get(i);
			if(e instanceof Parameter && e.name.equalsIgnoreCase(name)) {
				return (Parameter)e;
			}
		}
		return null;
	}
	
	
	
	
	public Block getBlockByName(String name) {
		for(int i=0; i<elements.size(); i++) {
			BlockElement e = elements.get(i);
			if(e instanceof Block && e.name.equalsIgnoreCase(name)) {
				return (Block)e;
			}
		}
		return null;
	}
	
	
	
	public List<Parameter> getAllParameters() {
		ArrayList<Parameter> params = new ArrayList<Parameter>();
		for(BlockElement e : elements) {
			if(e instanceof Parameter) {
				params.add((Parameter)e);
			}
		}
		return params;
	}
	
	
	
	public List<Parameter> getAllParameters(Class<? extends Parameter> type) {
		ArrayList<Parameter> params = new ArrayList<Parameter>();
		for(BlockElement e : elements) {
			if(e.getClass() == type) {
				params.add((Parameter)e);
			}
		}
		return params;
	}
	
	
	
	@Override
	public void prettyPrint(int level) {
		
		String str = "";
		for(int i=0; i<level; i++) {
			str += "  ";
		}
		
		str += "BLOCK: " + this.name + (metadata == null ? "" : "  (" + metadata + ")");
		
		Logger.get().debug(str);
		
		for(BlockElement e : elements) {
			e.prettyPrint(level+1);
		}
	}
	
	
	
	
	@Override
	public void resourcePrint(int level) {
		
		String str = "";
		for(int i=0; i<level; i++) {
			str += "  ";
		}
		
		
		Logger.get().debug(str + this.name + "{" + (metadata == null ? "" : "  (" + metadata + ")"));
		
		for(BlockElement e : elements) {
			e.resourcePrint(level+1);
		}
		
		Logger.get().debug(str + "}");
	}

	
}
