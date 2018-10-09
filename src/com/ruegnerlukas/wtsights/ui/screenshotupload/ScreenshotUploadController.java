package com.ruegnerlukas.wtsights.ui.screenshotupload;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticElement;
import com.ruegnerlukas.wtsights.data.vehicle.Ammo;
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
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.util.Callback;

public class ScreenshotUploadController implements IViewController {


	
	@FXML private ResourceBundle resources;
	@FXML private URL location;

	@FXML private Label labelTankName;
	@FXML private ListView<BallisticElement> listView;

	private List<Cell> cells = new ArrayList<Cell>();
	private File lastDirectory = null;

	private ScreenshotUploadService service;
	private Map<ParamKey,Object> parameters;
	
	
	
	
	
	@Override
	public void create(Map<ParamKey,Object> parameters) {
		
		this.parameters = parameters;
		this.service = (ScreenshotUploadService) ViewManager.getService(View.SCREENSHOT_UPLOAD);
		Vehicle vehicle = (Vehicle) parameters.get(ParamKey.SELECTED_VEHICLE);
		
		labelTankName.setText(vehicle.namePretty);
		listView.getItems().addAll(service.getListOfElements(vehicle));
		Logger.get().info("Loaded ammunition for " + vehicle.name + ": " + listView.getItems().size());
		
		listView.setCellFactory(new Callback<ListView<BallisticElement>, ListCell<BallisticElement>>() {
			@Override
			public ListCell<BallisticElement> call(ListView<BallisticElement> param) {
				Cell cell = new Cell();
				cells.add(cell);
				return cell;
			}
		});

	}

	
	
	
	@FXML
	void onCancel(ActionEvent event) {
		FXUtils.closeFXScene(View.SCREENSHOT_UPLOAD);
	}




	@FXML
	void onNext(ActionEvent event) {

		List<BallisticElement> listContentElements = new ArrayList<BallisticElement>();
		List<File> listContentFiles = new ArrayList<File>();

		for(Cell cell : cells) {
			if(cell.data != null) {
				if(cell.data.isRocketElement) {
					listContentElements.add(cell.data);
					listContentFiles.add(null);
				} else {
					if(cell.fileImage != null) {
						listContentElements.add(cell.data);
						listContentFiles.add(cell.fileImage);
					}
				}
			}
		}
		
		for(int i=0; i<listContentElements.size(); i++) {
			BallisticElement e = listContentElements.get(i);
			System.out.println(e + ":");
			for(Ammo a : e.ammunition) {
				System.out.println("    " + a.name);
			}
			System.out.println(" -> " + listContentFiles.get(i));
		}
		
		service.next(parameters, listContentElements, listContentFiles);
	}

	
	
	
	
	
	class Cell extends ListCell<BallisticElement> {
		
		public File fileImage;
		public Label label = new Label("null");
		
		private HBox hbox = new HBox();
		private TextField textField = new TextField();
		private Button browse = new Button("Browse");
		private Button reset = new Button("X");
		private BallisticElement data;
		
		
		public Cell() {
			super();

			hbox.getChildren().addAll(label, textField, browse, reset);
			hbox.setPrefHeight(40+2);
			hbox.setAlignment(Pos.CENTER_LEFT);
			
			HBox.setHgrow(textField, Priority.ALWAYS);
			textField.setEditable(false);
		
			reset.setMinWidth(40);
			reset.setMaxWidth(40);
			reset.setDisable(true);
			
			HBox.setMargin(label, new Insets(0, 10, 0, 0));
			label.setMinWidth(200);
			
			
			browse.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {

					Button source = (Button)event.getSource();
					
					FileChooser fc = new FileChooser();
					if(lastDirectory != null) {
						fc.setInitialDirectory(lastDirectory);
					}
					fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image (*.jpg, *.png)", "*.jpg", "*.png"));
					fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image (*.png)", "*.png"));
					fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image (*.jpg)", "*.jpg"));
					
					File file = fc.showOpenDialog(source.getScene().getWindow());
					if (file != null) {
						fileImage = file;
						textField.setText(file.getAbsolutePath());
						lastDirectory = file.getParentFile();
						reset.setDisable(false);
					}
					
					Logger.get().debug("Image selected: " + file);
					
				}
			});
			
			reset.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					fileImage = null;
					textField.setText("");
					reset.setDisable(true);
				}
			});
			
		}
		
		
		@Override
		protected void updateItem(BallisticElement item, boolean empty) {
			super.updateItem(item, empty);
			this.data = item;
			setText(null);
			
			if(empty || item == null || item.ammunition.isEmpty()) {
				setGraphic(null);
			} else {
				
				// multiple ammo
				if(item.ammunition.size() > 1) {
					
					label.setText(item.ammunition.get(0).parentWeapon.name);
					label.setTooltip(new Tooltip("TriggerGroup = " + item.ammunition.get(0).parentWeapon.triggerGroup.id));
					
					ImageView imgView = new ImageView(SwingFXUtils.toFXImage(AmmoIcons.getIcon("machinegun"), null));
					imgView.setSmooth(true);
					imgView.setPreserveRatio(true);
					imgView.setFitHeight(40);
					label.setGraphic(imgView);
					setGraphic(hbox);
					
					if(item.isRocketElement) {
						setDisable(true);
					} else {
						setDisable(false);
					}
					
					
				// single ammo
				} else if(item.ammunition.size() == 1) {
					label.setText(item.ammunition.get(0).namePretty);
					label.setTooltip(new Tooltip("Type = " + item.ammunition.get(0).type));
					ImageView imgView = new ImageView(SwingFXUtils.toFXImage(AmmoIcons.getIcon(item.ammunition.get(0).type), null));
					imgView.setSmooth(true);
					imgView.setPreserveRatio(true);
					imgView.setFitHeight(40);
					label.setGraphic(imgView);
					setGraphic(hbox);
					if(item.isRocketElement) {
						setDisable(true);
					} else {
						setDisable(false);
					}
				}
				
			}
		}
		
	}
	

}
