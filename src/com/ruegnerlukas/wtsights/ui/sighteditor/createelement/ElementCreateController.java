package com.ruegnerlukas.wtsights.ui.sighteditor.createelement;

import java.util.List;
import java.util.Map;

import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.sight.elements.Element;
import com.ruegnerlukas.wtsights.data.sight.elements.ElementType;
import com.ruegnerlukas.wtsights.ui.ElementIcons;
import com.ruegnerlukas.wtsights.ui.view.IViewController;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.ParamKey;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

public class ElementCreateController implements IViewController {

	@FXML private ComboBox<ElementType> choiceType;
	@FXML private TextField fieldName;

	private ElementCreateService service;
	
	
	
	
	

	@Override
	public void create(Map<ParamKey, Object> parameters) {
		
		@SuppressWarnings("unchecked") List<Element> existingElements = (List<Element>)parameters.get(ParamKey.LIST_SIGHT_ELEMENTS);
		DataPackage data = (DataPackage) parameters.get(ParamKey.DATA_PACKAGE);
		service = (ElementCreateService) ViewManager.getService(View.ELEMENT_CREATE);
		
		service.init(existingElements, data);
		
		List<ElementType> availableTypes = service.getAvailableTypes();

		choiceType.setButtonCell(new ListCell<ElementType>() {
			@Override protected void updateItem(ElementType item, boolean empty) {
				super.updateItem(item, empty);
				setText(item.defaultName);
				if (item == null || empty) {
					setGraphic(null);
				} else {
					ImageView imgView = new ImageView(SwingFXUtils.toFXImage(ElementIcons.getIcon(item.iconIndex), null));
					imgView.setSmooth(true);
					imgView.setPreserveRatio(true);
					imgView.setFitHeight(20);
					setGraphic(imgView);
					setText(item.defaultName);
				}
			}
		});
		
		choiceType.setCellFactory(new Callback<ListView<ElementType>, ListCell<ElementType>>() {
			@Override public ListCell<ElementType> call(ListView<ElementType> p) {
				return new ListCell<ElementType>() {
					@Override protected void updateItem(ElementType item, boolean empty) {
						super.updateItem(item, empty);
						setText(item.defaultName);
						if (item == null || empty) {
							setGraphic(null);
						} else {
							ImageView imgView = new ImageView(SwingFXUtils.toFXImage(ElementIcons.getIcon(item.iconIndex), null));
							imgView.setSmooth(true);
							imgView.setPreserveRatio(true);
							imgView.setFitHeight(20);
							setGraphic(imgView);
							setText(item.defaultName);
						}
					}
				};
			}
		});
		
		choiceType.getItems().addAll(availableTypes);
		
		choiceType.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ElementType>() {
			@Override public void changed(ObservableValue<? extends ElementType> observable, ElementType oldValue, ElementType newValue) {
				onTypeSelect(newValue);
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
		service.finish(null, null);
	}




	@FXML
	void onDone(ActionEvent event) {

		if(this.choiceType.getItems().isEmpty()) {
			service.finish(null, null);
			return;
	
		} else {
			String finalName = fieldName.getText() == null ? "" : fieldName.getText().trim();
			
			final int validationCode = service.validateElementName(finalName);
			if(validationCode == 0) {
				service.finish(choiceType.getValue(), finalName);
			}
			if(validationCode == 1) {
				FXUtils.showAlert(ViewManager.getResources().getString("se_ce_alert_name_null"), ViewManager.getStage(View.ELEMENT_CREATE));
				return;
			}
			if(validationCode == 2) {
				FXUtils.showAlert(ViewManager.getResources().getString("se_ce_alert_name_empty"), ViewManager.getStage(View.ELEMENT_CREATE));
				return;
			}
			if(validationCode == 3) {
				FXUtils.showAlert(ViewManager.getResources().getString("se_ce_alert_name_duplicate"), ViewManager.getStage(View.ELEMENT_CREATE));
				return;
			}
			
		}
	}




}




