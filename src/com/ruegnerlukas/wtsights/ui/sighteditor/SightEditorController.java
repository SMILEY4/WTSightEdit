package com.ruegnerlukas.wtsights.ui.sighteditor;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.simpleutils.collectionbuilders.MapBuilder;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticData;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.data.sight.sightElements.Element;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.ui.ElementIcons;
import com.ruegnerlukas.wtsights.ui.sighteditor.createelement.ElementCreateService;
import com.ruegnerlukas.wtsights.ui.sighteditor.modules.Module;
import com.ruegnerlukas.wtsights.ui.sighteditor.rendering.OverlayRenderer;
import com.ruegnerlukas.wtsights.ui.sighteditor.rendering.SightRenderer;
import com.ruegnerlukas.wtsights.ui.view.IViewController;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.ParamKey;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;
import com.ruegnerlukas.wtutils.FXUtils;
import com.ruegnerlukas.wtutils.canvas.WTCanvas;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
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
import javafx.scene.control.Spinner;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

public class SightEditorController implements IViewController {

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
	
	@FXML private ListView<Element> listViewElements;
	
	@FXML private Button btnAddElement;
	@FXML private Button btnRenameElement;
	@FXML private Button btnRemoveElement;
	@FXML private AnchorPane paneElements;
	
	private Map<ElementType,Module> mapElementControllers = new HashMap<ElementType,Module>();
	private Map<ElementType,Parent> mapElementPanels = new HashMap<ElementType,Parent>();

	private SightEditorService service;
	
	// debug conversion meters->mil
	@FXML private Spinner<Double> spinnerConvMMil;
	public static double convMMil = 0.095;
	
	
	
	
	@Override
	public void create(Map<ParamKey,Object> parameters) {
		service = (SightEditorService) ViewManager.getService(View.SIGHT_EDITOR, true);
		service.initDataPackage((BallisticData)parameters.get(ParamKey.BALLISTIC_DATA), (SightData)parameters.get(ParamKey.SIGHT_DATA));
	
		// debug conversion meters->mil
		spinnerConvMMil.setVisible(false);
		FXUtils.initSpinner(spinnerConvMMil, convMMil, 0, 99, 0.0005, 5, true, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				convMMil = newValue.doubleValue();
				service.getDataPackage().dataSight.setElementsDirty();
				wtCanvas.repaint();
			}
		});
		
		// CANVAS
		wtCanvas = new WTCanvas(paneCanvas) {
			@Override public void onMouseMoved() {
				setLabelsCursorPos();
			}
			@Override public void onMouseDragged() {
				setLabelsCursorPos();
			}
			@Override public void onMousePressed(MouseButton btn) {
				setLabelsCursorPos();
			}
			@Override public void onMouseReleased(MouseButton btn) {
				setLabelsCursorPos();
			}
			@Override public void onKeyReleased(KeyCode code) {
			}
			@Override public void onRepaint(GraphicsContext g) {
				if(wtCanvas != null) {
					SightRenderer.draw(wtCanvas.canvas, g, service.getDataPackage());
				}
			}
			@Override public void onRepaintOverlay(GraphicsContext g) {
				if(wtCanvas != null && cbShowSelections.isSelected()) {
					OverlayRenderer.draw(wtCanvas, g, service.getDataPackage());
				}
			}
		};
		wtCanvas.rebuildCanvas(1920, 1080);
		
		labelVehicleName.setText(service.getVehicleName());
		
		// PANEL: ENVIRONMENT
		Parent rootEnvironment = ViewManager.getLoader(View.SEM_ENVIRONMENT).openNew(null, new MapBuilder<ParamKey,Object>().add(ParamKey.DATA_PACKAGE,service.getDataPackage()).get());
		paneEnvironment.getChildren().add(rootEnvironment);
		AnchorPane.setTopAnchor(rootEnvironment, 0.0);
		AnchorPane.setBottomAnchor(rootEnvironment, 0.0);
		AnchorPane.setLeftAnchor(rootEnvironment, 0.0);
		AnchorPane.setRightAnchor(rootEnvironment, 0.0);
		
		// PANEL: GENERAL
		Parent rootGeneral = ViewManager.getLoader(View.SEM_GENERAL).openNew(null, new MapBuilder<ParamKey,Object>().add(ParamKey.DATA_PACKAGE,service.getDataPackage()).get());
		paneGeneral.getChildren().add(rootGeneral);
		AnchorPane.setTopAnchor(rootGeneral, 0.0);
		AnchorPane.setBottomAnchor(rootGeneral, 0.0);
		AnchorPane.setLeftAnchor(rootGeneral, 0.0);
		AnchorPane.setRightAnchor(rootGeneral, 0.0);
		
		// ELEMENT MODULES
		initModule(ElementType.RANGEFINDER, "/ui/sightEditor/layout_element_rangefinder.fxml");
		initModule(ElementType.CENTRAL_VERT_LINE, "/ui/sightEditor/layout_element_centralVertLine.fxml");
		initModule(ElementType.CENTRAL_HORZ_LINE, "/ui/sightEditor/layout_element_centralHorzLine.fxml");
		initModule(ElementType.HORZ_RANGE_INDICATORS, "/ui/sightEditor/layout_element_horzRangeIndicators.fxml");
		if(service.getDataPackage().dataBallistic.elements.isEmpty()) {
			service.getDataPackage().dataSight.elements.remove(ElementType.BALLISTIC_RANGE_INDICATORS);
			service.getDataPackage().dataSight.elements.remove(ElementType.SHELL_BALLISTICS_BLOCK);
		} else {
			initModule(ElementType.BALLISTIC_RANGE_INDICATORS, "/ui/sightEditor/layout_element_ballRangeIndicators.fxml");
			initModule(ElementType.SHELL_BALLISTICS_BLOCK, "/ui/sightEditor/layout_element_shellBlock.fxml");
		}
		initModule(ElementType.CUSTOM_LINE, "/ui/sightEditor/layout_element_custom_line.fxml");
		initModule(ElementType.CUSTOM_CIRCLE_OUTLINE, "/ui/sightEditor/layout_element_custom_circle.fxml");
		initModule(ElementType.CUSTOM_QUAD_FILLED, "/ui/sightEditor/layout_element_custom_quad_filled.fxml");
		initModule(ElementType.CUSTOM_QUAD_OUTLINE, "/ui/sightEditor/layout_element_custom_quad_outline.fxml");
		initModule(ElementType.CUSTOM_TEXT, "/ui/sightEditor/layout_element_custom_text.fxml");

		// ELEMENTS LIST
		listViewElements.setCellFactory(new Callback<ListView<Element>, ListCell<Element>>() {
			@Override public ListCell<Element> call(ListView<Element> param) {
				return new ListCell<Element>() {
					@Override public void updateItem(Element item, boolean empty) {
						super.updateItem(item, empty);
						if(empty) {
							setGraphic(null);
							setText(null);
						} else {
							ImageView imgView = new ImageView(SwingFXUtils.toFXImage(ElementIcons.getIcon(item.type.iconIndex), null));
							imgView.setSmooth(true);
							imgView.setPreserveRatio(true);
							imgView.setFitHeight(20);
							setGraphic(imgView);
							setText(item.name);
						}
					}
				};
			}
		});
		
		listViewElements.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Element>() {
			@Override public void changed(ObservableValue<? extends Element> observable, Element oldValue, Element newValue) {
				if(newValue != null) {
					onSelectElement(newValue);
				}
			}
		});
		
		listViewElements.getItems().addAll(service.getElements());
		
		onSelectElement(null);
		sortElementList();
		
		wtCanvas.rebuildCanvas(1920, 1080);
		
		Logger.get().debug("SightEditor created");
		
	}
	
	
	
	
	void initModule(ElementType type, String path) {
		try {
			FXMLLoader loader = new FXMLLoader(SightEditorController.class.getResource(path));
			loader.setResources(ViewManager.getResources());
			Parent root = (Parent) loader.load();
			paneGeneral.getChildren().add(root);
			Module ctrl = (Module) loader.getController();
			ctrl.create(service.getDataPackage());
			root.setVisible(false);
			mapElementControllers.put(type, ctrl);
			mapElementPanels.put(type, root);
		} catch (IOException e) {
			Logger.get().error(e);
		}
	}
	
	
	
	
	void sortElementList() {
		listViewElements.getItems().sort(new Comparator<Element>() {
			@Override public int compare(Element o1, Element o2) {
				int resType = o1.type.compareTo(o2.type);
				if(resType == 0) {
					return o1.type.compareTo(o2.type);
				} else {
					return resType;
				}
			}
		});
	}
	
	
	
	
	void onSelectElement(Element element) {
		Logger.get().debug("SELECT " + (element == null ? "null" : element.name));
		
		service.selectElement(element);
		
		for(Node node : paneElements.getChildren()) {
			node.setVisible(false);
		}
		paneElements.getChildren().clear();
		
		if(element != null) {

			// disable delete-btn, if element count of that type would fall below threshold 
			btnRemoveElement.setDisable(false);
			int typeCount = 0;
			for(Element e : service.getElements()) {
				if(e.type == element.type) {
					typeCount++;
				}
			}
			if(typeCount <= element.type.minCount) {
				btnRemoveElement.setDisable(true);
			}
			
			// display module of selected element
			Module module = mapElementControllers.get(element.type);
			Parent root = mapElementPanels.get(element.type);
			System.out.println("ON SELECT  " + module + "  " + root);
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
		
		wtCanvas.repaint();
	}
	
	
	
	
	@FXML
	void onAddElement(ActionEvent event) {
		
		ViewManager.getLoader(View.ELEMENT_CREATE).openNew(ViewManager.getStage(View.SIGHT_EDITOR),
				new MapBuilder<ParamKey,Object>()
				.add(ParamKey.LIST_SIGHT_ELEMENTS,listViewElements.getItems())
				.add(ParamKey.DATA_PACKAGE, service.getDataPackage())
				.get());
		
		Element element = ElementCreateService.getCreatedElement();
		
		if(element != null) {
			Logger.get().info("Created element: " + element.type + "; " + element.name);
			service.addElement(element);
			listViewElements.getItems().add(listViewElements.getItems().size(), element);
			listViewElements.getSelectionModel().select(listViewElements.getItems().size()-1);
			sortElementList();
			wtCanvas.repaint();
		} else {
			Logger.get().info("Created null element");
		}
	}
	
	
	
	
	@FXML
	void onRenameElement(ActionEvent event) {
		Element element = listViewElements.getSelectionModel().getSelectedItem();
		if(element == null) {
			return;
		}
		TextInputDialog dialog = new TextInputDialog(element.name);
		dialog.getDialogPane().setMinWidth(400);
		dialog.setTitle(ViewManager.getResources().getString("se_elements_rename_dialog").replaceAll("{elementname}", element.type.defaultName));

		Optional<String> result = dialog.showAndWait();
		if(result.isPresent()) {
			
			String finalName = result.get() == null ? "" : result.get().trim();
			
			final int validationCode = service.validateElementName(element, finalName);
			
			if(validationCode == 0) {
				service.renameElement(element, finalName);
				int index = listViewElements.getSelectionModel().getSelectedIndex();
				listViewElements.getItems().remove(index);
				listViewElements.getItems().add(index, element);
				listViewElements.getSelectionModel().select(index);
				sortElementList();
				wtCanvas.repaint();
			}
			if(validationCode == 1) {
				FXUtils.showAlert(ViewManager.getResources().getString("se_elements_rename_null"), ViewManager.getStage(View.SIGHT_EDITOR));
				return;
			}
			if(validationCode == 2) {
				FXUtils.showAlert(ViewManager.getResources().getString("se_elements_rename_empty"), ViewManager.getStage(View.SIGHT_EDITOR));
				return;
			}
			if(validationCode == 3) {
				FXUtils.showAlert(ViewManager.getResources().getString("se_elements_rename_duplicate"), ViewManager.getStage(View.SIGHT_EDITOR));
				return;
			}
			
		}
	}
	
	
	
	
	@FXML
	void onRemoveElement(ActionEvent event) {
		
		Element element = listViewElements.getSelectionModel().getSelectedItem();
		if(element == null) {
			return;
		}
		
		Alert alert = new Alert(AlertType.WARNING);
		alert.initOwner(ViewManager.getStage(View.SIGHT_EDITOR));
		alert.setTitle(ViewManager.getResources().getString("se_elements_delete_dialog_title"));
		alert.setHeaderText(ViewManager.getResources().getString("se_elements_delete_dialog_header") + " \"" + element.name + "\".");
		alert.setContentText(ViewManager.getResources().getString("se_elements_delete_dialog_content"));
		
		ButtonType buttonDelete = new ButtonType(ViewManager.getResources().getString("se_elements_delete_dialog_delete"), ButtonData.OK_DONE);
		ButtonType buttonCancel = new ButtonType(ViewManager.getResources().getString("se_elements_delete_dialog_cancel"), ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(buttonDelete, buttonCancel);
		
		Optional<ButtonType> result = alert.showAndWait();
		
		if(result.get() == buttonDelete) {
			onDeleteElement(listViewElements.getSelectionModel().getSelectedItem());
			
		} else if(result.get() == buttonCancel) {
			return;
		}
	}
	
	
	
	
	void onDeleteElement(Element element) {
		if(element != null) {
			service.deleteElement(element);
			listViewElements.getItems().remove(element);
			sortElementList();
			wtCanvas.repaint();
		}
	}
	
	
	
	
	void setLabelsCursorPos() {
		if(wtCanvas.cursorVisible) {
			Vector2d posMil = service.getCursorPosMil(wtCanvas.cursorPosition, wtCanvas.getWidth(), wtCanvas.getHeight());
			Vector2d posSS = service.getCursorPosSS(wtCanvas.cursorPosition, wtCanvas.getWidth(), wtCanvas.getHeight());
			labelPosMil.setText("mil: " + posMil.x + ", " + posMil.y);
			labelPosSS.setText("ss: " + posSS.x + ", " + posSS.y);
		} else {
			labelPosMil.setText("mil: - , -");
			labelPosSS.setText("ss: - , -");
		}
	}
	
	
	
	
	@FXML
	void onExport(ActionEvent event) {
		service.export();
	}

	
	
}
