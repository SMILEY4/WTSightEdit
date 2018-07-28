package com.ruegnerlukas.wtsights.ui.sighteditor;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationAmmoData;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.data.sight.SightData.ScaleMode;
import com.ruegnerlukas.wtsights.data.sight.SightData.TextAlign;
import com.ruegnerlukas.wtsights.ui.sighteditor.utils.BallisticIndicatorElement;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.layout.VBox;

public class UIBallisticRange {

	
	@FXML private ResourceBundle resources;
	@FXML private URL location;
	
	@FXML private VBox boxVertical;
	@FXML private VBox boxRadial;

	
	@FXML private ComboBox<String> circleMode;
	
	@FXML private ComboBox<String> scaleMode;
	@FXML private Spinner<Double> radialStretch;
	@FXML private Spinner<Double> radialAngle;
	@FXML private CheckBox cbRadiusUseMils;
	@FXML private Spinner<Double> radialRadius;
	
	@FXML private CheckBox cbCanMove;

	@FXML private CheckBox cbDrawCorr;
	@FXML private Spinner<Double> corrX;
	@FXML private Spinner<Double> corrY;
	
	@FXML private Spinner<Double> textShift;
	@FXML private Spinner<Double> textOffsetX;
	@FXML private Spinner<Double> textOffsetXvert;
	@FXML private Spinner<Double> textOffsetY;
	@FXML private ComboBox<String> textAlign;
	@FXML private ComboBox<String> textAlignVert;


	@FXML private Spinner<Double> distPosX;
	@FXML private Spinner<Double> distPosY;
	@FXML private Spinner<Double> distPosXvert;
	@FXML private Spinner<Double> distPosYvert;
	@FXML private Spinner<Double> sizeMajor;
	@FXML private Spinner<Double> sizeMajorVert;
	@FXML private Spinner<Double> sizeMinor;
	@FXML private Spinner<Double> circleRadius;


	@FXML private CheckBox cbDrawAdd;
	@FXML private Spinner<Double> sizeAddMajor;
	@FXML private Spinner<Double> sizeAddMinor;
	
	@FXML private CheckBox cbDrawUpward;
	
	@FXML private VBox vbox;

	private UISightEditor editor;
	private SightData dataSight;



	
	
	public void setEditor(UISightEditor editor) {
		this.editor = editor;
	}




	public void setDataSight(SightData data) {
		this.dataSight = data;
	}




	public void create() {

		if(dataSight.brIndicators.bScaleMode == ScaleMode.VERTICAL) {
			boxVertical.setDisable(false);
			boxVertical.setVisible(true);
			boxRadial.setDisable(true);
			boxRadial.setVisible(false);
		} else {
			boxVertical.setDisable(true);
			boxVertical.setVisible(false);
			boxRadial.setDisable(false);
			boxRadial.setVisible(true);
		}
		
	
		circleMode.getItems().addAll("Lines", "Circles");
		circleMode.getSelectionModel().select(dataSight.brIndicators.bCircleMode ? "Circles" : "Lines");
		circleMode.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				onCircleMode(newValue.equalsIgnoreCase("Circles"));
			}
		});
		
		
		scaleMode.getItems().addAll(ScaleMode.VERTICAL.toString(), ScaleMode.RADIAL.toString());
		scaleMode.getSelectionModel().select(dataSight.brIndicators.bScaleMode.toString());
		scaleMode.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				onScaleMode(newValue);
			}
		});
		
		FXUtils.initSpinner(radialStretch, dataSight.brIndicators.bRadialStretch, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onRadialStretch(newValue.doubleValue());
			}
		});
		
		FXUtils.initSpinner(radialAngle, dataSight.brIndicators.bRadialAngle, -1000, 1000, 1, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onRadialAngle(newValue.doubleValue());
			}
		});
		
		cbRadiusUseMils.setSelected(dataSight.brIndicators.bRadiusUseMils);
		
		FXUtils.initSpinner(radialRadius, dataSight.brIndicators.bRadialRadius, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onRadialRadius(newValue.doubleValue());
			}
		});
		
		
		cbDrawCorr.setSelected(dataSight.brIndicators.bDrawCorrection);
		
		FXUtils.initSpinner(corrX, dataSight.brIndicators.bCorrectionPos.x, -1000, 1000, 0.001, 3, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onCorrectionPos(newValue.doubleValue(), corrY.getValue().doubleValue());
			}
		});
		
		FXUtils.initSpinner(corrY, dataSight.brIndicators.bCorrectionPos.y, -1000, 1000, 0.001, 3, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onCorrectionPos(corrX.getValue().doubleValue(), newValue.doubleValue());
			}
		});
		
		
		cbCanMove.setSelected(dataSight.brIndicators.bMove);
		
		
		FXUtils.initSpinner(textShift, dataSight.brIndicators.bTextShift, -1000, 1000, 0.1, 1, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onTextShift(newValue.doubleValue());
			}
		});
		
		FXUtils.initSpinner(textOffsetX, dataSight.brIndicators.bTextOffset.x, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onTextOffset(newValue.doubleValue(), textOffsetY.getValue().doubleValue());
			}
		});
		
		FXUtils.initSpinner(textOffsetXvert, dataSight.brIndicators.bTextOffset.x, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onTextOffset(newValue.doubleValue(), textOffsetY.getValue().doubleValue());
			}
		});
		
		FXUtils.initSpinner(textOffsetY, dataSight.brIndicators.bTextOffset.y, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onTextOffset(dataSight.brIndicators.bScaleMode==ScaleMode.VERTICAL ? textOffsetXvert.getValue().doubleValue() : textOffsetX.getValue().doubleValue(), newValue.doubleValue());
			}
		});
		
		textAlign.getItems().addAll(TextAlign.LEFT.toString(), TextAlign.CENTER.toString(), TextAlign.RIGHT.toString());
		textAlign.getSelectionModel().select(dataSight.brIndicators.bTextAlign.toString());
		textAlign.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				onTextAlign(newValue);
			}
		});
		
		textAlignVert.getItems().addAll(TextAlign.LEFT.toString(), TextAlign.CENTER.toString(), TextAlign.RIGHT.toString());
		textAlignVert.getSelectionModel().select(dataSight.brIndicators.bTextAlign.toString());
		textAlignVert.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				onTextAlign(newValue);
			}
		});
		
		
		
		FXUtils.initSpinner(distPosX, dataSight.brIndicators.bMainPos.x, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onDistPosChanged(newValue.doubleValue(), distPosY.getValue().doubleValue());
			}
		});
		
		FXUtils.initSpinner(distPosY, dataSight.brIndicators.bMainPos.y, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onDistPosChanged(distPosX.getValue().doubleValue(), newValue.doubleValue());
			}
		});
		
		FXUtils.initSpinner(distPosXvert, dataSight.brIndicators.bMainPos.y, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onDistPosChanged(newValue.doubleValue(), distPosYvert.getValue().doubleValue());
			}
		});
		
		
		FXUtils.initSpinner(distPosYvert, dataSight.brIndicators.bMainPos.y, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onDistPosChanged(distPosXvert.getValue().doubleValue(), newValue.doubleValue());
			}
		});
		
		
		
		FXUtils.initSpinner(sizeMajorVert, dataSight.brIndicators.bSizeMain.x, -1000, 1000, 0.005, 3, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onSizeChanged(newValue.doubleValue(), sizeMinor.getValue().doubleValue());
			}
		});
		
		FXUtils.initSpinner(sizeMajor, dataSight.brIndicators.bSizeMain.x, -1000, 1000, 0.005, 3, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onSizeChanged(newValue.doubleValue(), (dataSight.brIndicators.bScaleMode==ScaleMode.RADIAL&&dataSight.brIndicators.bCircleMode) ? circleRadius.getValue().doubleValue() : sizeMinor.getValue().doubleValue());
			}
		});
		
		FXUtils.initSpinner(sizeMinor, dataSight.brIndicators.bSizeMain.y, -1000, 1000, 0.005, 3, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onSizeChanged(dataSight.brIndicators.bScaleMode==ScaleMode.VERTICAL?sizeMajorVert.getValue().doubleValue():sizeMajor.getValue().doubleValue(), newValue.doubleValue());
			}
		});
		
		FXUtils.initSpinner(circleRadius, dataSight.brIndicators.bSizeMain.y, -1000, 1000, 0.005, 3, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onSizeChanged(dataSight.brIndicators.bScaleMode==ScaleMode.VERTICAL?sizeMajorVert.getValue().doubleValue():sizeMajor.getValue().doubleValue(), newValue.doubleValue());
			}
		});
		
		
		cbDrawAdd.setSelected(dataSight.brIndicators.bDrawCenteredLines);
		
		FXUtils.initSpinner(sizeAddMinor, dataSight.brIndicators.bSizeCentered.x, -1000, 1000, 0.005, 3, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onAddSizeChanged(sizeAddMajor.getValue().doubleValue(), newValue.doubleValue());
			}
		});
		
		FXUtils.initSpinner(sizeAddMajor, dataSight.brIndicators.bSizeCentered.y, -1000, 1000, 0.005, 3, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onAddSizeChanged(newValue.doubleValue(), sizeAddMinor.getValue().doubleValue());
			}
		});
		
		
		cbDrawUpward.setSelected(dataSight.brIndicators.bDrawUpward);
		
		for(int i=0; i<dataSight.brIndicators.bDists.size(); i++) {
			addIndicator(
					dataSight.brIndicators.bDists.get(i),
					dataSight.brIndicators.bMajors.get(i),
					dataSight.brIndicators.bExtensions.get(i),
					dataSight.brIndicators.bTextOffsets.get(i).x,
					dataSight.brIndicators.bTextOffsets.get(i).y,
					false);
		}
		
	}

	
	
	
	
	
	void onCircleMode(boolean circle) {
		dataSight.brIndicators.bCircleMode = circle;
		if(dataSight.brIndicators.bCircleMode) {
			circleRadius.setDisable(false);
		} else {
			circleRadius.setDisable(true);
		}
		editor.repaintCanvas();
	}
	
	
	
	
	void onScaleMode(String strMode) {
		if(strMode.equals(ScaleMode.VERTICAL.toString())) { dataSight.brIndicators.bScaleMode = ScaleMode.VERTICAL; }
		if(strMode.equals(ScaleMode.RADIAL.toString()))   { dataSight.brIndicators.bScaleMode = ScaleMode.RADIAL; }
		
		if(dataSight.brIndicators.bScaleMode == ScaleMode.VERTICAL) {
			boxVertical.setDisable(false);
			boxVertical.setVisible(true);
			boxRadial.setDisable(true);
			boxRadial.setVisible(false);
		} else {
			boxVertical.setDisable(true);
			boxVertical.setVisible(false);
			boxRadial.setDisable(false);
			boxRadial.setVisible(true);
		}
		
		
		editor.repaintCanvas();
	}
	
	
	
	
	void onRadialStretch(double stretch) {
		dataSight.brIndicators.bRadialStretch = stretch;
		editor.repaintCanvas();
	}
	
	
	
	
	void onRadialAngle(double angle) {
		dataSight.brIndicators.bRadialAngle = angle;
		editor.repaintCanvas();
	}
	
	
	
	
	void onRadialRadius(double radius) {
		dataSight.brIndicators.bRadialRadius = radius;
		editor.repaintCanvas();
	}
	
	
	
	
	@FXML
	void onRadiusUseMils(ActionEvent event) {
		dataSight.brIndicators.bRadiusUseMils = cbRadiusUseMils.isSelected();
		
		if(dataSight.brIndicators.bRadiusUseMils) {
			FXUtils.initSpinner(radialRadius, dataSight.brIndicators.bRadialRadius, -1000, 1000, 0.5, 1, null);
		} else {
			FXUtils.initSpinner(radialRadius, dataSight.brIndicators.bRadialRadius, -1000, 1000, 0.01, 2, null);
		}
		
		editor.repaintCanvas();
	}
	
	
	
	
	@FXML
	void onDrawCorr(ActionEvent event) {
		dataSight.brIndicators.bDrawCorrection = cbDrawCorr.isSelected();
		editor.repaintCanvas();
	}
	
	
	
	
	void onCorrectionPos(double x, double y) {
		dataSight.brIndicators.bCorrectionPos.set(x, y);
		editor.repaintCanvas();
	}
	
	
	
	
	@FXML
	void onCanMove(ActionEvent event) {
		dataSight.brIndicators.bMove = cbCanMove.isSelected();
		editor.repaintCanvas();
	}
	
	
	
	
	void onTextShift(double shift) {
		dataSight.brIndicators.bTextShift = shift;
		editor.repaintCanvas();
	}
	
	
	
	
	void onTextOffset(double offX, double offY) {
		dataSight.brIndicators.bTextOffset.set(offX, offY);
		editor.repaintCanvas();
	}
	
	
	
	
	void onTextAlign(String strAlign) {
		if(strAlign.equals(TextAlign.LEFT.toString())) 	 { dataSight.brIndicators.bTextAlign = TextAlign.LEFT;   }
		if(strAlign.equals(TextAlign.CENTER.toString())) { dataSight.brIndicators.bTextAlign = TextAlign.CENTER; }
		if(strAlign.equals(TextAlign.RIGHT.toString()))  { dataSight.brIndicators.bTextAlign = TextAlign.RIGHT;  }
		editor.repaintCanvas();
	}
	
	
	

	@FXML
	void onDrawUpward(ActionEvent event) {
		dataSight.brIndicators.bDrawUpward = cbDrawUpward.isSelected();
		editor.repaintCanvas();
	}

	


	void onDistPosChanged(double x, double y) {
		dataSight.brIndicators.bMainPos.set(x, y);
		editor.repaintCanvas();
	}
	
	
	
	
	void onSizeChanged(double major, double minor) {
		dataSight.brIndicators.bSizeMain.set(major, minor);
		editor.repaintCanvas();
	}
	
	
	
	
	@FXML
	void onDrawAdd(ActionEvent event) {
		dataSight.brIndicators.bDrawCenteredLines = cbDrawAdd.isSelected();
		editor.repaintCanvas();		
	}
	
	
	
	
	void onAddSizeChanged(double major, double minor) {
		dataSight.brIndicators.bSizeCentered.set(major, minor);
		editor.repaintCanvas();
	}
	
	
	
	
	@FXML
	void onNew(ActionEvent event) {
		addIndicator(0, false, 0, 0, 0, true);
	}
	
	
	
	
	private void addIndicator(int valueRange, boolean isMajor, double valueExtend, double valueTextX, double valueTextY, boolean updateList) {
		
		BallisticIndicatorElement element = new BallisticIndicatorElement(valueRange, isMajor, valueExtend, valueTextX, valueTextY) {
			@Override public void onRangeChanged(int range) {
				updateList();
			}
			@Override public void onRankChanged(boolean isMajor) {
				updateList();
			}
			@Override public void onExtendChanged(double extend) {
				updateList();
			}
			@Override public void onTextXChanged(double textX) {
				updateList();
			}
			@Override public void onTextYChanged(double textY) {
				updateList();
			}
			@Override public void onRemove() {
				vbox.getChildren().remove(this.box);
				updateList();
			}
		};
		this.vbox.getChildren().add(vbox.getChildren().size()-1, element.box);
		if(updateList) {
			updateList();
		}
	}

	
	
	
	void updateList() {
		Logger.get().info("Updating indicator list");
		dataSight.brIndicators.bDists.clear();
		dataSight.brIndicators.bMajors.clear();
		dataSight.brIndicators.bExtensions.clear();
		dataSight.brIndicators.bTextOffsets.clear();
		for(Node node : vbox.getChildren()) {
			if(node instanceof ContainerHBox) {
				ContainerHBox box = (ContainerHBox)node;
				BallisticIndicatorElement e = (BallisticIndicatorElement)box.element;
				dataSight.brIndicators.bDists.add(e.range.getValue());
				dataSight.brIndicators.bMajors.add(e.rank.getValue().contains("Major"));
				dataSight.brIndicators.bExtensions.add(e.extend.getValue());
				dataSight.brIndicators.bTextOffsets.add(new Vector2d(e.textX.getValue().doubleValue(), e.textY.getValue().doubleValue()));
			}
		}
		editor.repaintCanvas();
	}
	
	
	
	
	@FXML
	void onSortList(ActionEvent event) {
		
		ObservableList<Node> nodes = FXCollections.observableArrayList();
		nodes.addAll(vbox.getChildren());
		
		Collections.sort(nodes, new Comparator<Node>() {
			@Override
			public int compare(Node a, Node b) {
				if( !(a instanceof ContainerHBox) || !(b instanceof ContainerHBox) ) {
					return -1;
				}
				int valueA = ((BallisticIndicatorElement)((ContainerHBox)a).element).range.getValue();
				int valueB = ((BallisticIndicatorElement)((ContainerHBox)b).element).range.getValue();
				return valueA < valueB ? -1 : +1;
			}
		});
		
		vbox.getChildren().clear();
		for(Node n : nodes) {
			vbox.getChildren().add(n);
		}
	}




	@FXML
	void initialize() {
        assert cbCanMove != null : "fx:id=\"cbCanMove\" was not injected: check your FXML file 'layout_sighteditor_ballRange.fxml'.";
        assert textShift != null : "fx:id=\"textShift\" was not injected: check your FXML file 'layout_sighteditor_ballRange.fxml'.";
        assert textOffsetX != null : "fx:id=\"textOffsetX\" was not injected: check your FXML file 'layout_sighteditor_ballRange.fxml'.";
        assert textOffsetXvert != null : "fx:id=\"textOffsetX\" was not injected: check your FXML file 'layout_sighteditor_ballRange.fxml'.";
        assert textOffsetY != null : "fx:id=\"textOffsetY\" was not injected: check your FXML file 'layout_sighteditor_ballRange.fxml'.";
        assert textAlign != null : "fx:id=\"textAlign\" was not injected: check your FXML file 'layout_sighteditor_ballRange.fxml'.";
        assert textAlignVert != null : "fx:id=\"textAlign\" was not injected: check your FXML file 'layout_sighteditor_ballRange.fxml'.";
        assert cbDrawUpward != null : "fx:id=\"cbDrawUpward\" was not injected: check your FXML file 'layout_sighteditor_ballRange.fxml'.";
		assert distPosX != null : "fx:id=\"distPosX\" was not injected: check your FXML file 'layout_sighteditor_ballRange.fxml'.";
		assert distPosY != null : "fx:id=\"distPosY\" was not injected: check your FXML file 'layout_sighteditor_ballRange.fxml'.";
		assert distPosXvert != null : "fx:id=\"distPosX\" was not injected: check your FXML file 'layout_sighteditor_ballRange.fxml'.";
		assert distPosYvert != null : "fx:id=\"distPosY\" was not injected: check your FXML file 'layout_sighteditor_ballRange.fxml'.";
		assert sizeMajor != null : "fx:id=\"sizeMajor\" was not injected: check your FXML file 'layout_sighteditor_ballRange.fxml'.";
		assert sizeMajorVert != null : "fx:id=\"sizeMajor\" was not injected: check your FXML file 'layout_sighteditor_ballRange.fxml'.";
		assert sizeMinor != null : "fx:id=\"sizeMinor\" was not injected: check your FXML file 'layout_sighteditor_ballRange.fxml'.";
		assert cbDrawAdd != null : "fx:id=\"cbDrawAdd\" was not injected: check your FXML file 'layout_sighteditor_ballRange.fxml'.";
		assert sizeAddMajor != null : "fx:id=\"sizeAddMajor\" was not injected: check your FXML file 'layout_sighteditor_ballRange.fxml'.";
		assert sizeAddMinor != null : "fx:id=\"sizeAddMinor\" was not injected: check your FXML file 'layout_sighteditor_ballRange.fxml'.";
		assert vbox != null : "fx:id=\"vbox\" was not injected: check your FXML file 'layout_sighteditor_ballRange.fxml'.";
		assert scaleMode != null : "fx:id=\"scaleMode\" was not injected: check your FXML file 'layout_sighteditor_ballRange.fxml'.";
		assert radialStretch != null : "fx:id=\"radialStretch\" was not injected: check your FXML file 'layout_sighteditor_ballRange.fxml'.";
		assert radialAngle != null : "fx:id=\"radialAngle\" was not injected: check your FXML file 'layout_sighteditor_ballRange.fxml'.";
		assert cbRadiusUseMils != null : "fx:id=\"cbRadiusUseMils\" was not injected: check your FXML file 'layout_sighteditor_ballRange.fxml'.";
		assert radialRadius != null : "fx:id=\"radialRadius\" was not injected: check your FXML file 'layout_sighteditor_ballRange.fxml'.";
	}


	
}


