package com.ruegnerlukas.wtsights.data.sight.sightElements.elements;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementSingle;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.layouts.LayoutQuadOutlineObject;
import com.ruegnerlukas.wtutils.Conversion;

public class ElementCustomQuadOutlineOLD extends ElementCustomMultiObject {

	
	public Vector2d pos1 = new Vector2d( 0.0, -0.1);
	public Vector2d pos2 = new Vector2d( 0.1, -0.1);
	public Vector2d pos3 = new Vector2d( 0.1,  0.15);
	public Vector2d pos4 = new Vector2d(-0.2,  0.1);
	
	private ElementCustomLine line0, line1, line2, line3;
	
	
	
	
	public ElementCustomQuadOutlineOLD() {
		this(ElementType.CUSTOM_QUAD_OUTLINE.defaultName);
	}
	
	
	public ElementCustomQuadOutlineOLD(String name) {
		super(name, ElementType.CUSTOM_QUAD_OUTLINE);
		line0 = new ElementCustomLine(name+"_line0");
		line1 = new ElementCustomLine(name+"_line1");
		line2 = new ElementCustomLine(name+"_line2");
		line3 = new ElementCustomLine(name+"_line3");
		this.getSubElements().add(line0);
		this.getSubElements().add(line1);
		this.getSubElements().add(line2);
		this.getSubElements().add(line3);
		setMainLayout(new LayoutQuadOutlineObject());
	}
	
	
	
	
	@Override
	public void layout(DataPackage data, double canvasWidth, double canvasHeight) {
		if(isDirty()) {
			setDirty(false);
			
			LayoutQuadOutlineObject layout = (LayoutQuadOutlineObject)getMainLayout();
			
			if(movement == Movement.MOVE_RADIAL) {
				if(autoCenter) {
					layout.center.set(0).add(pos1).add(pos2).add(pos3).add(pos4).scale(1.0/4.0);
				} else {
					layout.center.set(center);
				}
				if(useThousandth) {
					layout.center.set(
							Conversion.get().mil2pixel(layout.center.x,canvasHeight, data.dataSight.envZoomedIn),
							Conversion.get().mil2pixel(layout.center.y, canvasHeight, data.dataSight.envZoomedIn));
					layout.radCenter.set(
							Conversion.get().mil2pixel(radCenter.x,canvasHeight, data.dataSight.envZoomedIn),
							Conversion.get().mil2pixel(radCenter.y, canvasHeight, data.dataSight.envZoomedIn));
				} else {
					layout.center.set(
							Conversion.get().screenspace2pixel(layout.center.x,canvasHeight, data.dataSight.envZoomedIn),
							Conversion.get().screenspace2pixel(layout.center.y, canvasHeight, data.dataSight.envZoomedIn));
					layout.radCenter.set(
							Conversion.get().screenspace2pixel(radCenter.x,canvasHeight, data.dataSight.envZoomedIn),
							Conversion.get().screenspace2pixel(radCenter.y, canvasHeight, data.dataSight.envZoomedIn));
				}
				layout.center.add(canvasWidth/2, canvasHeight/2);
				layout.radCenter.add(canvasWidth/2, canvasHeight/2);
			} else {
				layout.center.set(-10000, -10000);
				layout.radCenter.set(-10000, -10000);
			}
			
			line0.start.set(pos1);
			line0.end.set(pos2);
			line1.start.set(pos2);
			line1.end.set(pos3);
			line2.start.set(pos3);
			line2.end.set(pos4);
			line3.start.set(pos4);
			line3.end.set(pos1);
			
			for(ElementSingle element : this.getSubElements()) {
				ElementCustomLine line = (ElementCustomLine)element;
				line.useThousandth = useThousandth;
				line.movement = movement;
				line.angle = angle;
				line.autoCenter = autoCenter;
				if(autoCenter) {
					Vector2d centerOW = new Vector2d().add(pos1).add(pos2).add(pos3).add(pos4.x).scale(1.0/4.0);
					line.center = centerOW;
				} else {
					line.center = center;
				}
				line.radCenter = radCenter;
				
				
				Vector2d centerOW_line = new Vector2d();
				centerOW_line.add(line.start).add(line.end).scale(0.5f);
				
				double radius_line;
				if(useThousandth) {
					radius_line = centerOW_line.dist(radCenter);
				} else {
					radius_line = Conversion.get().screenspace2mil(centerOW_line.dist(radCenter), false);
				}
				
				Vector2d centerOW_master = new Vector2d();
				if(autoCenter) {
					centerOW_master.add(pos1).add(pos2).add(pos3).add(pos4).scale(1.0/4.0);
				} else {
					centerOW_master.set(center);
				}
				
				double radius_master;
				if(useThousandth) {
					radius_master = centerOW_master.dist(radCenter);
				} else {
					radius_master = Conversion.get().screenspace2mil(centerOW_master.dist(radCenter), false);
				}
				
				double speedMod = radius_line / radius_master;
				
				line.speed = speed * speedMod;
				line.layout(data, canvasWidth, canvasHeight);
			}
		}
		
	}

}
