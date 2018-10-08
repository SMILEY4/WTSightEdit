package com.ruegnerlukas.wtsights.ui.main;

import java.io.File;

import com.ruegnerlukas.simpleutils.collectionbuilders.MapBuilder;
import com.ruegnerlukas.wtsights.WTSights;
import com.ruegnerlukas.wtsights.data.DataLoader;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticData;
import com.ruegnerlukas.wtsights.ui.view.IViewService;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.ParamKey;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;
import com.ruegnerlukas.wtutils.Config;
import com.ruegnerlukas.wtutils.FXUtils;
import com.ruegnerlukas.wtutils.Workflow;
import com.ruegnerlukas.wtutils.Workflow.Step;

import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MainMenuService implements IViewService {


	public void createBallisticData() {
		Workflow.steps.clear();
		Workflow.steps.add(Step.CREATE_CALIBRATION);
		ViewManager.getLoader(View.VEHICLE_SELECT).openNew(null, new MapBuilder<ParamKey,Object>().get());
	}




	public void loadBallisticData() {
		Workflow.steps.clear();
		Workflow.steps.add(Step.LOAD_CALIBRATION);

		FileChooser fc = new FileChooser();
		fc.setTitle("Open Ballistic Data");
		fc.getExtensionFilters().add(new ExtensionFilter("Ballistic Data (*.xml)", "*.xml"));

		File fileCalib = fc.showOpenDialog(WTSights.getPrimaryStage());
		if (fileCalib != null) {
			BallisticData data = DataLoader.loadBallisticDataFile(fileCalib);
			ViewManager.getLoader(View.CALIBRATION_EDITOR).openNew(null, new MapBuilder<ParamKey,Object>().add(ParamKey.BALLISTIC_DATA, data).get());
		}
	}




	public void createSight() {
		Workflow.steps.clear();
		Workflow.steps.add(Step.CREATE_SIGHT);
		ViewManager.getLoader(View.CALIBRATION_SELECT).openNew(null, new MapBuilder<ParamKey,Object>().get());
	}




	public void loadSight() {
		Workflow.steps.clear();
		Workflow.steps.add(Step.LOAD_SIGHT);
		
		FileChooser fc = new FileChooser();
		fc.setTitle("Open Sight");
		fc.getExtensionFilters().add(new ExtensionFilter("Sight (*.blk)", "*.blk"));

		File fileSight = fc.showOpenDialog(WTSights.getPrimaryStage());
		if (fileSight != null) {
			ViewManager.getLoader(View.CALIBRATION_SELECT).openNew(null, new MapBuilder<ParamKey,Object>().add(ParamKey.FILE_SIGHT, fileSight).get());
		}
	}




	public void openAbout() {
		Workflow.steps.clear();
		Workflow.steps.add(Step.ABOUT);
		ViewManager.getLoader(View.ABOUT).openNew(null, new MapBuilder<ParamKey,Object>().get());
	}




	public void openHelp() {
		Workflow.steps.clear();
		Workflow.steps.add(Step.HELP);
		ViewManager.getLoader(View.HELP).openNew(null, new MapBuilder<ParamKey,Object>().get());
	}




	public void setTheme(boolean isDark) {
		if(isDark) {
			Config.app_style = "dark";
		} else {
			Config.app_style = "default";
		}
		
		Scene scene = ViewManager.getScene(View.MAIN_MENU);
		scene.getStylesheets().clear();
		if(isDark) {
			if(WTSights.DEV_MODE) {
				String css = FXUtils.class.getResource("/ui/modena_dark.css").toExternalForm();
				scene.getStylesheets().add(css);
			} else {
				String css = FXUtils.class.getResource("/ui/modena_dark.css").toExternalForm();
				scene.getStylesheets().add(css);
			}
		}
		Config.write();
	}

}
