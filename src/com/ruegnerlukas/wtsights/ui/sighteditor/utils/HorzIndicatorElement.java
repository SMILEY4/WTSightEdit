package com.ruegnerlukas.wtsights.ui.sighteditor.utils;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;

public abstract class HorzIndicatorElement {


	public ContainerHBox box;
	public Spinner<Integer> spinner;
	public ComboBox<String> combo;
	public Button button;




	public HorzIndicatorElement(int value, boolean isMajor) {

		spinner = new Spinner<Integer>();
		spinner.setMinWidth(70);
		spinner.setMaxWidth(80);
		spinner.setEditable(true);
		HBox.setHgrow(spinner, Priority.NEVER);
		
		FXUtils.initSpinner(spinner, value, -100, 100, 4, 0, new ChangeListener<Integer>() {
			@Override
			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				onRangeChanged(newValue.intValue());
			}
		});
		
		combo = new ComboBox<String>();
		combo.setMinWidth(50);
		combo.setMaxWidth(10000);
		HBox.setHgrow(combo, Priority.ALWAYS);
		combo.getItems().addAll("Major", "Minor");
		combo.getSelectionModel().select((isMajor ? 0 : 1));
		combo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				onRankChanged(newValue.equalsIgnoreCase("Major"));
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
		box.getChildren().addAll(spinner, combo, button);
	}




	public abstract void onRangeChanged(int range);




	public abstract void onRankChanged(boolean isMajor);




	public abstract void onRemove();

}
