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
import com.ruegnerlukas.wtsights.WTSights;
import com.ruegnerlukas.wtsights.data.DataLoader;
import com.ruegnerlukas.wtsights.data.DataWriter;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationAmmoData;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationData;
import com.ruegnerlukas.wtsights.data.vehicle.Vehicle;
import com.ruegnerlukas.wtsights.ui.Workflow;
import com.ruegnerlukas.wtsights.ui.Workflow.Step;
import com.ruegnerlukas.wtsights.ui.sighteditor.UISightEditor;
import com.ruegnerlukas.wtutils.Config;
import com.ruegnerlukas.wtutils.FXUtils;
import com.ruegnerlukas.wtutils.SightUtils;
import com.ruegnerlukas.wtutils.ZoomableScrollPane;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

public class UICalibrationEditor {

	private Stage stage;

	@FXML private ResourceBundle resources;
	@FXML private URL location;

	@FXML private ChoiceBox<String> choiceAmmo;
	
	@FXML private CheckBox cbZoomedIn;
	
	@FXML private VBox boxRanges;

	@FXML private Label labelInfo;

	@FXML private AnchorPane paneCanvas;
	private ZoomableScrollPane paneCanvasControl;
	private Canvas canvas;

	private Vehicle vehicle;
	private File fileSight;
	
	
	private CalibrationData data;
	private CalibrationAmmoData currentAmmoData;
	
	private boolean cursorVisible = false;
	private Vector2d cursorPosition = new Vector2d();
	private Image currentImage;
	
	private Map<String,Image> imageCache = new HashMap<String,Image>();
	private Font font = new Font("Arial", 10);
	
	
	
	
	
	public static void openNew(Stage stage, Vehicle vehicle, List<String> ammoNames, List<File> imgFiles, File fileSight) {

		Logger.get().info("Opening CalibrationEditor (" + Workflow.toString(Workflow.steps) + ")  vehicle=" + (vehicle==null ? "null" : vehicle.name) + "; ammo=" + ammoNames + "; sight=" + fileSight);

		
		try {
			final Stage window = new Stage();
			window.initModality(Modality.WINDOW_MODAL);
			window.initOwner(WTSights.getPrimaryStage());
			FXUtils.addIcons(window);

			FXMLLoader loader;
//			if(WTSights.DARK_MODE) {
//				loader = new FXMLLoader(UICalibrationEditor.class.getResource("/ui/layout_calibration_dark.fxml"));
//			} else {
//				loader = new FXMLLoader(UICalibrationEditor.class.getResource("/ui/layout_calibration.fxml"));
//			}
			loader = new FXMLLoader(UICalibrationEditor.class.getResource("/ui/layout_calibration.fxml"));
			Parent root = (Parent) loader.load();

			UICalibrationEditor controller = (UICalibrationEditor) loader.getController();

			int width = 1280;
			int height = 720;
			String[] valuesScreenSize = Config.getValues("window_size");
			if(valuesScreenSize != null && valuesScreenSize.length == 2) {
				width = Integer.parseInt(valuesScreenSize[0]);
				height = Integer.parseInt(valuesScreenSize[1]);
			}
			
			Scene scene = new Scene(root, width, height, true, SceneAntialiasing.DISABLED);
//			if(WTSights.DARK_MODE) {
//				scene.getStylesheets().add("/ui/modena_dark.css");
//			}
			window.setTitle("Calibrate Sight");
			window.setScene(scene);

			controller.create(window, vehicle, ammoNames, imgFiles, fileSight);
			window.show();

		} catch (IOException e) {
			Logger.get().error(e);
		}

	}
	
	
	
	
	public static void openNew(Stage stage, CalibrationData data, File fileSight) {
		
		Logger.get().info("Opening CalibrationEditor (" + Workflow.toString(Workflow.steps) + ")  vehicle=" + (data==null ? "null" : data.vehicle.name) + "; sight=" + fileSight);

		
		try {
			final Stage window = new Stage();
			window.initModality(Modality.WINDOW_MODAL);
			window.initOwner(WTSights.getPrimaryStage());
			FXUtils.addIcons(window);

			FXMLLoader loader;
//			if(WTSights.DARK_MODE) {
//				loader = new FXMLLoader(UICalibrationEditor.class.getResource("/ui/layout_calibration_dark.fxml"));
//			} else {
//				loader = new FXMLLoader(UICalibrationEditor.class.getResource("/ui/layout_calibration.fxml"));
//			}
			loader = new FXMLLoader(UICalibrationEditor.class.getResource("/ui/layout_calibration.fxml"));
			Parent root = (Parent) loader.load();

			UICalibrationEditor controller = (UICalibrationEditor) loader.getController();
			
			int width = 1280;
			int height = 720;
			String[] valuesScreenSize = Config.getValues("window_size");
			if(valuesScreenSize != null && valuesScreenSize.length == 2) {
				width = Integer.parseInt(valuesScreenSize[0]);
				height = Integer.parseInt(valuesScreenSize[1]);
			}
			
			Scene scene = new Scene(root, width, height, true, SceneAntialiasing.DISABLED);
//			if(WTSights.DARK_MODE) {
//				scene.getStylesheets().add("/ui/modena_dark.css");
//			}
			window.setTitle("Calibrate Sight");
			window.setScene(scene);

			controller.create(window, data, fileSight);
			window.show();

		} catch (IOException e) {
			Logger.get().error(e);
		}
		
	}

	
	
	
	
	
	
	@FXML
	void initialize() {
		assert choiceAmmo != null : "fx:id=\"choiceAmmo\" was not injected: check your FXML file 'layout_calibration.fxml'.";
		assert labelInfo != null : "fx:id=\"labelInfo\" was not injected: check your FXML file 'layout_calibration.fxml'.";
		assert paneCanvas != null : "fx:id=\"paneCanvas\" was not injected: check your FXML file 'layout_calibration.fxml'.";
	}


	
	
	public void create(Stage stage, CalibrationData data, File fileSight) {
		this.stage = stage;
		this.data = data;
		this.fileSight = fileSight;
		this.vehicle = new Vehicle();
		this.vehicle.name = data.vehicleName;
		this.vehicle.fovOut = data.fovOut;
		this.vehicle.fovIn = data.fovIn;
		create();
	}
	
	
	
	
	public void create(Stage stage, Vehicle vehicle, List<String> ammoNames, List<File> imgFiles, File fileSight) {
		
		this.stage = stage;
		this.fileSight = fileSight;
		this.vehicle = vehicle;
		
		// CREATE NEW CALIBRATION DATA
		data = new CalibrationData();
		data.vehicleName = this.vehicle.name;
		data.vehicle = vehicle;
		data.fovOut = vehicle.fovOut;
		data.fovIn = vehicle.fovIn;
		
		try {
			
			for(int i=0; i<ammoNames.size(); i++) {
				String name = ammoNames.get(i);
				
				String imgName = "image_"+name;
				File file = imgFiles.get(i);
				BufferedImage img = ImageIO.read(file);
				data.images.put(imgName, img);
				
				CalibrationAmmoData ammoData = new CalibrationAmmoData();
				ammoData.ammoName = name;
				ammoData.imgName = imgName;
				ammoData.markerCenter.set(img.getHeight()/2, 0);
				data.ammoData.add(ammoData);
				
			}
		
		} catch (IOException e) {
			Logger.get().error(e);
		}
		
		create();
		
	}


	
	private void create() {

		// AMMO CHOICE
		for(CalibrationAmmoData ammoData : data.ammoData) {
			choiceAmmo.getItems().add(ammoData.ammoName);
		}
		choiceAmmo.getSelectionModel().select(0);
		choiceAmmo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				onAmmoSelected(newValue);
			}
		});
		onAmmoSelected(choiceAmmo.getSelectionModel().getSelectedItem());
		
	}
	
	
	
	
	void onAmmoSelected(String ammoName) {
		
		Logger.get().debug("Select ammo '" + ammoName + "'");
		
		if(ammoName == null || "null".equalsIgnoreCase(ammoName)) {
			
			currentImage = null;
			currentAmmoData = null;
			cbZoomedIn.setDisable(true);
			rebuildCanvas(SwingFXUtils.toFXImage(new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB), null));
			
		} else {
			
			if(imageCache.containsKey("image_"+ammoName)) {
				currentImage = imageCache.get("image_"+ammoName);
				Logger.get().debug("Image retrieved from cache");
			} else {
				BufferedImage bufImg = data.images.get("image_"+ammoName);
				currentImage = SwingFXUtils.toFXImage(bufImg, null);
				imageCache.put("image_"+ammoName, currentImage);
			}
			
			currentAmmoData = null;
			for(int i=0; i<data.ammoData.size(); i++) {
				CalibrationAmmoData ammoData = data.ammoData.get(i);
				if((ammoData.ammoName).equalsIgnoreCase(ammoName)) {
					currentAmmoData = ammoData;
					break;
				}
			}
			
			Logger.get().debug("AmmoData selected: " + currentAmmoData.ammoName);
			
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
		for(CalibrationAmmoData d : data.ammoData) {
			if(d.ammoName.equalsIgnoreCase(ammoName)) {
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
		fc.setTitle("Save Calibration Data");
		
		File fileSelected = fc.showSaveDialog(stage);
		if(fileSelected == null) {
			return;
		}
		
		File file = new File(fileSelected.getAbsolutePath() + (fileSelected.getAbsolutePath().endsWith(".xml") ? "" : ".xml") );
		
		
		try {
			if(!DataWriter.saveExternalCalibFile(this.data, file)) {
				Logger.get().warn("(Alert) Calibration could not be saved.");
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText(null);
				alert.setContentText("Calibration could not be saved.");
				alert.showAndWait();
			} else {
				Logger.get().info("Saved Calibration to " + file);
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
			UISightEditor.openNew(stage, data);
		} else {
			Workflow.steps.add(Step.EDIT_CALIBRATION);
			UISightEditor.openNew(stage, data, this.fileSight);
		}
	}
	
	

	
	private boolean isValidMarkers() {
		
		for(CalibrationAmmoData ammoData : this.data.ammoData) {

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
