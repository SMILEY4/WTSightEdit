package com.ruegnerlukas.wtsights.data.sight.sightElements.elements;

import java.util.ArrayList;
import java.util.List;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.simplemath.vectors.vec3.Vector3i;
import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ILayoutData;
import com.ruegnerlukas.wtsights.data.sight.sightElements.layouts.LayoutCircleFilledObject;
import com.ruegnerlukas.wtsights.data.sight.sightElements.layouts.LayoutPolygonFilledObject;
import com.ruegnerlukas.wtutils.Conversion;

public class ElementCustomCircleFilled extends ElementCustomObject {

	public double quality = 1;
	
	public Vector2d 	position 	= new Vector2d(0.1,0);
	public double 		diameter 	= 0.1;
	public Vector2d 	segment 	= new Vector2d(0, 360);
	
	private ElementCustomPolygonFilled polygon = new ElementCustomPolygonFilled();
	
	
	
	
	public ElementCustomCircleFilled() {
		this(ElementType.CUSTOM_CIRCLE_FILLED.defaultName);
	}
	
	
	public ElementCustomCircleFilled(String name) {
		super(name, ElementType.CUSTOM_CIRCLE_FILLED);
		this.setLayout(new LayoutCircleFilledObject());
	}

	
	
	
	
	
	public List<ElementCustomQuadFilled> getQuads() {
		return polygon.getQuads();
	}
	
	
	
	
	public List<Vector2d> getVertices() {
		return polygon.getVertices();
	}
	
	
	
	
	public List<Vector3i> getTriangleIndices() {
		return polygon.getTriangleIndices();
	}
	
	
	
	
	public Vector3i getTriangleIndices(ElementCustomQuadFilled quad) {
		return polygon.getTriangleIndices(quad);
	}
	
	
	
	
	public int calcNumQuads(double diameter, double angleStart, double angleEnd) {
		final double dNrm = useThousandth ? diameter : Conversion.get().screenspace2mil(diameter, false);
		return (int) Math.max(3, Math.min(360, Math.max((int) (((angleEnd-angleStart)/10.0) * (dNrm/50.0)), 15)) * quality);
	}
	
	
	
	
	@Override
	public ILayoutData layout(DataPackage data, double canvasWidth, double canvasHeight) {
		
		LayoutCircleFilledObject layout = (LayoutCircleFilledObject)getLayout();

		if(isDirty()) {
			setDirty(false);
			
			polygon.useThousandth = useThousandth;
			polygon.movement = movement;
			polygon.angle = angle;
			polygon.autoCenter = autoCenter;
			polygon.radCenter = radCenter;
			polygon.speed = speed;
			
			List<Vector2d> vertices = new ArrayList<Vector2d>();
			
			final double angleStart = segment.x;
			final double angleEnd = segment.y;
			if(angleStart > angleEnd) {
				return null;
			}
			
			final int nLines = calcNumQuads(diameter, angleStart, angleEnd);
			double angleStep = (angleEnd-angleStart)/nLines;
			
			vertices.add(new Vector2d(position.x, position.y));
			
			Vector2d pointer = new Vector2d(0,1).rotateDeg(-angleStart).setLength(diameter/2);
			for(int i=0; i<nLines; i++) {
				vertices.add(new Vector2d(position.x+pointer.x, position.y+pointer.y));
				pointer.rotateDeg(-angleStep).setLength(diameter/2);
			}
			
			vertices.add(new Vector2d(position.x+pointer.x, position.y+pointer.y));

			polygon.setVertices(vertices);
			polygon.setDirty(true);
			polygon.layout(data, canvasWidth, canvasHeight);
			
			layout.center.set( ((LayoutPolygonFilledObject)polygon.getLayout()).center );
			layout.radCenter.set( ((LayoutPolygonFilledObject)polygon.getLayout()).radCenter );

		}
		
		return layout;
	}

	

}













