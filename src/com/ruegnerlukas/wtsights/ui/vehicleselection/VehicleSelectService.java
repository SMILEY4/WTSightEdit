package com.ruegnerlukas.wtsights.ui.vehicleselection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ruegnerlukas.simpleutils.collectionbuilders.MapBuilder;
import com.ruegnerlukas.wtsights.data.vehicle.Vehicle;
import com.ruegnerlukas.wtsights.ui.view.IViewService;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.ParamKey;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;
import com.ruegnerlukas.wtutils.FXUtils;
import com.ruegnerlukas.wtutils.Workflow;
import com.ruegnerlukas.wtutils.Workflow.Step;

public class VehicleSelectService implements IViewService {

	
	
	
	public List<Vehicle> applyFilter(String filter, List<Vehicle> vehiclesIn) {
		
		if(filter.isEmpty()) {
			return vehiclesIn;
		}
		
		List<Vehicle> vehiclesFiltered = new ArrayList<Vehicle>();
		for(int i=0,n=vehiclesIn.size(); i<n; i++) {
			Vehicle vehicle = vehiclesIn.get(i);
			if(vehicle.namePretty.toLowerCase().contains(filter)) {
				vehiclesFiltered.add(vehicle);
			}
		}
		
		return vehiclesFiltered;
	}
	
	
	
	
	public void next(Map<ParamKey,Object> parameters, Vehicle vehicle) {

		if(vehicle == null) {
			FXUtils.showAlert("No vehicle selected. Select vehicle to continue.", ViewManager.getStage(View.VEHICLE_SELECT));
			return;
		}

		FXUtils.closeFXScene(View.VEHICLE_SELECT);
		Workflow.steps.add(Step.SELECT_VEHICLE);
		ViewManager.getLoader(View.SCREENSHOT_UPLOAD).openNew(
				null, new MapBuilder<ParamKey,Object>().add(parameters).add(ParamKey.SELECTED_VEHICLE, vehicle).get());
		
	}
	
}
