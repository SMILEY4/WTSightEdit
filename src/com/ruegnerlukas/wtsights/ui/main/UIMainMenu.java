package com.ruegnerlukas.wtsights.ui.main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.WTSights;
import com.ruegnerlukas.wtsights.ui.Workflow;
import com.ruegnerlukas.wtsights.ui.Workflow.Step;
import com.ruegnerlukas.wtsights.ui.about.UIAbout;
import com.ruegnerlukas.wtsights.ui.calibrationeditor.UICalibrationEditor;
import com.ruegnerlukas.wtsights.ui.calibrationselect.UICalibrationSelect;
import com.ruegnerlukas.wtsights.ui.vehicleselection.UIVehicleSelect;
import com.ruegnerlukas.wtutils.Config2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class UIMainMenu {


	private Stage stage;

	@FXML private ResourceBundle resources;
	@FXML private URL location;
	
	@FXML private AnchorPane contentPane;
	@FXML private Label labelVersion;
	
	
	
	public static void openNew(Stage stage) {
		
		Logger.get().info("Opening MainMenu");
		
		try {

			FXMLLoader loader = new FXMLLoader(UIMainMenu.class.getResource("/ui/layout_main.fxml"));
			Parent root = (Parent) loader.load();
			UIMainMenu controller = (UIMainMenu) loader.getController();
			
			Scene scene = null;
			if(WTSights.DARK_MODE) {
//				UndecoratorScene.setClassicDecoration();
//				scene = new UndecoratorScene(stage, (Region)root);
//				JFXDecorator decorator = new JFXDecorator(stage, root, false, true, true);
//				decorator.setCustomMaximize(true);
//				scene = new Scene(decorator, 500, 510+30);
//				scene.getStylesheets().add("ui/decoration.css");
			} else {
				scene = new Scene(root, 500, 550, true, SceneAntialiasing.DISABLED);
			}
			
			
			stage.setTitle("WT Sight Editor");
			if(WTSights.DARK_MODE) {
				scene.getStylesheets().add("/ui/modena_dark.css");
			}
			stage.setScene(scene);
			
			controller.create(stage);

		} catch (IOException e) {
			Logger.get().error(e);
		}
		

	}

	
	
	
	private void create(Stage stage) {
		labelVersion.setText("Version " + Config2.build_version);
	}
	
	


	@FXML
	void onNewCalibration(ActionEvent event) {
		Workflow.steps.clear();
		Workflow.steps.add(Step.CREATE_CALIBRATION);
		UIVehicleSelect.openNew(stage);
	}




	@FXML
	void onLoadCalibration(ActionEvent event) {
		Workflow.steps.clear();
		Workflow.steps.add(Step.LOAD_CALIBRATION);
		UICalibrationSelect.openNew(stage);
	}




	@FXML
	void onNewSight(ActionEvent event) {
		Workflow.steps.clear();
		Workflow.steps.add(Step.CREATE_SIGHT);
		UIVehicleSelect.openNew(stage);
	}




	@FXML
	void onLoadSight(ActionEvent event) {
		Workflow.steps.clear();
		Workflow.steps.add(Step.LOAD_SIGHT);
		
		FileChooser fc = new FileChooser();
		fc.setTitle("Open Sight");
		fc.getExtensionFilters().add(new ExtensionFilter("Sight (*.blk)", "*.blk"));

		File fileSight = fc.showOpenDialog(stage);
		if (fileSight != null) {
			UICalibrationSelect.openNew(stage, fileSight);
		}
	}


	
	
	
	@FXML
	void onSettings(ActionEvent event) {
		
		try {
			final Stage window = new Stage();
			window.initModality(Modality.NONE);
			window.initOwner(WTSights.getPrimaryStage());

			FXMLLoader loader = new FXMLLoader(UICalibrationEditor.class.getResource("/ui/layout_settings.fxml"));

			Parent root = (Parent) loader.load();

			Scene scene = new Scene(root, 500, 400, true, SceneAntialiasing.DISABLED);
			if(WTSights.DARK_MODE) {
				scene.getStylesheets().add("/ui/modena_dark.css");
			}
			window.setTitle("Settings");
			window.setScene(scene);

			window.show();

		} catch (IOException e) {
			Logger.get().error(e);
		}
		
	}

	
	

	@FXML
	void onAbout(ActionEvent event) {
		Workflow.steps.clear();
		Workflow.steps.add(Step.ABOUT);
		UIAbout.openNew(stage);
	}




	@FXML
	void onHelp(ActionEvent event) {
	}


}