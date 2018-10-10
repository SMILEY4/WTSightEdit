package com.ruegnerlukas.wtsights.ui.view;

import com.ruegnerlukas.wtsights.ui.about.AboutLoader;
import com.ruegnerlukas.wtsights.ui.about.AboutService;
import com.ruegnerlukas.wtsights.ui.calibrationeditor.CalibrationEditorLoader;
import com.ruegnerlukas.wtsights.ui.calibrationeditor.CalibrationEditorService;
import com.ruegnerlukas.wtsights.ui.calibrationselect.CalibrationSelectLoader;
import com.ruegnerlukas.wtsights.ui.calibrationselect.CalibrationSelectService;
import com.ruegnerlukas.wtsights.ui.help.HelpLoader;
import com.ruegnerlukas.wtsights.ui.help.HelpService;
import com.ruegnerlukas.wtsights.ui.language.DefaultResourceBundle;
import com.ruegnerlukas.wtsights.ui.main.MainMenuLoader;
import com.ruegnerlukas.wtsights.ui.main.MainMenuService;
import com.ruegnerlukas.wtsights.ui.screenshotupload.ScreenshotUploadLoader;
import com.ruegnerlukas.wtsights.ui.screenshotupload.ScreenshotUploadService;
import com.ruegnerlukas.wtsights.ui.sighteditor.SightEditorLoader;
import com.ruegnerlukas.wtsights.ui.sighteditor.SightEditorService;
import com.ruegnerlukas.wtsights.ui.sighteditor.createelement.ElementCreateLoader;
import com.ruegnerlukas.wtsights.ui.sighteditor.createelement.ElementCreateService;
import com.ruegnerlukas.wtsights.ui.sighteditor.environment.EnvironmentLoader;
import com.ruegnerlukas.wtsights.ui.sighteditor.environment.EnvironmentService;
import com.ruegnerlukas.wtsights.ui.sighteditor.general.GeneralLoader;
import com.ruegnerlukas.wtsights.ui.sighteditor.general.GeneralService;
import com.ruegnerlukas.wtsights.ui.vehicleselection.VehicleSelectLoader;
import com.ruegnerlukas.wtsights.ui.vehicleselection.VehicleSelectService;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class ViewManager {

	
	public static enum View {
		MAIN_MENU(new MainMenuLoader(), new MainMenuService()),
		HELP(new HelpLoader(), new HelpService()),
		ABOUT(new AboutLoader(), new AboutService()),
		CALIBRATION_SELECT(new CalibrationSelectLoader(), new CalibrationSelectService()),
		SCREENSHOT_UPLOAD(new ScreenshotUploadLoader(), new ScreenshotUploadService()),
		VEHICLE_SELECT(new VehicleSelectLoader(), new VehicleSelectService()),
		CALIBRATION_EDITOR(new CalibrationEditorLoader(), new CalibrationEditorService()),
		SIGHT_EDITOR(new SightEditorLoader(), new SightEditorService()),
		ELEMENT_CREATE(new ElementCreateLoader(), new ElementCreateService()),
		SEM_ENVIRONMENT(new EnvironmentLoader(), new EnvironmentService()),
		SEM_GENERAL(new GeneralLoader(), new GeneralService())
		;

		private final IViewLoader loader;
		private final IViewService service;
		private IViewController controller;
		private Scene scene;
		private Stage stage;

		private View(IViewLoader loader, IViewService service) {
			this.loader = loader;
			this.service = service;
		}
		
	}
	
	
	private static DefaultResourceBundle resources = new DefaultResourceBundle();
	
	
	public static enum ParamKey {
		FILE_SIGHT,
		SIGHT_DATA,
		SELECTED_VEHICLE,
		BALLISTIC_DATA,
		LIST_BALLISTIC_ELEMENTS,
		MAP_IMAGES,
		LIST_SIGHT_ELEMENTS,
		DATA_PACKAGE
		;
	}
	
	
	
	
	public static DefaultResourceBundle getResources() {
		return resources;
	}
	
	
	
	
	public static IViewLoader getLoader(View view) {
		if(view != null) {
			return view.loader;
		} else {
			return null;
		}
	}
	
	
	
	
	public static IViewController getController(View view) {
		if(view != null) {
			return view.controller;
		} else {
			return null;
		}
	}
	
	
	
	
	public static IViewService getService(View view) {
		if(view != null) {
			return view.service;
		} else {
			return null;
		}
	}
	
	
	public static Scene getScene(View view) {
		if(view != null) {
			return view.scene;
		} else {
			return null;
		}
	}
	
	
	public static Stage getStage(View view) {
		if(view != null) {
			return view.stage;
		} else {
			return null;
		}
	}
	
	
	
	public static void setController(View view, IViewController controller) {
		if(view != null) {
			view.controller = controller;
		}
	}
	
	
	
	
	public static void setScene(View view, Scene scene) {
		if(view != null) {
			view.scene = scene;
		}
	}
	
	
	
	public static void setStage(View view, Stage stage) {
		if(view != null) {
			view.stage = stage;
		}
	}
	
	
	
}
