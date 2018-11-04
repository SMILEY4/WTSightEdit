package com.ruegnerlukas.wtsights.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.loading.DataLoader;
import com.ruegnerlukas.wtsights.data.vehicle.Ammo;
import com.ruegnerlukas.wtsights.data.vehicle.Vehicle;
import com.ruegnerlukas.wtsights.data.vehicle.Weapon;

public class Database {

	private static List<Vehicle> vehicles;
	private static Map<String, Vehicle> vehicleMap;

	
	
	public static List<Vehicle> getVehicles() {
		return vehicles;
	}
	
	
	public static void loadVehicles(File file) {
		try {
			vehicleMap = new HashMap<String,Vehicle>();
			vehicles = DataLoader.get(FileVersion.AUTO_DETECT).loadVehicleDataFile(file);
			for(int i=0, n=vehicles.size(); i<n; i++) {
				Vehicle vehicle = vehicles.get(i);
				vehicle.namePretty = toPrettyVehicleName(vehicle.name);
				
				for(Weapon weapon : vehicle.weaponsList) {
					for(Ammo ammo : weapon.ammo) {
						ammo.namePretty = toPrettyAmmoName(ammo.name);
					}
				}
				
				vehicleMap.put(vehicle.name, vehicle);
			}
			Logger.get().info("Vehicles loaded (" + vehicleMap.size() + ")");
		} catch (Exception e) {
			Logger.get().error(e);
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
	
	
	
	
	public static List<String> getVehicleNamesFiltered(String filter) {
		List<String> names = new ArrayList<String>();
		for(int i=0, n=vehicles.size(); i<n; i++) {
			Vehicle v = vehicles.get(i);
			if(filter.isEmpty()) {
				names.add(v.name);
			} else {
				if(v.name.toLowerCase().contains(filter.toLowerCase())) {
					names.add(v.name);
				}
			}
		}
		return names;
	}

	
	
	
	public static Vehicle getVehicleByName(String name) {
		for(Vehicle vehicle : vehicles) {
			if(vehicle.name.equals(name)) {
				return vehicle;
			}
		}
		return null;
	}
	
	
	
	
	public static List<String> getInternalCalibrationDataNames(String vehicleName) {
		return new ArrayList<String>();
	}
	
	
	
	
	public static List<String> getAllAmmoNames(String vehicleName) {
		return getAllAmmoNames(getVehicleByName(vehicleName));
	}
	
	
	
	public static List<String> getAllAmmoNames(Vehicle vehicle) {
		List<String> ammoNames = new ArrayList<String>();;
		for(int i=0; i<vehicle.weaponsList.size(); i++) {
			Weapon weapon = vehicle.weaponsList.get(i);
			for(int j=0; j<weapon.ammo.size(); j++) {
				Ammo ammo = weapon.ammo.get(j);
				ammoNames.add(ammo.name);
			}
		}
		return ammoNames;
	}
	
	
	
	public static List<Ammo> getAmmo(String vehicleName, String type, int speed) {
		
		List<Ammo> ammoList = new ArrayList<Ammo>();
		
		Vehicle vehicle = getVehicleByName(vehicleName);
		if(vehicle != null) {
			
			for(Weapon weapon : vehicle.weaponsList) {
				for(Ammo ammo : weapon.ammo) {
					if(ammo.type.equalsIgnoreCase(type) && ammo.speed == speed) {
						ammoList.add(ammo);
					}
				}
			}
			
		}
		
		return ammoList;
	}
	
	
}




