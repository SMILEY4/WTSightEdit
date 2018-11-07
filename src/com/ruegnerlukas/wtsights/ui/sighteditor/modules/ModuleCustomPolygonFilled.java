package com.ruegnerlukas.wtsights.ui.sighteditor.modules;

import java.util.ArrayList;
import java.util.List;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.sight.sightElements.BaseElement;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementCustomPolygonFilled;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.Movement;
import com.ruegnerlukas.wtsights.ui.sighteditor.SightEditorController;
import com.ruegnerlukas.wtsights.ui.sighteditor.StepSizes;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;
import com.ruegnerlukas.wtutils.Conversion;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ModuleCustomPolygonFilled implements Module {

	private ElementCustomPolygonFilled element;

	@FXML private CheckBox cbUseThousandth;
	@FXML private ChoiceBox<String> choiceMovement;
	@FXML private Spinner<Double> spinnerAngle;
	@FXML private CheckBox cbUseAutoCenter;
	@FXML private Spinner<Double> spinnerCenterX;
	@FXML private Spinner<Double> spinnerCenterY;
	@FXML private Spinner<Double> spinnerOriginX;
	@FXML private Spinner<Double> spinnerOriginY;
	@FXML private Spinner<Double> spinnerSpeed;
	
	@FXML private Spinner<Double> spinnerPosOffsetX;
	@FXML private Spinner<Double> spinnerPosOffsetY;
	
	@FXML private VBox boxVertices;
	private List<Vertex> vertices = new ArrayList<Vertex>();
	
	
	
	
	
	
	@Override
	public void create(DataPackage data) {
		
		// get element with default values
		ElementCustomPolygonFilled elementDefault = new ElementCustomPolygonFilled();
		
		cbUseThousandth.setSelected(elementDefault.useThousandth);
		cbUseThousandth.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				if(element != null) {
					element.useThousandth = cbUseThousandth.isSelected();
					if(element.useThousandth) {
						FXUtils.initSpinner(spinnerOriginX, Conversion.get().screenspace2mil(spinnerOriginX.getValue(), data.dataSight.envZoomedIn), -9999, 9999, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, null);
						FXUtils.initSpinner(spinnerOriginY, Conversion.get().screenspace2mil(spinnerOriginY.getValue(), data.dataSight.envZoomedIn), -9999, 9999, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, null);
						for(Vertex vertex : vertices) {
							FXUtils.initSpinner(vertex.spinnerX, Conversion.get().screenspace2mil(vertex.spinnerX.getValue(), data.dataSight.envZoomedIn), -9999, 9999, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, null);
							FXUtils.initSpinner(vertex.spinnerY, Conversion.get().screenspace2mil(vertex.spinnerY.getValue(), data.dataSight.envZoomedIn), -9999, 9999, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, null);
						}
						FXUtils.initSpinner(spinnerPosOffsetX, Conversion.get().screenspace2mil(spinnerPosOffsetX.getValue(), data.dataSight.envZoomedIn), -9999, 9999, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, null);
						FXUtils.initSpinner(spinnerPosOffsetY, Conversion.get().screenspace2mil(spinnerPosOffsetY.getValue(), data.dataSight.envZoomedIn), -9999, 9999, StepSizes.STEP_MIL, StepSizes.DECPLACES_MIL, null);
					} else {
						FXUtils.initSpinner(spinnerOriginX, Conversion.get().mil2screenspace(spinnerOriginX.getValue(), data.dataSight.envZoomedIn), -9999, 9999, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, null);
						FXUtils.initSpinner(spinnerOriginY, Conversion.get().mil2screenspace(spinnerOriginY.getValue(), data.dataSight.envZoomedIn), -9999, 9999, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, null);
						for(Vertex vertex : vertices) {
							FXUtils.initSpinner(vertex.spinnerY, Conversion.get().mil2screenspace(vertex.spinnerY.getValue(), data.dataSight.envZoomedIn), -9999, 9999, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, null);
							FXUtils.initSpinner(vertex.spinnerX, Conversion.get().mil2screenspace(vertex.spinnerX.getValue(), data.dataSight.envZoomedIn), -9999, 9999, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, null);
						}
						FXUtils.initSpinner(spinnerPosOffsetX, Conversion.get().mil2screenspace(spinnerPosOffsetX.getValue(),data.dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, null);
						FXUtils.initSpinner(spinnerPosOffsetY, Conversion.get().mil2screenspace(spinnerPosOffsetY.getValue(), data.dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, null);
					}
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		choiceMovement.getItems().addAll(Movement.STATIC.toString(), Movement.MOVE.toString(), Movement.MOVE_RADIAL.toString());
		choiceMovement.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(element != null) {
					element.movement = Movement.get(newValue);
					spinnerAngle.setDisable(element.movement != Movement.MOVE_RADIAL);
					spinnerCenterX.setDisable(element.movement != Movement.MOVE_RADIAL);
					spinnerCenterY.setDisable(element.movement != Movement.MOVE_RADIAL);
					spinnerOriginX.setDisable(element.movement != Movement.MOVE_RADIAL);
					spinnerOriginY.setDisable(element.movement != Movement.MOVE_RADIAL);
					spinnerSpeed.setDisable(element.movement != Movement.MOVE_RADIAL);
					cbUseAutoCenter.setDisable(element.movement != Movement.MOVE_RADIAL);
					spinnerCenterX.setDisable(elementDefault.autoCenter);
					spinnerCenterY.setDisable(elementDefault.autoCenter);
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		choiceMovement.getSelectionModel().select(elementDefault.movement.toString());
		
		FXUtils.initSpinner(spinnerAngle, elementDefault.angle, -360, 360, StepSizes.STEP_ANGLE, StepSizes.DECPLACES_ANGLE, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.angle = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		cbUseAutoCenter.setSelected(elementDefault.autoCenter);
		cbUseAutoCenter.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				if(element != null) {
					element.autoCenter = cbUseAutoCenter.isSelected();
					spinnerCenterX.setDisable(element.autoCenter);
					spinnerCenterY.setDisable(element.autoCenter);
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		spinnerCenterX.setDisable(elementDefault.autoCenter);
		spinnerCenterY.setDisable(elementDefault.autoCenter);

		FXUtils.initSpinner(spinnerCenterX, elementDefault.center.x, -9999, 9999, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.center.x = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerCenterY, elementDefault.center.y, -9999, 9999, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.center.y = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerOriginX, elementDefault.radCenter.x, -9999, 9999, (elementDefault.useThousandth ? StepSizes.STEP_MIL : StepSizes.STEP_SCREENSPACE), (elementDefault.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE), new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.radCenter.x = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerOriginY, elementDefault.radCenter.y, -9999, 9999, (elementDefault.useThousandth ? StepSizes.STEP_MIL : StepSizes.STEP_SCREENSPACE), (elementDefault.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE), new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.radCenter.y = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerSpeed, elementDefault.speed, 0, 9999, StepSizes.STEP_SPEED, StepSizes.DECPLACES_SPEED, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.speed = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		FXUtils.initSpinner(spinnerPosOffsetX, elementDefault.positionOffset.x, -9999, 9999, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.positionOffset.x = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerPosOffsetY, elementDefault.positionOffset.y, -9999, 9999, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.positionOffset.y = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		
		setElement(null);
	}
	
	
	
	
	
	@FXML
	void onAddVertex(ActionEvent event) {
		vertices.add(new Vertex(0, 0));
		updateVertexList();
	}
	

	
	
	private void updateVertexList() {
		
		ElementCustomPolygonFilled element = this.element == null ? new ElementCustomPolygonFilled() : this.element;
		
		boxVertices.getChildren().clear();
		List<Vector2d> vecList = new ArrayList<Vector2d>();
		
		for(int i=0; i<vertices.size(); i++) {
			Vertex vertex = vertices.get(i);
			HBox boxVertex = buildVertexBox(element, vertex);
			boxVertices.getChildren().add(boxVertex);
			vecList.add(vertex);
		}
		
		element.setVertices(vecList);
		
		element.setDirty(true);
		((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
	}
	
	
	
	
	private HBox buildVertexBox(ElementCustomPolygonFilled element, Vertex vertex) {
		
		HBox box = new HBox();
		box.setMinSize(0, 32);
		box.setPrefSize(100000, 32);
		box.setMaxSize(HBox.USE_COMPUTED_SIZE, 32);
		vertex.box = box;
		
		Label labelIndex = new Label((vertices.indexOf(vertex)+1)+": ");
		labelIndex.setMinSize(32, 32);
		labelIndex.setPrefSize(32, 32);
		labelIndex.setMaxSize(32, 32);
		
		Spinner<Double> spinnerX = new Spinner<Double>();
		spinnerX.setMinSize(0, 32);
		spinnerX.setPrefSize(100000, 32);
		spinnerX.setMaxSize(100000, 32);
		spinnerX.setEditable(true);
		vertex.spinnerX = spinnerX;
		
		Spinner<Double> spinnerY = new Spinner<Double>();
		spinnerY.setMinSize(0, 32);
		spinnerY.setPrefSize(100000, 32);
		spinnerY.setMaxSize(100000, 32);	
		spinnerY.setEditable(true);
		vertex.spinnerY = spinnerY;
		
		FXUtils.initSpinner(spinnerX, vertex.x, -9999, 9999,
				(element.useThousandth ? StepSizes.STEP_MIL : StepSizes.STEP_SCREENSPACE), (element.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE),
				true, new ChangeListener<Double>() {
			@Override
			public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				vertex.x = newValue.doubleValue();
				element.getVertices().get(vertices.indexOf(vertex)).x = vertex.x;
				element.setDirty(true);
				((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
			}
		});
		
		FXUtils.initSpinner(spinnerY, vertex.y, -9999, 9999,
				(element.useThousandth ? StepSizes.STEP_MIL : StepSizes.STEP_SCREENSPACE), (element.useThousandth ? StepSizes.DECPLACES_MIL : StepSizes.DECPLACES_SCREENSPACE),
				true, new ChangeListener<Double>() {
			@Override
			public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				vertex.y = newValue.doubleValue();
				element.getVertices().get(vertices.indexOf(vertex)).y = vertex.y;
				element.setDirty(true);
				((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
			}
		});
		
		Button btnRemove = new Button();
		btnRemove.setText(ViewManager.getResources().getString("se_mcpo_remove_vertex"));
		btnRemove.setMinSize(32, 32);
		btnRemove.setPrefSize(32, 32);
		btnRemove.setMaxSize(32, 32);	
		btnRemove.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				vertices.remove(vertex);
				updateVertexList();
			}
		});
		
		box.getChildren().addAll(labelIndex, spinnerX, spinnerY, btnRemove);
		
		return box;
	}

	
	
	
	public void setElement(BaseElement e) {
		if(e == null || e.type != ElementType.CUSTOM_POLY_FILLED) {
			this.element = null;
		} else {
			this.element = (ElementCustomPolygonFilled)e;
		}
		if(this.element != null) {
			cbUseThousandth.setSelected(element.useThousandth);
			choiceMovement.getSelectionModel().select(element.movement.toString());
			spinnerAngle.getValueFactory().setValue(element.angle);
			cbUseAutoCenter.setSelected(element.autoCenter);
			spinnerCenterX.setDisable(element.autoCenter);
			spinnerCenterY.setDisable(element.autoCenter);
			spinnerCenterX.getValueFactory().setValue(element.center.x);
			spinnerCenterY.getValueFactory().setValue(element.center.y);
			spinnerOriginX.getValueFactory().setValue(element.radCenter.x);
			spinnerOriginY.getValueFactory().setValue(element.radCenter.y);
			spinnerSpeed.getValueFactory().setValue(element.speed);
			spinnerAngle.setDisable(element.movement != Movement.MOVE_RADIAL);
			spinnerCenterX.setDisable(element.movement != Movement.MOVE_RADIAL);
			spinnerCenterY.setDisable(element.movement != Movement.MOVE_RADIAL);
			spinnerOriginX.setDisable(element.movement != Movement.MOVE_RADIAL);
			spinnerOriginY.setDisable(element.movement != Movement.MOVE_RADIAL);
			spinnerSpeed.setDisable(element.movement != Movement.MOVE_RADIAL);
			cbUseAutoCenter.setDisable(element.movement != Movement.MOVE_RADIAL);
			this.vertices.clear();
			for(int i=0; i<element.getVertices().size(); i++) {
				this.vertices.add(new Vertex(element.getVertices().get(i).x, element.getVertices().get(i).y));
			}
			spinnerPosOffsetX.getValueFactory().setValue(element.positionOffset.x);
			spinnerPosOffsetY.getValueFactory().setValue(element.positionOffset.y);
		}
		updateVertexList();
	}
	
	
	
	
	
	
	class Vertex extends Vector2d {
		
		public Vertex(double x, double y) {
			super(x, y);
		}
		
		public HBox box;
		public Spinner<Double> spinnerX, spinnerY;
	}
	
}
