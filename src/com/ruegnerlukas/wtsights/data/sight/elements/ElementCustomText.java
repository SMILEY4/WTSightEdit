package com.ruegnerlukas.wtsights.data.sight.elements;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.wtutils.SightUtils.TextAlign;

public class ElementCustomText extends ElementCustomObject {

	public String 		text 		= "text";
	public Vector2d 	position 	= new Vector2d(0,0);
	public double 		size 		= 1;
	public TextAlign 	align 		= TextAlign.LEFT;
	
	public ElementCustomText(String name) {
		super(name, ElementType.CUSTOM_TEXT);
	}
	
	public ElementCustomText() {
		super(ElementType.CUSTOM_TEXT.defaultName, ElementType.CUSTOM_TEXT);
	}
	
}
