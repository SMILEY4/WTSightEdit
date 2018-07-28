package com.ruegnerlukas.wtsights.ui.sighteditor.utils;

import java.text.DecimalFormat;
import java.text.ParseException;

import com.ruegnerlukas.wtsights.ui.sighteditor.ContainerHBox;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;

public abstract class BallisticIndicatorElement {


	public ContainerHBox box;

	public Spinner<Integer> range;
	public ComboBox<String> rank;
	public Spinner<Double> extend;
	public Spinner<Double> textX;
	public Spinner<Double> textY;

	public Button button;




	public BallisticIndicatorElement(int valueRange, boolean isMajor, double valueExtend, double valueTextX, double valueTextY) {


		range = new Spinner<Integer>();
		range.setEditable(true);
		range.setMinWidth(100);
		range.setMaxWidth(100);
		range.setEditable(true);
		HBox.setHgrow(range, Priority.NEVER);
		
		FXUtils.initSpinner(range, valueRange, 100, 900000, 100, 0, new ChangeListener<Integer>() {
			@Override
			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				onRangeChanged(newValue.intValue());
			}
		});

		rank = new ComboBox<String>();
		rank.setMinWidth(50);
		rank.setMaxWidth(10000);
		HBox.setHgrow(rank, Priority.ALWAYS);
		rank.getItems().addAll("Major", "Minor");
		rank.getSelectionModel().select(isMajor ? 0 : 1);
		rank.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(newValue.equalsIgnoreCase("Major")) {
					textX.setDisable(false);
					textY.setDisable(false);
					onRankChanged(true);
				} else {
					textX.setDisable(true);
					textY.setDisable(true);
					onRankChanged(false);
				}
			}
		});

		extend = new Spinner<Double>();
		extend.setEditable(true);
		extend.setMinWidth(0);
		extend.setPrefWidth(100);
		extend.setEditable(true);
		HBox.setHgrow(extend, Priority.SOMETIMES);
		
		
		FXUtils.initSpinner(extend, valueExtend, -100, 100, 0.005, 3, new ChangeListener<Double>() {
			@Override
			public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onExtendChanged(newValue.doubleValue());
			}
		});
		

		textX = new Spinner<Double>();
		textX.setEditable(true);
		textX.setMinWidth(0);
		textX.setPrefWidth(100);
		textX.setEditable(true);
		HBox.setHgrow(textX, Priority.SOMETIMES);
		
		FXUtils.initSpinner(textX, valueTextX, -100, 100, 0.01, 2, new ChangeListener<Double>() {
			@Override
			public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onTextXChanged(newValue.doubleValue());
			}
		});
		
		textY = new Spinner<Double>();
		textY.setMinWidth(0);
		textY.setPrefWidth(100);
		textY.setEditable(true);
		HBox.setHgrow(textY, Priority.SOMETIMES);
		
		FXUtils.initSpinner(textY, valueTextY, -100, 100, 0.01, 2, new ChangeListener<Double>() {
			@Override
			public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onTextYChanged(newValue.doubleValue());
			}
		});

		button = new Button("X");
		button.setMinWidth(40);
		button.setMaxWidth(40);
		HBox.setHgrow(button, Priority.NEVER);
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				onRemove();
			}
		});

		box = new ContainerHBox();
		box.element = this;
		box.setPrefWidth(10000);
		box.getChildren().addAll(range, rank, extend, textX, textY, button);

		if(isMajor) {
			textX.setDisable(false);
			textY.setDisable(false);
		} else {
			textX.setDisable(true);
			textY.setDisable(true);
		}
		
	}




	public abstract void onRangeChanged(int range);




	public abstract void onRankChanged(boolean isMajor);




	public abstract void onExtendChanged(double extend);




	public abstract void onTextXChanged(double textX);




	public abstract void onTextYChanged(double textY);




	public abstract void onRemove();

}
