package com.ruegnerlukas.wtsights.ui.calibrationeditor;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2f;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticData;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticElement;
import com.ruegnerlukas.wtsights.data.ballisticdata.Marker;
import com.ruegnerlukas.wtsights.data.vehicle.Vehicle;
import com.ruegnerlukas.wtsights.ui.view.IViewController;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.ParamKey;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;
import com.ruegnerlukas.wtutils.FXUtils;
import com.ruegnerlukas.wtutils.canvas.WTCanvas;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class CalibrationEditorController implements IViewController {

	@FXML private ResourceBundle resources;
	@FXML private URL location;

	@FXML private Label labelVehicleName;
	@FXML private ComboBox<BallisticElement> choiceAmmo;
	@FXML private CheckBox cbZoomedIn;
	@FXML private VBox boxRanges;
	@FXML private Label labelInfo;

	private CalibrationEditorService service;
	
	
	@FXML private AnchorPane paneCanvas;
	private WTCanvas wtCanvas;
	private Font font = new Font("Arial", 18);
	
	
	
	
	@Override
	public void create(Map<ParamKey,Object> parameters) {
		
		this.service = (CalibrationEditorService) ViewManager.getService(View.CALIBRATION_EDITOR);
	
		// file sight
		if(parameters.containsKey(ParamKey.FILE_SIGHT)) {
			service.initFileSight((File) parameters.get(ParamKey.FILE_SIGHT));
		} else {
			service.initFileSight(null);
		}

		// ballistic data
		if(parameters.containsKey(ParamKey.BALLISTIC_DATA)) {
			service.initNewBallisticData((BallisticData) parameters.get(ParamKey.BALLISTIC_DATA));
		} else {
			Vehicle vehicle = (Vehicle) parameters.get(ParamKey.SELECTED_VEHICLE);
			@SuppressWarnings("unchecked") List<BallisticElement> dataList = (List<BallisticElement>) parameters.get(ParamKey.LIST_BALLISTIC_ELEMENTS);
			@SuppressWarnings("unchecked") Map<BallisticElement, File> imageMap = (Map<BallisticElement, File>) parameters.get(ParamKey.MAP_IMAGES);
			service.initNewBallisticData(vehicle, dataList, imageMap);
		}
		
		
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
				if(code == KeyCode.BACK_SPACE || code == KeyCode.DELETE) {
					onDeleteMarkerRequest(wtCanvas.cursorPosition.x, wtCanvas.cursorPosition.y);
					wtCanvas.repaint();
				}
			}
			@Override public void onRepaint(GraphicsContext g) {
				repaintCanvas(g);
			};
			@Override public void onRepaintOverlay(GraphicsContext g) {
				repaintOverlayCanvas(g);
			};
		};
		
		// NAME LABEL
		labelVehicleName.setText(service.getVehicleName());
		
		// AMMO CHOICE
		FXUtils.initComboboxBallisticElement(choiceAmmo);
		if(service.getBallisticElements(false).isEmpty()) {
			Logger.get().error("No Ballistic elements available!");
		} else {
			choiceAmmo.getItems().addAll(service.getBallisticElements(false));
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
		service.selectElement(element);
		cbZoomedIn.setSelected(service.isZoomedIn());
		if(service.getCurrentImage() != null) {
			wtCanvas.rebuildCanvas(service.getCurrentImage());
		} else {
			wtCanvas.rebuildCanvas(1920, 1080);
		}
	}
	
	
	
	
	private void updateMarkerList() {
		
		Logger.get().debug("Updating Marker List - " + service.getMarkers(false).size());
		
		
		boxRanges.getChildren().clear();
		boxRanges.getChildren().clear();
		
		for(int i=0; i<service.getMarkers(false).size(); i++) {
			Marker marker = service.getMarkers(false).get(i);
			
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
					service.editMarkerRange(marker, newValue.intValue());
					wtCanvas.repaint();
				}
			});
			
			Button btnRemove = new Button("X");
			btnRemove.setMinSize(31, 31);
			btnRemove.setPrefSize(31, 31);
			btnRemove.setMaxSize(31, 31);
			btnRemove.setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent event) {
					service.deleteMarker(marker);
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
			
			if(service.getCurrentImage() != null) {
				g.drawImage(service.getCurrentImage(), 0, 0, wtCanvas.getWidth(), wtCanvas.getHeight());
			}
			
			
			List<Vector2f> indicators = service.getApproxRangeIndicators(wtCanvas.getWidth());
			g.setStroke(Color.RED);
			for(int i=0; i<indicators.size(); i++) {
				Vector2f indicator = indicators.get(i);
				g.strokeLine(indicator.x-6, indicator.y, indicator.x+6, indicator.y);
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
		
		// MARKERS
		Marker selectedMarker = service.getSelectedMarker(wtCanvas.cursorPosition.getIntX(), wtCanvas.cursorPosition.getIntY());
		for(int i=0; i<service.getMarkers().size(); i++) {
			Marker marker = service.getMarkers().get(i);
			
			if(marker == selectedMarker) {
				g.setFill(Color.YELLOW);
				g.setStroke(Color.YELLOW);
			} else {
				g.setFill(Color.DEEPPINK);
				g.setStroke(Color.DEEPPINK);			
			}
			
			double scale = wtCanvas.canvas.getScaleX();
			double mx = service.getCurrentImage() != null ? service.getCurrentImage().getWidth()/2 : 1280/2;
			double my = marker.yPos + service.getCenterMarkerY();
			Point2D p = wtCanvas.transformToOverlay(mx, my);
			
			g.setFont(font);
			g.fillText(""+marker.id, p.getX()+6*scale, p.getY());
			
			g.setLineWidth(Math.max(1, scale));
			g.strokeLine(p.getX()-3*scale, p.getY()-3*scale, p.getX()+3*scale, p.getY()+3*scale);
			g.strokeLine(p.getX()+3*scale, p.getY()-3*scale, p.getX()-3*scale, p.getY()+3*scale);
			g.setLineWidth(1);
			
		}
		
	}
	
	
	
	@FXML
	void onZoomedIn(ActionEvent event) {
		service.setZoomedIn(cbZoomedIn.isSelected());
		wtCanvas.repaint();
	}
	
	
	
	
	private void onAddMarker(double y) {
		service.addMarker(y);
		Logger.get().debug("Adding marker at y=" + (int)(y-service.getCenterMarkerY()) );
		updateMarkerList();
		wtCanvas.repaint();
	}

	
	
	
	private void onDeleteMarkerRequest(double x, double y) {
		service.deleteMarker(x, y);
		updateMarkerList();
		wtCanvas.repaint();
	}
	
	
	
	
	@FXML
	void onExport(ActionEvent event) {
		service.exportData();
	}
	
	
	

	@FXML
	void onEditSight(ActionEvent event) {
		service.editSight();
	}
	
	

	
	

}
