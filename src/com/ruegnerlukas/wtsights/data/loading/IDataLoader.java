package com.ruegnerlukas.wtsights.data.loading;

import java.io.File;
import java.util.List;

import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticData;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.data.vehicle.Vehicle;

public interface IDataLoader {
	
	public List<Vehicle> loadVehicleDataFile(File file) throws Exception;
	
	public BallisticData loadBallisticDataFile(File file) throws Exception;

	public SightData loadSightDataFile(File file, BallisticData dataBall) throws Exception;
	
}


