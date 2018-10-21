package com.ruegnerlukas.wtsights.data.sight.sightElements.elements;


import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.sight.sightElements.Element;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.layouts.LayoutRangefinder;
import com.ruegnerlukas.wtsights.ui.sighteditor.rendering.SightRenderer;
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
	
	private Text text = new Text();

	
	
	public ElementRangefinder() {
		this(ElementType.RANGEFINDER.defaultName);
	}
	
	
	public ElementRangefinder(String name) {
		super(name, ElementType.RANGEFINDER);
		this.setLayout(new LayoutRangefinder());
	}
	


	
	@Override
	public LayoutRangefinder layout(DataPackage data, double canvasWidth, double canvasHeight) {
	
		LayoutRangefinder layout = (LayoutRangefinder)getLayout();
		
		if(isDirty()) {
			setDirty(false);
			
			double x = 0;
			if(data.dataSight.envZoomedIn) {
				x = position.x * data.dataSight.gnrFontScale * Conversion.get().zoomInMul;
			} else {
				x = position.x * data.dataSight.gnrFontScale;
			}
			
			double y = 0;
			if(useThousandth) {
				y = Conversion.get().mil2pixel(position.y+3, canvasHeight, data.dataSight.envZoomedIn) * data.dataSight.gnrFontScale;
				
			} else {
				if(data.dataSight.envZoomedIn) {
					y = Conversion.get().screenspace2pixel(position.y+0.002, canvasHeight, data.dataSight.envZoomedIn) * data.dataSight.gnrFontScale;
				} else {
					y = Conversion.get().screenspace2pixel(position.y, canvasHeight, data.dataSight.envZoomedIn) * data.dataSight.gnrFontScale;
				}
			}
			
			x += canvasWidth/2.0;
			y = canvasHeight/2.0 - y;
			
			if(data.dataSight.envZoomedIn) {
				layout.fontSize = 18.0 * data.dataSight.gnrFontScale * textScale * Conversion.get().zoomInMul;
			} else {
				layout.fontSize = 17.5 * data.dataSight.gnrFontScale * textScale;
			}
			
			Font font = SightRenderer.getFont(layout.fontSize);
			text.setFont(font);
			text.setText("Measuring range");
			text.setWrappingWidth(0);
			text.setLineSpacing(0);
			
			final double width  = text.getLayoutBounds().getWidth();
			final double height = text.getLayoutBounds().getHeight();
			
			layout.bounds.set(x, y-height/2, width, height);
			layout.textPos.set(x+width/2, y);
		}
		
		return layout;
	}
	
	
}
