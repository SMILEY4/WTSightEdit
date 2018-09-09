package com.ruegnerlukas.wtsights.data.sight.elements;


import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationAmmoData;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationData;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.data.sight.elements.layouts.LayoutRangefinder;
import com.ruegnerlukas.wtutils.Conversion;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


public class ElementRangefinder extends Element {

	public Vector2d		position		= new Vector2d(5, 15);	// the position relative to the center. x-pos in pixels, y-pos in mils or screenspace
	public boolean 		useThousandth	= true;					// whether to use mils or screenspace for y-position unit
	public Color 		color1 		= new Color(0, 1, 0, 64.0/255.0);	// color of the progress bar
	public Color 		color2 		= new Color(1, 1, 1, 64.0/255.0);	// color of the background
	public double		textScale		= 0.7;					// the scale of the text and progressbar (relative to general font scale)
	
	public LayoutRangefinder layoutData = new LayoutRangefinder();
	
	
	
	
	public ElementRangefinder(String name) {
		super(name, ElementType.RANGEFINDER);
	}
	
	public ElementRangefinder() {
		super(ElementType.RANGEFINDER.defaultName, ElementType.RANGEFINDER);
	}

	
	
	
	@Override
	public LayoutRangefinder layout(SightData sightData, CalibrationData calibData, CalibrationAmmoData ammoData, double canvasWidth, double canvasHeight) {
	
		double x = 0;
		if(sightData.envZoomedIn) {
			x = position.x * sightData.gnrFontScale * Conversion.get().zoomInMul;
		} else {
			x = position.x * sightData.gnrFontScale;
		}
		
		double y = 0;
		if(useThousandth) {
			y = Conversion.get().mil2pixel(position.y+3, canvasHeight, sightData.envZoomedIn) * sightData.gnrFontScale;
			
		} else {
			if(sightData.envZoomedIn) {
				y = Conversion.get().screenspace2pixel(position.y+0.002, canvasHeight, sightData.envZoomedIn) * sightData.gnrFontScale;
			} else {
				y = Conversion.get().screenspace2pixel(position.y, canvasHeight, sightData.envZoomedIn) * sightData.gnrFontScale;
			}
		}
		
		x += canvasWidth/2.0;
		y = canvasHeight/2.0 - y;
		
		
		Font font = null;
		if(sightData.envZoomedIn) {
			font = new Font("Arial", 18.0 * sightData.gnrFontScale * textScale * Conversion.get().zoomInMul);
		} else {
			font = new Font("Arial", 17.5 * sightData.gnrFontScale * textScale);
		}
		Text text = new Text();
		text.setFont(font);
		text.setText("Measuring range");
		text.setWrappingWidth(0);
		text.setLineSpacing(0);
		
		final double width  = text.getLayoutBounds().getWidth();
		final double height = text.getLayoutBounds().getHeight();
		
		layoutData.bounds.set(x, y-height/2, width, height);
		layoutData.textPos.set(x+width/2, y);
		
		return layoutData;
	}
	
	
}
