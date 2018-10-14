package com.ruegnerlukas.wtsights.data.vehicle;

import java.util.ArrayList;
import java.util.List;

import com.ruegnerlukas.wtutils.SightUtils.TriggerGroup;


public class Weapon {
	public String name;
	public TriggerGroup triggerGroup;
	public List<Ammo> ammo = new ArrayList<Ammo>();
}
