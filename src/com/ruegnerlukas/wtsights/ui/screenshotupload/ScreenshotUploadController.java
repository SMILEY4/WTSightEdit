package com.ruegnerlukas.wtsights.ui.screenshotupload;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticElement;
import com.ruegnerlukas.wtsights.data.vehicle.Vehicle;
import com.ruegnerlukas.wtsights.ui.AmmoIcons;
import com.ruegnerlukas.wtsights.ui.view.IViewController;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.ParamKey;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class ScreenshotUploadController implements IViewController {


	
	@FXML private ResourceBundle resources;
	@FXML private URL location;

	@FXML private Label labelTankName;
	@FXML private VBox boxList;
	
	private File lastDirectory = null;
	
	private ScreenshotUploadService service;
	private Map<ParamKey,Object> parameters;
	
	
	
	
	
	@Override
	public void create(Map<ParamKey,Object> parameters) {
		
		this.parameters = parameters;
		this.service = (ScreenshotUploadService) ViewManager.getService(View.SCREENSHOT_UPLOAD, true);
		Vehicle vehicle = (Vehicle) parameters.get(ParamKey.SELECTED_VEHICLE);
		
		labelTankName.setText(vehicle.namePretty);
		
		List<BallisticElement> elements = service.getListOfElements(vehicle);
		Logger.get().info("Loaded ammunition for " + vehicle.name + ": " + elements.size());

		boxList.getChildren().clear();
		
		HBox boxZI = buildBox(true);
		boxList.getChildren().add(boxZI);
		
		HBox boxZO = buildBox(false);
		boxList.getChildren().add(boxZO);
		
		HBox boxSeperator = new HBox();
		boxSeperator.setMinSize(0, 10);
		boxSeperator.setPrefSize(100000, 10);
		boxSeperator.setMaxSize(100000, 10);
		boxList.getChildren().add(boxSeperator);
		
		for(BallisticElement element : elements) {
			HBox box = buildBox(element);
			boxList.getChildren().add(box);
		}

	}

	
	
	
	private HBox buildBox(BallisticElement element) {
		
		HBox box = buildBaseBox();
		
		Label labelName = (Label) box.getChildren().get(0);
		TextField fieldPath = (TextField) box.getChildren().get(1);
		Button btnBrowse = (Button) box.getChildren().get(2);
		Button btnReset = (Button) box.getChildren().get(3);
		
		if(element.ammunition.size() > 1) {
			labelName.setText(element.ammunition.get(0).parentWeapon.name);
			labelName.setTooltip(new Tooltip(ViewManager.getResources().getString("ssu_triggergroup_tt") + element.ammunition.get(0).parentWeapon.triggerGroup.id));
			ImageView imgView = new ImageView(SwingFXUtils.toFXImage(AmmoIcons.getIcon("machinegun"), null));
			imgView.setSmooth(true);
			imgView.setPreserveRatio(true);
			imgView.setFitHeight(40);
			labelName.setGraphic(imgView);
			if(element.isRocketElement) {
				box.setDisable(true);
			} else {
				box.setDisable(false);
			}
		} else if(element.ammunition.size() == 1) {
			labelName.setText(element.ammunition.get(0).namePretty);
			labelName.setTooltip(new Tooltip(ViewManager.getResources().getString("ssu_shelltype_tt") + element.ammunition.get(0).type));
			ImageView imgView = new ImageView(SwingFXUtils.toFXImage(AmmoIcons.getIcon(element.ammunition.get(0).type), null));
			imgView.setSmooth(true);
			imgView.setPreserveRatio(true);
			imgView.setFitHeight(40);
			labelName.setGraphic(imgView);
			if(element.isRocketElement) {
				box.setDisable(true);
			} else {
				box.setDisable(false);
			}
		}
		
		btnBrowse.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				FileChooser fc = new FileChooser();
				if(lastDirectory != null) {
					fc.setInitialDirectory(lastDirectory);
				}
				fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(ViewManager.getResources().getString("ssu_file_type_img") + " (*.jpg, *.png)", "*.jpg", "*.png"));
				fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(ViewManager.getResources().getString("ssu_file_type_img") + " (*.png)", "*.png"));
				fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(ViewManager.getResources().getString("ssu_file_type_img") + " (*.jpg)", "*.jpg"));
				
				File file = fc.showOpenDialog(ViewManager.getScene(View.SCREENSHOT_UPLOAD).getWindow());
				if (file != null) {
					service.selectImage(file, element);
					fieldPath.setText(file.getAbsolutePath());
					lastDirectory = file.getParentFile();
					btnReset.setDisable(false);
				}
				
				Logger.get().debug("Image selected: " + file);
			}
		});
		
		btnReset.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				service.selectImage(null, element);
				fieldPath.setText("");
				btnReset.setDisable(true);
			}
		});
		
		return box;
	}
	
	
	
	
	private HBox buildBox(boolean zoomedIn) {
		
		HBox box = buildBaseBox();
		Label labelName = (Label) box.getChildren().get(0);
		TextField fieldPath = (TextField) box.getChildren().get(1);
		Button btnBrowse = (Button) box.getChildren().get(2);
		Button btnReset = (Button) box.getChildren().get(3);
		
		labelName.setPadding(new Insets(0, 10, 0, 0));
		if(zoomedIn) {
			labelName.setText(ViewManager.getResources().getString("ssu_zoomed_in"));
			labelName.setTooltip(new Tooltip(ViewManager.getResources().getString("ssu_zoomed_in_tt")));
		} else {
			labelName.setText(ViewManager.getResources().getString("ssu_zoomed_out"));
			labelName.setTooltip(new Tooltip(ViewManager.getResources().getString("ssu_zoomed_out_tt")));
		}
		
		btnBrowse.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				FileChooser fc = new FileChooser();
				if(lastDirectory != null) {
					fc.setInitialDirectory(lastDirectory);
				}
				fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(ViewManager.getResources().getString("ssu_file_type_img") + " (*.jpg, *.png)", "*.jpg", "*.png"));
				fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(ViewManager.getResources().getString("ssu_file_type_img") + " (*.png)", "*.png"));
				fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(ViewManager.getResources().getString("ssu_file_type_img") + " (*.jpg)", "*.jpg"));
				
				File file = fc.showOpenDialog(ViewManager.getScene(View.SCREENSHOT_UPLOAD).getWindow());
				if (file != null) {
					service.selectImage(file, zoomedIn);
					fieldPath.setText(file.getAbsolutePath());
					lastDirectory = file.getParentFile();
					btnReset.setDisable(false);
				}
				
				Logger.get().debug("Image selected for zoomedIn=" + zoomedIn +": " + file);
			}
		});
		
		btnReset.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				service.selectImage(null, zoomedIn);
				fieldPath.setText("");
				btnReset.setDisable(true);
			}
		});
		
		return box;
	}
	
	
	
	
	private HBox buildBaseBox() {
		
		HBox box = new HBox();
		box.setAlignment(Pos.CENTER_LEFT);
		box.setSpacing(2);
		box.setPadding(new Insets(2, 2, 2, 2));
		box.setMinSize(HBox.USE_COMPUTED_SIZE, 42);
		box.setPrefSize(100000, 42);
		box.setMaxSize(HBox.USE_COMPUTED_SIZE, 42);

		Label labelName = new Label("error");
		labelName.setAlignment(Pos.CENTER_RIGHT);
		labelName.setContentDisplay(ContentDisplay.RIGHT);
		labelName.setMinSize(200, 42);
		labelName.setPrefSize(200, 42);
		labelName.setMaxSize(200, 42);
		
		TextField fieldPath = new TextField("");
		fieldPath.setMinSize(20, 31);
		fieldPath.setPrefSize(100000, 31);
		fieldPath.setMaxSize(100000, 31);
		fieldPath.setEditable(false);
		
		Button btnBrowse = new Button(ViewManager.getResources().getString("ssu_browse"));
		btnBrowse.setMinSize(70, 31);
		btnBrowse.setPrefSize(70, 31);
		btnBrowse.setMaxSize(70, 31);
		
		Button btnReset = new Button(ViewManager.getResources().getString("ssu_reset"));
		btnReset.setMinSize(42, 31);
		btnReset.setPrefSize(42, 31);
		btnReset.setMaxSize(42, 31);
		btnReset.setDisable(true);
		
		box.getChildren().addAll(labelName, fieldPath, btnBrowse, btnReset);
		
		return box;
	}
	
	
	
	
	@FXML
	void onCancel(ActionEvent event) {
		FXUtils.closeFXScene(View.SCREENSHOT_UPLOAD);
	}




	@FXML
	void onNext(ActionEvent event) {
		service.next(parameters);
	}


}
