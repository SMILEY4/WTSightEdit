package com.ruegnerlukas.wtsights.data.sight.sightElements.elements;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticElement;
import com.ruegnerlukas.wtsights.data.sight.sightElements.BaseElement;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.layouts.LayoutFunnel;
import com.ruegnerlukas.wtutils.Conversion;


public class ElementFunnel extends BaseElement {


	public int rangeStart = 100;
	public int rangeEnd = 3200;
	public int rangeStep = 50;
	public int sizeTargetCM = 600;
	public BallisticElement elementBallistic = null;
	public Movement movement = Movement.STATIC;
	
	
	private List<ElementCustomLine> lines = new ArrayList<ElementCustomLine>();
	
	
	
	
	public ElementFunnel() {
		this(ElementType.FUNNEL.defaultName);
	}
	
	
	public ElementFunnel(String name) {
		super(name, ElementType.FUNNEL);
		this.setLayout(new LayoutFunnel());
	}
	

	
	
	
	
	public List<ElementCustomLine> getLines() {
		return Collections.unmodifiableList(lines);
	}
	

	
	
	@Override
	public LayoutFunnel layout(DataPackage data, double canvasWidth, double canvasHeight) {
	
		LayoutFunnel layout = (LayoutFunnel)getLayout();
		
		if(isDirty() && elementBallistic != null) {
			setDirty(false);
			

			List<Vector2d> points = new ArrayList<Vector2d>(); // list of tuples (range, mil)
			
			
			final double s = sizeTargetCM * 10;
			points.add(new Vector2d(rangeStart, s / Math.max(rangeStart, 1)));
			for(double d=rangeStart+rangeStep; d<=rangeEnd-rangeStep; d+=rangeStep) {
				double mil = s / Math.max(d, 1);
				points.add(new Vector2d(d, mil));
			}
			points.add(new Vector2d(rangeEnd, s / Math.max(rangeEnd, 1)));

			
			lines.clear();
			if(rangeStart < rangeEnd) {
				for(int i=0; i<points.size()-1; i++) {
					
					Vector2d p0 = points.get(i);
					Vector2d p1 = points.get(i+1);
					
					double py0 = Conversion.get().pixel2mil(elementBallistic.function.eval(p0.x), canvasHeight, false);
					double py1 = Conversion.get().pixel2mil(elementBallistic.function.eval(p1.x), canvasHeight, false);
					
					ElementCustomLine lineRight = new ElementCustomLine();
					lineRight.useThousandth = true;
					lineRight.movement = movement;
					lineRight.start.set(p0.y/2, py0);
					lineRight.end.set(p1.y/2, py1);
					lineRight.setDirty(true);
					lineRight.layout(data, canvasWidth, canvasHeight);
					
					ElementCustomLine lineLeft = new ElementCustomLine();
					lineLeft.useThousandth = true;
					lineLeft.movement = movement;
					lineLeft.start.set(-p0.y/2, py0);
					lineLeft.end.set(-p1.y/2, py1);
					lineLeft.setDirty(true);
					lineLeft.layout(data, canvasWidth, canvasHeight);
					
					lines.add(lineRight);
					lines.add(lineLeft);
					
				}
			}
			
			
		}
		
		return layout;
	}
	
	
}





