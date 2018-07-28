package com.ruegnerlukas.wtsights.data.sight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.wtsights.data.sight.objects.SightObject;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class SightData {

	
	
	
	public static enum Thousandth {
		USSR("ussr", "ussr (6000)", 6000),
		NATO("nato", "nato (6400)", 6400),
		REAL("real", "real (6283)", 6283);
		
		public final String tag;
		public final String display;
		public int value;

		private Thousandth(String tag, String display, int value) {
			this.tag = tag;
			this.display = display;
			this.value = value;
		}
		
		public static Thousandth get(String str) {
			if(str.equalsIgnoreCase("ussr")) { return USSR; };
			if(str.equalsIgnoreCase("nato")) { return NATO; };
			if(str.equalsIgnoreCase("real")) { return REAL; };
			return USSR;
		}
	}

	public static final Thousandth[] THOUSANDTH_LIST = new Thousandth[] {Thousandth.USSR, Thousandth.NATO, Thousandth.REAL};
	
	
	
	
	public static enum TextAlign {
		LEFT(-1),
		CENTER(0),
		RIGHT(1);
		
		public final int id;
		
		private TextAlign(int id) {
			this.id = id;
		}
		
		public static TextAlign get(int id) {
			if(id == -1) { return LEFT; }
			if(id ==  0) { return CENTER; }
			if(id == +1) { return RIGHT; }
			return LEFT;
		}
		
		public static TextAlign get(String str) {
			if(LEFT.toString().equalsIgnoreCase(str)) { return LEFT; }
			if(RIGHT.toString().equalsIgnoreCase(str)) { return RIGHT; }
			if(CENTER.toString().equalsIgnoreCase(str)) { return CENTER; }
			return LEFT;
		}
	}
	
	
	
	
	public static enum ScaleMode {
		VERTICAL,
		RADIAL
	}
	
	
	
	public static enum TriggerGroup {
		PRIMARY,
		SECONDARY,
		COAXIAL,
		MACHINEGUN,
		SPECIAL,
		TORPEDOES,
		DEPTH_CHARGE,
		ROCKETS,
		MINE,
		SMOKE;
		
		
		public static TriggerGroup get(String strTriggerGroup) {
			if("primary".equalsIgnoreCase(strTriggerGroup)) 	 { return PRIMARY; }
			if("secondary".equalsIgnoreCase(strTriggerGroup)) 	 { return SECONDARY; }
			if("coaxial".equalsIgnoreCase(strTriggerGroup)) 	 { return COAXIAL; }
			if("machinegun".equalsIgnoreCase(strTriggerGroup))   { return MACHINEGUN; }
			if("special".equalsIgnoreCase(strTriggerGroup)) 	 { return SPECIAL; }
			if("torpedoes".equalsIgnoreCase(strTriggerGroup)) 	 { return TORPEDOES; }
			if("depth_charge".equalsIgnoreCase(strTriggerGroup)) { return DEPTH_CHARGE; }
			if("rockets".equalsIgnoreCase(strTriggerGroup)) 	 { return ROCKETS; }
			if("mine".equalsIgnoreCase(strTriggerGroup)) 		 { return MINE; }
			if("smoke".equalsIgnoreCase(strTriggerGroup)) 		 { return SMOKE; }
			return null;
		}
		
	}
	
	
	
	
	public SightData() {
		
		for(int i=-32, j=0; i<=32; i+=4, j++) {
			if(i == 0) { continue; }
			hrMils.add(i);
			hrMajors.add(j%2==0);
		}
		
	}

	
	
	
	// misc data / debug
	public boolean 	envZoomedIn 		= false;
	public Image 	envBackground 		= null;
	public Color 	envSightColor 		= Color.BLACK;
	public boolean 	envShowRangeFinder 	= true;
	public double 	envProgress 		= 30;
	public int		envRangeCorrection 	= 0;
	
	
	// general
	public Thousandth 	gnrThousandth 			= Thousandth.USSR;
	public double 		gnrFontScale 			= 1;
	public double 		gnrLineSize 			= 1;
	public boolean 		gnrDrawCentralHorzLine 	= true;
	public boolean 		gnrDrawCentralVertLine 	= true;
	public boolean		gnrApplyCorrectionToGun = true;	// TODO
	
	// rangefinder
	public Vector2d rfOffset		= new Vector2d(5, 15);
	public Color 	rfColor1 		= new Color(0/255f, 255/255f, 0/255f, 64/255f);
	public Color 	rfColor2 		= new Color(255/255f, 255/255f, 255/255f, 64/255f);
	public double 	rfTextScale 	= 0.7f;
	public boolean	rfUseThousandth = true;

	
	// horz range indicators
	public double 			hrSizeMajor = 3;
	public double 			hrSizeMinor = 2;
	public List<Integer> 	hrMils 		= new ArrayList<Integer>();
	public List<Boolean> 	hrMajors 	= new ArrayList<Boolean>();

	
	// ballistic range indicators
	public BallisticsBlock brIndicators = new BallisticsBlock(true, "BALLISTIC_RANGE_INDICATORS_"+Integer.toHexString("BALLISTIC_RANGE_INDICATORS".hashCode()));
	
	
	// shell ballisticsBlock
	public Map<String, BallisticsBlock> shellBlocks = new HashMap<String, BallisticsBlock>();
	
	
	// custom elements
	public Map<String, SightObject> objects = new HashMap<String, SightObject>();

	
	
	// selected element
	public static enum SelectedElement {
		ENVIRONMENT,
		GENERAL,
		RANGEFINDER,
		HORZ_RANGE,
		BALL_RANGE,
		SHELL_BLOCK,
		CUSTOM_ELEMENT;
	}
	
	public SelectedElement selectedElement;
	public String selectedSubElement;
	
	
}





