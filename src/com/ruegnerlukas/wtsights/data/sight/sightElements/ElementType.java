package com.ruegnerlukas.wtsights.data.sight.sightElements;

import com.ruegnerlukas.wtsights.ui.view.ViewManager;

public enum ElementType {

	CENTRAL_HORZ_LINE			(ViewManager.getResources().getString("et_central_horzline"), 			0, 1, 1),
	CENTRAL_VERT_LINE			(ViewManager.getResources().getString("et_central_vert_line"), 			1, 1, 1),
	RANGEFINDER					(ViewManager.getResources().getString("et_rangefinder"), 				2, 1, 1),
	HORZ_RANGE_INDICATORS		(ViewManager.getResources().getString("et_horz_range_indicators"), 		3, 1, 1),
	BALLISTIC_RANGE_INDICATORS	(ViewManager.getResources().getString("et_ballistic_range_indicators"), 4, 1, 1),
	SHELL_BALLISTICS_BLOCK		(ViewManager.getResources().getString("et_shell_ballistic_block"), 		5, 0, 9999),
	CUSTOM_LINE					(ViewManager.getResources().getString("et_custom_line"), 				6, 0, 9999),
	CUSTOM_TEXT					(ViewManager.getResources().getString("et_custom_text"),				9, 0, 9999),
	CUSTOM_CIRCLE_OUTLINE		(ViewManager.getResources().getString("et_custom_circle_outline"),		7, 0, 9999),	
	CUSTOM_QUAD_FILLED			(ViewManager.getResources().getString("et_custom_quad_filled"),			8, 0, 9999),
	
//	CUSTOM_CIRCLE_FILLED		(ViewManager.getResources().getString("et_custom_circle_filled"),		7, 0, 9999),
//	CUSTOM_QUAD_OUTLINE			(ViewManager.getResources().getString("et_custom_quad_outline"),		8, 0, 9999),
//	CUSTOM_RECT_OUTLINE			(ViewManager.getResources().getString("et_custom_rect_outline"),		8, 0, 9999),
//	CUSTOM_RECT_FILLED			(ViewManager.getResources().getString("et_custom_rect_filled"),			8, 0, 9999),
//	CUSTOM_POLY_OUTLINE			(ViewManager.getResources().getString("et_custom_poly_outline"),		8, 0, 9999),
//	CUSTOM_POLY_FILLED			(ViewManager.getResources().getString("et_custom_poly_filled"),			8, 0, 9999),
	;

	
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
