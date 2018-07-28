package com.ruegnerlukas.wtsights.ui.sighteditor;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.ui.sighteditor.utils.HorzIndicatorElement;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.layout.VBox;

public class UIHorzRange {


	@FXML private ResourceBundle resources;
	@FXML private URL location;

	private UISightEditor editor;
	private SightData dataSight;
	
	@FXML private Spinner<Double> sizeMajor;
	@FXML private Spinner<Double> sizeMinor;
	@FXML private VBox vbox;


	
	
	
	
	public void setEditor(UISightEditor editor) {
		this.editor = editor;
	}
	
	
	
	
	public void setDataSight(SightData data) {
		this.dataSight = data;
	}
	
	
	
	
	public void create() {

		FXUtils.initSpinner(sizeMajor, dataSight.hrSizeMajor, 0, 1000, 1, 1, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onSizeMajor(newValue);
			}
		});
		
		FXUtils.initSpinner(sizeMinor, dataSight.hrSizeMinor, 0, 1000, 1, 1, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onSizeMinor(newValue);
			}
		});
		
		for(int i=0; i<dataSight.hrMils.size(); i++) {
			addIndicator(dataSight.hrMils.get(i), dataSight.hrMajors.get(i), false);
		}
	}
	
	


	void onSizeMajor(double size) {
		dataSight.hrSizeMajor = size;
		editor.repaintCanvas();
	}
	
	
	
	
	void onSizeMinor(double size) {
		dataSight.hrSizeMinor = size;
		editor.repaintCanvas();
	}
	
	
	
	
	@FXML
	void onNew(ActionEvent event) {
		addIndicator(2, false, true);
	}

	
	

	private void addIndicator(int value, boolean isMajor, boolean updateList) {
		HorzIndicatorElement element = new HorzIndicatorElement(value, isMajor) {
			@Override public void onRangeChanged(int range) {
				updateList();
			}
			@Override public void onRankChanged(boolean isMajor) {
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
		dataSight.hrMils.clear();
		dataSight.hrMajors.clear();
		for(Node node : vbox.getChildren()) {
			if(node instanceof ContainerHBox) {
				ContainerHBox box = (ContainerHBox)node;
				HorzIndicatorElement e = (HorzIndicatorElement)box.element;
				dataSight.hrMils.add(e.spinner.getValue());
				dataSight.hrMajors.add(e.combo.getValue().contains("Major"));
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
				int valueA = ((HorzIndicatorElement)((ContainerHBox)a).element).spinner.getValue();
				int valueB = ((HorzIndicatorElement)((ContainerHBox)b).element).spinner.getValue();
				return valueA < valueB ? +1 : -1;
			}
		});
		
		vbox.getChildren().clear();
		for(Node n : nodes) {
			vbox.getChildren().add(n);
		}
	}




	@FXML
	void initialize() {
		assert sizeMajor != null : "fx:id=\"sizeMajor\" was not injected: check your FXML file 'layout_sighteditor_horzRange.fxml'.";
		assert sizeMinor != null : "fx:id=\"sizeMinor\" was not injected: check your FXML file 'layout_sighteditor_horzRange.fxml'.";
		assert vbox != null : "fx:id=\"vbox\" was not injected: check your FXML file 'layout_sighteditor_horzRange.fxml'.";
	}
	
}
