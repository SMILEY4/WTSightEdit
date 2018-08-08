package com.ruegnerlukas.wtlauncher;

import java.io.File;
import java.io.IOException;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtlauncher.Updater.InstallStatus;
import com.ruegnerlukas.wtlauncher.Updater.SearchStatus;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;

public class LauncherController {


	@FXML private AnchorPane paneSearch;
	@FXML private AnchorPane paneSelect;
	@FXML private AnchorPane paneUpdate;
	@FXML private Label labelUpdateInfo;
	@FXML private Label labelUpdateStatus;
	
	private String latestVersion = "-";
	
	
	
	
	public void start() {
		paneSearch.setVisible(true);
		paneSelect.setVisible(false);
		paneUpdate.setVisible(false);
		paneSearch.setDisable(false);
		paneSelect.setDisable(true);
		paneUpdate.setDisable(true);
		Updater.searchUpdate(this);
	}
	
	
	
	
	public void onUpdateSearchDone(SearchStatus status, String localVersion, String latestVersion) {
		Platform.runLater( () -> {
				if(status == SearchStatus.FOUND_UPDATE) {
					paneSearch.setVisible(false);
					paneSelect.setVisible(true);
					paneUpdate.setVisible(false);
					paneSearch.setDisable(true);
					paneSelect.setDisable(false);
					paneUpdate.setDisable(true);
					this.latestVersion = latestVersion;
					labelUpdateInfo.setText("New version found: " + latestVersion + ". Current version: " + localVersion + ".");
				} else {
					labelUpdateInfo.setText("Already up-to-date.");
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
		paneSearch.setVisible(false);
		paneSelect.setVisible(false);
		paneUpdate.setVisible(true);
		paneSearch.setDisable(true);
		paneSelect.setDisable(true);
		paneUpdate.setDisable(false);
		labelUpdateStatus.setText("Updating to version " + latestVersion + ".");
		Updater.installUpdate(this);
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
