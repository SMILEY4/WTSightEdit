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


	public BallisticElement elementBallistic = null;
	
	public Movement movement = Movement.STATIC;
	
	public boolean showRight = true;
	public boolean showLeft = true;
	public boolean baseLine = false;
	public boolean flip = false;
	public boolean horz = false; // todo
	
	public boolean useThousandth = true;
	public Vector2d offset = new Vector2d(0,0);
	
	public int sizeTargetCM = 600;
	public int rangeStart = 100;
	public int rangeEnd = 3200;
	public int rangeStep = 50;
	
	
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

			if(flip) {
				List<Vector2d> flippedPoints = new ArrayList<Vector2d>(points.size());
				for(int i=0; i<points.size(); i++) {
					Vector2d pointA = points.get(i);
					Vector2d pointB = points.get(points.size()-i-1);
					flippedPoints.add(new Vector2d(pointA.x, pointB.y));
				}
				points = flippedPoints;
			}
			
			lines.clear();
			if(rangeStart < rangeEnd) {
				for(int i=0; i<points.size()-1; i++) {
					
					Vector2d p0 = points.get(i);
					Vector2d p1 = points.get(i+1);
					
					double px0 = 0;
					double px1 = 0;
					if(useThousandth) {
						px0 = p0.y;
						px1 = p1.y;
					} else {
						px0 = Conversion.get().mil2screenspace(p0.y, false);
						px1 = Conversion.get().mil2screenspace(p1.y, false);
					}
					
					double py0 = 0;
					double py1 = 0;
					if(useThousandth) {
						py0 = Conversion.get().pixel2mil(elementBallistic.function.eval(p0.x), canvasHeight, false);
						py1 = Conversion.get().pixel2mil(elementBallistic.function.eval(p1.x), canvasHeight, false);
					} else {
						py0 = Conversion.get().pixel2screenspace(elementBallistic.function.eval(p0.x), canvasHeight, false);
						py1 = Conversion.get().pixel2screenspace(elementBallistic.function.eval(p1.x), canvasHeight, false);
					}
					
					if(showRight) {
						ElementCustomLine lineRight = new ElementCustomLine();
						lineRight.useThousandth = true;
						lineRight.movement = movement;
						lineRight.useThousandth = this.useThousandth;
						if(horz) {
							lineRight.start.set( py0 + offset.x, (showLeft ? px0/2 : px0) + offset.y);
							lineRight.end.set( py1 + offset.x, (showLeft ? px1/2 : px1) + offset.y);
						} else {
							lineRight.start.set((showLeft ? px0/2 : px0) + offset.x, py0 + offset.y);
							lineRight.end.set((showLeft ? px1/2 : px1) + offset.x, py1 + offset.y);
						}
						lineRight.setDirty(true);
						lineRight.layout(data, canvasWidth, canvasHeight);
						lines.add(lineRight);
					}
					
					if(showLeft) {
						ElementCustomLine lineLeft = new ElementCustomLine();
						lineLeft.useThousandth = true;
						lineLeft.movement = movement;
						lineLeft.useThousandth = this.useThousandth;
						if(horz) {
							lineLeft.start.set( py0 + offset.x, (showRight ? -px0/2 : -px0) + offset.y);
							lineLeft.end.set( py1 + offset.x, (showRight ? -px1/2 : -px1) + offset.y);
						} else {
							lineLeft.start.set((showRight ? -px0/2 : -px0) + offset.x, py0 + offset.y);
							lineLeft.end.set((showRight ? -px1/2 : -px1) + offset.x, py1 + offset.y);
						}
						lineLeft.setDirty(true);
						lineLeft.layout(data, canvasWidth, canvasHeight);
						lines.add(lineLeft);
					}
					
				}
				
				if((showRight ^ showLeft) && baseLine) {
					double py0 = Conversion.get().pixel2mil(elementBallistic.function.eval(rangeStart), canvasHeight, false);
					double py1 = Conversion.get().pixel2mil(elementBallistic.function.eval(rangeEnd), canvasHeight, false);
					ElementCustomLine baseLine = new ElementCustomLine();
					baseLine.useThousandth = this.useThousandth;
					baseLine.movement = movement;
					if(horz) {
						baseLine.start.set(py0 + offset.x, 0 + offset.y);
						baseLine.end.set(py1 + offset.x, 0 + offset.y);
					} else {
						baseLine.start.set(0 + offset.x, py0 + offset.y);
						baseLine.end.set(0 + offset.x, py1 + offset.y);
					}
					baseLine.setDirty(true);
					baseLine.layout(data, canvasWidth, canvasHeight);
					lines.add(baseLine);
				}
				
			}
			
			
		}
		
		return layout;
	}
	
	
}





