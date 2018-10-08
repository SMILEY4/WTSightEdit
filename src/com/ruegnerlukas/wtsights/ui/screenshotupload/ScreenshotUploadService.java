package com.ruegnerlukas.wtsights.ui.screenshotupload;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ruegnerlukas.simpleutils.collectionbuilders.MapBuilder;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticElement;
import com.ruegnerlukas.wtsights.data.vehicle.Ammo;
import com.ruegnerlukas.wtsights.data.vehicle.Vehicle;
import com.ruegnerlukas.wtsights.data.vehicle.Weapon;
import com.ruegnerlukas.wtsights.ui.view.IViewService;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.ParamKey;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;
import com.ruegnerlukas.wtutils.FXUtils;
import com.ruegnerlukas.wtutils.SightUtils.TriggerGroup;
import com.ruegnerlukas.wtutils.Workflow;
import com.ruegnerlukas.wtutils.Workflow.Step;

public class ScreenshotUploadService implements IViewService {

	
	
	
	public List<BallisticElement> getListOfElements(Vehicle vehicle) {
		
		List<BallisticElement> list = new ArrayList<BallisticElement>();
		
		for(int i=0; i<vehicle.weaponsList.size(); i++) {
			Weapon weapon = vehicle.weaponsList.get(i);
			
			// cannons
			if(weapon.triggerGroup == TriggerGroup.PRIMARY || weapon.triggerGroup == TriggerGroup.SECONDARY) {
				for(int j=0; j<weapon.ammo.size(); j++) {
					Ammo ammo = weapon.ammo.get(j);
					BallisticElement element = new BallisticElement();
					element.ammunition.add(ammo);
					if(element.ammunition.size() == 1) {
						if(element.ammunition.get(0).type.contains("rocket") || element.ammunition.get(0).type.contains("atgm")) {
							element.isRocketElement = true;
						}
					}
					list.add(element);
				}
			
			// machineguns
			} else if(weapon.triggerGroup == TriggerGroup.MACHINEGUN || weapon.triggerGroup == TriggerGroup.COAXIAL) {
				BallisticElement element = new BallisticElement();
				element.ammunition.addAll(weapon.ammo);
				element.isRocketElement = false;
				list.add(element);
				
			// other
			} else {
				continue;
			}
			
		}
		
		return list;
	}
	
	
	
	
	public void next(Map<ParamKey,Object> parameters, List<BallisticElement> elements, List<File> files) {
		
		Map<BallisticElement, File> imageMap = new HashMap<BallisticElement,File>();
		List<BallisticElement> dataList = new ArrayList<BallisticElement>();
		
		int nShells = 0;
		int nRockets = 0;
		
		for(int i=0; i<elements.size(); i++) {
			BallisticElement element = elements.get(i);
			File file = files.get(i);
			if(element.isRocketElement) {
				nRockets++;
				dataList.add(element);
			} else {
				if(file != null) {
					nShells++;
					dataList.add(element);
					imageMap.put(element, file);
				}
			}
		}
		
		boolean isValid = true;
		
		if(nShells+nRockets == 0) {
			isValid = false;
		}
		if(imageMap.size() != nShells) {
			isValid = false;
		}
		
		
		Logger.get().debug("onNext: " + "data="+dataList + "; imgs=" + imageMap + "; valid=" + isValid); 
		
		if(!isValid) {
			FXUtils.showAlert("No Images selected. Select at least one image to continue.", ViewManager.getStage(View.SCREENSHOT_UPLOAD));
			return;
		}
		
		Vehicle vehicle = (Vehicle) parameters.get(ParamKey.SELECTED_VEHICLE);
		
		if(Workflow.is(Step.CREATE_CALIBRATION, Step.SELECT_VEHICLE)) {
			FXUtils.closeFXScene(View.SCREENSHOT_UPLOAD);
			Workflow.steps.add(Step.UPLOAD_SCREENSHOTS);
			ViewManager.getLoader(View.CALIBRATION_EDITOR).openNew(
					null, new MapBuilder<ParamKey,Object>()
					.add(ParamKey.SELECTED_VEHICLE, vehicle)
					.add(ParamKey.LIST_BALLISTIC_ELEMENTS, dataList)
					.add(ParamKey.MAP_IMAGES, imageMap)
					.get());
		}
		
		if(Workflow.is(Step.CREATE_SIGHT, Step.SELECT_CALIBRATION, Step.SELECT_VEHICLE)) {
			FXUtils.closeFXScene(View.SCREENSHOT_UPLOAD);
			Workflow.steps.add(Step.UPLOAD_SCREENSHOTS);
			ViewManager.getLoader(View.CALIBRATION_EDITOR).openNew(
					null, new MapBuilder<ParamKey,Object>()
					.add(ParamKey.SELECTED_VEHICLE, vehicle)
					.add(ParamKey.LIST_BALLISTIC_ELEMENTS, dataList)
					.add(ParamKey.MAP_IMAGES, imageMap)
					.get());
		}
		
		if(Workflow.is(Step.LOAD_SIGHT, Step.SELECT_CALIBRATION, Step.SELECT_VEHICLE)) {
			FXUtils.closeFXScene(View.SCREENSHOT_UPLOAD);
			Workflow.steps.add(Step.UPLOAD_SCREENSHOTS);
			ViewManager.getLoader(View.CALIBRATION_EDITOR).openNew(
					null, new MapBuilder<ParamKey,Object>()
					.add(ParamKey.SELECTED_VEHICLE, vehicle)
					.add(ParamKey.LIST_BALLISTIC_ELEMENTS, dataList)
					.add(ParamKey.MAP_IMAGES, imageMap)
					.add(ParamKey.FILE_SIGHT, parameters.get(ParamKey.FILE_SIGHT))
					.get());
		}
		
		
	}
	
	
}
