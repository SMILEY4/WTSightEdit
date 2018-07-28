package com.ruegnerlukas.wtsights.ui.sighteditor;


import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationAmmoData;
import com.ruegnerlukas.wtsights.data.calibration.CalibrationData;
import com.ruegnerlukas.wtsights.data.sight.BallisticsBlock;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.data.sight.SightData.ScaleMode;
import com.ruegnerlukas.wtsights.data.sight.SightData.TextAlign;
import com.ruegnerlukas.wtsights.data.sight.SightData.TriggerGroup;
import com.ruegnerlukas.wtsights.data.vehicle.Ammo;
import com.ruegnerlukas.wtsights.data.vehicle.Weapon;
import com.ruegnerlukas.wtsights.sight.Conversion;
import com.ruegnerlukas.wtsights.ui.sighteditor.utils.BallisticIndicatorElement;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;

public class UIShellBlocks {

	@FXML private ResourceBundle resources;
	@FXML private URL location;
	
	private UISightEditor editor;
	private SightData dataSight;
	private CalibrationData dataCalib;
	
	@FXML private CheckBox cbIsVisible;
	
	@FXML private TextField nameBlock;
	@FXML private Button btnAddBlock;
	@FXML private ComboBox<String> selectBlock;
	@FXML private Button btnRemoveBlock;
	@FXML private Label labelBlock;
	@FXML private ComboBox<String> selectAmmo;
	@FXML private VBox boxBlock;
	
	@FXML private VBox boxVertical;
	@FXML private VBox boxRadial;
	
	@FXML private ComboBox<String> circleMode;
	
	@FXML private ComboBox<String> scaleMode;
	@FXML private Spinner<Double> radialStretch;
	@FXML private Spinner<Double> radialAngle;
	@FXML private CheckBox cbRadiusUseMils;
	@FXML private Spinner<Double> radialRadius;
	
	@FXML private CheckBox cbCanMove;

	@FXML private Spinner<Double> textShift;
	@FXML private Spinner<Double> textOffsetX;
	@FXML private Spinner<Double> textOffsetXvert;
	@FXML private Spinner<Double> textOffsetY;
	@FXML private ComboBox<String> textAlign;
	@FXML private ComboBox<String> textAlignVert;

	@FXML private Spinner<Double> distPosX;
	@FXML private Spinner<Double> distPosY;
	@FXML private Spinner<Double> distPosXvert;
	@FXML private Spinner<Double> distPosYvert;
	@FXML private Spinner<Double> sizeMajorVert;
	@FXML private Spinner<Double> sizeMinor;

	@FXML private VBox boxLine;
	@FXML private Spinner<Double> sizeLine;

	@FXML private VBox boxCircle;
	@FXML private Spinner<Double> circleWidth;
	@FXML private Spinner<Double> circleRadius;

	@FXML private CheckBox cbDrawAdd;
	@FXML private Spinner<Double> sizeAddMajor;
	@FXML private Spinner<Double> sizeAddMinor;
	
	@FXML private CheckBox cbDrawUpward;
	
	@FXML private VBox vbox;
	
	public BallisticsBlock selectedBlock = null;
	
	
	
	
	
	
	public void setEditor(UISightEditor editor) {
		this.editor = editor;
	}
	
	
	
	
	public void setDataSight(SightData data) {
		this.dataSight = data;
	}
	
	
	
	
	public void setDataCalib(CalibrationData dataCalib) {
		this.dataCalib = dataCalib;
	}
	
	
	
	
	public void create() {
		
		for(CalibrationAmmoData data : dataCalib.ammoData) {
			selectAmmo.getItems().add(data.ammoName);
		}
		selectAmmo.getSelectionModel().select(0);
		selectAmmo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				onAmmoSelected(newValue);
			}
		});
		
		selectBlock.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				onSelectBlock(newValue);
			}
		});
		
		nameBlock.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(nameBlock.getText().trim().isEmpty()) {
					btnAddBlock.setDisable(true);
				} else {
					btnAddBlock.setDisable(false);
				}
			}
		});
		if(nameBlock.getText().trim().isEmpty()) {
			btnAddBlock.setDisable(true);
		}
		if(selectBlock.getValue() == null) {
			btnRemoveBlock.setDisable(true);
		}
		
		circleMode.getItems().addAll("Lines", "Circles");
		circleMode.getSelectionModel().select(dataSight.brIndicators.bCircleMode ? "Circles" : "Lines");
		circleMode.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				onCircleMode(newValue.equalsIgnoreCase("Circles"));
			}
		});
		
		
		scaleMode.getItems().addAll(ScaleMode.VERTICAL.toString(), ScaleMode.RADIAL.toString());
		scaleMode.getSelectionModel().select(dataSight.brIndicators.bScaleMode.toString());
		scaleMode.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				onScaleMode(newValue);
			}
		});
		
		FXUtils.initSpinner(radialStretch, dataSight.brIndicators.bRadialStretch, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onRadialStretch(newValue.doubleValue());
			}
		});
		
		FXUtils.initSpinner(radialAngle, dataSight.brIndicators.bRadialStretch, -1000, 1000, 1, 1, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onRadialAngle(newValue.doubleValue());
			}
		});
		
		cbRadiusUseMils.setSelected(dataSight.brIndicators.bRadiusUseMils);
		
		FXUtils.initSpinner(radialRadius, dataSight.brIndicators.bRadialRadius, 0, 1000, (dataSight.brIndicators.bRadiusUseMils ? 0.5 : 0.001), (dataSight.brIndicators.bRadiusUseMils ? 1 : 3), new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onRadialRadius(newValue.doubleValue());
			}
		});
		
		cbCanMove.setSelected(dataSight.brIndicators.bMove);
		
		
		FXUtils.initSpinner(textShift, dataSight.brIndicators.bTextShift, -1000, 1000, 0.1, 1, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onTextShift(newValue.doubleValue());
			}
		});
		
		FXUtils.initSpinner(textOffsetX, dataSight.brIndicators.bTextOffset.x, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onTextOffset(newValue.doubleValue(), textOffsetY.getValue().doubleValue());
			}
		});
		
		FXUtils.initSpinner(textOffsetXvert, dataSight.brIndicators.bTextOffset.x, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onTextOffset(newValue.doubleValue(), textOffsetY.getValue().doubleValue());
			}
		});
		
		FXUtils.initSpinner(textOffsetY, dataSight.brIndicators.bTextOffset.y, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onTextOffset(dataSight.brIndicators.bScaleMode==ScaleMode.VERTICAL ? textOffsetXvert.getValue().doubleValue() : textOffsetX.getValue().doubleValue(), newValue.doubleValue());
			}
		});
		
		textAlign.getItems().addAll(TextAlign.LEFT.toString(), TextAlign.CENTER.toString(), TextAlign.RIGHT.toString());
		textAlign.getSelectionModel().select(dataSight.brIndicators.bTextAlign.toString());
		textAlign.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				onTextAlign(newValue);
			}
		});
		
		textAlignVert.getItems().addAll(TextAlign.LEFT.toString(), TextAlign.CENTER.toString(), TextAlign.RIGHT.toString());
		textAlignVert.getSelectionModel().select(dataSight.brIndicators.bTextAlign.toString());
		textAlignVert.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				onTextAlign(newValue);
			}
		});
		
		
		
		
		FXUtils.initSpinner(distPosX, dataSight.brIndicators.bMainPos.x, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onDistPosChanged(newValue.doubleValue(), distPosY.getValue().doubleValue());
			}
		});

		FXUtils.initSpinner(distPosY, dataSight.brIndicators.bMainPos.y, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onDistPosChanged(distPosX.getValue().doubleValue(), newValue.doubleValue());
			}
		});
		
		
		FXUtils.initSpinner(distPosXvert, dataSight.brIndicators.bMainPos.x, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onDistPosChanged(newValue.doubleValue(), distPosYvert.getValue().doubleValue());
			}
		});
		
		FXUtils.initSpinner(distPosYvert, dataSight.brIndicators.bMainPos.y, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onDistPosChanged(distPosXvert.getValue().doubleValue(), newValue.doubleValue());
			}
		});
		
		
		
		FXUtils.initSpinner(sizeMajorVert, dataSight.brIndicators.bSizeMain.x, 0, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onSizeMajorChanged(newValue.doubleValue());
			}
		});
		
		FXUtils.initSpinner(sizeMinor, dataSight.brIndicators.bSizeMain.y, 0, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onSizeMinorChanged(newValue.doubleValue());
			}
		});
		
		cbDrawAdd.setSelected(dataSight.brIndicators.bDrawCenteredLines);
		
		FXUtils.initSpinner(sizeAddMinor, dataSight.brIndicators.bSizeCentered.x, 0, 1000, 0.005, 3, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onAddSizeChanged(sizeAddMajor.getValue().doubleValue(), newValue.doubleValue());
			}
		});
		
		FXUtils.initSpinner(sizeAddMajor, dataSight.brIndicators.bSizeCentered.y, 0, 1000, 0.005, 3, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onAddSizeChanged(newValue.doubleValue(), sizeAddMinor.getValue().doubleValue());
			}
		});
		
		
		
		FXUtils.initSpinner(sizeLine, dataSight.brIndicators.bSizeMain.x, 0, 1000, 0.005, 3, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onSizeLineChanged(newValue.doubleValue());
			}
		});
		
		FXUtils.initSpinner(circleWidth, dataSight.brIndicators.bSizeMain.x, 0, 1000, 0.005, 3, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onSizeCircleWidth(newValue.doubleValue());
			}
		});
		
		FXUtils.initSpinner(circleRadius, dataSight.brIndicators.bSizeMain.y, 0, 1000, 0.005, 3, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				onSizeCircleRadius(newValue.doubleValue());
			}
		});
		
		
		cbDrawUpward.setSelected(dataSight.brIndicators.bDrawUpward);
		
		for(int i=0; i<dataSight.brIndicators.bDists.size(); i++) {
			addIndicator(dataSight.brIndicators.bDists.get(i), dataSight.brIndicators.bMajors.get(i),
					dataSight.brIndicators.bExtensions.get(i),dataSight.brIndicators.bTextOffsets.get(i).x, dataSight.brIndicators.bTextOffsets.get(i).y, false);
		}
		
		
		if(selectBlock.getValue() == null) {
			boxBlock.setDisable(true);
		}
		
		
		for(Entry<String,BallisticsBlock> entry : dataSight.shellBlocks.entrySet()) {
			selectBlock.getItems().add(entry.getValue().name);
		}
		for(String blockName : dataSight.shellBlocks.keySet()) {
			onSelectBlock(blockName);
			break;
		}
		
	}
	
	
	
	
	
	
	@FXML
	void onAddBlock(ActionEvent event) {
		
		String ammoName = selectAmmo.getValue();
		String blockName = nameBlock.getText().trim();
		nameBlock.setText("");
		
		if(dataSight.shellBlocks.containsKey(blockName)) {
			Logger.get().warn( "(Alert) Block " + blockName + " already exists!");
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText("Block " + blockName + " already exists!");
			alert.showAndWait();
			return;
		}
		
		if(ammoName != null && blockName != null && !blockName.isEmpty()) {

			Weapon weapon = null;
			Ammo ammo = null;
			weapon: for(Weapon w : dataCalib.vehicle.weaponsList) {
				for(Ammo a : w.ammo) {
					if(a.name.equals(ammoName)) {
						weapon = w;
						ammo = a;
						break weapon;
					}
				}
			}
			if(ammo == null) {
				Logger.get().warn(" No ammo found: " + ammoName);
				return;
			}
			BallisticsBlock block = new BallisticsBlock(false, blockName);
			block.bBulletName = ammo.name;
			block.bBulletType = ammo.type;
			block.bBulletSpeed = ammo.speed;
			block.bTriggerGroup = TriggerGroup.get(weapon.triggerGroup);
			dataSight.shellBlocks.put(block.name, block);
			selectBlock.getItems().add(block.name);
			onSelectBlock(block.name);
		}
		
		editor.repaintCanvas();
	}

	
	
	
	@FXML
	void onRemoveBlock(ActionEvent event) {
		if(selectedBlock == null) {
			return;
		}

		dataSight.shellBlocks.remove(selectedBlock.name);
		selectBlock.getItems().remove(selectedBlock.name);
		selectedBlock = null;
		
		String nextBlock = null;
		for(Entry<String,BallisticsBlock> entry : dataSight.shellBlocks.entrySet()) {
			nextBlock = entry.getValue().name;
			break;
		}
		onSelectBlock(nextBlock);
		
		editor.repaintCanvas();
	}
	
	
	
	void onSelectBlock(String blockName) {
		
		dataSight.selectedSubElement = "";
		
		if(blockName == null) {
			boxBlock.setDisable(true);
			return;
		}
		
		selectedBlock = dataSight.shellBlocks.get(blockName);
		if(selectedBlock == null) {
			boxBlock.setDisable(true);
			return;
		}
		
		dataSight.selectedSubElement = blockName;
		
		boxBlock.setDisable(false);
		selectBlock.setValue(blockName);
		btnRemoveBlock.setDisable(false);
		
		
		cbIsVisible.setSelected(selectedBlock.isVisible);
		
		labelBlock.setText(selectedBlock.name + ":");
		
		distPosX.getValueFactory().setValue(selectedBlock.bMainPos.x);
		distPosY.getValueFactory().setValue(selectedBlock.bMainPos.y);

		distPosXvert.getValueFactory().setValue(selectedBlock.bMainPos.x);
		distPosYvert.getValueFactory().setValue(selectedBlock.bMainPos.y);
		
		cbRadiusUseMils.setSelected(selectedBlock.bRadiusUseMils);
		radialRadius.getValueFactory().setValue(selectedBlock.bRadialRadius);
		
		radialStretch.getValueFactory().setValue(selectedBlock.bRadialStretch);
		radialAngle.getValueFactory().setValue(selectedBlock.bRadialAngle);
		
		textShift.getValueFactory().setValue(selectedBlock.bTextShift);
		textOffsetX.getValueFactory().setValue(selectedBlock.bTextOffset.x);
		textOffsetY.getValueFactory().setValue(selectedBlock.bTextOffset.y);
		textAlign.setValue(selectedBlock.bTextAlign.toString());
		
		cbDrawAdd.setSelected(selectedBlock.bDrawCenteredLines);
		sizeAddMajor.getValueFactory().setValue(selectedBlock.bSizeCentered.x);
		sizeAddMinor.getValueFactory().setValue(selectedBlock.bSizeCentered.y);

		
		if(selectedBlock.bScaleMode == ScaleMode.VERTICAL) {
			sizeMajorVert.getValueFactory().setValue(selectedBlock.bSizeMain.x);
			sizeMinor.getValueFactory().setValue(selectedBlock.bSizeMain.y);
			
		} else {
			if(selectedBlock.bCircleMode) {
				circleWidth.getValueFactory().setValue(selectedBlock.bSizeMain.x);
				circleRadius.getValueFactory().setValue(selectedBlock.bSizeMain.y);
			} else {
				sizeLine.getValueFactory().setValue(selectedBlock.bSizeMain.x);
			}
		}
		

		cbDrawUpward.setSelected(selectedBlock.bDrawUpward);
		
		cbCanMove.setSelected(selectedBlock.bMove);
				
		selectAmmo.setValue(selectedBlock.bBulletName);
		
		scaleMode.setValue(selectedBlock.bScaleMode.toString());
		
		circleMode.setValue(selectedBlock.bCircleMode ? "Circles" : "Lines");
		
		clearIndicatorList();
		
		for(int i=0; i<selectedBlock.bDists.size(); i++) {
			addIndicator(
					selectedBlock.bDists.get(i),
					selectedBlock.bMajors.get(i),
					selectedBlock.bExtensions.get(i),
					selectedBlock.bTextOffsets.get(i).x,
					selectedBlock.bTextOffsets.get(i).y,
					false);
		}
		
		editor.repaintCanvas();
	}
	
	
	
	
	void onAmmoSelected(String ammoName) {
		if(selectedBlock == null) {
			return;
		}
		
		Weapon weapon = null;
		Ammo ammo = null;
		
		weapon: for(Weapon w : dataCalib.vehicle.weaponsList) {
			for(Ammo a : w.ammo) {
				if(a.name.equals(ammoName)) {
					ammo = a;
					weapon = w;
					break weapon;
				}
			}
		}
		if(ammo == null) {
			Logger.get().warn(this, "No ammo found.: " + ammoName);
			return;
		}
		
		selectedBlock.bBulletName = ammoName;
		selectedBlock.bBulletSpeed = ammo.speed;
		selectedBlock.bBulletType = ammo.type;
		selectedBlock.bTriggerGroup = TriggerGroup.get(weapon.triggerGroup);
		
		editor.repaintCanvas();
	}
	
	
	
	
	@FXML
	void onIsVisible(ActionEvent event) {
		if(selectedBlock == null) {
			return;
		}
		selectedBlock.isVisible = cbIsVisible.isSelected();
		editor.repaintCanvas();
	}
	
	

	
	void onCircleMode(boolean circle) {
		if(selectedBlock == null) {
			return;
		}
		selectedBlock.bCircleMode = circle;
		if(circle) {
			boxLine.setVisible(false);
			boxCircle.setVisible(true);
		} else {
			boxLine.setVisible(true);
			boxCircle.setVisible(false);
		}
		editor.repaintCanvas();
	}
	
	
	
	
	void onScaleMode(String strMode) {
		if(selectedBlock == null) {
			return;
		}
		
		if(strMode.equals(ScaleMode.VERTICAL.toString())) { selectedBlock.bScaleMode = ScaleMode.VERTICAL; }
		if(strMode.equals(ScaleMode.RADIAL.toString()))   { selectedBlock.bScaleMode = ScaleMode.RADIAL; }
		
		if(selectedBlock.bScaleMode == ScaleMode.VERTICAL) {
			boxVertical.setDisable(false);
			boxVertical.setVisible(true);
			boxRadial.setDisable(true);
			boxRadial.setVisible(false);
		} else {
			boxVertical.setDisable(true);
			boxVertical.setVisible(false);
			boxRadial.setDisable(false);
			boxRadial.setVisible(true);
		}
		
		editor.repaintCanvas();
	}
	
	
	
	
	void onRadialStretch(double stretch) {
		if(selectedBlock == null) {
			return;
		}
		selectedBlock.bRadialStretch = stretch;
		editor.repaintCanvas();
	}
	
	
	
	
	void onRadialAngle(double angle) {
		if(selectedBlock == null) {
			return;
		}
		selectedBlock.bRadialAngle = angle;
		editor.repaintCanvas();
	}
	
	
	
	
	void onRadialRadius(double radius) {
		if(selectedBlock == null) {
			return;
		}
		selectedBlock.bRadialRadius = radius;
		editor.repaintCanvas();
	}
	
	
	
	
	@FXML
	void onRadiusUseMils(ActionEvent event) {
		if(selectedBlock == null) {
			return;
		}
		
		selectedBlock.bRadiusUseMils = cbRadiusUseMils.isSelected();
		
		if(selectedBlock.bRadiusUseMils) {
			FXUtils.initSpinner(radialRadius, Conversion.get().screenspace2mil(selectedBlock.bRadialRadius, dataSight.envZoomedIn), 0, 1000, 0.5, 1, null);
		} else {
			FXUtils.initSpinner(radialRadius, Conversion.get().mil2screenspace(selectedBlock.bRadialRadius, dataSight.envZoomedIn), 0, 1000, 0.001, 3, null);
		}
		
		editor.repaintCanvas();
	}
	
	
	
	
	@FXML
	void onCanMove(ActionEvent event) {
		if(selectedBlock == null) {
			return;
		}
		selectedBlock.bMove = cbCanMove.isSelected();
		editor.repaintCanvas();
	}
	
	
	
	
	void onTextShift(double shift) {
		if(selectedBlock == null) {
			return;
		}
		selectedBlock.bTextShift = shift;
		editor.repaintCanvas();
	}
	
	
	
	
	void onTextOffset(double offX, double offY) {
		if(selectedBlock == null) {
			return;
		}
		selectedBlock.bTextOffset.set(offX, offY);
		editor.repaintCanvas();
	}
	
	
	
	
	void onTextAlign(String strAlign) {
		if(selectedBlock == null) {
			return;
		}
		if(strAlign.equals(TextAlign.LEFT.toString())) 	 { selectedBlock.bTextAlign = TextAlign.LEFT;   }
		if(strAlign.equals(TextAlign.CENTER.toString())) { selectedBlock.bTextAlign = TextAlign.CENTER; }
		if(strAlign.equals(TextAlign.RIGHT.toString()))  { selectedBlock.bTextAlign = TextAlign.RIGHT;  }
		editor.repaintCanvas();
	}
	
	
	

	@FXML
	void onDrawUpward(ActionEvent event) {
		if(selectedBlock == null) {
			return;
		}
		selectedBlock.bDrawUpward = cbDrawUpward.isSelected();
		editor.repaintCanvas();
	}

	


	void onDistPosChanged(double x, double y) {
		if(selectedBlock == null) {
			return;
		}
		selectedBlock.bMainPos.set(x, y);
		editor.repaintCanvas();
	}
	
	
	
	
	void onSizeMajorChanged(double major) {
		if(selectedBlock == null) {
			return;
		}
		selectedBlock.bSizeMain.x = major;
		editor.repaintCanvas();
	}
	
	
	
	
	void onSizeMinorChanged(double minor) {
		if(selectedBlock == null) {
			return;
		}
		selectedBlock.bSizeMain.y = minor;
		editor.repaintCanvas();
	}
	
	
	
	
	void onSizeLineChanged(double size) {
		if(selectedBlock == null) {
			return;
		}
		selectedBlock.bSizeMain.x = size;
		editor.repaintCanvas();
	}
	
	
	
	
	void onSizeCircleWidth(double width) {
		if(selectedBlock == null) {
			return;
		}
		selectedBlock.bSizeMain.x = width;
		editor.repaintCanvas();
	}
	
	
	void onSizeCircleRadius(double radius) {
		if(selectedBlock == null) {
			return;
		}
		selectedBlock.bSizeMain.y = radius;
		editor.repaintCanvas();
	}
	
	
	
	
	@FXML
	void onDrawAdd(ActionEvent event) {
		if(selectedBlock == null) {
			return;
		}
		selectedBlock.bDrawCenteredLines = cbDrawAdd.isSelected();
		editor.repaintCanvas();		
	}
	
	
	
	
	void onAddSizeChanged(double major, double minor) {
		if(selectedBlock == null) {
			return;
		}
		selectedBlock.bSizeCentered.set(major, minor);
		editor.repaintCanvas();
	}
	
	
	
	
	@FXML
	void onNew(ActionEvent event) {
		if(selectedBlock == null) {
			return;
		}
		addIndicator(0, false, 0, 0, 0, true);
	}
	
	
	
	
	private void addIndicator(int valueRange, boolean isMajor, double valueExtend, double valueTextX, double valueTextY, boolean updateList) {
		if(selectedBlock == null) {
			return;
		}
		BallisticIndicatorElement element = new BallisticIndicatorElement(valueRange, isMajor, valueExtend, valueTextX, valueTextY) {
			@Override public void onRangeChanged(int range) {
				updateList();
			}
			@Override public void onRankChanged(boolean isMajor) {
				updateList();
			}
			@Override public void onExtendChanged(double extend) {
				updateList();
			}
			@Override public void onTextXChanged(double textX) {
				updateList();
			}
			@Override public void onTextYChanged(double textY) {
				updateList();
			}
			@Override public void onRemove() {
				vbox.getChildren().remove(this.box);
				updateList();
			}
		};
		this.vbox.getChildren().add(vbox.getChildren().size()-1, element.box);
		if(updateList) {
			updateList();
		}
	}
	
	
	
	
	void clearIndicatorList() {
		Node nodeBtn = null;
		for(Node node : vbox.getChildren()) {
			if(node instanceof Button) {
				nodeBtn = node;
				break;
			}
		}
		vbox.getChildren().clear();
		vbox.getChildren().add(nodeBtn);
	}

	
	
	
	void updateList() {
		if(selectedBlock == null) {
			return;
		}
		
		selectedBlock.bDists.clear();
		selectedBlock.bMajors.clear();
		selectedBlock.bExtensions.clear();
		selectedBlock.bTextOffsets.clear();
		for(Node node : vbox.getChildren()) {
			if(node instanceof ContainerHBox) {
				ContainerHBox box = (ContainerHBox)node;
				BallisticIndicatorElement e = (BallisticIndicatorElement)box.element;
				selectedBlock.bDists.add(e.range.getValue());
				selectedBlock.bMajors.add(e.rank.getValue().contains("Major"));
				selectedBlock.bExtensions.add(e.extend.getValue());
				selectedBlock.bTextOffsets.add(new Vector2d(e.textX.getValue().doubleValue(), e.textY.getValue().doubleValue()));
			}
		}
		editor.repaintCanvas();
	}
	
	
	
	
	
	@FXML
	void onSortList(ActionEvent event) {
		
		if(selectedBlock == null) {
			return;
		}
		
		ObservableList<Node> nodes = FXCollections.observableArrayList();
		nodes.addAll(vbox.getChildren());
		
		Collections.sort(nodes, new Comparator<Node>() {
			@Override
			public int compare(Node a, Node b) {
				if( !(a instanceof ContainerHBox) || !(b instanceof ContainerHBox) ) {
					return -1;
				}
				int valueA = ((BallisticIndicatorElement)((ContainerHBox)a).element).range.getValue();
				int valueB = ((BallisticIndicatorElement)((ContainerHBox)b).element).range.getValue();
				return valueA < valueB ? -1 : +1;
			}
		});
		
		vbox.getChildren().clear();
		for(Node n : nodes) {
			vbox.getChildren().add(n);
		}
	}


	
	
}
