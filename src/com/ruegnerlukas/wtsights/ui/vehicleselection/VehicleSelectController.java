package com.ruegnerlukas.wtsights.ui.vehicleselection;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.Database;
import com.ruegnerlukas.wtsights.data.vehicle.Vehicle;
import com.ruegnerlukas.wtsights.ui.view.IViewController;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.ParamKey;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

public class VehicleSelectController implements IViewController {


	@FXML private ResourceBundle resources;
	@FXML private URL location;

	@FXML private TextField textfieldFilter;
	@FXML private ComboBox<Vehicle> comboVehicles;

	private VehicleSelectService service;
	private Map<ParamKey,Object> parameters;
	
	
	
	@Override
	public void create(Map<ParamKey,Object> parameters) {
		
		this.parameters = parameters;
		this.service = (VehicleSelectService)ViewManager.getService(View.VEHICLE_SELECT, true);
		
		comboVehicles.setButtonCell(new ListCell<Vehicle>() {
			@Override protected void updateItem(Vehicle item, boolean empty) {
				super.updateItem(item, empty);
				if(item == null || empty) {
					setGraphic(null);
					setText("");
				} else {
					setGraphic(null);
					setText(item.namePretty);
				}
			}
		});
		comboVehicles.setCellFactory(new Callback<ListView<Vehicle>, ListCell<Vehicle>>() {
			@Override public ListCell<Vehicle> call(ListView<Vehicle> param) {
				return new ListCell<Vehicle>() {
					@Override protected void updateItem(Vehicle item, boolean empty) {
						super.updateItem(item, empty);
						if(item == null || empty) {
							setGraphic(null);
							setText("");
						} else {
							setGraphic(null);
							setText(item.namePretty);
						}
					}
				};
			}
		});
		
		onFilter(null);
	}

	

	
	@FXML
	void onFilter(ActionEvent event) {
		
		String strFilter = "";
		if(event != null) {
			strFilter = textfieldFilter.getText().trim().toLowerCase();
		}
		
		List<Vehicle> vehicles = service.applyFilter(strFilter, Database.getVehicles());
		
		if(vehicles.size() > 0) {
			comboVehicles.setItems(FXCollections.observableArrayList(vehicles));
			comboVehicles.setValue(null);
			comboVehicles.getSelectionModel().select(vehicles.get(0));
		
		} else {
			comboVehicles.setItems(FXCollections.observableArrayList());
			comboVehicles.getSelectionModel().clearSelection();
		}
		
		Logger.get().info("Filtered list by '" + strFilter + "' (" + vehicles.size() + ")");
		
	}

	


	@FXML
	void onCancel(ActionEvent event) {
		FXUtils.closeFXScene(View.VEHICLE_SELECT);
	}




	@FXML
	void onNext(ActionEvent event) {
		service.next(parameters, comboVehicles.getValue());
	}

	
}
