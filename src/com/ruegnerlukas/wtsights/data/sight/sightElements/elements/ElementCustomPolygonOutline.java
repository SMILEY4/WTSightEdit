package com.ruegnerlukas.wtsights.data.sight.sightElements.elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementSingle;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.layouts.LayoutPolygonOutlineObject;
import com.ruegnerlukas.wtutils.Conversion;

public class ElementCustomPolygonOutline extends ElementCustomMultiObject {

	
	private List<Vector2d> vertices = new ArrayList<Vector2d>();
	private List<ElementCustomLine> lines = new ArrayList<ElementCustomLine>();
	
	
	
	
	public ElementCustomPolygonOutline() {
		this(ElementType.CUSTOM_POLY_OUTLINE.defaultName);
	}
	
	
	public ElementCustomPolygonOutline(String name) {
		super(name, ElementType.CUSTOM_POLY_OUTLINE);
		addVertex(-0.1,  0.1);
		addVertex( 0.1,  0.1);
		addVertex( 0.0, -0.1);
		setMainLayout(new LayoutPolygonOutlineObject());
	}
	
	
	

	
	
	public void setVertices(List<Vector2d> vertices) {
		this.vertices.clear();
		this.getSubElements().clear();
		this.lines.clear();
		for(int i=0; i<vertices.size(); i++) {
			Vector2d vertex = vertices.get(i);
			addVertex(vertex.x, vertex.y);
		}
	}
	
	
	
	
	public void addVertex(double x, double y) {
		ElementCustomLine line = new ElementCustomLine(name+"_line" + lines.size());
		this.lines.add(line);
		this.getSubElements().add(line);
		this.vertices.add(new Vector2d(x, y));
	}
	
	
	
	
	public List<Vector2d> getVertices() {
		return Collections.unmodifiableList(vertices);
	}
	
	
	
	
	@Override
	public void layout(DataPackage data, double canvasWidth, double canvasHeight) {
		
		if(isDirty()) {
			setDirty(false);
			
			LayoutPolygonOutlineObject layout = (LayoutPolygonOutlineObject)getMainLayout();
			
			if(movement == Movement.MOVE_RADIAL) {
				if(autoCenter) {
					layout.center.set(0);
					for(Vector2d vertex : vertices) {
						layout.center.add(vertex);
					}
					layout.center.scale(1.0/(double)vertices.size());
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
			

			lines.get(0).start.set(vertices.get(0));
			for(int i=1; i<vertices.size(); i++) {
				Vector2d vertex = vertices.get(i);
				ElementCustomLine lineA = lines.get(i-1);
				ElementCustomLine lineB = lines.get(i);
				lineA.end.set(vertex);
				lineB.start.set(vertex);
			}
			lines.get(lines.size()-1).end.set(vertices.get(0));
			
			
			for(ElementSingle element : this.getSubElements()) {
				ElementCustomLine line = (ElementCustomLine)element;
				line.useThousandth = useThousandth;
				line.movement = movement;
				line.angle = angle;
				line.autoCenter = autoCenter;
				if(autoCenter) {
					Vector2d centerOW = new Vector2d();
					layout.center.set(0);
					for(Vector2d vertex : vertices) {
						layout.center.add(vertex);
					}
					layout.center.scale(1.0/(double)vertices.size());
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
				for(Vector2d vertex : vertices) {
					centerOW_master.add(vertex);
				}
				centerOW_master.scale(1.0/(double)vertices.size());
				
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
