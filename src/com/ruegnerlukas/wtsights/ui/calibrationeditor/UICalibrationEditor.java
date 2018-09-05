package com.ruegnerlukas.wtsights.ui.calibrationeditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
import com.ruegnerlukas.wtsights.ui.sighteditor.UISightEditor;
import com.ruegnerlukas.wtutils.Config2;
import com.ruegnerlukas.wtutils.FXUtils;
import com.ruegnerlukas.wtutils.SightUtils;
import com.ruegnerlukas.wtutils.canvas.WTCanvas;
import com.ruegnerlukas.wtutils.canvas.pin.TextPin;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
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
	private WTCanvas wtCanvas;
	private Font font = new Font("Arial", Font.PLAIN, 10);


	private File fileSight;
	
	private CalibrationData dataCalib;
	private CalibrationAmmoData currentAmmoData;
	
	
	
	
	
	
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

		// canvas
		wtCanvas = new WTCanvas(paneCanvas, 1920, 1080) {
			@Override
			public void onMouseMoved() {
				repaintCanvas();
			}
			@Override
			public void onMouseDragged() {
				repaintCanvas();
			}
			@Override
			public void onMousePressed(MouseButton btn) {
				repaintCanvas();
			}
			@Override
			public void onMouseReleased(MouseButton btn) {
				onAddMarker(wtCanvas.getCursorY());
				repaintCanvas();
			}
		};
		stage.getScene().setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.BACK_SPACE && wtCanvas.isCursorVisible()) {
					event.consume();
					onDeleteMarkerRequest(wtCanvas.getCursorY(), wtCanvas.getCursorY());
					repaintCanvas();
				}
			}
		});
		
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
			currentAmmoData = null;
			cbZoomedIn.setDisable(true);
			wtCanvas.resizeRendertarget(1280, 720);
			repaintCanvas();
			
		} else {
			
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
			
			BufferedImage currentImage = dataCalib.images.get("image_"+currentAmmoData.ammo.name);
			wtCanvas.resizeRendertarget(currentImage.getWidth(), currentImage.getHeight());
			repaintCanvas();
		}
		
	}
	
	
	
	private void updateMarkerPins() {
		
		// remove all
		for(int i=0; i<currentAmmoData.markerRanges.size()+10; i++) {
			wtCanvas.getPinboard().removePin("marker_"+i);
		}
		
		for(int i=0; i<currentAmmoData.markerRanges.size(); i++) {
			double y = currentAmmoData.markerRanges.get(i).x + currentAmmoData.markerCenter.x;
			TextPin pin = new TextPin("marker_" + (i+1) );
			pin.text = ""+(i+1);
			pin.position.set(wtCanvas.getWidth()/2+7, (int)y);
			pin.color = Color.MAGENTA;
			pin.align = TextPin.Align.LEFT_CENTER;
			wtCanvas.getPinboard().addPin(pin);
		}
		
	}
	
	
	private void updateRangeList() {
		
		Logger.get().debug("Updating Range List " + currentAmmoData.markerRanges);
		
		boxRanges.getChildren().clear();
		
		if(currentAmmoData != null) {
			
			for(int i=0; i<currentAmmoData.markerRanges.size(); i++) {
				
				Vector2i marker = currentAmmoData.markerRanges.get(i);
				
				HBox boxMarker = new HBox();
				boxMarker.setMinSize(0, 31);
				boxMarker.setPrefSize(10000, 31);
				boxMarker.setMaxSize(10000, 31);

				Label label = new Label((i+1) + ":");
				label.setAlignment(Pos.CENTER);
				label.setMinSize(31, 31);
				label.setPrefSize(31, 31);
				label.setMaxSize(31, 31);
				
				Spinner<Integer> spinner = new Spinner<Integer>();
				spinner.setMinSize(0, 31);
				spinner.setPrefSize(10000, 31);
				spinner.setMaxSize(10000, 31);
				FXUtils.initSpinner(spinner, marker.y, 0, 3200, 200, 0, new ChangeListener<Integer>() {
					@Override public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
						marker.y = newValue;
						repaintCanvas();
					}
				});
				
				boxMarker.getChildren().addAll(label, spinner);
				
				boxRanges.getChildren().add(boxMarker);
				VBox.setVgrow(spinner, Priority.ALWAYS);
			}
			
		}
		
	}
	
	
	
	final static BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{5.0f}, 0.0f);
	final static Color DARK_RED = new Color(150, 0, 0);
	
	private void repaintCanvas() {
		
		Graphics2D g = wtCanvas.getGraphics();
		
		// draw background
		BufferedImage currentImage = dataCalib.images.get("image_"+currentAmmoData.ammo.name);
		if(currentAmmoData != null) {
			g.drawImage(currentImage, 0, 0, null);
		} else {
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(0, 0, wtCanvas.getWidth(), wtCanvas.getHeight());
		}
		
		// draw cursor
		Vector2i cpos = wtCanvas.getCursorPosition();
		if(wtCanvas.isCursorVisible()) {
			g.setColor(DARK_RED);
			Stroke defaultStroke = g.getStroke();
			g.setStroke(dashed);
			g.drawLine(-2, cpos.y, wtCanvas.getWidth(), cpos.y);
			g.setStroke(defaultStroke);
			g.setColor(Color.RED);
			g.drawLine(wtCanvas.getWidth()/2, cpos.y+3, wtCanvas.getWidth()/2, cpos.y-3);
		}
		

		if(currentAmmoData != null && currentImage != null) {
			
			List<Vector2i> allMarkers = currentAmmoData.markerRanges;
			
			int mc = currentAmmoData.markerCenter.x;
			
			double minDist = Integer.MAX_VALUE;
			Vector2i minMarker = null;
			Vector2i tmp = new Vector2i();
			
			for(Vector2i m : currentAmmoData.markerRanges) {
				double dist = tmp.set(cpos.getIntX(), m.x+mc).dist(cpos.getIntX(), cpos.getIntY());
				if(dist < minDist) {
					minDist = dist;
					minMarker = m;
				}
			}
			
			
			for (Vector2i marker : allMarkers) {
				
				if (marker == minMarker) {
					g.setColor(Color.YELLOW);
				} else {
					g.setColor(Color.MAGENTA);
				}
				
				int mx = currentImage.getWidth()/2;
				int my = marker.x + mc;
				
				g.drawLine(mx-3, my-3, mx+3, my+3);
				g.drawLine(mx+3, my-3, mx-3, my+3);
				

				Font fontSaved = g.getFont();
				g.setFont(font);
//				g.drawString(""+(allMarkers.indexOf(marker)+1), mx + 5, my);
				g.setFont(fontSaved);
				
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
						int x = (int) (wtCanvas.getWidth()/2) - 15;
						int y = (int) (currentAmmoData.markerCenter.x + resultPX);
						g.setColor(Color.RED);
						g.drawLine(x-4, y, x+4, y);
					}
				}
				
				
			}
			
		}
		
		
		wtCanvas.repaint();
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
		Vector2i vec = null;
		if(currentAmmoData.markerRanges.size() == 0) {
			vec = new Vector2i( (int)y-mc, 200);
		} else {
			vec = new Vector2i( (int)y-mc, currentAmmoData.markerRanges.get(currentAmmoData.markerRanges.size()-1).y+200);
		}
		currentAmmoData.markerRanges.add(vec);
		
		updateMarkerPins();
		
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
		
		updateMarkerPins();
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
