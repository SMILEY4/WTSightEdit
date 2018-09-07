package com.ruegnerlukas.playground;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ruegnerlukas.simpleutils.JarLocation;
import com.ruegnerlukas.wtsights.WTSights;
import com.ruegnerlukas.wtsights.data.Database;
import com.ruegnerlukas.wtsights.data.vehicle.Ammo;
import com.ruegnerlukas.wtsights.data.vehicle.Vehicle;
import com.ruegnerlukas.wtsights.data.vehicle.Weapon;

public class Names {

	
	public static void main(String[] args) {
		
		Database.loadVehicles(new File(JarLocation.getJarLocation(WTSights.class) + "/data/vehicle_data.xml"));
		List<Vehicle> vehicles = Database.getVehicles();
		
		
		// AMMO
		List<Ammo> ammoList = new ArrayList<Ammo>();
		for(Vehicle vehicle : vehicles) {
			for(Weapon w : vehicle.weaponsList) {
				ammoList.addAll(w.ammo);
			}
		}
		
		for(int i=0; i<ammoList.size(); i+=20) {
			Ammo ammo = ammoList.get(i);
			System.out.printf("%-40.40s  %-40.40s%n", ammo.name, toPrettyAmmoName(ammo.name));
		}
		
		
		// VEHICLES
		for(int i=0; i<vehicles.size(); i+=10) {
			Vehicle vehicle = vehicles.get(i);
			System.out.printf("%-60.60s  %-60.60s%n", vehicle.name, toPrettyVehicleName(vehicle.name));
		}
		
		
	}
	
	
	
	public static String toPrettyAmmoName(String name) {
		name = name.replace("ammo_", "").replaceAll("_", " ");
		return name;
	}
	
	
	public static String toPrettyVehicleName(String name) {
		name = name.replace("vehicle_", "");
		String nation = name.split("_",2)[0];
		String vehicle = name.split("_",2)[1];
		vehicle = vehicle.replaceAll("_", " ");
		return "(" + nation.toUpperCase() + ") " + vehicle;
	}
	
	
	
	
	
	
}
