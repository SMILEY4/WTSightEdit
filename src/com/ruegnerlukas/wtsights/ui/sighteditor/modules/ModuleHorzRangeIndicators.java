package com.ruegnerlukas.wtsights.ui.sighteditor.modules;

import java.util.Comparator;

import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.sight.HIndicator;
import com.ruegnerlukas.wtsights.data.sight.sightElements.BaseElement;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.data.sight.sightElements.elements.ElementHorzRangeIndicators;
import com.ruegnerlukas.wtsights.ui.sighteditor.SightEditorController;
import com.ruegnerlukas.wtsights.ui.sighteditor.StepSizes;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ModuleHorzRangeIndicators implements Module {

	private ElementHorzRangeIndicators element;

	@FXML private Spinner<Double> spinnerSizeMajor;
	@FXML private Spinner<Double> spinnerSizeMinor;

	@FXML private AnchorPane paneDistances;
	@FXML private ScrollPane scrollTable;
	@FXML private AnchorPane paneDistanceContent;
	
	private VBox boxTable;





	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void create(DataPackage data) {
		
		ElementHorzRangeIndicators elementDefault = new ElementHorzRangeIndicators();
		
		// size major
		FXUtils.initSpinner(spinnerSizeMajor, elementDefault.sizeMajor, 0, 1000, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.sizeMajor = newValue.floatValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		// size minor
		FXUtils.initSpinner(spinnerSizeMinor, elementDefault.sizeMinor, 0, 1000, StepSizes.STEP_SCREENSPACE, StepSizes.DECPLACES_SCREENSPACE, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				if(element != null) {
					element.sizeMinor = newValue.floatValue();
					element.setDirty(true);
					((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
				}
			}
		});
		
		
		// table
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
		
		boxTable = new VBox();
		FXUtils.createCustomTable(
				paneDistanceContent, boxTable,
				new String[]{ViewManager.getResources().getString("se_mhri_table_mil"), ViewManager.getResources().getString("se_mhri_table_rank")},
				new int[] {90, 90},
				new EventHandler<ActionEvent>() {
					@Override public void handle(ActionEvent event) {
						if(element != null) {
							HIndicator indicator = new HIndicator(0, true);
							element.indicators.add(indicator);
							addTableRow(boxTable, 0, true);
							element.setDirty(true);
							((SightEditorController)ViewManager.getController(View.SIGHT_EDITOR)).wtCanvas.repaint();
						}
					}
				},
				new EventHandler<MouseEvent>() {
					@Override public void handle(MouseEvent event) {
						if(element == null) {
							return;
						}
						boxTable.getChildren().remove(1, boxTable.getChildren().size()-1);
						element.indicators.sort(new Comparator<HIndicator>() {
							@Override public int compare(HIndicator o1, HIndicator o2) {
								if(o1.getMil() > o2.getMil()) {
									return +1;
								}
								if(o1.getMil() < o2.getMil()) {
									return -1;
								}
								return 0;
							}
						});
						for(int i=0; i<element.indicators.size(); i++) {
							HIndicator indicator = element.indicators.get(i);
							addTableRow(boxTable, indicator.getMil(), indicator.isMajor() );
						}
					}
				});
		
		setElement(null);
	}


	
	@Override
	public void setElement(BaseElement e) {
		
		if(e == null || e.type != ElementType.HORZ_RANGE_INDICATORS) {
			this.element = null;
		} else {
			this.element = (ElementHorzRangeIndicators)e;
		}
		
		if(element != null) {
			spinnerSizeMajor.getValueFactory().setValue(element.sizeMajor);
			spinnerSizeMinor.getValueFactory().setValue(element.sizeMinor);
			boxTable.getChildren().remove(1, boxTable.getChildren().size()-1);
			for(int i=0; i<element.indicators.size(); i++) {
				HIndicator indicator = element.indicators.get(i);
				addTableRow(boxTable, indicator.getMil(), indicator.isMajor());
			}
		}
	}
	

	
	
	void addTableRow(VBox boxTable, int distance, boolean isMajor) {
		
		HBox boxRow = new HBox();
		boxRow.setMinSize(ScrollPane.USE_COMPUTED_SIZE, 31);
		boxRow.setPrefSize(ScrollPane.USE_COMPUTED_SIZE, 31);
		boxRow.setMaxSize(ScrollPane.USE_COMPUTED_SIZE, 31);	
		boxTable.getChildren().add(boxTable.getChildren().size()-1, boxRow);
	
		Spinner<Integer> spMils = new Spinner<Integer>();
		spMils.setEditable(true);
		FXUtils.initSpinner(spMils, distance, -99, 99, StepSizes.STEP_RANGEMIL, StepSizes.DECPLACES_RANGEMIL, false, new ChangeListener<Integer>() {
			@Override public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				if(element != null) {
					int index = boxTable.getChildren().indexOf(boxRow) - 1;
					HIndicator indicator = element.indicators.get(index);
					indicator.setMil(newValue.intValue());
					onIndicatorEdit(indicator);
				}
			}
		});
		spMils.setMinSize(90, 31);
		spMils.setPrefSize(90, 31);
		boxRow.getChildren().add(spMils);
		
		ChoiceBox<String> cbMajor = new ChoiceBox<String>();
		cbMajor.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(element != null) {
					boolean isMajor = ViewManager.getResources().getString("se_mhri_cell_major").equalsIgnoreCase(newValue);
					int index = boxTable.getChildren().indexOf(boxRow) - 1;
					HIndicator indicator = element.indicators.get(index);
					indicator.setMajor(isMajor);
					onIndicatorEdit(indicator);
				}
			}
		});
		cbMajor.getItems().addAll(ViewManager.getResources().getString("se_mhri_cell_major"), ViewManager.getResources().getString("se_mhri_cell_minor"));
		cbMajor.getSelectionModel().select(isMajor ? 0 : 1);
		cbMajor.setMinSize(90, 31);
		cbMajor.setPrefSize(90, 31);
		boxRow.getChildren().add(1, cbMajor);
		
		
		Button btnDelete = new Button(ViewManager.getResources().getString("se_mhri_cell_remove"));
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
	
	
	
	
	void onIndicatorEdit(HIndicator indicator) {
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


