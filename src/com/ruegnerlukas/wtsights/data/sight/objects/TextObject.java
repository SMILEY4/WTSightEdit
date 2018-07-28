package com.ruegnerlukas.wtsights.data.sight.objects;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.wtsights.data.sight.SightData.TextAlign;

public class TextObject extends SightObject {

	public TextObject() {
		this.type = Type.TEXT;
	}
	
	public String text = "text";
	public Vector2d pos = new Vector2d();
	public double size = 1;
	public TextAlign align = TextAlign.LEFT;
	
}
