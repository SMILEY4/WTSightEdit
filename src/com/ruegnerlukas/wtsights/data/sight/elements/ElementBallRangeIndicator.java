package com.ruegnerlukas.wtsights.data.sight.elements;

import java.util.ArrayList;
import java.util.List;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.wtsights.data.sight.BIndicator;
import com.ruegnerlukas.wtsights.data.sight.HIndicator;
import com.ruegnerlukas.wtutils.SightUtils.ScaleMode;
import com.ruegnerlukas.wtutils.SightUtils.TextAlign;

public class ElementBallRangeIndicator extends Element {

	public boolean 		drawUpward 		= false;						// flip the scale and movement direction.
	public Vector2d 	position 		= new Vector2d(0.15, 0.0);		// position of the main indicator notches + labels relative to center
	public Vector2d		size			= new Vector2d(0.03, 0.02);		// size of the main indicators. vertical: length of major/minor notches; radial-lines: first value = length of lines; radial-circles: first value = strocke width, secodn value = circle radius
	public double		textShift		= 0.2;							// vertical: extends left side of major notches together with labels
	public Vector2d		textPos			= new Vector2d(0.0, 0.0);		// vertical: offset for all main indicator labels
	public TextAlign	textAlign		= TextAlign.RIGHT;				// label text alignment
	public ScaleMode	scaleMode		= ScaleMode.VERTICAL;			// vertical or radial
	public boolean		circleMode		= false;						// radial: lines or circles
	public boolean 		drawAddLines	= true;							// vertical: whether to draw the add. ball. range indicator lines alogn central vertical axis
	public Vector2d		sizeAddLine		= new Vector2d(0.005, 0.003);	// vertical: length of the add. ball. range indicator lines
	public boolean 		move 			= true;							// move or not when correcting for range
	public boolean 		drawCorrLabel	= true;							// whether to draw the range correction label
	public Vector2d		posCorrLabel	= new Vector2d(-0.2, -0.05);	// the position of the range correction label
	public double		radialStretch	= 1.0;							// radial: (for values > 1) stretches the notches/circles over longer arc
	public double		radialAngle		= 0.0;							// radial: angular displacement of the scales (in deg)
	public double		radialRadius	= 20.0;							// radial: the scales radius in screenspace or mils (see radiusUseMils)
	public boolean		radiusUseMils	= true;							// whether to use mils or screenspace for radialRadius
	public List<BIndicator> indicators = new ArrayList<BIndicator>();
	
	
	public ElementBallRangeIndicator(String name) {
		super(name, ElementType.BALLISTIC_RANGE_INDICATORS);
		resetIndicators();
	}
	
	public ElementBallRangeIndicator() {
		super(ElementType.BALLISTIC_RANGE_INDICATORS.defaultName, ElementType.BALLISTIC_RANGE_INDICATORS);
		resetIndicators();
	}
	
	
	public void resetIndicators() {
		indicators.clear();
		for(int i=200, j=1; i<=2800; i+=200, j++) {
			if(i == 0) { continue; }
			indicators.add(new BIndicator(i, (j%2==0), 0f, 0f, 0f));
		}
	}
	
}
