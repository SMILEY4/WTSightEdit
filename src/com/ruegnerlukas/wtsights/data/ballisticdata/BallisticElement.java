package com.ruegnerlukas.wtsights.data.ballisticdata;

import java.util.ArrayList;
import java.util.List;

import com.ruegnerlukas.wtsights.data.ballisticdata.ballfunctions.IBallisticFunction;
import com.ruegnerlukas.wtsights.data.ballisticdata.ballfunctions.NullBallisticFunction;
import com.ruegnerlukas.wtsights.data.vehicle.Ammo;

public class BallisticElement {
	
	public List<Ammo> ammunition = new ArrayList<Ammo>();
	public boolean isRocketElement = false;
	public IBallisticFunction function = new NullBallisticFunction();
	public MarkerData markerData;
	
	
}
