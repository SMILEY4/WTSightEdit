package com.ruegnerlukas.wtsights.ui.calibrationeditor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2i;
import com.ruegnerlukas.simplemath.vectors.vec3.Vector3d;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.DataLoader;
import com.ruegnerlukas.wtsights.data.DataWriter;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationAmmoData;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationData;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.data.vehicle.Ammo;
import com.ruegnerlukas.wtsights.data.vehicle.Vehicle;
import com.ruegnerlukas.wtsights.ui.AmmoIcons;
import com.ruegnerlukas.wtsights.ui.Workflow;
import com.ruegnerlukas.wtsights.ui.Workflow.Step;
import com.ruegnerlukas.wtsights.ui.calibrationselect.UICalibrationSelect;
import com.ruegnerlukas.wtsights.ui.sighteditor.UISightEditor;
import com.ruegnerlukas.wtsights.ui.sighteditor.UISightEditor;
import com.ruegnerlukas.wtutils.Config2;
import com.ruegnerlukas.wtutils.FXUtils;
import com.ruegnerlukas.wtutils.SightUtils;
import com.ruegnerlukas.wtutils.ZoomableScrollPane;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

public class UICalibrationEditor {

	private Stage stage;

	@FXML private ResourceBundle resources;
	@FXML private URL location;

	@FXML private ComboBox<String> choiceAmmo;
	
	@FXML private CheckBox cbZoomedIn;
	
	@FXML private VBox boxRanges;

	@FXML private Label labelInfo;

	@FXML private AnchorPane paneCanvas;
	private ZoomableScrollPane paneCanvasControl;
	private Canvas canvas;

	private File fileSight;
	
	private CalibrationData dataCalib;
	private CalibrationAmmoData currentAmmoData;
	
	private boolean cursorVisible = false;
	private Vector2d cursorPosition = new Vector2d();
	private Image currentImage;
	
	private Map<String,Image> imageCache = new HashMap<String,Image>();
	private Font font = new Font("Arial", 10);
	
	
	
	
	
	
	public static void openNew(Vehicle vehicle, List<Ammo> ammoList, Map<Ammo,File> imageMap, File fileSight) {

		Logger.get().info("Navigate to 'CalibrationEditor' (" + Workflow.toString(Workflow.steps) + ")  vehicle=" + (vehicle==null ? "null" : vehicle.name) + "; ammo=" + ammoList + "; sight=" + fileSight.getAbsolutePath());

		int width = Config2.app_window_size.x;
		int height = Config2.app_window_size.y;
		
		Object[] sceneObjects = FXUtils.openFXScene(null, "/ui/layout_calibration.fxml", width, height, "Create Ballistic Data");
		UICalibrationEditor controller = (UICalibrationEditor)sceneObjects[0];
		Stage stage = (Stage)sceneObjects[1];
		
		controller.create(stage, vehicle, ammoList, imageMap, fileSight);
	}
	
	
	
	
	public static void openNew(Vehicle vehicle, List<Ammo> ammoList, Map<Ammo,File> imageMap) {

		Logger.get().info("Navigate to 'CalibrationEditor' (" + Workflow.toString(Workflow.steps) + ")  vehicle=" + (vehicle==null ? "null" : vehicle.name) + "; ammo=" + ammoList);

		int width = Config2.app_window_size.x;
		int height = Config2.app_window_size.y;
		
		Object[] sceneObjects = FXUtils.openFXScene(null, "/ui/layout_calibration.fxml", width, height, "Create Ballistic Data");
		UICalibrationEditor controller = (UICalibrationEditor)sceneObjects[0];
		Stage stage = (Stage)sceneObjects[1];
		
		controller.create(stage, vehicle, ammoList, imageMap);
	}
	
	
	
	public static void openNew(CalibrationData dataCalib) {

		Logger.get().info("Navigate to 'CalibrationEditor' (" + Workflow.toString(Workflow.steps) + ")  data=" + dataCalib + "; vehicle="+dataCalib.vehicle.name);

		int width = Config2.app_window_size.x;
		int height = Config2.app_window_size.y;
		
		Object[] sceneObjects = FXUtils.openFXScene(null, "/ui/layout_calibration.fxml", width, height, "Create Ballistic Data");
		UICalibrationEditor controller = (UICalibrationEditor)sceneObjects[0];
		Stage stage = (Stage)sceneObjects[1];
		
		controller.create(stage, dataCalib);
	}
	
	
	
	
	@FXML
	void initialize() {
		assert choiceAmmo != null : "fx:id=\"choiceAmmo\" was not injected: check your FXML file 'layout_calibration.fxml'.";
		assert labelInfo != null : "fx:id=\"labelInfo\" was not injected: check your FXML file 'layout_calibration.fxml'.";
		assert paneCanvas != null : "fx:id=\"paneCanvas\" was not injected: check your FXML file 'layout_calibration.fxml'.";
	}


	
	
	
	
	public void create(Stage stage, Vehicle vehicle, List<Ammo> ammoList, Map<Ammo,File> imageMap) {
		create(stage, vehicle, ammoList, imageMap, null);
	}
	
	
	
	
	public void create(Stage stage, Vehicle vehicle, List<Ammo> ammoList, Map<Ammo,File> imageMap, File fileSight) {
		this.stage = stage;
		this.fileSight = fileSight;
		
		dataCalib = new CalibrationData();
		dataCalib.vehicle = vehicle;
		
		try {
			for(int i=0; i<ammoList.size(); i++) {
				Ammo ammo  = ammoList.get(i);
				
				String imgName = "image_"+ammo.name;
				File file = imageMap.get(ammo);
				BufferedImage img = ImageIO.read(file);
				dataCalib.images.put(imgName, img);
				
				CalibrationAmmoData ammoData = new CalibrationAmmoData();
				ammoData.ammo = ammo;
				ammoData.imgName = imgName;
				ammoData.markerCenter.set(img.getHeight()/2, 0);
				dataCalib.ammoData.add(ammoData);
			}
			
		} catch (IOException e) {
			Logger.get().error(e);
		}
		
		create(stage, dataCalib);
	}
	
	
	
	
	public void create(Stage stage, CalibrationData dataCalib) {
		this.stage = stage;
		this.dataCalib = dataCalib;
		create();
		
	}


	
	
	private void create() {

		// AMMO CHOICE
		choiceAmmo.setButtonCell(new ListCell<String>() {
			@Override protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				setText(item);
				if (item == null || empty) {
					setGraphic(null);
				} else {
					String name = item != null ? item.split(";")[0] : "<null>";
					String type = item != null ? item.split(";")[1] : "<null>";
					ImageView imgView = new ImageView(SwingFXUtils.toFXImage(AmmoIcons.getIcon(type, false), null));
					imgView.setSmooth(true);
					imgView.setPreserveRatio(true);
					imgView.setFitHeight(40);
					setGraphic(imgView);
					setText(name);
				}
			}
		});
		
		choiceAmmo.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override public ListCell<String> call(ListView<String> p) {
				return new ListCell<String>() {
					@Override protected void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						setText(item);
						if (item == null || empty) {
							setGraphic(null);
						} else {
							String name = item != null ? item.split(";")[0] : "<null>";
							String type = item != null ? item.split(";")[1] : "<null>";
							ImageView imgView = new ImageView(SwingFXUtils.toFXImage(AmmoIcons.getIcon(type, false), null));
							imgView.setSmooth(true);
							imgView.setPreserveRatio(true);
							imgView.setFitHeight(40);
							setGraphic(imgView);
							setText(name);
						}
					}
				};
			}
		});

		if(dataCalib.ammoData.isEmpty()) {
			choiceAmmo.getItems().add("No Ammunition available;-");
		} else {
			for(CalibrationAmmoData ammoData : dataCalib.ammoData) {
				choiceAmmo.getItems().add(ammoData.ammo.name + ";" + ammoData.ammo.type);
			}
		}
		choiceAmmo.getSelectionModel().select(0);
		choiceAmmo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				onAmmoSelected(newValue.split(";")[0]);
			}
		});
		onAmmoSelected(choiceAmmo.getSelectionModel().getSelectedItem().split(";")[0]);
		
	}
	
	
	
	
	void onAmmoSelected(String ammoName) {
		
		
		Logger.get().debug("Select ammo '" + ammoName + "'");
		
		if(ammoName == null || "null".equalsIgnoreCase(ammoName) || "No Ammunition available".equalsIgnoreCase(ammoName)) {
			
			currentImage = null;
			currentAmmoData = null;
			cbZoomedIn.setDisable(true);
			rebuildCanvas(SwingFXUtils.toFXImage(new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB), null));
			
		} else {
			
			if(imageCache.containsKey("image_"+ammoName)) {
				currentImage = imageCache.get("image_"+ammoName);
				Logger.get().debug("Image retrieved from cache");
			} else {
				BufferedImage bufImg = dataCalib.images.get("image_"+ammoName);
				currentImage = SwingFXUtils.toFXImage(bufImg, null);
				imageCache.put("image_"+ammoName, currentImage);
			}
			
			currentAmmoData = null;
			for(int i=0; i<dataCalib.ammoData.size(); i++) {
				CalibrationAmmoData ammoData = dataCalib.ammoData.get(i);
				if((ammoData.ammo.name).equalsIgnoreCase(ammoName)) {
					currentAmmoData = ammoData;
					break;
				}
			}
			
			Logger.get().debug("AmmoData selected: " + currentAmmoData.ammo.name);
			
			cbZoomedIn.setDisable(false);
			cbZoomedIn.setSelected(currentAmmoData.zoomedIn);
			updateRangeList();
			rebuildCanvas(currentImage);
		}
		
	}
	
	
	
	
	private void updateRangeList() {
		
		Logger.get().debug("Updating Range List " + currentAmmoData.markerRanges);
		
		boxRanges.getChildren().clear();
		
		if(currentAmmoData != null) {
			
			for(int i=0; i<currentAmmoData.markerRanges.size(); i++) {
				
				Vector2i marker = currentAmmoData.markerRanges.get(i);
				
				Spinner<Integer> spinner = new Spinner<Integer>();
				FXUtils.initSpinner(spinner, marker.y, 0, 3200, 200, 0, new ChangeListener<Integer>() {
					@Override public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
						marker.y = newValue;
						repaintCanvas();
					}
				});
				
				boxRanges.getChildren().add(spinner);
				VBox.setVgrow(spinner, Priority.ALWAYS);
			}
			
		}
		
	}
	
	
	
	
	private void rebuildCanvas(Image img) {
		
		canvas = new Canvas(img.getWidth(), img.getHeight());
		
		canvas.setOnMouseMoved(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				event.consume();
				cursorVisible = true;
				cursorPosition.set(event.getX(), event.getY());
				repaintCanvas();
			}
		});
		canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getButton() == MouseButton.PRIMARY) {
					event.consume();
					cursorVisible = true;
					cursorPosition.set(event.getX(), event.getY());
					repaintCanvas();
				}
			}
		});
		canvas.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				event.consume();
				cursorVisible = false;
				repaintCanvas();
			}
		});
		canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getButton() == MouseButton.PRIMARY) {
					event.consume();
					cursorVisible = true;
					repaintCanvas();
				}
			}
		});
		canvas.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getButton() == MouseButton.PRIMARY) {
					event.consume();
					onAddMarker(event.getY());
					cursorVisible = true;
					repaintCanvas();
				}
			}
		});
	
		stage.getScene().setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.BACK_SPACE && cursorVisible) {
					event.consume();
					onDeleteMarkerRequest(cursorPosition.x, cursorPosition.y);
					repaintCanvas();
				}
			}
		});
		
		paneCanvasControl = new ZoomableScrollPane(canvas);
		paneCanvas.getChildren().add(paneCanvasControl);
		AnchorPane.setLeftAnchor(paneCanvasControl, 0.0);
		AnchorPane.setRightAnchor(paneCanvasControl, 0.0);
		AnchorPane.setTopAnchor(paneCanvasControl, 0.0);
		AnchorPane.setBottomAnchor(paneCanvasControl, 0.0);
		
		repaintCanvas();
		
	}
	
	
	
	
	private void repaintCanvas() {
		
		if(canvas != null) {

			GraphicsContext g = canvas.getGraphicsContext2D();
			g.setFill(Color.LIGHTGRAY);
			g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
			
			if(currentImage != null) {
				g.drawImage(currentImage, 0, 0, canvas.getWidth(), canvas.getHeight());
			}
			
			if(cursorVisible) {
				g.setStroke(Color.RED);
				g.strokeLine(cursorPosition.x-10, cursorPosition.y, cursorPosition.x+10, cursorPosition.y);
				g.strokeLine(cursorPosition.x, cursorPosition.y-10, cursorPosition.x, cursorPosition.y+10);
				g.setLineDashes(5);
				g.strokeLine(0, cursorPosition.y, canvas.getWidth(), cursorPosition.y);
				g.setLineDashes(null);
			}

			
			if(currentAmmoData != null && currentImage != null) {
				
				List<Vector2i> allMarkers = currentAmmoData.markerRanges;
				
				
				int mc = currentAmmoData.markerCenter.x;
				
				double minDist = Integer.MAX_VALUE;
				Vector2i minMarker = null;
				Vector2i tmp = new Vector2i();
				
				for(Vector2i m : currentAmmoData.markerRanges) {
					double dist = tmp.set(cursorPosition.getIntX(), m.x+mc).dist(cursorPosition.getIntX(), cursorPosition.getIntY());
					if(dist < minDist) {
						minDist = dist;
						minMarker = m;
					}
				}
				
				
				for (Vector2i marker : allMarkers) {
					
					if (marker == minMarker) {
						g.setFill(Color.YELLOW);
						g.setStroke(Color.YELLOW);
					} else {
						g.setFill(Color.MEDIUMVIOLETRED);
						g.setStroke(Color.MEDIUMVIOLETRED);
					}
					
					double mx = currentImage.getWidth()/2;
					double my = marker.x + mc;
					
					g.strokeLine(mx-3, my-3, mx+3, my+3);
					g.strokeLine(mx+3, my-3, mx-3, my+3);
					
					g.setFont(font);
					g.strokeText(""+(allMarkers.indexOf(marker)+1), mx + 5, my);
					
					
				}
				
				
				if(currentAmmoData.markerRanges.size() > 0) {
					
					// ballistic range indicators
					List<Vector2d> fittingPoints = new ArrayList<Vector2d>();
					fittingPoints.add(new Vector2d(0, 0));
					for(int i=0; i<currentAmmoData.markerRanges.size(); i++) {
						Vector2d p = new Vector2d(currentAmmoData.markerRanges.get(i).y/100, currentAmmoData.markerRanges.get(i).x);
						fittingPoints.add(p);
					}
					Vector3d fittingParams = SightUtils.fitBallisticFunction(fittingPoints, 1);
					
					if(fittingParams != null) {
						for(int i=200; i<=2800; i+=200) {
							double resultPX = SightUtils.ballisticFunction(i/100.0, fittingParams);
							float x = (float) (canvas.getWidth()/2) - 15;
							float y = (float) (currentAmmoData.markerCenter.x + resultPX);
							g.setStroke(Color.RED);
							g.strokeLine(x-4, y, x+4, y);
						}
					}
					
					
				}
				
			}
			
		}
		
	}
	
	
	
	
	@FXML
	void onZoomedIn(ActionEvent event) {
		if(currentAmmoData == null) {
			return;
		}
		currentAmmoData.zoomedIn = cbZoomedIn.isSelected();
		repaintCanvas();
	}
	
	
	
	
	private void onAddMarker(double y) {
		if(currentAmmoData == null) {
			return;
		}
		int mc = currentAmmoData.markerCenter.x;
		if(currentAmmoData.markerRanges.size() == 0) {
			currentAmmoData.markerRanges.add(new Vector2i( (int)y-mc, 200));
		} else {
			currentAmmoData.markerRanges.add(new Vector2i( (int)y-mc, currentAmmoData.markerRanges.get(currentAmmoData.markerRanges.size()-1).y+200));
		}
		Logger.get().debug("Adding marker at y=" + (int)(y-mc) );
		updateRangeList();
		repaintCanvas();
	}

	
	
	
	private void onDeleteMarkerRequest(double x, double y) {
		if(currentAmmoData == null) {
			return;
		}
		
		int mc = currentAmmoData.markerCenter.x;
		
		double minDist = Integer.MAX_VALUE;
		Vector2i minMarker = null;
		Vector2i tmp = new Vector2i();
		
		for(Vector2i m : currentAmmoData.markerRanges) {
			double dist = tmp.set((int)x, m.x+mc).dist((int)x, (int)y);
			if(dist < minDist) {
				minDist = dist;
				minMarker = m;
			}
		}
		
		if(minMarker != null) {
			currentAmmoData.markerRanges.remove(minMarker);
			Logger.get().debug("Deleted marker " + minMarker );
		}
		
		updateRangeList();
		repaintCanvas();
	}
	
	
	
	
	void onSetMarkerRange(int index, int distance) {
		if(currentAmmoData == null) {
			return;
		}
		
		String ammoName = choiceAmmo.getSelectionModel().getSelectedItem();
		
		CalibrationAmmoData ammoData = null;
		for(CalibrationAmmoData d : dataCalib.ammoData) {
			if(d.ammo.name.equalsIgnoreCase(ammoName)) {
				ammoData = d;
				break;
			}
		}
		
		ammoData.markerRanges.get(index).y = distance;
		
		Logger.get("Set marker range: index="+index + " to " + distance + "m");
		
		repaintCanvas();
	}
	
	
	
	
	@FXML
	void onExport(ActionEvent event) {
		
		if(!isValidMarkers()) {
			Logger.get().warn("(Alert) Can not save data. At least one ammunition does not have enough markers.");
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText("Can not save data. At least one ammunition does not have enough markers.");
			alert.showAndWait();
			return;
		}
		
		FileChooser fc = new FileChooser();
		fc.setTitle("Save Ballistic Data");
		
		File fileSelected = fc.showSaveDialog(stage);
		if(fileSelected == null) {
			return;
		}
		
		File file = new File(fileSelected.getAbsolutePath() + (fileSelected.getAbsolutePath().endsWith(".xml") ? "" : ".xml") );
		
		
		try {
			if(!DataWriter.saveExternalCalibFile(this.dataCalib, file)) {
				Logger.get().warn("(Alert) Ballistic Data could not be saved.");
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText(null);
				alert.setContentText("Ballistic Data could not be saved.");
				alert.showAndWait();
			} else {
				Logger.get().info("Saved Ballistic Data to " + file);
			}
		} catch (Exception e) {
			Logger.get().error(e);
		}
	}
	
	
	

	@FXML
	void onEditSight(ActionEvent event) {
		
		if(!isValidMarkers()) {
			Logger.get().warn("(Alert) Can not save data. At least one ammunition does not have enough markers.");
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText("Can not save data. At least one ammunition does not have enough markers.");
			alert.showAndWait();
			return;
		}
		
		if(this.fileSight == null) {
			this.stage.close();
			Workflow.steps.add(Step.EDIT_CALIBRATION);
			UISightEditor.openNew(dataCalib);

		} else {
			Workflow.steps.add(Step.EDIT_CALIBRATION);
			SightData dataSight = DataLoader.loadSight(fileSight, dataCalib);
			UISightEditor.openNew(dataCalib, dataSight);
		}
		
	}
	
	

	
	private boolean isValidMarkers() {
		
		for(CalibrationAmmoData ammoData : this.dataCalib.ammoData) {

			if(ammoData.markerRanges.size() == 0) {
				return false;
			}
			
			List<Vector2d> fittingPoints = new ArrayList<Vector2d>();
			fittingPoints.add(0, new Vector2d(0, 0));
			for(int i=0; i<ammoData.markerRanges.size(); i++) {
				Vector2d p = new Vector2d(ammoData.markerRanges.get(i).y/100, ammoData.markerRanges.get(i).x);
				fittingPoints.add(p);
			}

			
			Vector3d fittingParams = SightUtils.fitBallisticFunction(fittingPoints, 1);
			if(fittingParams == null) {
				return false;
			}
			
		}
		
		return true;
	}
	

}
