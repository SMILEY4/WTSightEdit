package com.ruegnerlukas.wtsights.data.sight.elements;

import com.ruegnerlukas.wtsights.data.calibration.CalibrationAmmoData;
import com.ruegnerlukas.wtsights.data.sight.BIndicator;
import com.ruegnerlukas.wtutils.SightUtils.TriggerGroup;

public class ElementShellBlock extends ElementBallRangeIndicator {
	
	public TriggerGroup	triggerGroup 	= TriggerGroup.PRIMARY;	// the armament you want calculate ballistics for
	public CalibrationAmmoData dataAmmo		= null;				// the ammunition data
	
	
	
	public ElementShellBlock(String name) {
		this.name = name;
		this.type = ElementType.SHELL_BALLISTICS_BLOCK;
		resetIndicators();
	}
	
	public ElementShellBlock() {
		this.name = ElementType.SHELL_BALLISTICS_BLOCK.defaultName;
		this.type = ElementType.SHELL_BALLISTICS_BLOCK;
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
