package com.ruegnerlukas.wtsights.data.sight.sightElements.elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.layouts.LayoutPolygonOutlineObject;
import com.ruegnerlukas.wtutils.Conversion;

public class ElementCustomPolygonOutline extends ElementCustomObject {

	
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
		setLayout(new LayoutPolygonOutlineObject());
	}
	
	
	

	
	
	public void setVertices(Vector2d... vertices) {
		this.vertices.clear();
		this.lines.clear();
		if(vertices != null) {
			this.lines.clear();
			for(int i=0; i<vertices.length; i++) {
				Vector2d vertex = vertices[i];
				addVertex(vertex);
			}
		}
	}
	
	
	
	public void setVertices(List<Vector2d> vertices) {
		this.vertices.clear();
		this.lines.clear();
		if(vertices != null) {
			for(int i=0; i<vertices.size(); i++) {
				Vector2d vertex = vertices.get(i);
				addVertex(vertex);
			}
		}
	}
	
	
	
	
	public void addVertex(Vector2d vertex) {
		addVertex(vertex.x, vertex.y);
	}
	
	
	public void addVertex(double x, double y) {
		ElementCustomLine line = new ElementCustomLine(name+"_line" + lines.size());
		this.lines.add(line);
		this.vertices.add(new Vector2d(x, y));
	}
	
	
	
	
	public List<Vector2d> getVertices() {
		return Collections.unmodifiableList(vertices);
	}
	
	
	
	
	public List<ElementCustomLine> getLines() {
		return Collections.unmodifiableList(lines);
	}
	
	
	
	
	@Override
	public LayoutPolygonOutlineObject layout(DataPackage data, double canvasWidth, double canvasHeight) {
		
		LayoutPolygonOutlineObject layout = (LayoutPolygonOutlineObject)getLayout();

		if(isDirty()) {
			setDirty(false);
			
			List<Vector2d> vertices = new ArrayList<Vector2d>();
			for(Vector2d vertex : this.vertices) {
				vertices.add(vertex.copy().add(this.positionOffset));
			}
			
			// calculate center (if autocenter) and layout of this parent element
			if(movement == Movement.MOVE_RADIAL) {
				
				if(autoCenter) {
					center.set(0);
					for(Vector2d vertex : vertices) {
						center.add(vertex);
					}
					center.scale(1.0/(double)vertices.size());
				}
				
				if(useThousandth) {
					layout.center.set(
							Conversion.get().mil2pixel(center.x,canvasHeight, data.dataSight.envZoomedIn),
							Conversion.get().mil2pixel(center.y, canvasHeight, data.dataSight.envZoomedIn));
					layout.radCenter.set(
							Conversion.get().mil2pixel(radCenter.x,canvasHeight, data.dataSight.envZoomedIn),
							Conversion.get().mil2pixel(radCenter.y, canvasHeight, data.dataSight.envZoomedIn));
				} else {
					layout.center.set(
							Conversion.get().screenspace2pixel(center.x,canvasHeight, data.dataSight.envZoomedIn),
							Conversion.get().screenspace2pixel(center.y, canvasHeight, data.dataSight.envZoomedIn));
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
			
			
			for(ElementCustomLine line : this.lines) {
				line.useThousandth = useThousandth;
				line.movement = movement;
				line.angle = angle;
				line.autoCenter = false;
				if(autoCenter) {
					line.center.set(0);
					for(Vector2d vertex : vertices) {
						line.center.add(vertex);
					}
					line.center.scale(1.0/(double)vertices.size());
				} else {
					line.center.set(center);
				}
				line.speed = speed;
				line.radCenter = radCenter;
				
				line.setDirty(true);
				line.layout(data, canvasWidth, canvasHeight);
			}
		}
		
		return layout;
	}

}
