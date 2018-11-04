package com.ruegnerlukas.wtsights.data.sight.sightElements.elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ruegnerlukas.simplemath.geometry.shapes.polygon.Polygonf;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.simplemath.vectors.vec3.Vector3i;
import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.layouts.LayoutPolygonFilledObject;
import com.ruegnerlukas.wtutils.Conversion;

public class ElementCustomPolygonFilled extends ElementCustomObject {

	
	private List<Vector2d> vertices = new ArrayList<Vector2d>();
	private List<ElementCustomQuadFilled> quads = new ArrayList<ElementCustomQuadFilled>();
	private List<Vector3i> triangleIndices = new ArrayList<Vector3i>();
	
	
	
	
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
	
	
	
	
	public List<Vector3i> getTriangleIndices() {
		return Collections.unmodifiableList(triangleIndices);
	}
	
	
	
	
	public Vector3i getTriangleIndices(ElementCustomQuadFilled quad) {
		final int indexQuad = quads.indexOf(quad);
		if(indexQuad != -1) {
			return triangleIndices.get(indexQuad);
		}
		return null;
	}
	
	
	
	
	@Override
	public LayoutPolygonFilledObject layout(DataPackage data, double canvasWidth, double canvasHeight) {
		
		LayoutPolygonFilledObject layout = (LayoutPolygonFilledObject)getLayout();

		if(isDirty()) {
			setDirty(false);
			
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
			

			quads.clear();
			Polygonf polygon = new Polygonf(vertices.size());
			for(int i=0; i<vertices.size(); i++) {
				polygon.setVertex(vertices.get(i), i);
			}
			
			triangleIndices.clear();
			
			polygon.triangulateIndices(triangleIndices);
				for(int i=0; i<triangleIndices.size(); i++) {
				Vector3i triangle = triangleIndices.get(i);
				ElementCustomQuadFilled quad = new ElementCustomQuadFilled(name+"_quad"+(i+1));
				quad.pos1.set(vertices.get(triangle.x));
				quad.pos2.set(vertices.get(triangle.y));
				quad.pos3.set(vertices.get(triangle.z));
				quad.pos4.set(vertices.get(triangle.z));
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
