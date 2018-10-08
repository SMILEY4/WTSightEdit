package com.ruegnerlukas.wtsights.ui.main;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import com.ruegnerlukas.wtsights.ui.view.IViewController;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.ParamKey;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;
import com.ruegnerlukas.wtutils.Config;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class MainMenuController implements IViewController {


	@FXML private ResourceBundle resources;
	@FXML private URL location;

	@FXML private AnchorPane contentPane;
	@FXML private Label labelVersion;

	@FXML private CheckBox cbSettingDark;

	private MainMenuService service;




	public void create(Map<ParamKey,Object> parameters) {
		this.service = (MainMenuService) ViewManager.getService(View.MAIN_MENU);
		labelVersion.setText("Version " + Config.build_version);
		cbSettingDark.setSelected("dark".equals(Config.app_style)); // themes: "default", "dark"
	}




	@FXML
	void onNewCalibration(ActionEvent event) {
		service.createBallisticData();
	}




	@FXML
	void onLoadCalibration(ActionEvent event) {
		service.loadBallisticData();
	}




	@FXML
	void onNewSight(ActionEvent event) {
		service.createSight();
	}




	@FXML
	void onLoadSight(ActionEvent event) {
		service.loadSight();
	}




	@FXML
	void onAbout(ActionEvent event) {
		service.openAbout();
	}




	@FXML
	void onHelp(ActionEvent event) {
		service.openHelp();
	}




	@FXML
	void onSettingDark(ActionEvent event) {
		boolean isDark = cbSettingDark.isSelected();
		service.setTheme(isDark);
	}

}