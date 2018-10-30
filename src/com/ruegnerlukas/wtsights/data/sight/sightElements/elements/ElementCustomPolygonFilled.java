package com.ruegnerlukas.wtsights.data.sight.sightElements.elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ruegnerlukas.simplemath.geometry.shapes.polygon.IPolygon;
import com.ruegnerlukas.simplemath.geometry.shapes.polygon.Polygonf;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.layouts.LayoutPolygonFilledObject;
import com.ruegnerlukas.wtutils.Conversion;

public class ElementCustomPolygonFilled extends ElementCustomObject {

	
	private List<Vector2d> vertices = new ArrayList<Vector2d>();
	private List<ElementCustomQuadFilled> quads = new ArrayList<ElementCustomQuadFilled>();
	
	
	
	
	public ElementCustomPolygonFilled() {
		this(ElementType.CUSTOM_POLY_FILLED.defaultName);
	}
	
	
	public ElementCustomPolygonFilled(String name) {
		super(name, ElementType.CUSTOM_POLY_FILLED);
		addVertex(-0.1,  0.1);
		addVertex( 0.1,  0.1);
		addVertex( 0.1,  0.0);
		addVertex( 0.0, -0.1);
		setLayout(new LayoutPolygonFilledObject());
	}
	
	
	

	
	
	public void setVertices(Vector2d... vertices) {
		this.vertices.clear();
		this.quads.clear();
		if(vertices != null) {
			this.quads.clear();
			for(int i=0; i<vertices.length; i++) {
				Vector2d vertex = vertices[i];
				addVertex(vertex);
			}
		}
	}
	
	
	
	public void setVertices(List<Vector2d> vertices) {
		this.vertices.clear();
		this.quads.clear();
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
		this.vertices.add(new Vector2d(x, y));
	}
	
	
	
	
	public List<Vector2d> getVertices() {
		return Collections.unmodifiableList(vertices);
	}
	
	
	
	
	public List<ElementCustomQuadFilled> getQuads() {
		return Collections.unmodifiableList(quads);
	}
	
	
	
	
	@Override
	public LayoutPolygonFilledObject layout(DataPackage data, double canvasWidth, double canvasHeight) {
		
		LayoutPolygonFilledObject layout = (LayoutPolygonFilledObject)getLayout();

		if(isDirty()) {
			setDirty(false);
			
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
			

			quads.clear();
			Polygonf polygon = new Polygonf(vertices.size());
			for(int i=0; i<vertices.size(); i++) {
				polygon.setVertex(vertices.get(i), i);
			}
			List<IPolygon> triangles = polygon.triangulate();
			for(int i=0; i<triangles.size(); i++) {
				IPolygon triangle = triangles.get(i);
				ElementCustomQuadFilled quad = new ElementCustomQuadFilled(name+"_quad"+(i+1));
				quad.pos1.set(triangle.getVertex(0));
				quad.pos2.set(triangle.getVertex(1));
				quad.pos3.set(triangle.getVertex(2));
				quad.pos4.set(triangle.getVertex(2));
				quads.add(quad);
			}
			
			for(ElementCustomQuadFilled quad : this.quads) {
				quad.useThousandth = useThousandth;
				quad.movement = movement;
				quad.angle = angle;
				quad.autoCenter = false;
				if(autoCenter) {
					quad.center.set(0);
					for(Vector2d vertex : vertices) {
						quad.center.add(vertex);
					}
					quad.center.scale(1.0/(double)vertices.size());
				} else {
					quad.center.set(center);
				}
				quad.radCenter = radCenter;
				quad.speed = speed;
				
				quad.setDirty(true);
				quad.layout(data, canvasWidth, canvasHeight);
			}
			
		}
		
		return layout;
	}

}
