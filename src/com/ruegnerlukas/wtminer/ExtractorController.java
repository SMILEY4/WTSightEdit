package com.ruegnerlukas.wtminer;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class ExtractorController {


	@FXML private TextField fieldWTDir;
	@FXML private Button btnSearchWTDir;

	@FXML private TextField fieldOutput;
	@FXML private Button btnSearchOutput;

	@FXML private Button btnCreate;




	public void create(Stage stage) {

		btnSearchWTDir.setOnAction(e -> {
			DirectoryChooser dc = new DirectoryChooser();
			File directory = dc.showDialog(stage);
			if (directory != null) {
				fieldWTDir.setText(directory.getAbsolutePath());
			} else {
				fieldWTDir.setText("");
			}
		});

		btnSearchOutput.setOnAction(e -> {
			DirectoryChooser dc = new DirectoryChooser();
			File directory = dc.showDialog(stage);
			if (directory != null) {
				fieldOutput.setText(directory.getAbsolutePath());
			} else {
				fieldOutput.setText("");
			}
		});


		btnCreate.setOnAction(e -> {
			if (fieldWTDir.getText().isEmpty() || fieldOutput.getText().isEmpty()) {
				System.out.println("Could not create file: one input-field is empty.");
			}
			try {
				ExtractorLogic.extract(fieldWTDir.getText(), fieldOutput.getText());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});


	}




}
