package com.ruegnerlukas.wtsights.ui.screenshotupload;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ruegnerlukas.simpleutils.collectionbuilders.MapBuilder;
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

	
	private File fileZoomedOut = null;
	private File fileZoomedIn = null;
	private Map<BallisticElement,File> images = new HashMap<BallisticElement,File>();
	
	
	
	
	@Override
	public void initService() {
		fileZoomedIn = null;
		fileZoomedOut = null;
		images.clear();
	}
	
	
	
	
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
	
	
	
	
	public void selectImage(File file, boolean zoomedIn) {
		if(zoomedIn) {
			this.fileZoomedIn = file;
		} else {
			this.fileZoomedOut = file;
		}
	}
	
	
	
	
	public void selectImage(File file, BallisticElement element) {
		if(element != null) {
			if(file == null) {
				this.images.remove(element);
			} else {
				this.images.put(element, file);
			}
		}
	}
	
	
	
	
	public void next(Map<ParamKey,Object> parameters) {
		
		Vehicle vehicle = (Vehicle) parameters.get(ParamKey.SELECTED_VEHICLE);
		List<BallisticElement> elements = getListOfElements(vehicle);
		
		// validate
		boolean hasRockets = false;
		for(BallisticElement element : elements) {
			if(element.isRocketElement) {
				hasRockets = true;
				break;
			}
		}
		if(!hasRockets && images.isEmpty()) {
			FXUtils.showAlert(ViewManager.getResources().getString("ssu_alert_missing"), ViewManager.getStage(View.SCREENSHOT_UPLOAD));
			return;
		}
		
		// add rockets
		for(BallisticElement element : elements) {
			if(element.isRocketElement) {
				images.put(element, null);
			}
		}
		
		// open next
		if(Workflow.is(Step.CREATE_CALIBRATION, Step.SELECT_VEHICLE)) {
			FXUtils.closeFXScene(View.SCREENSHOT_UPLOAD);
			Workflow.steps.add(Step.UPLOAD_SCREENSHOTS);
			ViewManager.getLoader(View.CALIBRATION_EDITOR).openNew(
					null, new MapBuilder<ParamKey,Object>()
					.add(ParamKey.SELECTED_VEHICLE, vehicle)
					.add(ParamKey.BALLISTIC_IMAGES_MAP, images)
					.add(ParamKey.BALLISTIC_IMAGES_ZOOM, new File[]{fileZoomedIn, fileZoomedOut})
					.get());
		}
		
		if(Workflow.is(Step.CREATE_SIGHT, Step.SELECT_CALIBRATION, Step.SELECT_VEHICLE)) {
			FXUtils.closeFXScene(View.SCREENSHOT_UPLOAD);
			Workflow.steps.add(Step.UPLOAD_SCREENSHOTS);
			ViewManager.getLoader(View.CALIBRATION_EDITOR).openNew(
					null, new MapBuilder<ParamKey,Object>()
					.add(ParamKey.SELECTED_VEHICLE, vehicle)
					.add(ParamKey.BALLISTIC_IMAGES_MAP, images)
					.add(ParamKey.BALLISTIC_IMAGES_ZOOM, new File[]{fileZoomedIn, fileZoomedOut})
					.get());
		}
		
		if(Workflow.is(Step.LOAD_SIGHT, Step.SELECT_CALIBRATION, Step.SELECT_VEHICLE)) {
			FXUtils.closeFXScene(View.SCREENSHOT_UPLOAD);
			Workflow.steps.add(Step.UPLOAD_SCREENSHOTS);
			ViewManager.getLoader(View.CALIBRATION_EDITOR).openNew(
					null, new MapBuilder<ParamKey,Object>()
					.add(ParamKey.SELECTED_VEHICLE, vehicle)
					.add(ParamKey.BALLISTIC_IMAGES_MAP, images)
					.add(ParamKey.BALLISTIC_IMAGES_ZOOM, new File[]{fileZoomedIn, fileZoomedOut})
					.add(ParamKey.FILE_SIGHT, parameters.get(ParamKey.FILE_SIGHT))
					.get());
		}
		
		
	}
	
	
}
