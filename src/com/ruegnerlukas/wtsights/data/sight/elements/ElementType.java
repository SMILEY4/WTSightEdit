package com.ruegnerlukas.wtsights.data.sight.elements;



public enum ElementType {

	CENTRAL_HORZ_LINE("Central Horizontal Line", 0, 1, 1),
	CENTRAL_VERT_LINE("Central Vertical Line", 1, 1, 1),
	RANGEFINDER("Rangefinder", 2, 1, 1),
	HORZ_RANGE_INDICATORS("Horzizontal Range Indicators", 3, 1, 1),
	BALLISTIC_RANGE_INDICATORS("Ballistic Range Indicators", 4, 1, 1),
	SHELL_BALLISTICS_BLOCK("Shell Ballistics Block", 5, 0, 999),
	CUSTOM_LINE("Custom Line", 6, 0, 999),
	CUSTOM_CIRCLE("Custom Circle", 7, 0, 999),
	CUSTOM_QUAD("Custom Quad", 8, 0, 999),
	CUSTOM_TEXT("Custom Text", 9, 0, 999);
	
	
	
	public final String defaultName;
	public final int iconIndex;
	public final int minCount, maxCount;
	
	
	private ElementType(String defaultName, int iconIndex, int minCount, int maxCount) {
		this.defaultName = defaultName;
		this.iconIndex = iconIndex;
		this.minCount = minCount;
		this.maxCount = maxCount;
	}
	
	
	
	public static ElementType get(String str) {
		for(ElementType type : ElementType.values()) {
			if(type.toString().equals(str)) {
				return type;
			}
		}
		return null;
	}
	
	
	public static ElementType getByDefaultName(String name) {
		for(ElementType type : ElementType.values()) {
			if(type.defaultName.equals(name)) {
				return type;
			}
		}
		return null;
	}
	
}
