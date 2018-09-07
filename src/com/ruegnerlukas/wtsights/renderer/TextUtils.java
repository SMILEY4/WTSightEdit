package com.ruegnerlukas.wtsights.renderer;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class TextUtils {

	
	public static enum VPos {
		TOP, CENTER, BOTTOM;
	}
	public static enum HPos {
		LEFT, CENTER, RIGHT;
	}
	
	
	public static double calcWidth(Graphics2D g, Font font, String str) {
		FontMetrics metrics = g.getFontMetrics(font);
		return metrics.stringWidth(str);
	}
	
	public static double calcHeight(Graphics2D g, Font font, String str) {
		FontMetrics metrics = g.getFontMetrics(font);
		return metrics.getAscent();
	}
	
	
	public static double calcOffsetX(Graphics2D g, Font font, HPos align, String str) {
		double width = calcWidth(g, font, str);
		if(align == HPos.LEFT) {
			return 0;
		}
		if(align == HPos.CENTER) {
			return -width/2;
		}
		if(align == HPos.RIGHT) {
			return -width;
		}
		return 0;
	}
	
	
	public static double calcOffsetY(Graphics2D g, Font font, VPos align, String str) {
		double height = calcHeight(g, font, str);
		if(align == VPos.TOP) {
			return 0;
		}
		if(align == VPos.CENTER) {
			return height/2;
		}
		if(align == VPos.BOTTOM) {
			return height;
		}
		return 0;
	}
	
}
