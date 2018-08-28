package com.ruegnerlukas.wtutils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.WTSights;
import com.ruegnerlukas.wtsights.ui.main.UIMainMenu;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class FXUtils {


	
	static class StringConverterInt extends StringConverter<Integer> {
		
		@Override
		public String toString(Integer value) {
			if (value == null) {
				return "";
				}
			return ""+value.intValue();
		}
		
		@Override
		public Integer fromString(String value) {
			try {
				if (value == null) {
					return 0;
					}
				value = value.trim();
				if (value.length() < 1) {
					return 0;
				}
				return Integer.parseInt(value);
			} catch (Exception ex) {
				return 0;
			}
		}
		
	}
	
	
	static class StringConverterDouble extends StringConverter<Double> {

		private final DecimalFormat format;
		private final double defaultValue;
		
		
		public StringConverterDouble(int decPlaces) {
			this(decPlaces, 0.0);
		}
		
		public StringConverterDouble(int decPlaces, double defaultValue) {
			String strDecPlaces = "";
			for(int i=0; i<decPlaces; i++) {
				strDecPlaces += "#";
			}
			format = new DecimalFormat("#" + (strDecPlaces.isEmpty() ? "" : ".") + strDecPlaces);
			this.defaultValue = defaultValue;
		}
		
		
		@Override
		public String toString(Double value) {
			if(value == null) {
				return "NaN";
			} else {
				return format.format(value);
			}
		}

		@Override
		public Double fromString(String value) {
			try {
				
				if(value ==  null) {
					return defaultValue;
				} else {
					value = value.trim();
					if(value.length() < 1) {
						return defaultValue;
					} else {
						return format.parse(value).doubleValue();
					}
				}
				
			} catch(ParseException e) {
				return defaultValue;
			}
		}
		
	}
	
	
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void initSpinner(Spinner<?> spinner, double defaultValue, double min, double max, double step, int decPlaces, ChangeListener listener) {
		if(decPlaces <= 0) {
			SpinnerValueFactory valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory((int)min, (int)max, (int)defaultValue, (int)step);
			valueFactory.setConverter(new StringConverterInt());
			spinner.setValueFactory(valueFactory);
			
		} else {
			SpinnerValueFactory valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max, defaultValue, step);
			valueFactory.setConverter(new StringConverterDouble(decPlaces, defaultValue));
			spinner.setValueFactory(valueFactory);
		}
		
		if(listener != null) {
			spinner.valueProperty().addListener(listener);
		}
	}
	
	
	
	
	public static void addIcons(Stage stage) {
		stage.getIcons().add(new Image("/icons/wtseIcon256.png"));
		stage.getIcons().add(new Image("/icons/wtseIcon128.png"));
		stage.getIcons().add(new Image("/icons/wtseIcon48.png"));
	}
	
	
	
	public static Object[] openFXScene(Stage stage, String pathFXML, double width, double height, String title) {
		return openFXScene(stage, pathFXML, width, height, title, false);
	}
	
	public static Object[] openFXScene(Stage stage, String pathFXML, double width, double height, String title, boolean wait) {
		
		if(stage == null) {
			stage = new Stage();
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(WTSights.getPrimaryStage());
			FXUtils.addIcons(stage);
		}

		FXMLLoader loader = new FXMLLoader(UIMainMenu.class.getResource(pathFXML));
		Parent root = null;
		try {
			root = (Parent) loader.load();
		} catch (IOException e) {
			Logger.get().error("Error loading fxmlScene: " + pathFXML, e);
			return null;
		}

		
		Scene scene = new Scene(root, width, height, true, SceneAntialiasing.BALANCED);
		stage.setTitle(title);
		stage.setScene(scene);
		if(wait) {
			stage.showAndWait();
		} else {
			stage.show();
		}
		
		return new Object[]{loader.getController(), stage};
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	public static void tableViewDisableReorder(TableView table, TableColumn... columns) {
		table.getColumns().addListener(new ListChangeListener() {
			public boolean suspended;
			@Override public void onChanged(Change c) {
				c.next();
				if(c.wasReplaced() && !suspended) {
					this.suspended = true;
					table.getColumns().setAll();
					suspended = false;
				}
			}
		});
	}
	
	
	
	public static void createCustomTable(AnchorPane parent, VBox boxTable, String[] colNames, int[] colSizes, EventHandler<ActionEvent> handlerButtonNew, EventHandler<MouseEvent> handlerSort) {
		
		boxTable.setMinSize(ScrollPane.USE_COMPUTED_SIZE, ScrollPane.USE_COMPUTED_SIZE);
		boxTable.setPrefSize(ScrollPane.USE_COMPUTED_SIZE, ScrollPane.USE_COMPUTED_SIZE);
		boxTable.setMaxSize(ScrollPane.USE_COMPUTED_SIZE, ScrollPane.USE_COMPUTED_SIZE);		
		parent.getChildren().add(boxTable);
		AnchorPane.setTopAnchor(boxTable, 0.0);
		AnchorPane.setBottomAnchor(boxTable, 0.0);
		AnchorPane.setLeftAnchor(boxTable, 0.0);
		AnchorPane.setRightAnchor(boxTable, 0.0);
		
		// HEADER
		HBox boxHeader = new HBox();
		boxHeader.setMinSize(ScrollPane.USE_COMPUTED_SIZE, 31);
		boxHeader.setPrefSize(ScrollPane.USE_COMPUTED_SIZE, 31);
		boxHeader.setMaxSize(ScrollPane.USE_COMPUTED_SIZE, 31);	
		boxHeader.setStyle("-fx-background-color:  linear-gradient(#f8f8f8, #e7e7e7); -fx-border-color:  linear-gradient(#fafafa, #b5b5b5); -fx-border-radius: 3;");
		boxTable.getChildren().add(boxHeader);
		
		for(int i=0; i<colNames.length; i++) {
			Label label = new Label(colNames[i]);
			label.setAlignment(Pos.CENTER);
			label.setMinSize(colSizes[i], 31);
			label.setPrefSize(colSizes[i], 31);
			label.setMaxSize(ScrollPane.USE_COMPUTED_SIZE, 31);
			label.setStyle("-fx-border-color:  linear-gradient(#fafafa, #b5b5b5); -fx-border-radius: 3;");
			if(i == 0) {
				label.setOnMouseClicked(handlerSort);
			}
			boxHeader.getChildren().add(label);
		}
		
		int width = 0;
		for(int i=0; i<colSizes.length; i++) {
			width += colSizes[i];
		}
		
		Button btnNew = new Button("New");
		btnNew.setAlignment(Pos.CENTER);
		btnNew.setMinSize(width+31, 31);
		btnNew.setPrefSize(width+31, 31);
		btnNew.setOnAction(handlerButtonNew);
		boxTable.getChildren().add(btnNew);
	}
	
	
	
}
















