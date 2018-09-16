package com.ruegnerlukas.wtsights.ui.sighteditor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.WTSights;
import com.ruegnerlukas.wtsights.data.WorkingData;
import com.ruegnerlukas.wtsights.data.sight.elements.Element;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementBallRangeIndicator;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCentralHorzLine;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCentralVertLine;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCustomCircle;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCustomLine;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCustomQuad;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementCustomText;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementHorzRangeIndicators;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementRangefinder;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementShellBlock;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementType;
import com.ruegnerlukas.wtsights.ui.ElementIcons;
import com.ruegnerlukas.wtsights.ui.main.UIMainMenu;
import com.ruegnerlukas.wtutils.Config;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class UIElementCreate {

	private Stage stage;
	
	@FXML private ComboBox<String> choiceType;
	@FXML private TextField fieldName;

	private List<String> existingItems;
	
	public Element createdElement;
	

	public static Element openNew(Stage owner, List<String> existingItems, WorkingData data) {
		
		Logger.get().info("Open 'ElementCreate'");

		Stage stage = new Stage();
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(owner);
		FXUtils.addIcons(stage);

		FXMLLoader loader = new FXMLLoader(UIMainMenu.class.getResource("/ui/sightEditor/layout_element_create.fxml"));
		Parent root = null;
		try {
			root = (Parent) loader.load();
		} catch (IOException e) {
			Logger.get().error("Error loading fxmlScene: " + "/ui/sightEditor/layout_element_create.fxml", e);
			return null;
		}

		
		Scene scene = new Scene(root, 500, 225, true, SceneAntialiasing.BALANCED);
		if("dark".equals(Config.app_style)) {
			if(WTSights.DEV_MODE) {
				String css = FXUtils.class.getResource("/ui/modena_dark.css").toExternalForm();
				scene.getStylesheets().add(css);
			} else {
				String css = FXUtils.class.getResource("/ui/modena_dark.css").toExternalForm();
				scene.getStylesheets().add(css);
			}
		}

		stage.setTitle("Create Element");
		stage.setScene(scene);
		
		UIElementCreate controller = (UIElementCreate)loader.getController();
		controller.create(stage, existingItems, data);
		
		stage.showAndWait();
		
		return controller.createdElement;
	}
	
	
	
	
	private void create(Stage stage, List<String> existingItems, WorkingData data) {
		
		this.stage = stage;
		this.existingItems = existingItems;
		
		List<String> availableTypes = new ArrayList<String>();
		
		for(ElementType type : ElementType.values()) {

			if(data.dataBallistic.elements.isEmpty() && (type == ElementType.SHELL_BALLISTICS_BLOCK || type == ElementType.BALLISTIC_RANGE_INDICATORS) ) {
				continue;
			}
			
			int listCount = 0;
			for(String str : existingItems) {
				ElementType typeList = ElementType.get(str.split(";")[1]);
				if(typeList == type) {
					listCount++;
				}
			}
			
			if(listCount < type.maxCount) {
				availableTypes.add(type.defaultName);
			} else {
				continue;
			}
			
		}
		
		
		choiceType.setButtonCell(new ListCell<String>() {
			@Override protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				setText(item);
				if (item == null || empty) {
					setGraphic(null);
				} else {
					ImageView imgView = new ImageView(SwingFXUtils.toFXImage(ElementIcons.getIcon(ElementType.getByDefaultName(item).iconIndex), null));
					imgView.setSmooth(true);
					imgView.setPreserveRatio(true);
					imgView.setFitHeight(20);
					setGraphic(imgView);
					setText(item);
				}
			}
		});
		
		choiceType.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override public ListCell<String> call(ListView<String> p) {
				return new ListCell<String>() {
					@Override protected void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						setText(item);
						if (item == null || empty) {
							setGraphic(null);
						} else {
							ImageView imgView = new ImageView(SwingFXUtils.toFXImage(ElementIcons.getIcon(ElementType.getByDefaultName(item).iconIndex), null));
							imgView.setSmooth(true);
							imgView.setPreserveRatio(true);
							imgView.setFitHeight(20);
							setGraphic(imgView);
							setText(item);
						}
					}
				};
			}
		});
		
		choiceType.getItems().addAll(availableTypes);
		
		choiceType.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				onTypeSelect(ElementType.getByDefaultName(newValue));
			}
		});
		if(!choiceType.getItems().isEmpty()) {
			choiceType.getSelectionModel().select(0);
		}
		
	}
	
	
	
	
	void onTypeSelect(ElementType type) {
		fieldName.setText(type.defaultName);
	}
	
	


	@FXML
	void onCancel(ActionEvent event) {
		this.stage.close();
	}




	@FXML
	void onDone(ActionEvent event) {

		if(this.choiceType.getItems().isEmpty()) {
			createdElement = null;
			this.stage.close();
			return;
		}
		
		String finalName = fieldName.getText() == null ? "" : fieldName.getText().trim();
		if(finalName.isEmpty()) {
			Logger.get().warn("(Alert) The name of the element can not be empty.");
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText("The name of the element can not be empty.");
			alert.showAndWait();
			return;
		}
		
		
		if(finalName.contains(";")) {
			Logger.get().warn("(Alert) The name of the element can not contain a ';'.");
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText("The name of the element can not contain a ';'.");
			alert.showAndWait();
			return;
		}
		
		
		for(String item : existingItems) {
			String itemName = item.split(";")[0];
			if(itemName.equalsIgnoreCase(finalName)) {
				Logger.get().warn("(Alert) The name of the element must be unique.");
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText(null);
				alert.setContentText("The name of the element must be unique.");
				alert.showAndWait();
				return;
			}
			
		}
		
		ElementType finalType = ElementType.getByDefaultName(this.choiceType.getSelectionModel().getSelectedItem());
		if(finalType == ElementType.BALLISTIC_RANGE_INDICATORS) {
			createdElement = new ElementBallRangeIndicator(finalName);
		}
		if(finalType == ElementType.CENTRAL_HORZ_LINE) {
			createdElement = new ElementCentralHorzLine(finalName);
		}
		if(finalType == ElementType.CENTRAL_VERT_LINE) {
			createdElement = new ElementCentralVertLine(finalName);
		}
		if(finalType == ElementType.CUSTOM_CIRCLE) {
			createdElement = new ElementCustomCircle(finalName);
		}
		if(finalType == ElementType.CUSTOM_LINE) {
			createdElement = new ElementCustomLine(finalName);
		}
		if(finalType == ElementType.CUSTOM_QUAD) {
			createdElement = new ElementCustomQuad(finalName);
		}
		if(finalType == ElementType.CUSTOM_TEXT) {
			createdElement = new ElementCustomText(finalName);
		}
		if(finalType == ElementType.HORZ_RANGE_INDICATORS) {
			createdElement = new ElementHorzRangeIndicators(finalName);
		}
		if(finalType == ElementType.RANGEFINDER) {
			createdElement = new ElementRangefinder(finalName);
		}
		if(finalType == ElementType.SHELL_BALLISTICS_BLOCK) {
			createdElement = new ElementShellBlock(finalName);
		}
		
		this.stage.close();
	}

}




