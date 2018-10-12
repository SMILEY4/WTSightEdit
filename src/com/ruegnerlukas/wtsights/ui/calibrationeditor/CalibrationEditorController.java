package com.ruegnerlukas.wtsights.ui.calibrationeditor;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2f;
import com.ruegnerlukas.simplemath.vectors.vec3.Vector3d;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticData;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticElement;
import com.ruegnerlukas.wtsights.data.ballisticdata.Marker;
import com.ruegnerlukas.wtsights.data.vehicle.Vehicle;
import com.ruegnerlukas.wtsights.ui.AmmoIcons;
import com.ruegnerlukas.wtsights.ui.view.IViewController;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.ParamKey;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;
import com.ruegnerlukas.wtutils.FXUtils;
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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;

public class CalibrationEditorController implements IViewController {

	@FXML private ResourceBundle resources;
	@FXML private URL location;

	@FXML private Label labelVehicleName;
	@FXML private ComboBox<Object> choiceImage;
	@FXML private CheckBox cbZoomedIn;
	@FXML private Spinner<Double> spinnerZoomMod;
	@FXML private VBox boxRanges;
	@FXML private Label labelInfo;

	@FXML private Spinner<Double> spinnerZoomModOut;
	@FXML private Spinner<Double> spinnerZoomModIn;
	
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
			@SuppressWarnings("unchecked") Map<BallisticElement, File> imagesBallistic = (Map<BallisticElement, File>) parameters.get(ParamKey.BALLISTIC_IMAGES_MAP);
			File[] imagesZoom = (File[]) parameters.get(ParamKey.BALLISTIC_IMAGES_ZOOM);
			service.initNewBallisticData(vehicle, imagesBallistic, imagesZoom);
		}
		
		// zoom mod
		FXUtils.initSpinner(spinnerZoomModOut, service.getZoomModifier(false), 0.00000001, 10, 0.001, 3, true, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				service.setZoomModifier(newValue.doubleValue(), false);
				wtCanvas.repaint();
			}
		});
		FXUtils.initSpinner(spinnerZoomModIn, service.getZoomModifier(true), 0.00000001, 10, 0.001, 3, true, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				service.setZoomModifier(newValue.doubleValue(), true);
				wtCanvas.repaint();
			}
		});

		
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
		
		// IMAGE/ELEMENT CHOICE
		choiceImage.setButtonCell(new ListCell<Object>() {
			@Override protected void updateItem(Object item, boolean empty) {
				super.updateItem(item, empty);
				if(item instanceof BallisticElement) {
					BallisticElement element = (BallisticElement) item;
					if (element == null || empty || element.ammunition.isEmpty()) {
						setText("");
						setGraphic(null);
					} else {
						ImageView imgView = new ImageView(SwingFXUtils.toFXImage(AmmoIcons.getIcon(element.ammunition.get(0).type), null));
						imgView.setSmooth(true);
						imgView.setPreserveRatio(true);
						imgView.setFitHeight(40);
						setGraphic(imgView);
						String displayName = "";
						for(int i=0; i<element.ammunition.size(); i++) {
							if(i == element.ammunition.size()-1) {
								displayName += element.ammunition.get(i).namePretty + "";
							} else {
								displayName += element.ammunition.get(i).namePretty + ", ";
							}
						}
						setText(displayName);
					}
				} else if(item instanceof Boolean) {
					boolean zoomedIn = (Boolean) item;
					if(zoomedIn) {
						setText(ViewManager.getResources().getString("ce_img_zoomed_in"));
					} else {
						setText(ViewManager.getResources().getString("ce_img_zoomed_out"));
					}
					setGraphic(null);
				}
				
			}
		});
		choiceImage.setCellFactory(new Callback<ListView<Object>, ListCell<Object>>() {
			@Override public ListCell<Object> call(ListView<Object> p) {
				return new ListCell<Object>() {
					@Override protected void updateItem(Object item, boolean empty) {
						super.updateItem(item, empty);
						if(item instanceof BallisticElement) {
							BallisticElement element = (BallisticElement) item;
							if (element == null || empty || element.ammunition.isEmpty()) {
								setText("");
								setGraphic(null);
							} else {
								ImageView imgView = new ImageView(SwingFXUtils.toFXImage(AmmoIcons.getIcon(element.ammunition.get(0).type), null));
								imgView.setSmooth(true);
								imgView.setPreserveRatio(true);
								imgView.setFitHeight(40);
								setGraphic(imgView);
								String displayName = "";
								for(int i=0; i<element.ammunition.size(); i++) {
									if(i == element.ammunition.size()-1) {
										displayName += element.ammunition.get(i).namePretty + "";
									} else {
										displayName += element.ammunition.get(i).namePretty + ", ";
									}
								}
								setText(displayName);
							}
						} else if(item instanceof Boolean) {
							boolean zoomedIn = (Boolean) item;
							if(zoomedIn) {
								setText(ViewManager.getResources().getString("ce_img_zoomed_in"));
							} else {
								setText(ViewManager.getResources().getString("ce_img_zoomed_out"));
							}
							setGraphic(null);
						}

					}
				};
			}
		});
		if(service.getSelectableObjects().isEmpty()) {
			Logger.get().error("No objects available!");
		} else {
			choiceImage.getItems().addAll(service.getSelectableObjects());
		}
		
		choiceImage.getSelectionModel().select(0);
		choiceImage.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
				onElementSelected(newValue);
			}
		});
		onElementSelected(choiceImage.getSelectionModel().getSelectedItem());
	
	}
	
	
	
	
	void onElementSelected(Object obj) {
		service.selectElement(obj);
		
		if(obj instanceof BallisticElement ) {
			BallisticElement element = (BallisticElement) obj;

			if(element == null || (element != null && element.isRocketElement)) {
				cbZoomedIn.setSelected(false);
				cbZoomedIn.setDisable(true);
			} else {
				cbZoomedIn.setDisable(false);	
				cbZoomedIn.setSelected(service.isZoomedIn());
			}
			
		} else if(obj instanceof Boolean) {
			boolean zoomedIn = (Boolean) obj;
			cbZoomedIn.setSelected(zoomedIn);
			cbZoomedIn.setDisable(true);
		}
		
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
			
			// background image
			if(service.getCurrentImage() != null) {
				g.drawImage(service.getCurrentImage(), 0, 0, wtCanvas.getWidth(), wtCanvas.getHeight());
			}
			
			// center lines
			g.setStroke(new Color(1.0, 0.0, 0.0, 0.6));
			g.strokeLine(0, wtCanvas.getHeight()/2, wtCanvas.getWidth(), wtCanvas.getHeight()/2);
			g.strokeLine(wtCanvas.getWidth()/2, 0, wtCanvas.getWidth()/2, wtCanvas.getHeight());

			// horz indicators
			g.setStroke(new Color(1.0, 0.0, 0.0, 0.6));
			for(Vector3d indicator : service.getHorzRangeIndicators(wtCanvas.getWidth(), wtCanvas.getHeight())) {
				final double length = indicator.z > 0 ? 40 : 30;
				g.strokeLine(indicator.x, indicator.y-length/2, indicator.x, indicator.y+length/2);
			}
			
			// range indicators
			g.setStroke(Color.RED);
			List<Vector2f> indicators = service.getApproxRangeIndicators(wtCanvas.getWidth());
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
