package com.ruegnerlukas.wtsights.data.sight;

import java.util.ArrayList;
import java.util.List;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.wtsights.data.sight.SightData.ScaleMode;
import com.ruegnerlukas.wtsights.data.sight.SightData.TextAlign;
import com.ruegnerlukas.wtsights.data.sight.SightData.TriggerGroup;

public class BallisticsBlock {

	
	public BallisticsBlock(boolean isBallisticsRangeIndicators, String name) {
		this.name = name;
		this.isBallisticsRangeIndicators = isBallisticsRangeIndicators;
		
		if(!isBallisticsRangeIndicators) {
			bDrawCenteredLines = false;
		}
		
		for(int i=200, j=1; i<=2800; i+=200, j++) {
			if(i == 0) { continue; }
			bDists.add(i);
			bMajors.add(j%2==0);
			bExtensions.add(0.0);
			bTextOffsets.add(new Vector2d(0,0));
		}
		
	}
	
	
	
	public final String name;
	public final boolean isBallisticsRangeIndicators;
	public boolean isVisible = true;
	
	// general
	public boolean 			bDrawUpward 	= false;
	public Vector2d 		bMainPos 		= new Vector2d(0.15, 0.0);
	public List<Integer> 	bDists 			= new ArrayList<Integer>();
	public List<Boolean> 	bMajors 		= new ArrayList<Boolean>();
	
	public List<Vector2d> 	bTextOffsets 	= new ArrayList<Vector2d>();
	public double			bTextShift		= 0.2;
	public Vector2d			bTextOffset		= new Vector2d(0.0, 0.0);
	public TextAlign		bTextAlign		= TextAlign.RIGHT;
	public ScaleMode		bScaleMode		= ScaleMode.VERTICAL;
	public boolean 			bCircleMode 	= false;
	public List<Double> 	bExtensions 	= new ArrayList<Double>();
	public boolean 			bDrawCenteredLines = true;
	public Vector2d			bSizeCentered 	= new Vector2d(0.005, 0.003);
	public boolean 			bMove			= true;
	public boolean 			bDrawCorrection = true;
	public Vector2d			bCorrectionPos  = new Vector2d(-0.2, -0.05);

	public String 			bBulletName		= "";
	public String 			bBulletType		= "";
	public TriggerGroup		bTriggerGroup	= TriggerGroup.PRIMARY;
	public int				bBulletSpeed 	= 0;
	
	public double 			bRadialStretch  = 1.0;
	public double			bRadialAngle 	= 0.0;
	public double			bRadialRadius 	= 20;
	public boolean			bRadiusUseMils  = true;
	
	public Vector2d			bSizeMain		= new Vector2d(0.03, 0.02); // vert = (major,minor)   radLine = (length,-)   radCircle = (width, radius)


	
	
	
	
}
