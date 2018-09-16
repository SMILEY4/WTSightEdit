package com.ruegnerlukas.wtutils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.WTSights;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticElement;
import com.ruegnerlukas.wtsights.data.ballisticdata.NullElement;
import com.ruegnerlukas.wtsights.data.vehicle.Ammo;
import com.ruegnerlukas.wtsights.ui.AmmoIcons;
import com.ruegnerlukas.wtsights.ui.main.UIMainMenu;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
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
	
	
	
	@SuppressWarnings({ "rawtypes" })
	public static void initSpinner(Spinner<?> spinner, double defaultValue, double min, double max, double step, int decPlaces, ChangeListener listener) {
		initSpinner(spinner, defaultValue, min, max, step, decPlaces, true, listener);
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void initSpinner(Spinner<?> spinner, double defaultValue, double min, double max, double step, int decPlaces, boolean enableMouseWheel, ChangeListener listener) {
		if(decPlaces <= 0) {
			SpinnerValueFactory valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory((int)min, (int)max, (int)defaultValue, (int)step);
			valueFactory.setConverter(new StringConverterInt());
			spinner.setValueFactory(valueFactory);
			
		} else {
			SpinnerValueFactory valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max, defaultValue, step);
			valueFactory.setConverter(new StringConverterDouble(decPlaces, defaultValue));
			spinner.setValueFactory(valueFactory);
		}
		if(enableMouseWheel) {
			spinner.setOnScroll(new EventHandler<ScrollEvent>() {
				@Override public void handle(ScrollEvent event) {

					// early out if is in scrollable scrollpane
					if(spinner.getParent() != null) {
						Parent parent = spinner;
						while( (parent = parent.getParent()) != null) {
							if(parent instanceof ScrollPane) {
								ScrollPane scrollPane = (ScrollPane)parent;
								if(scrollPane.getContent() != null && scrollPane.getContent() instanceof AnchorPane) {
									double heightScroll = scrollPane.getHeight();
									double heightContent = ((AnchorPane)scrollPane.getContent()).getHeight();
									if(heightScroll < heightContent) {
										return;
									} 
								}
							}
						}
					}
					
					
					if(event.getDeltaY() > 0) {
						spinner.getValueFactory().increment(1);
					} else {
						spinner.getValueFactory().decrement(1);
					}
					event.consume();
				}
			});
		}
		if(listener != null) {
			spinner.valueProperty().addListener(listener);
		}
	}
	
	
	
	
	public static void initComboboxBallistic(ComboBox<BallisticElement> combobox) {
		combobox.setButtonCell(new ListCell<BallisticElement>() {
			@Override protected void updateItem(BallisticElement item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText("");
					setGraphic(null);
				} else if(item instanceof NullElement) {
					ImageView imgView = new ImageView(SwingFXUtils.toFXImage(AmmoIcons.getIcon("?"), null));
					imgView.setSmooth(true);
					imgView.setPreserveRatio(true);
					imgView.setFitHeight(40);
					setGraphic(imgView);
					setText("No ballistic data to select");
				} else {
					if(item.ammunition.size() == 1) {
						Ammo ammo = item.ammunition.get(0);
						ImageView imgView = new ImageView(SwingFXUtils.toFXImage(AmmoIcons.getIcon(ammo.type), null));
						imgView.setSmooth(true);
						imgView.setPreserveRatio(true);
						imgView.setFitHeight(40);
						setGraphic(imgView);
						setText(ammo.namePretty);
					} else {
						ImageView imgView = new ImageView(SwingFXUtils.toFXImage(AmmoIcons.getIcon(item.ammunition.get(0).type), null));
						imgView.setSmooth(true);
						imgView.setPreserveRatio(true);
						imgView.setFitHeight(40);
						setGraphic(imgView);
						setText(item.ammunition.get(0).parentWeapon.name);
					}
				}
			}
		});
		combobox.setCellFactory(new Callback<ListView<BallisticElement>, ListCell<BallisticElement>>() {
			@Override public ListCell<BallisticElement> call(ListView<BallisticElement> p) {
				return new ListCell<BallisticElement>() {
					@Override protected void updateItem(BallisticElement item, boolean empty) {
						super.updateItem(item, empty);
						if (item == null || empty) {
							setText("");
							setGraphic(null);
						} else if(item instanceof NullElement) {
							ImageView imgView = new ImageView(SwingFXUtils.toFXImage(AmmoIcons.getIcon("?"), null));
							imgView.setSmooth(true);
							imgView.setPreserveRatio(true);
							imgView.setFitHeight(40);
							setGraphic(imgView);
							setText("No ballistic data to select");
						} else {
							if(item.ammunition.size() == 1) {
								Ammo ammo = item.ammunition.get(0);
								ImageView imgView = new ImageView(SwingFXUtils.toFXImage(AmmoIcons.getIcon(ammo.type), null));
								imgView.setSmooth(true);
								imgView.setPreserveRatio(true);
								imgView.setFitHeight(40);
								setGraphic(imgView);
								setText(ammo.namePretty);
							} else {
								ImageView imgView = new ImageView(SwingFXUtils.toFXImage(AmmoIcons.getIcon(item.ammunition.get(0).type), null));
								imgView.setSmooth(true);
								imgView.setPreserveRatio(true);
								imgView.setFitHeight(40);
								setGraphic(imgView);
								setText(item.ammunition.get(0).parentWeapon.name);
							}
						}
					}
				};
			}
		});
	}
	
	
	
	
	public static void initComboboxBallisticElement(ComboBox<BallisticElement> combobox) {
		combobox.setButtonCell(new ListCell<BallisticElement>() {
			@Override protected void updateItem(BallisticElement item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty || item.ammunition.isEmpty()) {
					setText("");
					setGraphic(null);
				} else {
					ImageView imgView = new ImageView(SwingFXUtils.toFXImage(AmmoIcons.getIcon(item.ammunition.get(0).type), null));
					imgView.setSmooth(true);
					imgView.setPreserveRatio(true);
					imgView.setFitHeight(40);
					setGraphic(imgView);
					String displayName = "";
					for(int i=0; i<item.ammunition.size(); i++) {
						if(i == item.ammunition.size()-1) {
							displayName += item.ammunition.get(i).namePretty + "";
						} else {
							displayName += item.ammunition.get(i).namePretty + ", ";
						}
					}
					setText(displayName);
				}
			}
		});
		combobox.setCellFactory(new Callback<ListView<BallisticElement>, ListCell<BallisticElement>>() {
			@Override public ListCell<BallisticElement> call(ListView<BallisticElement> p) {
				return new ListCell<BallisticElement>() {
					@Override protected void updateItem(BallisticElement item, boolean empty) {
						super.updateItem(item, empty);
						if (item == null || empty || item.ammunition.isEmpty()) {
							setText("");
							setGraphic(null);
						} else {
							ImageView imgView = new ImageView(SwingFXUtils.toFXImage(AmmoIcons.getIcon(item.ammunition.get(0).type), null));
							imgView.setSmooth(true);
							imgView.setPreserveRatio(true);
							imgView.setFitHeight(40);
							setGraphic(imgView);
							String displayName = "";
							for(int i=0; i<item.ammunition.size(); i++) {
								if(i == item.ammunition.size()-1) {
									displayName += item.ammunition.get(i).namePretty + "";
								} else {
									displayName += item.ammunition.get(i).namePretty + ", ";
								}
							}
							setText(displayName);
						}
					}
				};
			}
		});
	}
	
	
	
	
	public static void addIcons(Stage stage) {
		stage.getIcons().add(new Image("/icons/wtseIcon256.png"));
		stage.getIcons().add(new Image("/icons/wtseIcon128.png"));
		stage.getIcons().add(new Image("/icons/wtseIcon48.png"));
	}
	
	
	
	public static Object[] openFXScene(Stage stage, String pathFXML, double width, double height, String title) {
		return openFXScene(stage, pathFXML, width, height, title, "dark".equals(Config.app_style), false);
	}
	
	public static Object[] openFXScene(Stage stage, String pathFXML, double width, double height, String title, boolean styleDark) {
		return openFXScene(stage, pathFXML, width, height, title, styleDark, false);
	}
	
	public static Object[] openFXScene(Stage stage, String pathFXML, double width, double height, String title, boolean styleDark, boolean wait) {
		
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
		if(styleDark) {
			if(WTSights.DEV_MODE) {
				String css = FXUtils.class.getResource("/ui/modena_dark.css").toExternalForm();
				scene.getStylesheets().add(css);
			} else {
				String css = FXUtils.class.getResource("/ui/modena_dark.css").toExternalForm();
				scene.getStylesheets().add(css);
			}
		}
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

		boolean styleDark = "dark".equals(Config.app_style);
		
		// HEADER
		HBox boxHeader = new HBox();
		boxHeader.setMinSize(ScrollPane.USE_COMPUTED_SIZE, 31);
		boxHeader.setPrefSize(ScrollPane.USE_COMPUTED_SIZE, 31);
		boxHeader.setMaxSize(ScrollPane.USE_COMPUTED_SIZE, 31);	
		if(styleDark) {
			boxHeader.setStyle("-fx-background-color:  linear-gradient(#474747, #3a3a3a); -fx-border-color:  linear-gradient(#484848, #5a5a5a); -fx-border-radius: 3;");
		} else {
			boxHeader.setStyle("-fx-background-color:  linear-gradient(#f8f8f8, #e7e7e7); -fx-border-color:  linear-gradient(#fafafa, #b5b5b5); -fx-border-radius: 3;");
		}
		boxTable.getChildren().add(boxHeader);
		
		for(int i=0; i<colNames.length; i++) {
			Label label = new Label(colNames[i]);
			label.setAlignment(Pos.CENTER);
			label.setMinSize(colSizes[i], 31);
			label.setPrefSize(colSizes[i], 31);
			label.setMaxSize(ScrollPane.USE_COMPUTED_SIZE, 31);
			if(styleDark) {
				label.setStyle("-fx-border-color:  linear-gradient(#484848, #5a5a5a); -fx-border-radius: 3;");
			} else {
				label.setStyle("-fx-border-color:  linear-gradient(#fafafa, #b5b5b5); -fx-border-radius: 3;");
			}
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
















