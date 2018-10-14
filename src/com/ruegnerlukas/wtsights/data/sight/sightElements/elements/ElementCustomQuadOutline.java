package com.ruegnerlukas.wtsights.data.sight.sightElements.elements;

//public class ElementCustomQuadOutline extends ElementCustomMultiObject {
//
//	
//	public Vector2d pos1 = new Vector2d(-0.1, -0.1);
//	public Vector2d pos2 = new Vector2d(+0.1, -0.1);
//	public Vector2d pos3 = new Vector2d(+0.1, +0.1);
//	public Vector2d pos4 = new Vector2d(-0.1, +0.1);
//	
//	private ElementCustomLine line0, line1, line2, line3;
//	
//	
//	
//	public ElementCustomQuadOutline() {
//		this(ElementType.CUSTOM_QUAD_OUTLINE.defaultName);
//	}
//	
//	
//	public ElementCustomQuadOutline(String name) {
//		super(name, ElementType.CUSTOM_QUAD_OUTLINE);
//		line0 = new ElementCustomLine(name+"_line0");
//		line1 = new ElementCustomLine(name+"_line1");
//		line2 = new ElementCustomLine(name+"_line2");
//		line3 = new ElementCustomLine(name+"_line3");
//		this.getSubElements().add(line0);
//		this.getSubElements().add(line1);
//		this.getSubElements().add(line2);
//		this.getSubElements().add(line3);
//	}
//	
//	
//	
//	
//	@Override
//	public void layout(DataPackage data, double canvasWidth, double canvasHeight) {
//		if(isDirty()) {
//			setDirty(false);
//			
//			line0.start.set(pos1);
//			line0.end.set(pos2);
//			line1.start.set(pos2);
//			line1.end.set(pos3);
//			line2.start.set(pos3);
//			line2.end.set(pos4);
//			line3.start.set(pos4);
//			line3.end.set(pos1);
//			
//			for(ElementSingle element : this.getSubElements()) {
//				ElementCustomLine line = (ElementCustomLine)element;
//				line.useThousandth = useThousandth;
//				line.movement = movement;
//				line.angle = angle;
//				line.autoCenter = autoCenter;
//				if(autoCenter) {
//					Vector2d centerOW = new Vector2d().add(pos1).add(pos2).add(pos3).add(pos4.x).scale(1.0/4.0);
//					line.center = centerOW;
//				} else {
//					line.center = center;
//				}
//				line.radCenter = radCenter;
//				line.speed = speed; // todo: calc speed for each line indiviually
//				line.layout(data, canvasWidth, canvasHeight);
//			}
//		}
//	}
//
//}
