package com.ruegnerlukas.wtsights.data.sight.elements;


import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;

import javafx.scene.paint.Color;


public class ElementRangefinder extends Element {

	public Vector2d		position		= new Vector2d(5, 15);	// the position relative to the center. x-pos in pixels, y-pos in mils or screenspace
	public boolean 		useThousandth	= true;					// whether to use mils or screenspace for y-position unit
	public Color 		color1 		= new Color(0, 1, 0, 64.0/255.0);	// color of the progress bar
	public Color 		color2 		= new Color(1, 1, 1, 64.0/255.0);	// color of the background
	public double		textScale		= 0.7;					// the scale of the text and progressbar (relative to general font scale)
	
	
	public ElementRangefinder(String name) {
		super(name, ElementType.RANGEFINDER);
	}
	
	public ElementRangefinder() {
		super(ElementType.RANGEFINDER.defaultName, ElementType.RANGEFINDER);
	}
	
}
