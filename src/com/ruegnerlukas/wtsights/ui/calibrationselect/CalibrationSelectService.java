package com.ruegnerlukas.wtsights.ui.calibrationselect;

import java.io.File;
import java.util.Map;

import com.ruegnerlukas.simpleutils.collectionbuilders.MapBuilder;
import com.ruegnerlukas.wtsights.data.DataLoader;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticData;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.ui.view.IViewService;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.ParamKey;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;
import com.ruegnerlukas.wtutils.FXUtils;
import com.ruegnerlukas.wtutils.Workflow;
import com.ruegnerlukas.wtutils.Workflow.Step;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class CalibrationSelectService implements IViewService {

	
	private boolean createNew = false;
	private File fileCalibration;
	
	
	
	
	public void selectCreateNew(boolean createNew) {
		this.createNew = createNew;
	}
	
	
	
	
	public void browse() {
		FileChooser fc = new FileChooser();
		fc.setTitle(ViewManager.getResources().getString("cs_browse_title"));
		fc.getExtensionFilters().add(new ExtensionFilter(ViewManager.getResources().getString("cs_browse_file_tye") + " (*.xml)", "*.xml"));
		
		File file = fc.showOpenDialog(ViewManager.getStage(View.CALIBRATION_SELECT));
		
		if (file != null) {
			((CalibrationSelectController)ViewManager.getController(View.CALIBRATION_SELECT)).setTextPath(file.getAbsolutePath());
			this.fileCalibration = file;
		}
	}

	
	
	
	public void next(Map<ParamKey,Object> parameters) {
		
		if(!createNew && this.fileCalibration == null) {
			FXUtils.showAlert(ViewManager.getResources().getString("cs_alert_missing"), ViewManager.getStage(View.CALIBRATION_SELECT));
			return;
		}
		
		
		if(Workflow.is(Step.CREATE_SIGHT)) {
			Workflow.steps.add(Step.SELECT_CALIBRATION);
			
			if(createNew) {
				FXUtils.closeFXScene(View.CALIBRATION_SELECT);
				ViewManager.getLoader(View.VEHICLE_SELECT).openNew(null, new MapBuilder<ParamKey,Object>().get());
		
			} else {
				BallisticData dataBall = DataLoader.loadBallisticDataFile(fileCalibration);
				FXUtils.closeFXScene(View.CALIBRATION_SELECT);
				ViewManager.getLoader(View.SIGHT_EDITOR).openNew(
						null, new MapBuilder<ParamKey,Object>().add(ParamKey.BALLISTIC_DATA,dataBall).add(ParamKey.SIGHT_DATA,null).get());

			}
			
		} else if(Workflow.is(Step.LOAD_SIGHT)) {
			Workflow.steps.add(Step.SELECT_CALIBRATION);
			
			File fileSight = (File) parameters.get(ParamKey.FILE_SIGHT);

			if(createNew) {
				FXUtils.closeFXScene(View.CALIBRATION_SELECT);
				ViewManager.getLoader(View.VEHICLE_SELECT).openNew(null, new MapBuilder<ParamKey,Object>().add(ParamKey.FILE_SIGHT, fileSight).get());
			
			} else {
				BallisticData dataBall = DataLoader.loadBallisticDataFile(fileCalibration);
				SightData dataSight = DataLoader.loadSight(fileSight, dataBall);
				FXUtils.closeFXScene(View.CALIBRATION_SELECT);
				ViewManager.getLoader(View.SIGHT_EDITOR).openNew(
						null, new MapBuilder<ParamKey,Object>().add(ParamKey.BALLISTIC_DATA,dataBall).add(ParamKey.SIGHT_DATA,dataSight).get());
			}
		}
		
	}
	
	
	
}
