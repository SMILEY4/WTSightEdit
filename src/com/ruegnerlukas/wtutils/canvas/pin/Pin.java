package com.ruegnerlukas.wtutils.canvas.pin;

import java.awt.Color;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;

public class Pin {
	public final String name;
	public Vector2d position = new Vector2d(0,0);
	public Color color = Color.BLACK;
	public Pin(String name) {
		this.name = name;
	}
}


