package com.ruegnerlukas.wtsights.ui.sighteditor.modules;

import java.util.Comparator;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticElement;
import com.ruegnerlukas.wtsights.data.ballisticdata.NullElement;
import com.ruegnerlukas.wtsights.data.sight.BIndicator;
import com.ruegnerlukas.wtsights.data.sight.sightElements.Element;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementBallRangeIndicator;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementShellBlock;
import com.ruegnerlukas.wtsights.ui.sighteditor.SightEditorController;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;
import com.ruegnerlukas.wtutils.Conversion;
import com.ruegnerlukas.wtutils.FXUtils;
import com.ruegnerlukas.wtutils.SightUtils.ScaleMode;
import com.ruegnerlukas.wtutils.SightUtils.TextAlign;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ModuleShellBlock implements Module {

	private ElementShellBlock element;
	

	@FXML private ComboBox<BallisticElement> comboAmmo;
	@FXML private ChoiceBox<String> choiceScaleMode;

	@FXML private VBox boxVertical;
	@FXML private Spinner<Double> vTextShift;
	@FXML private Spinner<Double> vTextX;
	@FXML private Spinner<Double> vTextY;
	@FXML private ChoiceBox<String> vTextAlignment;
	@FXML private Spinner<Double> vPosX;
	@FXML private Spinner<Double> vPosY;
	@FXML private Spinner<Double> vSizeMajor;
	@FXML private Spinner<Double> vSizeMinor;
	@FXML private CheckBox vDrawAddLines;
	@FXML private Spinner<Double> vSizeAddMajor;
	@FXML private Spinner<Double> vSizeAddMinor;

	@FXML private VBox boxRadial;
	@FXML private ChoiceBox<String> choiceCircleMode;
	@FXML private Spinner<Double> rPosX;
	@FXML private Spinner<Double> rPosY;
	@FXML private Label rLabelSize;
	@FXML private Spinner<Double> rSize;
	@FXML private Spinner<Double> rCircleRadius;
	@FXML private CheckBox rRadiusUseMils;
	@FXML private Spinner<Double> rRadius;
	@FXML private Spinner<Double> rStretch;
	@FXML private Spinner<Double> rAngle;
	@FXML private Spinner<Double> rTextOffset;
	@FXML private ChoiceBox<String> rTextAlignment;
	
	@FXML private CheckBox cbDrawUpward;
	@FXML private CheckBox cbCanMove;
	@FXML private CheckBox cbDrawCorrLabel;
	
	@FXML private Spinner<Double> spinnerCorrX;
	@FXML private Spinner<Double> spinnerCorrY;
	
	@FXML private AnchorPane paneDistances;
	@FXML private ScrollPane scrollTable;
	@FXML private AnchorPane paneDistanceContent;
	
	private VBox boxTable;
	

	
	


	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void create(DataPackage data) {
		
		ElementBallRangeIndicator elementDefault = new ElementBallRangeIndicator();
		
		// AMMO
		FXUtils.initComboboxBallistic(comboAmmo);
		for(BallisticElement element : data.dataBallistic.elements) {
			if(!element.isRocketElement) {
				comboAmmo.getItems().add(element);
			}
		}
//		comboAmmo.getItems().addAll(data.dataBallistic.elements);
		comboAmmo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BallisticElement>() {
			@Override
			public void changed(ObservableValue<? extends BallisticElement> observable, BallisticElement oldValue, BallisticElement newValue) {
				BallisticElement selected = comboAmmo.getSelectionModel().getSelectedItem();
				if(element == null || selected == null || selected instanceof NullElement) {
					return;
				}
				element.elementBallistic = selected;
				element.setDirty(true);
				Logger.get().debug("Selected ballistic element: " + (element.elementBallistic == null ? "null" : element.elementBallistic) );
				((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
			}
		});
		comboAmmo.getSelectionModel().select(0);

		
		if(elementDefault.scaleMode == ScaleMode.VERTICAL) {
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
		
		choiceScaleMode.getItems().addAll(ScaleMode.VERTICAL.toString(), ScaleMode.RADIAL.toString());
		choiceScaleMode.getSelectionModel().select(elementDefault.scaleMode.toString());
		choiceScaleMode.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(element == null) {
					return;
				}
				if(newValue.equals(ScaleMode.VERTICAL.toString())) { element.scaleMode = ScaleMode.VERTICAL; }
				if(newValue.equals(ScaleMode.RADIAL.toString()))   { element.scaleMode = ScaleMode.RADIAL; }
				if(element.scaleMode == ScaleMode.VERTICAL) {
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
				element.setDirty(true);
				((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
			}
		});
		
		// VERTICAL
		FXUtils.initSpinner(vTextShift, elementDefault.textShift, -1000, 1000, 0.1, 1, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.textShift = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(vTextX, elementDefault.textPos.x, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.textPos.x = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(vTextY, elementDefault.textPos.y, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.textPos.y = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(vPosX, elementDefault.position.x, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.position.x = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(vPosY, elementDefault.position.y, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.position.y = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(vSizeMajor, elementDefault.size.x, 0, 1000, 0.005, 3, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.size.x = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(vSizeMinor, elementDefault.size.y, 0, 1000, 0.005, 3, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.size.y = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(vSizeAddMajor, elementDefault.sizeAddLine.x, 0, 1000, 0.005, 3, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.sizeAddLine.x = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(vSizeAddMinor, elementDefault.sizeAddLine.y, 0, 1000, 0.005, 3, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.sizeAddLine.y = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		vTextAlignment.getItems().addAll(TextAlign.LEFT.toString(), TextAlign.CENTER.toString(), TextAlign.RIGHT.toString());
		vTextAlignment.getSelectionModel().select(elementDefault.textAlign.toString());
		vTextAlignment.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(element != null) {
					if(newValue.equals(TextAlign.LEFT.toString())) 	 { element.textAlign = TextAlign.LEFT;   }
					if(newValue.equals(TextAlign.CENTER.toString())) { element.textAlign = TextAlign.CENTER; }
					if(newValue.equals(TextAlign.RIGHT.toString()))  { element.textAlign = TextAlign.RIGHT;  }
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});		
		
		vDrawAddLines.setSelected(elementDefault.drawAddLines);
		vDrawAddLines.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				if(element != null) {
					element.drawAddLines = vDrawAddLines.isSelected();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		
		// RADIAL
		choiceCircleMode.getItems().addAll(ViewManager.getResources().getString("se_msb_circles"), ViewManager.getResources().getString("se_msb_lines"));
		choiceCircleMode.getSelectionModel().select(elementDefault.circleMode ? ViewManager.getResources().getString("se_msb_circles") : ViewManager.getResources().getString("se_msb_lines"));
		choiceCircleMode.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(element == null) {
					return;
				}
				element.circleMode = newValue.equalsIgnoreCase(ViewManager.getResources().getString("se_msb_circles"));
				if(element.circleMode) {
					rCircleRadius.setDisable(false);
					rLabelSize.setText(ViewManager.getResources().getString("se_msb_stroke_width"));
				} else {
					rCircleRadius.setDisable(true);
					rLabelSize.setText(ViewManager.getResources().getString("se_msb_line_size"));
				}
				element.setDirty(true);
				((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
			}
		});
		if(elementDefault.circleMode) {
			rCircleRadius.setDisable(false);
			rLabelSize.setText(ViewManager.getResources().getString("se_msb_stroke_width"));
		} else {
			rCircleRadius.setDisable(true);
			rLabelSize.setText(ViewManager.getResources().getString("se_msb_line_size"));
		}
		
		FXUtils.initSpinner(rPosX, elementDefault.position.x, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.position.x = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(rPosY, elementDefault.position.y, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.position.y = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(rSize, elementDefault.size.x, 0, 1000, 0.001, 3, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.size.x = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(rCircleRadius, elementDefault.size.y, 0, 1000, 0.001, 3, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.size.y = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(rRadius, elementDefault.radialRadius, 0, 1000, (elementDefault.radiusUseMils ? 0.5 : 0.001), (elementDefault.radiusUseMils ? 1 : 3), new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.radialRadius = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(rAngle, elementDefault.radialAngle, -1000, 1000, 1, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.radialAngle = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(rStretch, elementDefault.radialStretch, -1000, 1000, 0.05, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.radialStretch = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		
		FXUtils.initSpinner(rTextOffset, elementDefault.textPos.x, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.textPos.x = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		rRadiusUseMils.setSelected(elementDefault.radiusUseMils);
		rRadiusUseMils.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				if(element == null) {
					return;
				}
				element.radiusUseMils = rRadiusUseMils.isSelected();
				if(element.radiusUseMils) {
					FXUtils.initSpinner(rCircleRadius, Conversion.get().screenspace2mil(element.radialRadius, data.dataSight.envZoomedIn), -1000, 1000, 0.5, 1, null);
				} else {
					FXUtils.initSpinner(rCircleRadius, Conversion.get().mil2screenspace(element.radialRadius, data.dataSight.envZoomedIn), -1000, 1000, 0.001, 3, null);
				}
				element.setDirty(true);
				((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
			}
		});
		rTextAlignment.getItems().addAll(TextAlign.LEFT.toString(), TextAlign.CENTER.toString(), TextAlign.RIGHT.toString());
		rTextAlignment.getSelectionModel().select(elementDefault.textAlign.toString());
		rTextAlignment.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(element != null) {
					if(newValue.equals(TextAlign.LEFT.toString())) 	 { element.textAlign = TextAlign.LEFT;   }
					if(newValue.equals(TextAlign.CENTER.toString())) { element.textAlign = TextAlign.CENTER; }
					if(newValue.equals(TextAlign.RIGHT.toString()))  { element.textAlign = TextAlign.RIGHT;  }
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});	
		
		
		cbDrawUpward.setSelected(elementDefault.drawUpward);
		cbDrawUpward.selectedProperty().addListener(new ChangeListener() {
			@Override public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				if(element != null) {
					element.drawUpward = cbDrawUpward.isSelected();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		cbCanMove.setSelected(elementDefault.move);
		cbCanMove.selectedProperty().addListener(new ChangeListener() {
			@Override public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				if(element != null) {
					element.move = cbCanMove.isSelected();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		cbDrawCorrLabel.setSelected(elementDefault.drawCorrLabel);
		cbDrawCorrLabel.selectedProperty().addListener(new ChangeListener() {
			@Override public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				if(element != null) {
					element.drawCorrLabel = cbDrawCorrLabel.isSelected();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		
		FXUtils.initSpinner(spinnerCorrX, elementDefault.posCorrLabel.x, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.posCorrLabel.x = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		FXUtils.initSpinner(spinnerCorrY, elementDefault.posCorrLabel.y, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.posCorrLabel.y = newValue.doubleValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		
		
		
		paneDistances.widthProperty().addListener(new ChangeListener() {
			@Override public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				double widthBase = paneDistances.getWidth()-26;
				double widthTable = paneDistanceContent.getWidth()+1;
				double width = Math.min(widthBase, widthTable);
				scrollTable.setMinWidth(width);	
				scrollTable.setPrefWidth(width);
				scrollTable.setMaxWidth(width);
				scrollTable.widthProperty().multiply(0).add(width);
			}
		});
		
		createTable(paneDistanceContent);

		setElement(null);
	}


	
	
	private void createTable(AnchorPane parent) {
		
		boxTable = new VBox();
		FXUtils.createCustomTable(
				parent, boxTable,
				new String[]{
						ViewManager.getResources().getString("se_msb_table_distance"),
						ViewManager.getResources().getString("se_msb_table_rank"),
						ViewManager.getResources().getString("se_msb_table_extend"),
						ViewManager.getResources().getString("se_msb_table_text_x"),
						ViewManager.getResources().getString("se_msb_table_text_y")
				},
				new int[] {90, 90, 90, 90, 90},
				new EventHandler<ActionEvent>() {
					@Override public void handle(ActionEvent event) {
						if(element != null) {
							BIndicator indicator = new BIndicator(0, true, 0, 0, 0);
							element.indicators.add(indicator);
							addTableRow(boxTable, 0, true, 0, 0, 0);
							element.setDirty(true);
							((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
						}
					}
				},
				new EventHandler<MouseEvent>() {
					@Override public void handle(MouseEvent event) {
						if(element != null) {
							return;
						}
						boxTable.getChildren().remove(1, boxTable.getChildren().size()-1);
						element.indicators.sort(new Comparator<BIndicator>() {
							@Override public int compare(BIndicator o1, BIndicator o2) {
								if(o1.getDistance() > o2.getDistance()) {
									return +1;
								}
								if(o1.getDistance() < o2.getDistance()) {
									return -1;
								}
								return 0;
							}
						});
						for(int i=0; i<element.indicators.size(); i++) {
							BIndicator indicator = element.indicators.get(i);
							addTableRow(boxTable, indicator.getDistance(), indicator.isMajor(), indicator.getExtend(), indicator.getTextX(), indicator.getTextY() );
						}
					}
				});
		
	}
	
	
	
	
	@Override
	public void setElement(Element e) {
		
		if(e == null || e.type != ElementType.SHELL_BALLISTICS_BLOCK) {
			this.element = null;
		} else {
			this.element = (ElementShellBlock)e;
		}
		
		if(element != null) {
			if(element.elementBallistic == null) {
				element.elementBallistic = comboAmmo.getValue();
				
			} else {
				comboAmmo.getSelectionModel().select(element.elementBallistic);
			}
			choiceScaleMode.getSelectionModel().select(element.scaleMode.toString());
			vTextShift.getValueFactory().setValue(element.textShift);
			vTextX.getValueFactory().setValue(element.textPos.x);
			vTextY.getValueFactory().setValue(element.textPos.y);
			vTextAlignment.getSelectionModel().select(element.textAlign.toString());
			vPosX.getValueFactory().setValue(element.position.x);
			vPosY.getValueFactory().setValue(element.position.y);
			vSizeMajor.getValueFactory().setValue(element.size.x);
			vSizeMinor.getValueFactory().setValue(element.size.y);
			vDrawAddLines.setSelected(element.drawAddLines);
			vSizeAddMajor.getValueFactory().setValue(element.sizeAddLine.x);
			vSizeAddMinor.getValueFactory().setValue(element.sizeAddLine.y);
			choiceCircleMode.getSelectionModel().select(element.circleMode ? ViewManager.getResources().getString("se_msb_circles") : ViewManager.getResources().getString("se_msb_lines"));
			rPosX.getValueFactory().setValue(element.position.x);
			rPosY.getValueFactory().setValue(element.position.y);
			rSize.getValueFactory().setValue(element.size.x);
			rCircleRadius.getValueFactory().setValue(element.size.y);
			rRadiusUseMils.setSelected(element.radiusUseMils);
			rRadius.getValueFactory().setValue(element.radialRadius);
			rStretch.getValueFactory().setValue(element.radialStretch);
			rAngle.getValueFactory().setValue(element.radialAngle);
			rTextOffset.getValueFactory().setValue(element.textPos.x);
			rTextAlignment.getSelectionModel().select(element.textAlign.toString());
			boxTable.getChildren().remove(1, boxTable.getChildren().size()-1);
			for(int i=0; i<element.indicators.size(); i++) {
				BIndicator indicator = element.indicators.get(i);
				addTableRow(boxTable, indicator.getDistance(), indicator.isMajor(), indicator.getExtend(), indicator.getTextX(), indicator.getTextY() );
			}
		}
	}
	
	
	
	
	void addTableRow(VBox boxTable, int distance, boolean isMajor, double extend, double textX, double textY) {
		
		HBox boxRow = new HBox();
		boxRow.setMinSize(ScrollPane.USE_COMPUTED_SIZE, 31);
		boxRow.setPrefSize(ScrollPane.USE_COMPUTED_SIZE, 31);
		boxRow.setMaxSize(ScrollPane.USE_COMPUTED_SIZE, 31);
		boxTable.getChildren().add(boxTable.getChildren().size()-1, boxRow);
	
		Spinner<Integer> spDistance = new Spinner<Integer>();
		spDistance.setEditable(true);
		FXUtils.initSpinner(spDistance, distance, 0, 9000, 100, 0, false, new ChangeListener<Integer>() {
			@Override public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				if(element != null) {
					int index = boxTable.getChildren().indexOf(boxRow) - 1;
					BIndicator indicator = element.indicators.get(index);
					indicator.setDistance(newValue.intValue());
					onIndicatorEdit(indicator);
				}
			}
		});
		spDistance.setMinSize(90, 31);
		spDistance.setPrefSize(90, 31);
		boxRow.getChildren().add(spDistance);
		
		Spinner<Double> spExtend = new Spinner<Double>();
		spExtend.setEditable(true);
		FXUtils.initSpinner(spExtend, extend, -999, 999, 0.005, 3, false, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					int index = boxTable.getChildren().indexOf(boxRow) - 1;
					BIndicator indicator = element.indicators.get(index);
					indicator.setExtend(newValue.floatValue());
					onIndicatorEdit(indicator);
				}
			}
		});
		spExtend.setMinSize(90, 31);
		spExtend.setPrefSize(90, 31);
		boxRow.getChildren().add(spExtend);
		
		Spinner<Double> spTextX = new Spinner<Double>();
		spTextX.setEditable(true);
		FXUtils.initSpinner(spTextX, textX, -999, 999, 0.005, 3, false, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					int index = boxTable.getChildren().indexOf(boxRow) - 1;
					BIndicator indicator = element.indicators.get(index);
					indicator.setTextX(newValue.floatValue());
					onIndicatorEdit(indicator);
				}
			}
		});
		spTextX.setMinSize(90, 31);
		spTextX.setPrefSize(90, 31);
		boxRow.getChildren().add(spTextX);
	
		Spinner<Double> spTextY = new Spinner<Double>();
		spTextY.setEditable(true);
		FXUtils.initSpinner(spTextY, textY, -999, 999, 0.005, 3, false, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					int index = boxTable.getChildren().indexOf(boxRow) - 1;
					BIndicator indicator = element.indicators.get(index);
					indicator.setTextY(newValue.floatValue());
					onIndicatorEdit(indicator);
				}
			}
		});
		spTextY.setMinSize(90, 31);
		spTextY.setPrefSize(90, 31);
		boxRow.getChildren().add(spTextY);
		
		ChoiceBox<String> cbMajor = new ChoiceBox<String>();
		cbMajor.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(element == null) {
					return;
				}
				boolean isMajor = ViewManager.getResources().getString("se_msb_cell_major").equalsIgnoreCase(newValue);
				if(isMajor) {
					spTextX.setDisable(false);
					spTextY.setDisable(false);
				} else {
					spTextX.setDisable(true);
					spTextY.setDisable(true);
				}
				int index = boxTable.getChildren().indexOf(boxRow) - 1;
				BIndicator indicator = element.indicators.get(index);
				indicator.setMajor(isMajor);
				onIndicatorEdit(indicator);
			}
		});
		cbMajor.getItems().addAll(ViewManager.getResources().getString("se_msb_cell_major"), ViewManager.getResources().getString("se_msb_cell_minor"));
		cbMajor.getSelectionModel().select(isMajor ? 0 : 1);
		cbMajor.setMinSize(90, 31);
		cbMajor.setPrefSize(90, 31);
		boxRow.getChildren().add(1, cbMajor);
		
		
		Button btnDelete = new Button(ViewManager.getResources().getString("se_msb_cell_remove"));
		btnDelete.setMinSize(31, 31);
		btnDelete.setPrefSize(31, 31);
		btnDelete.setMaxSize(31, 31);
		btnDelete.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {
				if(element != null) {
					int index = boxTable.getChildren().indexOf(boxRow);
					boxTable.getChildren().remove(boxRow);
					onTableDelete(index-1);
				}
			}
		});
		boxRow.getChildren().add(btnDelete);
		
		if(element != null) {
			element.setDirty(true);
		}
	}
	
	
	
	
	void onIndicatorEdit(BIndicator indicator) {
		if(element != null) {
			element.setDirty(true);
		}
		((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
	}
	
	
	
	
	void onTableDelete(int index) {
		if(element != null) {
			element.indicators.remove(index);
			element.setDirty(true);
			((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
		}
	}
	
}

