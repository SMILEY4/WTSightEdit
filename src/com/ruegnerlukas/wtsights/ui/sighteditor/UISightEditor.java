package com.ruegnerlukas.wtsights.ui.sighteditor;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.DataWriter;
import com.ruegnerlukas.wtsights.data.WorkingData;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticData;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.data.sight.elements.Element;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementType;
import com.ruegnerlukas.wtsights.ui.ElementIcons;
import com.ruegnerlukas.wtsights.ui.Workflow;
import com.ruegnerlukas.wtsights.ui.sighteditor.modules.Module;
import com.ruegnerlukas.wtsights.ui.sighteditor.rendering.OverlayRenderer;
import com.ruegnerlukas.wtsights.ui.sighteditor.rendering.SightRenderer;
import com.ruegnerlukas.wtutils.Config;
import com.ruegnerlukas.wtutils.Conversion;
import com.ruegnerlukas.wtutils.FXUtils;
import com.ruegnerlukas.wtutils.canvas.WTCanvas;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

public class UISightEditor {

	private Stage stage;
	
	@FXML private ResourceBundle resources;
	@FXML private URL location;

	// canvas
	@FXML private AnchorPane paneCanvas;
	public WTCanvas wtCanvas;
	
	@FXML private CheckBox cbShowSelections;
	@FXML private Label labelPosMil;
	@FXML private Label labelPosSS;
	
	// ui
	@FXML private Label labelVehicleName;
	@FXML private AnchorPane paneGeneral;
	@FXML private AnchorPane paneEnvironment;
	
	@FXML private ListView<String> listViewElements;
	
	@FXML private Button btnAddElement;
	@FXML private Button btnRenameElement;
	@FXML private Button btnRemoveElement;
	@FXML private AnchorPane paneElements;
	
	private WorkingData data;

	private UIGeneral ctrlGeneral;
	private UIEnvironment ctrlEnvironment;

	private Map<ElementType,Module> mapElementControllers = new HashMap<ElementType,Module>();
	private Map<ElementType,Parent> mapElementPanels = new HashMap<ElementType,Parent>();

	
	
	
	
	public static void openNew(BallisticData dataBall) {
		
		Logger.get().info("Navigate to 'SightEditor New' (" + Workflow.toString(Workflow.steps) + ") vehicle=" + (dataBall == null ? "null" : dataBall.vehicle.name) );

		int width = Config.app_window_size.x;
		int height = Config.app_window_size.y;
		
		Object[] sceneObjects = FXUtils.openFXScene(null, "/ui/sightEditor/layout_sighteditor.fxml", width, height, "Edit Sight");
		UISightEditor controller = (UISightEditor)sceneObjects[0];
		Stage stage = (Stage)sceneObjects[1];
		
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override public void handle(WindowEvent event) {
				if(controller.wtCanvas != null && controller.wtCanvas.ses != null) {
					controller.wtCanvas.ses.shutdownNow();
				}
			}
		});
		
		controller.create(stage, dataBall);
	}



	public static void openNew(BallisticData dataBall, SightData dataSight) {
		
		Logger.get().info("Navigate to 'SightEditor New' (" + Workflow.toString(Workflow.steps) + ") vehicle=" + (dataBall == null ? "null" : dataBall.vehicle.name) + "; sightData="+dataSight);

		int width = Config.app_window_size.x;
		int height = Config.app_window_size.y;
		
		Object[] sceneObjects = FXUtils.openFXScene(null, "/ui/sightEditor/layout_sighteditor.fxml", width, height, "Edit Sight");
		UISightEditor controller = (UISightEditor)sceneObjects[0];
		Stage stage = (Stage)sceneObjects[1];
		
		controller.create(stage, dataBall, dataSight);
	}


	

	private void create(Stage stage, BallisticData dataBall) {
		create(stage, dataBall, new SightData(true));
	}
	
	
	
	
	private void create(Stage stage, BallisticData dataBall, SightData dataSight) {
		this.stage = stage;
		data = new WorkingData();
		data.dataBallistic = dataBall;
		data.dataSight = dataSight;
		create();
	}

	
	
	
	private void create() {
		
		// CANVAS
		wtCanvas = new WTCanvas(paneCanvas) {
			@Override public void onMouseMoved() {
				setLabelsPos();
			}
			@Override public void onMouseDragged() {
				setLabelsPos();
			}
			@Override public void onMousePressed(MouseButton btn) {
				setLabelsPos();
			}
			@Override public void onMouseReleased(MouseButton btn) {
				setLabelsPos();
			}
			@Override public void onKeyReleased(KeyCode code) {
//				wtCanvas.repaint();
			}
			@Override public void onRepaint(GraphicsContext g) {
				if(wtCanvas != null) {
					SightRenderer.draw(wtCanvas.canvas, g, data);
				}
			}
			@Override public void onRepaintOverlay(GraphicsContext g) {
				if(wtCanvas != null && cbShowSelections.isSelected()) {
					OverlayRenderer.draw(wtCanvas, g, data);
				}
			}
		};
		wtCanvas.rebuildCanvas(1920, 1080);
		
		labelVehicleName.setText(data.dataBallistic.vehicle.namePretty);
		
		try {
			FXMLLoader loader = new FXMLLoader(UISightEditor.class.getResource("/ui/sightEditor/layout_general.fxml"));
			Parent root = (Parent) loader.load();
			paneGeneral.getChildren().add(root);
			AnchorPane.setTopAnchor(root, 0.0);
			AnchorPane.setBottomAnchor(root, 0.0);
			AnchorPane.setLeftAnchor(root, 0.0);
			AnchorPane.setRightAnchor(root, 0.0);
			ctrlGeneral = (UIGeneral) loader.getController();
			ctrlGeneral.setEditor(this);
			ctrlGeneral.create();
		} catch (IOException e) {
			Logger.get().error(e);
		}
		
		try {
			FXMLLoader loader = new FXMLLoader(UISightEditor.class.getResource("/ui/sightEditor/layout_environment.fxml"));
			Parent root = (Parent) loader.load();
			paneEnvironment.getChildren().add(root);
			AnchorPane.setTopAnchor(root, 0.0);
			AnchorPane.setBottomAnchor(root, 0.0);
			AnchorPane.setLeftAnchor(root, 0.0);
			AnchorPane.setRightAnchor(root, 0.0);
			ctrlEnvironment = (UIEnvironment) loader.getController();
			ctrlEnvironment.setEditor(this);
			ctrlEnvironment.create();
		} catch (IOException e) {
			Logger.get().error(e);
		}
		
		initModule(ElementType.RANGEFINDER, "/ui/sightEditor/layout_element_rangefinder.fxml");
		initModule(ElementType.CENTRAL_VERT_LINE, "/ui/sightEditor/layout_element_centralVertLine.fxml");
		initModule(ElementType.CENTRAL_HORZ_LINE, "/ui/sightEditor/layout_element_centralHorzLine.fxml");
		initModule(ElementType.HORZ_RANGE_INDICATORS, "/ui/sightEditor/layout_element_horzRangeIndicators.fxml");
		if(data.dataBallistic.elements.isEmpty()) {
			data.dataSight.elements.remove(ElementType.BALLISTIC_RANGE_INDICATORS);
			data.dataSight.elements.remove(ElementType.SHELL_BALLISTICS_BLOCK);
		} else {
			initModule(ElementType.BALLISTIC_RANGE_INDICATORS, "/ui/sightEditor/layout_element_ballRangeIndicators.fxml");
			initModule(ElementType.SHELL_BALLISTICS_BLOCK, "/ui/sightEditor/layout_element_shellBlock.fxml");
		}
		initModule(ElementType.CUSTOM_LINE, "/ui/sightEditor/layout_element_custom_line.fxml");
		initModule(ElementType.CUSTOM_CIRCLE, "/ui/sightEditor/layout_element_custom_circle.fxml");
		initModule(ElementType.CUSTOM_QUAD, "/ui/sightEditor/layout_element_custom_quad.fxml");
		initModule(ElementType.CUSTOM_TEXT, "/ui/sightEditor/layout_element_custom_text.fxml");


		// ELEMENTS LIST
		listViewElements.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override public ListCell<String> call(ListView<String> param) {
				return new ListCell<String>() {
					
					@Override public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if(empty) {
							setGraphic(null);
							setText(null);
						} else {
							String[] split = item.split(";");
							String name = split[0];
							ElementType type = ElementType.get(split[1]);
							ImageView imgView = new ImageView(SwingFXUtils.toFXImage(ElementIcons.getIcon(type.iconIndex), null));
							imgView.setSmooth(true);
							imgView.setPreserveRatio(true);
							imgView.setFitHeight(20);
							setGraphic(imgView);
							setText(name);
						}
					}
					
				};
			}
		});
		
		listViewElements.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(newValue != null) {
					String elementName = newValue.split(";")[0];
					for(Element e : data.dataSight.collectElements()) {
						if(e.name.equals(elementName)) {
							onSelectElement(e);
							break;
						}
					}
				}
			}
		});
		
		for(Element e : data.dataSight.collectElements()) {
			listViewElements.getItems().add(e.name + ";" + e.type);
		}
		
		onSelectElement(null);
		sortList();
		
		wtCanvas.rebuildCanvas(1920, 1080);
		
		Logger.get().debug("SightEditor created");
	
	}

	
	
	
	void initModule(ElementType type, String path) {
		try {
			FXMLLoader loader = new FXMLLoader(UISightEditor.class.getResource(path));
			Parent root = (Parent) loader.load();
			paneGeneral.getChildren().add(root);
			Module ctrl = (Module) loader.getController();
			ctrl.setEditor(this);
			ctrl.create();
			root.setVisible(false);
			mapElementControllers.put(type, ctrl);
			mapElementPanels.put(type, root);
		} catch (IOException e) {
			Logger.get().error(e);
		}
	}
	
	
	
	void setLabelsPos() {
		
		if(wtCanvas.cursorVisible) {
			
			Conversion.get().initialize(wtCanvas.getWidth(), wtCanvas.getHeight(), data.dataBallistic.vehicle.fovOut, data.dataBallistic.vehicle.fovIn, data.dataSight.gnrThousandth);
			
			Vector2d posMil = new Vector2d(wtCanvas.cursorPosition);
			posMil.x -= wtCanvas.getWidth()/2;
			posMil.y -= wtCanvas.getHeight()/2;
			posMil.y *= -1;
			posMil.x = Conversion.get().pixel2mil(posMil.x, wtCanvas.getHeight(), data.dataSight.envZoomedIn);
			posMil.y = Conversion.get().pixel2mil(posMil.y, wtCanvas.getHeight(), data.dataSight.envZoomedIn);
			posMil.x = ((int)(posMil.x*100)) / 100.0;
			posMil.y = ((int)(posMil.y*100)) / 100.0;
			labelPosMil.setText("mil: " + posMil.x + ", " + posMil.y);
			
			Vector2d posSS = new Vector2d(wtCanvas.cursorPosition);
			posSS.x -= wtCanvas.getWidth()/2;
			posSS.y -= wtCanvas.getHeight()/2;
			posSS.y *= -1;
			posSS.x = Conversion.get().pixel2screenspace(posSS.x, wtCanvas.getHeight(), data.dataSight.envZoomedIn);
			posSS.y = Conversion.get().pixel2screenspace(posSS.y, wtCanvas.getHeight(), data.dataSight.envZoomedIn);
			posSS.x = ((int)(posSS.x*1000)) / 1000.0;
			posSS.y = ((int)(posSS.y*1000)) / 1000.0;
			labelPosSS.setText("ss: " + posSS.x + ", " + posSS.y);
			
		} else {
			labelPosMil.setText("mil: - , -");
			labelPosSS.setText("ss: - , -");
		}
		
	}
	
	
	
	void sortList() {
		listViewElements.getItems().sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				int resType = (o1.split(";")[1]).compareTo(o2.split(";")[1]);
				if(resType == 0) {
					return (o1.split(";")[0]).compareTo(o2.split(";")[0]);
				} else {
					return resType;
				}
			}
		});
	}
	
	
	
	
	@FXML
	void onAddElement(ActionEvent event) {
		Element element = UIElementCreate.openNew(this.stage, listViewElements.getItems(), data);
		if(element != null) {
			Logger.get().info("Created element: " + element.type + "; " + element.name);
			data.dataSight.addElement(element);
			listViewElements.getItems().add(listViewElements.getItems().size(), element.name + ";" + element.type.toString());
			listViewElements.getSelectionModel().select(listViewElements.getItems().size()-1);
			sortList();
			wtCanvas.repaint();
		} else {
			Logger.get().info("Created null element");
		}
	}
	
	
	
	
	@FXML
	void onRenameElement(ActionEvent event) {
		String strElement = listViewElements.getSelectionModel().getSelectedItem();
		if(strElement == null) {
			return;
		}
		strElement = strElement.split(";")[0];
		Element element = getElementByName(strElement);
		
		TextInputDialog dialog = new TextInputDialog(element.name);
		dialog.getDialogPane().setMinWidth(400);
		dialog.setTitle("Rename Element (" + element.type.defaultName + ")");

		Optional<String> result = dialog.showAndWait();
		if(result.isPresent()) {
			
			String finalName = result.get() == null ? "" : result.get().trim();
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
			
			for(Element e : data.dataSight.collectElements()) {
				if(e == element) {
					continue;
				}
				String itemName = e.name.split(";")[0];
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
			
			element.name = finalName;
			int index = listViewElements.getSelectionModel().getSelectedIndex();
			listViewElements.getItems().remove(index);
			listViewElements.getItems().add(index, element.name+";"+element.type.toString());
			listViewElements.getSelectionModel().select(index);
			sortList();
			wtCanvas.repaint();
		}
	}
	
	
	
	
	@FXML
	void onRemoveElement(ActionEvent event) {
		
		String strElement = listViewElements.getSelectionModel().getSelectedItem();
		if(strElement == null) {
			return;
		}
		strElement = strElement.split(";")[0];
		
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Delete Element");
		alert.setHeaderText("You are about to delete the element \"" + strElement + "\".");
		alert.setContentText("Do you want to delete this element ?");
		
		ButtonType buttonDelete = new ButtonType("Delete", ButtonData.OK_DONE);
		ButtonType buttonCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(buttonDelete, buttonCancel);
		
		Optional<ButtonType> result = alert.showAndWait();
		
		if(result.get() == buttonDelete) {
			deleteElement(listViewElements.getSelectionModel().getSelectedItem(), true);
			sortList();
			wtCanvas.repaint();
			
		} else if(result.get() == buttonCancel) {
			return;
		}
	}
	
	
	
	
	void deleteElement(String elementName, boolean isFullItem) {
		if(isFullItem) {
			listViewElements.getItems().remove(elementName);
			data.dataSight.removeElement(elementName.split(";")[0]);
		} else {
			for(String item : listViewElements.getItems()) {
				if(item.split(";")[0].equals(elementName)) {
					listViewElements.getItems().remove(item);
					data.dataSight.removeElement(elementName);
					return;
				}
			}
			data.dataSight.removeElement(elementName);
		}
	}
	
	
	
	
	void onSelectElement(Element element) {
		Logger.get().debug("SELECT " + (element == null ? "null" : element.name));
		
		for(Node node : paneElements.getChildren()) {
			node.setVisible(false);
		}
		paneElements.getChildren().clear();
		
		if(element == null) {
			// do nothing
			
		} else {

			// disable delete button
			btnRemoveElement.setDisable(false);
			int typeCount = 0;
			for(Element e : data.dataSight.collectElements()) {
				if(e.type == element.type) {
					typeCount++;
				}
			}
			if(typeCount <= element.type.minCount) {
				btnRemoveElement.setDisable(true);
			}
			
			
			// display module
			Module module = mapElementControllers.get(element.type);
			Parent root = mapElementPanels.get(element.type);
			
			if( module != null && root != null) {
				root.setVisible(true);
				paneElements.getChildren().add(root);
				AnchorPane.setTopAnchor(root, 0.0);
				AnchorPane.setBottomAnchor(root, 0.0);
				AnchorPane.setLeftAnchor(root, 0.0);
				AnchorPane.setRightAnchor(root, 0.0);
				module.setElement(element);
			}

		}
		
		data.dataSight.selectedElement = element;
		wtCanvas.repaint();
		
	}
	
	
	
	
	@FXML
	void onExport(ActionEvent event) {
		
		FileChooser fc = new FileChooser();
		fc.setTitle("Save Sight");
		
		File fileSelected = fc.showSaveDialog(stage);
		if(fileSelected == null) {
			return;
		}
		
		File file = new File(fileSelected.getAbsolutePath() + ".blk");
		
		try {
			if(!DataWriter.saveSight(data.dataSight, data.dataBallistic, file)) {
				Logger.get().warn("(Alert) Sight could not be saved.");
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText(null);
				alert.setContentText("Calibration could not be saved.");
				alert.showAndWait();
			} else {
				Logger.get().info("Saved sight to " + file);
			}
			
		} catch (Exception e) {
			Logger.get().error(e);;
		}
		
	}
	
	
	
	
	public Element getElementByName(String name) {
		for(Element e : data.dataSight.collectElements()) {
			if(e.name.equals(name)) {
				return e;
			}
		}
		return null;
	}
	
	
	
	public WorkingData getData() {
		return data;
	}

}
