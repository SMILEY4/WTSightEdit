package com.ruegnerlukas.wtsights.data.sight.sightElements.elements;

import java.util.List;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ILayoutData;
import com.ruegnerlukas.wtsights.data.sight.sightElements.layouts.LayoutPolygonOutlineObject;
import com.ruegnerlukas.wtsights.data.sight.sightElements.layouts.LayoutQuadOutlineObject;

public class ElementCustomQuadOutline extends ElementCustomObject {

	
	public Vector2d pos1 = new Vector2d( 0.0, -0.1);
	public Vector2d pos2 = new Vector2d( 0.1, -0.1);
	public Vector2d pos3 = new Vector2d( 0.1,  0.15);
	public Vector2d pos4 = new Vector2d(-0.2,  0.1);
	
	private ElementCustomPolygonOutline polygon = new ElementCustomPolygonOutline();
	
	
	
	
	public ElementCustomQuadOutline() {
		this(ElementType.CUSTOM_QUAD_OUTLINE.defaultName);
	}
	
	
	public ElementCustomQuadOutline(String name) {
		super(name, ElementType.CUSTOM_QUAD_OUTLINE);
		this.setLayout(new LayoutQuadOutlineObject());
	}

	
	
	
	
	
	public List<ElementCustomLine> getLines() {
		return polygon.getLines();
	}
	
	

	
	@Override
	public ILayoutData layout(DataPackage data, double canvasWidth, double canvasHeight) {
		
		LayoutQuadOutlineObject layout = (LayoutQuadOutlineObject)getLayout();

		if(isDirty()) {
			setDirty(false);
			
			Vector2d pos1 = this.pos1.copy().add(this.positionOffset);
			Vector2d pos2 = this.pos2.copy().add(this.positionOffset);
			Vector2d pos3 = this.pos3.copy().add(this.positionOffset);
			Vector2d pos4 = this.pos4.copy().add(this.positionOffset);

			polygon.useThousandth = useThousandth;
			polygon.movement = movement;
			polygon.angle = angle;
			polygon.autoCenter = autoCenter;
			polygon.radCenter = radCenter;
			polygon.speed = speed;
			
			polygon.setVertices(pos1, pos2, pos3, pos4);
			polygon.setDirty(true);
			polygon.layout(data, canvasWidth, canvasHeight);
			
			layout.center.set( ((LayoutPolygonOutlineObject)polygon.getLayout()).center );
			layout.radCenter.set( ((LayoutPolygonOutlineObject)polygon.getLayout()).radCenter );

		}
		
		return layout;
	}
	

}













