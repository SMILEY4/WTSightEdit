//package com.ruegnerlukas.wtutils.awtCanvas;
//
//import java.awt.Color;
//import java.awt.Font;
//import java.awt.FontMetrics;
//import java.awt.Graphics2D;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import com.ruegnerlukas.wtutils.awtCanvas.pin.CrossPin;
//import com.ruegnerlukas.wtutils.awtCanvas.pin.Pin;
//import com.ruegnerlukas.wtutils.awtCanvas.pin.TextPin;
//
//import javafx.embed.swing.SwingNode;
//import javafx.geometry.Point2D;
//import javafx.scene.layout.Pane;
//
//public class Pinboard {
//
//	
//	private Map<String, Pin> pins = new HashMap<String, Pin>();
//	private Font font = new Font("Arial", Font.BOLD, 20);
//
//	
//	
//	
//	public void addPin(Pin pin) {
//		pins.put(pin.name, pin);
//	}
//	
//	
//	
//	
//	public void removePin(Pin pin) {
//		pins.remove(pin.name);
//	}
//	
//	
//	
//	
//	public void removePin(String name) {
//		pins.remove(name);
//	}
//	
//	
//	
//	
//	public Pin getPin(String name) {
//		return pins.get(name);
//	}
//	
//	
//	
//	
//	protected void draw(Graphics2D g, WTCanvas canvas, Pane paneDummy, SwingNode nodeCanvas) {
//		
//		for(Entry<String,Pin> entry : pins.entrySet()) {
//			Pin pin = entry.getValue();
//			g.setColor(pin.color);
//			
//			Point2D p = nodeCanvas.sceneToLocal(paneDummy.localToScene(pin.position.x, pin.position.y));
//			double x = p.getX();
//			double y = p.getY();
//			
//			double pixelSize = paneDummy.getScaleX();
//			if(pixelSize >= 2) {	// anchor pins to pixelgrid
//				x = (int)(x / pixelSize) * pixelSize;
//				y = (int)(y / pixelSize) * pixelSize;
//			}
//			
//			if(pin instanceof CrossPin) {
//				CrossPin crossPin = (CrossPin)pin;
//				double size = crossPin.size;
//				g.drawLine( (int)(x-size), (int)(y-size), (int)(x+size), (int)(y+size) );
//				g.drawLine( (int)(x-size), (int)(y+size), (int)(x+size), (int)(y-size) );
//			}
//			
//			if(pin instanceof TextPin) {
//				TextPin textPin = (TextPin)pin;
//				Font fontSaved = g.getFont();
//				g.setFont(font);
//				
//				FontMetrics metrics = g.getFontMetrics(font);
//				int width = metrics.stringWidth(textPin.text);
//				int height = metrics.getAscent();
//				
//				int ox = 0;
//				int oy = 0;
//				
//				if(textPin.align.horzID == 0) { // TOP
//					oy = 0;
//				}
//				if(textPin.align.horzID == 1) { // CENTER
//					oy = height/2;
//				}
//				if(textPin.align.horzID == 2) { // BOTTOM
//					oy = height;
//				}
//				
//				if(textPin.align.vertID == 0) { // LEFT
//					ox = 0;
//				}
//				if(textPin.align.vertID == 1) { // CENTER
//					ox = -width/2;
//				}
//				if(textPin.align.vertID == 2) { // RIGHT
//					ox = -width;
//				}
//
//				g.drawString(textPin.text, (int)x+ox, (int)y+oy);
//				
//				g.setFont(fontSaved);
//			}
//			
//		}
//		
//	}
//	
//	
//
//	
//	
//}
//
