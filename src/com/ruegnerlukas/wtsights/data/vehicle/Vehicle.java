package com.ruegnerlukas.wtsights.data.vehicle;

import java.util.ArrayList;
import java.util.List;


public class Vehicle {
	
	public String name;
	public String namePretty;
	
	public List<Weapon> weaponsList = new ArrayList<Weapon>();

	public List<String> weapons = new ArrayList<String>();
	public List<String> triggerGroups = new ArrayList<String>();
	
	public float fovOut = -1;
	public float fovIn = -1;
	public float fovSight = -1;
	
}
