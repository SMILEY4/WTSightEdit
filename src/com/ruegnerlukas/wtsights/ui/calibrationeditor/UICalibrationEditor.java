package com.ruegnerlukas.wtsights.ui.calibrationeditor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.DataLoader;
import com.ruegnerlukas.wtsights.data.DataWriter;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticData;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticElement;
import com.ruegnerlukas.wtsights.data.ballisticdata.Marker;
import com.ruegnerlukas.wtsights.data.ballisticdata.MarkerData;
import com.ruegnerlukas.wtsights.data.ballisticdata.ballfunctions.DefaultBallisticFuntion;
import com.ruegnerlukas.wtsights.data.ballisticdata.ballfunctions.IBallisticFunction;
import com.ruegnerlukas.wtsights.data.ballisticdata.ballfunctions.NullBallisticFunction;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.data.vehicle.Ammo;
import com.ruegnerlukas.wtsights.data.vehicle.Vehicle;
import com.ruegnerlukas.wtsights.ui.Workflow;
import com.ruegnerlukas.wtsights.ui.Workflow.Step;
import com.ruegnerlukas.wtsights.ui.sighteditor.UISightEditor;
import com.ruegnerlukas.wtutils.Config;
import com.ruegnerlukas.wtutils.FXUtils;
import com.ruegnerlukas.wtutils.SightUtils.TriggerGroup;
import com.ruegnerlukas.wtutils.canvas.WTCanvas;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class UICalibrationEditor {

	private Stage stage;

	@FXML private ResourceBundle resources;
	@FXML private URL location;

	@FXML private Label labelVehicleName;
	
	@FXML private ComboBox<BallisticElement> choiceAmmo;
	
	@FXML private CheckBox cbZoomedIn;
	
	@FXML private VBox boxRanges;

	@FXML private Label labelInfo;

	private File fileSight;
	
	private BallisticData dataBallistic;
	private BallisticElement elementBallistic;
	
	@FXML private AnchorPane paneCanvas;
	private WTCanvas wtCanvas;
	private Image currentImage;
	private Map<BallisticElement,Image> imageCache = new HashMap<BallisticElement,Image>();
	private Font font = new Font("Arial", 18);
	
	
	
	
	
	
	public static void openNew(Vehicle vehicle, List<Ammo> ammoList, Map<Ammo,File> imageMap, File fileSight) {

		Logger.get().info("Navigate to 'CalibrationEditor' (" + Workflow.toString(Workflow.steps) + ")  vehicle=" + (vehicle==null ? "null" : vehicle.name) + "; ammo=" + ammoList + "; sight=" + fileSight.getAbsolutePath());

		int width = Config.app_window_size.x;
		int height = Config.app_window_size.y;
		
		boolean styleDark = "dark".equals(Config.app_style);
		Object[] sceneObjects = FXUtils.openFXScene(null, "/ui/layout_calibration"+(styleDark?"_dark":"")+".fxml", width, height, "Create Ballistic Data");
		UICalibrationEditor controller = (UICalibrationEditor)sceneObjects[0];
		Stage stage = (Stage)sceneObjects[1];
		
		controller.create(stage, vehicle, ammoList, imageMap, fileSight);
	}
	
	
	
	
	public static void openNew(Vehicle vehicle, List<Ammo> ammoList, Map<Ammo,File> imageMap) {

		Logger.get().info("Navigate to 'CalibrationEditor' (" + Workflow.toString(Workflow.steps) + ")  vehicle=" + (vehicle==null ? "null" : vehicle.name) + "; ammo=" + ammoList);

		int width = Config.app_window_size.x;
		int height = Config.app_window_size.y;
		
		boolean styleDark = "dark".equals(Config.app_style);
		Object[] sceneObjects = FXUtils.openFXScene(null, "/ui/layout_calibration"+(styleDark?"_dark":"")+".fxml", width, height, "Create Ballistic Data");
		UICalibrationEditor controller = (UICalibrationEditor)sceneObjects[0];
		Stage stage = (Stage)sceneObjects[1];
		
		controller.create(stage, vehicle, ammoList, imageMap);
	}
	
	
	
	public static void openNew(BallisticData dataBall) {

		Logger.get().info("Navigate to 'CalibrationEditor' (" + Workflow.toString(Workflow.steps) + ")  data=" + dataBall + "; vehicle="+dataBall.vehicle.name);

		int width = Config.app_window_size.x;
		int height = Config.app_window_size.y;
		
		boolean styleDark = "dark".equals(Config.app_style);
		Object[] sceneObjects = FXUtils.openFXScene(null, "/ui/layout_calibration"+(styleDark?"_dark":"")+".fxml", width, height, "Create Ballistic Data");
		UICalibrationEditor controller = (UICalibrationEditor)sceneObjects[0];
		Stage stage = (Stage)sceneObjects[1];
		
		controller.create(stage, dataBall);
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
		
		this.dataBallistic = new BallisticData();
		this.dataBallistic.vehicle = vehicle;
		
		try {
			for(int i=0; i<ammoList.size(); i++) {
				Ammo ammo  = ammoList.get(i);
				BallisticElement e = new BallisticElement();
				e.ammunition.add(ammo);
				File file = imageMap.get(ammo);
				BufferedImage img = ImageIO.read(file);
				this.dataBallistic.images.put(e, img);
			}
			
		} catch (IOException e) {
			Logger.get().error(e);
		}
		
		create();
	}
	
	
	
	
	public void create(Stage stage, BallisticData dataBallistic) {
		this.stage = stage;
		this.dataBallistic = dataBallistic;
		create();
		
	}


	
	
	private void create() {
		
		// CANVAS
		wtCanvas = new WTCanvas(paneCanvas, false) {
			@Override public void onMouseMoved() {
				wtCanvas.repaint();
			}
			@Override public void onMouseDragged() {
				wtCanvas.repaint();
			}
			@Override public void onMousePressed(MouseButton btn) {
				wtCanvas.repaint();
			}
			@Override public void onMouseReleased(MouseButton btn) {
				if(btn == MouseButton.PRIMARY) {
					onAddMarker(wtCanvas.cursorPosition.y);
					wtCanvas.repaint();
				}
			}
			@Override public void onKeyReleased(KeyCode code) {
				onDeleteMarkerRequest(wtCanvas.cursorPosition.x, wtCanvas.cursorPosition.y);
				wtCanvas.repaint();
			}
			@Override public void onRepaint(GraphicsContext g) {
				repaintCanvas(g);
			};
			@Override public void onRepaintOverlay(GraphicsContext g) {
				repaintOverlayCanvas(g);
			};
		};
		
		// NAME LABEL
		labelVehicleName.setText(dataBallistic.vehicle.namePretty);
		
		// AMMO CHOICE
		FXUtils.initComboboxBallisticElement(choiceAmmo);
		if(dataBallistic.elements.isEmpty()) {
			Logger.get().error("No Ballistic elements available!");
		} else {
			for(BallisticElement elementBall : dataBallistic.elements) {
				choiceAmmo.getItems().add(elementBall);
			}
		}
		
		choiceAmmo.getSelectionModel().select(0);
		choiceAmmo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BallisticElement>() {
			@Override
			public void changed(ObservableValue<? extends BallisticElement> observable, BallisticElement oldValue, BallisticElement newValue) {
				onAmmoSelected(newValue);
			}
		});
		onAmmoSelected(choiceAmmo.getSelectionModel().getSelectedItem());
	}
	
	
	
	
	void onAmmoSelected(BallisticElement element) {
		
		Logger.get().debug("Ballistic Element '" + element + "'");
		
		if(element == null) {
			currentImage = null;
			elementBallistic = null;
			cbZoomedIn.setDisable(true);
			wtCanvas.rebuildCanvas(1920, 1080);
			
		} else {
			
			if(imageCache.containsKey(element)) {
				currentImage = imageCache.get(element);
				Logger.get().debug("Image retrieved from cache");
			} else {
				BufferedImage bufImg = dataBallistic.images.get(element);
				currentImage = SwingFXUtils.toFXImage(bufImg, null);
				imageCache.put(element, currentImage);
			}
			
			this.elementBallistic = element;
			
			Logger.get().debug("Ballistic Element selected: " + this.elementBallistic);
			
			cbZoomedIn.setDisable(false);
			cbZoomedIn.setSelected( dataBallistic.zoomedIn.containsKey(elementBallistic) ? dataBallistic.zoomedIn.get(elementBallistic) : false);
			updateMarkerList();
			wtCanvas.rebuildCanvas(currentImage);
		}
		
	}
	
	
	
	
	private void updateMarkerList() {
		if(this.elementBallistic == null || this.elementBallistic.markerData == null) {
			return;
		}
		
		Logger.get().debug("Updating Marker List - " + this.elementBallistic.markerData.markers.size());
		
		for(int i=0; i<this.elementBallistic.markerData.markers.size(); i++) {
			Marker marker = this.elementBallistic.markerData.markers.get(i);
			marker.id = i+1;
		}
		
		boxRanges.getChildren().clear();
		boxRanges.getChildren().clear();
		
		for(int i=0; i<this.elementBallistic.markerData.markers.size(); i++) {
			Marker marker = this.elementBallistic.markerData.markers.get(i);
			
			HBox boxMarker = new HBox();
			boxMarker.setMinSize(0, 31);
			boxMarker.setPrefSize(10000, 31);
			boxMarker.setMaxSize(10000, 31);

			Label label = new Label(marker.id + ":");
			label.setAlignment(Pos.CENTER);
			label.setMinSize(31, 31);
			label.setPrefSize(31, 31);
			label.setMaxSize(31, 31);
			
			Spinner<Integer> spinner = new Spinner<Integer>();
			spinner.setMinSize(0, 31);
			spinner.setPrefSize(10000, 31);
			spinner.setMaxSize(10000, 31);
			spinner.setEditable(true);
			FXUtils.initSpinner(spinner, marker.distMeters, 0, 3200, 200, 0, new ChangeListener<Integer>() {
				@Override public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
					marker.distMeters = newValue;
					MarkerData dataMarker = elementBallistic.markerData;
					if(dataMarker.markers.size() >= 3) {
						elementBallistic.function = new DefaultBallisticFuntion(dataMarker.markers);
					} else {
						elementBallistic.function = new NullBallisticFunction();
					}
					wtCanvas.repaint();
				}
			});
			
			Button btnRemove = new Button("X");
			btnRemove.setMinSize(31, 31);
			btnRemove.setPrefSize(31, 31);
			btnRemove.setMaxSize(31, 31);
			btnRemove.setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent event) {
					deleteMarker(marker);
				}
			});
			
			boxMarker.getChildren().addAll(label, spinner, btnRemove);
			
			boxRanges.getChildren().add(boxMarker);
			VBox.setVgrow(spinner, Priority.ALWAYS);
		}
		
	}
	
	
	
	
	private void repaintCanvas(GraphicsContext g) {
		
		if(wtCanvas != null) {

			g.setFill(Color.LIGHTGRAY);
			g.fillRect(0, 0, wtCanvas.getWidth(), wtCanvas.getHeight());
			
			if(currentImage != null) {
				g.drawImage(currentImage, 0, 0, wtCanvas.getWidth(), wtCanvas.getHeight());
			}
			
			
			if(this.elementBallistic != null && this.elementBallistic.markerData != null) {
				
				MarkerData dataMarker = this.elementBallistic.markerData;

				// draw approx. ball. indicators
				g.setStroke(Color.RED);
				IBallisticFunction func = elementBallistic.function;
				for(int d=200; d<=2800; d+=200) {
					double p = func.eval(d);
					float x = (float) (wtCanvas.getWidth()/2) - 20;
					float y = (float) (dataMarker.yPosCenter + p);
					g.strokeLine(x-6, y, x+6, y);
				}
				
			}
			
			repaintOverlayCanvas(wtCanvas.canvasOverlay.getGraphicsContext2D());
			
		}
		
	}
	
	
	
	private void repaintOverlayCanvas(GraphicsContext g) {

		if(wtCanvas == null) {
			return;
		}
		
		// CURSOR
		if(wtCanvas.cursorVisible) {
			
			g.setLineWidth(2);
			g.setStroke(Color.RED);
			
			Point2D pc0 = wtCanvas.transformToOverlay(wtCanvas.getWidth()/2, wtCanvas.cursorPosition.y-4);
			Point2D pc1 = wtCanvas.transformToOverlay(wtCanvas.getWidth()/2, wtCanvas.cursorPosition.y+4);
			g.strokeLine(pc0.getX(), pc0.getY(), pc1.getX(), pc1.getY());
		
			Point2D pl0 = wtCanvas.transformToOverlay(-3, wtCanvas.cursorPosition.y);
			Point2D pl1 = wtCanvas.transformToOverlay(wtCanvas.getWidth(), wtCanvas.cursorPosition.y);

			g.setLineDashes(6);
			g.strokeLine(pl0.getX(), pl0.getY(), pl1.getX(), pl1.getY());
			g.setLineDashes(null);
		}
		
		
		if(this.elementBallistic != null && this.elementBallistic.markerData != null) {
			
			if(this.elementBallistic != null && this.elementBallistic.markerData != null) {
				
				MarkerData dataMarker = this.elementBallistic.markerData;
				
				// find selected marker
				double minDist = Integer.MAX_VALUE;
				Marker minMarker = null;
				Vector2d tmp = new Vector2d();
				
				for(int i=0; i<dataMarker.markers.size(); i++) {
					Marker marker = dataMarker.markers.get(i);
					double dist = tmp.set(wtCanvas.cursorPosition.getIntX(), marker.yPos+dataMarker.yPosCenter).dist(wtCanvas.cursorPosition.getIntX(), wtCanvas.cursorPosition.getIntY());
					if(dist < minDist) {
						minDist = dist;
						minMarker = marker;
					}
				}
				
				// draw markers
				for(int i=0; i<dataMarker.markers.size(); i++) {
					Marker marker = dataMarker.markers.get(i);
					if(marker == minMarker) {
						g.setFill(Color.YELLOW);
						g.setStroke(Color.YELLOW);
					} else {
						g.setFill(Color.DEEPPINK);
						g.setStroke(Color.DEEPPINK);			
					}
					
					double scale = wtCanvas.canvas.getScaleX();
					double mx = currentImage.getWidth()/2;
					double my = marker.yPos + dataMarker.yPosCenter;
					Point2D p = wtCanvas.transformToOverlay(mx, my);
					
					g.setFont(font);
					g.fillText(""+marker.id, p.getX()+6*scale, p.getY());
					
					g.setLineWidth(Math.max(1, scale));
					g.strokeLine(p.getX()-3*scale, p.getY()-3*scale, p.getX()+3*scale, p.getY()+3*scale);
					g.strokeLine(p.getX()+3*scale, p.getY()-3*scale, p.getX()-3*scale, p.getY()+3*scale);
					g.setLineWidth(1);
					
				}
				
				
			}
		
		}
		
		
	}
	
	
	
	@FXML
	void onZoomedIn(ActionEvent event) {
		if(elementBallistic == null) {
			return;
		}
		dataBallistic.zoomedIn.put(elementBallistic, cbZoomedIn.isSelected());
		wtCanvas.repaint();
	}
	
	
	
	
	private void onAddMarker(double y) {
		if(this.elementBallistic == null) {
			return;
		}
		if(this.elementBallistic.markerData == null) {
			this.elementBallistic.markerData = new MarkerData();
		}
		MarkerData dataMarker = this.elementBallistic.markerData;
		double mc = dataMarker.yPosCenter;
		
		if(dataMarker.markers.size() == 0) {
			Marker marker = new Marker(200, y-mc);
			marker.id = dataMarker.markers.size()+1;
			dataMarker.markers.add(marker);
		} else {
			Marker marker = new Marker(dataMarker.markers.get(dataMarker.markers.size()-1).distMeters+200, y-mc);
			marker.id = dataMarker.markers.size()+1;
			dataMarker.markers.add(marker);
		}
		
		if(dataMarker.markers.size() >= 3) {
			this.elementBallistic.function = new DefaultBallisticFuntion(dataMarker.markers);
		} else {
			this.elementBallistic.function = new NullBallisticFunction();
		}
		
		Logger.get().debug("Adding marker at y=" + (int)(y-mc) );
		updateMarkerList();
		wtCanvas.repaint();
	}

	
	
	
	private void onDeleteMarkerRequest(double x, double y) {
		if(this.elementBallistic == null || this.elementBallistic.markerData == null) {
			return;
		}
		
		MarkerData dataMarker = this.elementBallistic.markerData;
		
		// find selected marker
		double minDist = Integer.MAX_VALUE;
		Marker minMarker = null;
		Vector2d tmp = new Vector2d();
		
		for(int i=0; i<dataMarker.markers.size(); i++) {
			Marker marker = dataMarker.markers.get(i);
			double dist = tmp.set(wtCanvas.cursorPosition.getIntX(), marker.yPos+dataMarker.yPosCenter).dist(x, y);
			if(dist < minDist) {
				minDist = dist;
				minMarker = marker;
			}
		}
		
		deleteMarker(minMarker);
		
	}
	
	
	
	
	private void deleteMarker(Marker marker) {
		if(marker != null && this.elementBallistic.markerData != null) {
			MarkerData dataMarker = this.elementBallistic.markerData;
			dataMarker.markers.remove(marker);
			if(dataMarker.markers.size() >= 3) {
				this.elementBallistic.function = new DefaultBallisticFuntion(dataMarker.markers);
			} else {
				this.elementBallistic.function = new NullBallisticFunction();
			}
			Logger.get().debug("Deleted marker " + marker );
		}
		updateMarkerList();
		wtCanvas.repaint();
	}
	
	
	
	void onSetMarkerRange(int id, int distance) {
		if(this.elementBallistic == null || this.elementBallistic.markerData == null) {
			return;
		}
		
		MarkerData dataMarker = this.elementBallistic.markerData;
		
		for(Marker m : dataMarker.markers) {
			if(m.id == id) {
				m.distMeters = distance;
			}
		}
		
		if(dataMarker.markers.size() >= 3) {
			this.elementBallistic.function = new DefaultBallisticFuntion(dataMarker.markers);
		} else {
			this.elementBallistic.function = new NullBallisticFunction();
		}
		
		Logger.get("Set marker range: id="+id + " to " + distance + "m");
		
		wtCanvas.repaint();
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
			if(!DataWriter.saveExternalBallisticFile(this.dataBallistic, file)) {
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
			UISightEditor.openNew(dataBallistic);

		} else {
			Workflow.steps.add(Step.EDIT_CALIBRATION);
			SightData dataSight = DataLoader.loadSight(fileSight, dataBallistic);
			UISightEditor.openNew(dataBallistic, dataSight);
		}
		
	}
	
	

	
	private boolean isValidMarkers() {
		
		// for each element
		for(BallisticElement element : this.dataBallistic.elements) {
			
			// needs markers ?
			boolean needsMarker = false;
			for(Ammo ammo : element.ammunition) {
				if(ammo.parentWeapon != null && ammo.parentWeapon.triggerGroup.isOr(TriggerGroup.PRIMARY, TriggerGroup.SECONDARY, TriggerGroup.COAXIAL, TriggerGroup.MACHINEGUN)) {
					needsMarker = true;
					break;
				}
			}
			
			// has markers ?
			if(needsMarker) {
				if(element.markerData == null || element.markerData.markers.size() < 3) {
					return false;
				}
			}
			
		}
		
		return true;
	}
	

}
