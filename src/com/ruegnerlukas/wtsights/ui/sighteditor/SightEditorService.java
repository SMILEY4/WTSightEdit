package com.ruegnerlukas.wtsights.ui.sighteditor;

import java.io.File;
import java.util.List;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticData;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.data.sight.sightElements.BaseElement;
import com.ruegnerlukas.wtsights.data.writing.DataWriter;
import com.ruegnerlukas.wtsights.ui.view.IViewService;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;
import com.ruegnerlukas.wtutils.Conversion;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.stage.FileChooser;


public class SightEditorService implements IViewService {

	
	private DataPackage data;
	
	
	
	
	@Override
	public void initService() {
		data = null;
	}
	
	
	
	
	public void initDataPackage(BallisticData dataBall, SightData dataSight) {
		data = new DataPackage();
		data.dataBallistic = dataBall;
		if(dataSight == null) {
			data.dataSight = new SightData(true);
		} else {
			data.dataSight = dataSight;
		}
	}
	
	
	
	
	public String getVehicleName() {
		return data.dataBallistic.vehicle.namePretty;
	}	
	
	
	
	
	public List<BaseElement> getElements() {
		return data.dataSight.collectElements();
	}
	
	
	
	
	public void selectElement(BaseElement element) {
		data.dataSight.selectedElement = element;
	}
	
	
	
	
	public void addElement(BaseElement element) {
		data.dataSight.addElement(element);
	}
	
	
	
	
	/**
	 * 0 = OK
	 * 1 = name is null
	 * 2 = name is empty
	 * 3 = name is not unique
	 * */
	public int validateElementName(BaseElement element, String name) {
		if(name == null) {
			return 1;
		}
		if(name.trim().length() == 0) {
			return 2;
		}
		for(BaseElement e : getElements()) {
			if(e != element) {
				if(e.name.trim().equalsIgnoreCase(name.trim())) {
					return 3;
				}
			}
		}
		return 0;
	}
	
	
	
	
	public void renameElement(BaseElement element, String name) {
		if(validateElementName(element, name) == 0) {
			element.name = name.trim();
		}
	}
	
	
	
	
	public void deleteElement(BaseElement element) {
		if(element != null) {
			data.dataSight.removeElement(element);
		}
	}
	
	
	
	
	public Vector2d getCursorPosMil(Vector2d cursorPosPX, double canvasWidth, double canvasHeight) {
		
		Conversion.get().initialize(
				canvasWidth,
				canvasHeight,
				data.dataBallistic.vehicle.fovOut*data.dataBallistic.zoomModOut,
				data.dataBallistic.vehicle.fovIn*data.dataBallistic.zoomModIn,
				data.dataSight.gnrThousandth);
		
		Vector2d posMil = new Vector2d(cursorPosPX);
		posMil.x -= canvasWidth/2;
		posMil.y -= canvasHeight/2;
		posMil.y *= -1;
		posMil.x = Conversion.get().pixel2mil(posMil.x, canvasHeight, isZoomedIn());
		posMil.y = Conversion.get().pixel2mil(posMil.y, canvasHeight, isZoomedIn());
		posMil.x = ((int)(posMil.x*100)) / 100.0;
		posMil.y = ((int)(posMil.y*100)) / 100.0;
		return posMil;
	}
	
	
	
	
	public Vector2d getCursorPosSS(Vector2d cursorPosPX, double canvasWidth, double canvasHeight) {
		
		Conversion.get().initialize(
				canvasWidth,
				canvasHeight,
				data.dataBallistic.vehicle.fovOut*data.dataBallistic.zoomModOut,
				data.dataBallistic.vehicle.fovIn*data.dataBallistic.zoomModIn,
				data.dataSight.gnrThousandth);
		
		Vector2d posSS = new Vector2d(cursorPosPX);
		posSS.x -= canvasWidth/2;
		posSS.y -= canvasHeight/2;
		posSS.y *= -1;
		posSS.x = Conversion.get().pixel2screenspace(posSS.x, canvasHeight, isZoomedIn());
		posSS.y = Conversion.get().pixel2screenspace(posSS.y, canvasHeight, isZoomedIn());
		posSS.x = ((int)(posSS.x*1000)) / 1000.0;
		posSS.y = ((int)(posSS.y*1000)) / 1000.0;
		return posSS;
	}
	
	
	
	
	public void export() {
		
		FileChooser fc = new FileChooser();
		fc.setTitle(ViewManager.getResources().getString("se_export_title"));
		
		File fileSelected = fc.showSaveDialog(ViewManager.getStage(View.SIGHT_EDITOR));
		if(fileSelected == null) {
			return;
		}
		
		File file = new File(fileSelected.getAbsolutePath() + (fileSelected.getAbsolutePath().endsWith(".blk") ? "" : ".blk") );
		if(file.getName().contains(" ")) {
			file = new File(file.getAbsolutePath().replace(file.getName(), file.getName().replaceAll(" ", "_")));
		}
		
		try {
			if(!DataWriter.get().saveSight(data.dataSight, data.dataBallistic, file)) {
				FXUtils.showAlert(ViewManager.getResources().getString("se_alert_export_failed"), ViewManager.getStage(View.SIGHT_EDITOR));
			} else {
				Logger.get().info("Saved sight to " + file);
			}
			
		} catch (Exception e) {
			Logger.get().error(e);;
		}
		
	}
	
	
	
	
	public DataPackage getDataPackage() {
		return this.data;
	}
	
	
	
	
	public boolean isZoomedIn() {
		return data.dataSight.envZoomedIn;
	}
	
	
}
