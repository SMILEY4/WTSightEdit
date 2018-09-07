package com.ruegnerlukas.wtutils.awtCanvas.pin;

public class TextPin extends Pin {

	public enum Align {
		LEFT_TOP(0,0),
		LEFT_CENTER(0,1),
		LEFT_BOTTOM(0,2),
		CENTER_TOP(1,0),
		CENTER(1,1),
		CENTER_BOTTOM(1,2),
		RIGHT_TOP(2,0),
		RIGHT_CENTER(2,1),
		RIGHT_BOTTOM(2,2);
	
		public final int vertID; // 0 = LEFT, 1 = CENTER, 2 = RIGHT
		public final int horzID; // 0 = TOP,  1 = CENTER, 2 = BOTTOM
		
		private Align(int vertID, int horzID) {
			this.vertID = vertID;
			this.horzID = horzID;
		}
		
	}
	
	
	public String text;
	public Align align = Align.CENTER;
	
	public TextPin(String name) {
		super(name);
	}
}


