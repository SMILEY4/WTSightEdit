//package com.ruegnerlukas.wtupdater;
//
//import java.awt.Desktop;
//import java.io.File;
//import java.io.IOException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.ResourceBundle;
//
//import com.ruegnerlukas.simpleutils.JarLocation;
//import com.ruegnerlukas.simpleutils.logging.logger.Logger;
//import com.ruegnerlukas.wtupdater.UpdateChecker.UpdateState;
//import com.ruegnerlukas.wtutils.Config;
//
//import javafx.application.Platform;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.ProgressIndicator;
//import javafx.scene.layout.VBox;
//
//public class UpdateController {
//
//
//	@FXML private ResourceBundle resources;
//	@FXML private URL location;
//
//	@FXML private VBox bockCheck;
//	@FXML private ProgressIndicator checkProgress;
//
//	@FXML private VBox boxUpdate;
//	@FXML private Label updateMessage;
//	@FXML private Button btnUpdate;
//	@FXML private ProgressIndicator updateProgress;
//	
//	
//	private final int STATE_NOTHING = 0;
//	private final int STATE_CHECKING = 1;
//	private final int STATE_INSTALLING = 2;
//	private volatile int state = STATE_NOTHING;
//	
//	private Map<String,ArrayList<String>> updateData;
//	private List<String[]> changedFiles;
//	
//	
//	
//	
//	@FXML
//	void initialize() {
//		assert bockCheck != null : "fx:id=\"bockCheck\" was not injected: check your FXML file 'layout_updater.fxml'.";
//		assert checkProgress != null : "fx:id=\"checkProgress\" was not injected: check your FXML file 'layout_updater.fxml'.";
//		assert boxUpdate != null : "fx:id=\"boxUpdate\" was not injected: check your FXML file 'layout_updater.fxml'.";
//		assert updateMessage != null : "fx:id=\"updateMessage\" was not injected: check your FXML file 'layout_updater.fxml'.";
//		assert btnUpdate != null : "fx:id=\"btnUpdate\" was not injected: check your FXML file 'layout_updater.fxml'.";
//		assert updateProgress != null : "fx:id=\"updateProgress\" was not injected: check your FXML file 'layout_updater.fxml'.";
//		
//		bockCheck.setVisible(true);
//		bockCheck.setDisable(false);
//		
//		boxUpdate.setVisible(false);
//		boxUpdate.setDisable(true);
//		
//		updateProgress.setVisible(false);
//		
//		state = STATE_CHECKING;
//		new UpdateChecker().checkForUpdates(this);
//	}
//	
//	
//
//
//	
//	
//	
//	void onUpdateCheckDone(Map<String,ArrayList<String>> updateData, List<String[]> changedFiles, UpdateState updateState) {
//		state = STATE_NOTHING;
//		this.updateData = updateData;
//		this.changedFiles = changedFiles;
//		Platform.runLater(
//			() -> {
//				Logger.get().info("UpdateState = " + updateState);
//				if(updateState == UpdateState.NO_UPDATE) {
//					startApplication();
//				} else {
//					bockCheck.setVisible(false);
//					bockCheck.setDisable(true);
//					boxUpdate.setVisible(true);
//					boxUpdate.setDisable(false);
//					if(updateState == UpdateState.REPAIR) {
//						updateMessage.setText("Some local files seem to be missing or corrupted.");
//						btnUpdate.setText("Repair");
//					} else if(updateState == UpdateState.AUTO_UPDATE) {
//						if(updateData.containsKey("update_message") && updateData.get("update_message").size() > 0) {
//							String currentVersion = Config.getValue("build_version") == null ? "unknown" : Config.getValue("build_version");
//							updateMessage.setText(updateData.get("update_message").get(0).replaceAll("\\{version_current\\}", currentVersion));
//							btnUpdate.setText("Update");
//						}
//					}
//					btnUpdate.setDisable(false);
//				}
//			}	
//		);
//	}
//	
//	
//	
//	
//	@FXML
//	void onCheckSkip(ActionEvent event) {
//		startApplication();
//	}
//
//
//
//	
//	@FXML
//	void onUpdate(ActionEvent event) {
//		state = STATE_INSTALLING;
//		new UpdateInstaller().install(this, updateData, changedFiles);
//		updateProgress.setVisible(true);
//		btnUpdate.setDisable(true);
//	}
//
//
//	
//	
//	void onUpdateInstallDone(int status) { // status: 0 = failed, 1 = successful, 2 = skipped
//		startApplication();
//	}
//	
//
//
//	
//	@FXML
//	void onUpdateSkip(ActionEvent event) {
//		if(state != STATE_INSTALLING) {
//			startApplication();
//		}
//	}
//
//	
//	
//	
//
//	
//	public void startApplication() {
//		
//		String jarLoc = JarLocation.getJarLocation(WTSightsStart.class) + "/data/wtedit_application.jar";
//		
//		Logger.get().info("Starting Application " + jarLoc + " (" + Config.getValue("build_version") + ") ... " );
//		Logger.get().info("=========================================");
//		Logger.get().close();
//		
//		try {
//			Desktop.getDesktop().open(new File(jarLoc));
//		} catch (IOException e) {
//			Logger.get().error(e);
//		}
//
//		Platform.exit();
//		System.exit(0);
//	}
//	
//
//
//}
