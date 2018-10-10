package com.ruegnerlukas.wtlauncher;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtlauncher.Updater.InstallStatus;
import com.ruegnerlukas.wtlauncher.Updater.SearchStatus;
import com.ruegnerlukas.wtutils.Config;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class LauncherController {


	LauncherController thisController;
	
	@FXML private ResourceBundle resources;
	@FXML private URL location;
	
	@FXML private AnchorPane paneSearch;
	@FXML private AnchorPane paneSelect;
	@FXML private AnchorPane paneUpdate;
	@FXML private Label labelUpdateInfo;
	@FXML private Label labelUpdateStatus;
	
	private String latestVersion = "-";
	
	
	
	
	public void start() {
		thisController = this;
		paneSearch.setDisable(false);
		paneSelect.setDisable(true);
		paneUpdate.setDisable(true);

		paneSearch.setOpacity(1.0);
		paneSelect.setOpacity(0.0);
		paneUpdate.setOpacity(0.0);

		if(Config.update_auto) {
			Updater.searchUpdate(this);
		} else {
			Logger.get().info("Auto-update disabled.");;
			startApplication();
		}
		
	}
	
	
	
	
	public void onUpdateSearchDone(SearchStatus status, String localVersion, String latestVersion) {
		Platform.runLater( () -> {
				if(status == SearchStatus.FOUND_UPDATE) {
					paneSearch.setDisable(true);
					paneSelect.setDisable(false);
					paneUpdate.setDisable(true);
					
					paneSearch.setOpacity(0.0);
					paneSelect.setOpacity(1.0);
					paneSearch.setOpacity(0.0);
					
					this.latestVersion = latestVersion;
					labelUpdateInfo.setText( resources.getString("lc_update_found_msg".replaceAll("{current}", localVersion).replaceAll("{new}", latestVersion)) );
				} else {
					labelUpdateInfo.setText( resources.getString("lc_update_not_found_msg".replaceAll("{current}", localVersion).replaceAll("{new}", latestVersion)) );
					startApplication();
					return;
				}
			});
	}
	
	
	
	
	@FXML
	void onSkipSearch(ActionEvent event) {
		startApplication();
	}
	
	
	
	
	@FXML
	void onSkipUpdate(ActionEvent event) {
		startApplication();
	}




	@FXML
	void onUpdate(ActionEvent event) {
			
			paneSearch.setDisable(true);
			paneSelect.setDisable(true);
			paneUpdate.setDisable(false);
		
			paneSearch.setOpacity(0.0);
			paneSelect.setOpacity(0.0);
			paneUpdate.setOpacity(1.0);
			
			labelUpdateStatus.setText( resources.getString("lc_updating_msg".replaceAll("{current}", Config.build_version).replaceAll("{new}", latestVersion)) );
			Updater.installUpdate(thisController);
	}	


	
	public void onInstallUpdateDone(InstallStatus status) {
		Platform.runLater( () -> {
			if(status == InstallStatus.FAILED) {
				Logger.get().warn("(Alert) Installation failed.");
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText(null);
				alert.setContentText("Installation failed.");
				alert.showAndWait();
				startApplication();
				
			} else if(status == InstallStatus.FATAL) {
				Logger.get().warn("(Alert) Installation failed (fatal).");
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText(null);
				alert.setContentText("Installation failed (fatal).");
				alert.showAndWait();
				Platform.exit();
				System.exit(0);
				
			} else {
				Platform.exit();
				System.exit(0);
			}
		});
	}
	
	
	
	
	private void startApplication() {
		Logger.get().info("Starting WTSE-application.");
		
		File fileApp = new File(WTSELauncher.BASE_DIR+"\\data\\wtedit_application.jar");
		ProcessBuilder builder = new ProcessBuilder("java", "-jar", fileApp.getAbsolutePath());
		
		try {
			Process proc = builder.start();
			Platform.exit();
			System.exit(0);
			
		} catch (IOException e) {
			Logger.get().error(e);
			Logger.get().warn("(Alert) Could not start Application.");
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText("Could not start Application.");
			alert.showAndWait();
			return;
		}
		
	}
	

}
