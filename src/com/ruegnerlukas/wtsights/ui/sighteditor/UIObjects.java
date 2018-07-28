package com.ruegnerlukas.wtsights.ui.sighteditor;

import java.net.URL;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.data.sight.SightData.TextAlign;
import com.ruegnerlukas.wtsights.data.sight.objects.CircleObject;
import com.ruegnerlukas.wtsights.data.sight.objects.LineObject;
import com.ruegnerlukas.wtsights.data.sight.objects.QuadObject;
import com.ruegnerlukas.wtsights.data.sight.objects.SightObject;
import com.ruegnerlukas.wtsights.data.sight.objects.SightObject.Movement;
import com.ruegnerlukas.wtsights.data.sight.objects.SightObject.Type;
import com.ruegnerlukas.wtsights.ui.Workflow;
import com.ruegnerlukas.wtsights.ui.screenshotupload.UIScreenshotUpload;
import com.ruegnerlukas.wtutils.FXUtils;
import com.ruegnerlukas.wtsights.data.sight.objects.TextObject;
import com.ruegnerlukas.wtsights.sight.Conversion;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.layout.VBox;

public class UIObjects {


	@FXML private ResourceBundle resources;
	@FXML private URL location;

	private UISightEditor editor;
	private SightData dataSight;

	@FXML private ComboBox<String> comboType;
	
	@FXML private ComboBox<String> comboObjects;

	@FXML private TextField objName;

	@FXML private CheckBox cmnThousandth;
	@FXML private ComboBox<String> cmnMovement;
	@FXML private Spinner<Double> cmnAngle;
	@FXML private CheckBox cmnAutoCenter;
	@FXML private Spinner<Double> cmnCenterX;
	@FXML private Spinner<Double> cmnCenterY;
	@FXML private Spinner<Double> cmnRadCenterX;
	@FXML private Spinner<Double> cmnRadCenterY;
	@FXML private Spinner<Double> cmnSpeed;
	
	@FXML private VBox boxLine;
	@FXML private Spinner<Double> lineStartX;
	@FXML private Spinner<Double> lineStartY;
	@FXML private Spinner<Double> lineEndX;
	@FXML private Spinner<Double> lineEndY;

	@FXML private VBox boxText;
	@FXML private TextField txtText;
	@FXML private Spinner<Double> txtPosX;
	@FXML private Spinner<Double> txtPosY;
	@FXML private Spinner<Double> txtSize;
	@FXML private ComboBox<String> txtAlign;

	@FXML private VBox boxCircle;
	@FXML private Spinner<Double> circlePosX;
	@FXML private Spinner<Double> circlePosY;
	@FXML private Spinner<Double> circleDiameter;
	@FXML private Spinner<Double> circleSeg1;
	@FXML private Spinner<Double> circleSeg2;
	@FXML private Spinner<Double> circleSize;

	@FXML private VBox boxQuad;
	@FXML private Spinner<Double> quadPos1x;
	@FXML private Spinner<Double> quadPos1y;
	@FXML private Spinner<Double> quadPos2x;
	@FXML private Spinner<Double> quadPos2y;
	@FXML private Spinner<Double> quadPos3x;
	@FXML private Spinner<Double> quadPos3y;
	@FXML private Spinner<Double> quadPos4x;
	@FXML private Spinner<Double> quadPos4y;

	public SightObject selectedObject;

	
	
	
	

	public void setEditor(UISightEditor editor) {
		this.editor = editor;
	}




	public void setDataSight(SightData data) {
		this.dataSight = data;
	}




	public void create() {
		
		// type
		comboType.getItems().addAll(Type.LINE.toString(), Type.TEXT.toString(), Type.CIRCLE.toString(), Type.QUAD.toString());
		comboType.getSelectionModel().select(Type.LINE.toString());
		
		
		// objects
		for(Entry<String,SightObject> entry : dataSight.objects.entrySet()) {
			comboObjects.getItems().add(entry.getValue().name);
		}
		comboObjects.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				onSelectObject(newValue);
			}
		});
		
		
		// name
		objName.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(dataSight.objects.containsKey(objName.getText().trim())) {
					Logger.get().warn(this, "Object with name " + objName.getText() + " already exists.");
					objName.setText(comboObjects.getValue());
				} else {
					onRenameObject(objName.getText());
				}
			}
		});
		
		
		// thousandth
		cmnThousandth.setSelected(false);
		
		
		// move
		cmnMovement.getItems().addAll(Movement.STATIC.toString(), Movement.MOVE.toString(), Movement.MOVE_RADIAL.toString());
		cmnMovement.getSelectionModel().select(Movement.STATIC.toString());
		cmnMovement.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				onSelectMovement(Movement.get(newValue));
			}
		});
		
		// angle
		FXUtils.initSpinner(cmnAngle, 0, 0, 1000, 1, 1, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				SightObject obj = dataSight.objects.get(comboObjects.getValue());
				if(obj == null) {
					return;
				}
				obj.cmnAngle = newValue.doubleValue();
				editor.repaintCanvas();
			}
		});
		
		cmnAutoCenter.setSelected(true);
		cmnCenterX.setDisable(cmnAutoCenter.isSelected());
		cmnCenterY.setDisable(cmnAutoCenter.isSelected());
		
		// centerX
		FXUtils.initSpinner(cmnCenterX, 0, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				SightObject obj = dataSight.objects.get(comboObjects.getValue());
				if(obj == null) {
					return;
				}
				obj.cmnCenter.x = newValue.doubleValue();
				editor.repaintCanvas();
			}
		});
		
		// centerY
		FXUtils.initSpinner(cmnCenterY, 0, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				SightObject obj = dataSight.objects.get(comboObjects.getValue());
				if(obj == null) {
					return;
				}
				obj.cmnCenter.y = newValue.doubleValue();
				editor.repaintCanvas();
			}
		});
		
		
		// rad centerX
		FXUtils.initSpinner(cmnRadCenterX, 0, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				SightObject obj = dataSight.objects.get(comboObjects.getValue());
				if(obj == null) {
					return;
				}
				obj.cmnRadCenter.x = newValue.doubleValue();
				editor.repaintCanvas();
			}
		});
		
		// rad centerY
		FXUtils.initSpinner(cmnRadCenterY, 0, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				SightObject obj = dataSight.objects.get(comboObjects.getValue());
				if(obj == null) {
					return;
				}
				obj.cmnRadCenter.y = newValue.doubleValue();
				editor.repaintCanvas();
			}
		});
		
		// speed
		FXUtils.initSpinner(cmnSpeed, 1, -1000, 1000, 0.05, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				SightObject obj = dataSight.objects.get(comboObjects.getValue());
				if(obj == null) {
					return;
				}
				obj.cmnSpeed = newValue.doubleValue();
				editor.repaintCanvas();
			}
		});
		
		// line pos 
		FXUtils.initSpinner(lineStartX, 0, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				SightObject obj = dataSight.objects.get(comboObjects.getValue());
				if(obj == null) {
					return;
				}
				if(obj.type == Type.LINE) {
					LineObject line = (LineObject)obj;
					line.start.x = newValue.doubleValue();
				}
				editor.repaintCanvas();
			}
		});
		
		FXUtils.initSpinner(lineStartY, 0, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				SightObject obj = dataSight.objects.get(comboObjects.getValue());
				if(obj == null) {
					return;
				}
				if(obj.type == Type.LINE) {
					LineObject line = (LineObject)obj;
					line.start.y = newValue.doubleValue();
				}
				editor.repaintCanvas();
			}
		});
		
		FXUtils.initSpinner(lineEndX, 0, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				SightObject obj = dataSight.objects.get(comboObjects.getValue());
				if(obj == null) {
					return;
				}
				if(obj.type == Type.LINE) {
					LineObject line = (LineObject)obj;
					line.end.x = newValue.doubleValue();
				}
				editor.repaintCanvas();
			}
		});
		
		FXUtils.initSpinner(lineEndY, 0, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				SightObject obj = dataSight.objects.get(comboObjects.getValue());
				if(obj == null) {
					return;
				}
				if(obj.type == Type.LINE) {
					LineObject line = (LineObject)obj;
					line.end.y = newValue.doubleValue();
				}
				editor.repaintCanvas();
			}
		});
		
		
		// text
		txtText.setText("text");
		txtText.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				SightObject obj = dataSight.objects.get(comboObjects.getValue());
				if(obj == null) {
					return;
				}
				if(obj.type == Type.TEXT) {
					TextObject text = (TextObject)obj;
					text.text = txtText.getText();
				}
				editor.repaintCanvas();
			}
		});
		
		
		
		FXUtils.initSpinner(txtPosX, 0, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				SightObject obj = dataSight.objects.get(comboObjects.getValue());
				if(obj == null) {
					return;
				}
				if(obj.type == Type.TEXT) {
					TextObject text = (TextObject)obj;
					text.pos.x = newValue.doubleValue();
				}
				editor.repaintCanvas();
			}
		});

		FXUtils.initSpinner(txtPosY, 0, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				SightObject obj = dataSight.objects.get(comboObjects.getValue());
				if(obj == null) {
					return;
				}
				if(obj.type == Type.TEXT) {
					TextObject text = (TextObject)obj;
					text.pos.y = newValue.doubleValue();
				}
				editor.repaintCanvas();
			}
		});
		
		FXUtils.initSpinner(txtSize, 1, -1000, 1000, 0.05, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				SightObject obj = dataSight.objects.get(comboObjects.getValue());
				if(obj == null) {
					return;
				}
				if(obj.type == Type.TEXT) {
					TextObject text = (TextObject)obj;
					text.size = newValue.doubleValue();
				}
				editor.repaintCanvas();
			}
		});
		
		
		txtAlign.getItems().addAll(TextAlign.LEFT.toString(), TextAlign.CENTER.toString(), TextAlign.RIGHT.toString());
		txtAlign.getSelectionModel().select(TextAlign.LEFT.toString());
		txtAlign.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				SightObject obj = dataSight.objects.get(comboObjects.getValue());
				if(obj == null) {
					return;
				}
				if(obj.type == Type.TEXT) {
					TextObject text = (TextObject)obj;
					text.align = TextAlign.get(newValue);
				}
				editor.repaintCanvas();
			}
		});
		
		
		
		// circle
		FXUtils.initSpinner(circlePosX, 0, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				SightObject obj = dataSight.objects.get(comboObjects.getValue());
				if(obj == null) {
					return;
				}
				if(obj.type == Type.CIRCLE) {
					CircleObject circle = (CircleObject)obj;
					circle.pos.x = newValue.doubleValue();
				}
				editor.repaintCanvas();
			}
		});
		
		FXUtils.initSpinner(circlePosY, 0, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				SightObject obj = dataSight.objects.get(comboObjects.getValue());
				if(obj == null) {
					return;
				}
				if(obj.type == Type.CIRCLE) {
					CircleObject circle = (CircleObject)obj;
					circle.pos.y = newValue.doubleValue();
				}
				editor.repaintCanvas();
			}
		});
		
		
		FXUtils.initSpinner(circleDiameter, 0, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				SightObject obj = dataSight.objects.get(comboObjects.getValue());
				if(obj == null) {
					return;
				}
				if(obj.type == Type.CIRCLE) {
					CircleObject circle = (CircleObject)obj;
					circle.diameter = newValue.doubleValue();
				}
				editor.repaintCanvas();
			}
		});
		
		
		FXUtils.initSpinner(circleSeg1, 0, 0, 360, 1, 1, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				SightObject obj = dataSight.objects.get(comboObjects.getValue());
				if(obj == null) {
					return;
				}
				if(obj.type == Type.CIRCLE) {
					CircleObject circle = (CircleObject)obj;
					circle.segment.x = newValue.doubleValue();
				}
				editor.repaintCanvas();
			}
		});
		
		FXUtils.initSpinner(circleSeg2, 0, 0, 360, 1, 1, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				SightObject obj = dataSight.objects.get(comboObjects.getValue());
				if(obj == null) {
					return;
				}
				if(obj.type == Type.CIRCLE) {
					CircleObject circle = (CircleObject)obj;
					circle.segment.y = newValue.doubleValue();
				}
				editor.repaintCanvas();
			}
		});
		
		FXUtils.initSpinner(circleSize, 0, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				SightObject obj = dataSight.objects.get(comboObjects.getValue());
				if(obj == null) {
					return;
				}
				if(obj.type == Type.CIRCLE) {
					CircleObject circle = (CircleObject)obj;
					circle.size = newValue.doubleValue();
				}
				editor.repaintCanvas();
			}
		});
		
		// quad
		FXUtils.initSpinner(quadPos1x, 0, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				SightObject obj = dataSight.objects.get(comboObjects.getValue());
				if(obj == null) {
					return;
				}
				if(obj.type == Type.QUAD) {
					QuadObject quad = (QuadObject)obj;
					quad.pos1.x = newValue.doubleValue();
				}
				editor.repaintCanvas();
			}
		});
		
		FXUtils.initSpinner(quadPos1y, 0, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				SightObject obj = dataSight.objects.get(comboObjects.getValue());
				if(obj == null) {
					return;
				}
				if(obj.type == Type.QUAD) {
					QuadObject quad = (QuadObject)obj;
					quad.pos1.y = newValue.doubleValue();
				}
				editor.repaintCanvas();
			}
		});
		
		
		FXUtils.initSpinner(quadPos2x, 0, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				SightObject obj = dataSight.objects.get(comboObjects.getValue());
				if(obj == null) {
					return;
				}
				if(obj.type == Type.QUAD) {
					QuadObject quad = (QuadObject)obj;
					quad.pos2.x = newValue.doubleValue();
				}
				editor.repaintCanvas();
			}
		});
		
		FXUtils.initSpinner(quadPos2y, 0, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				SightObject obj = dataSight.objects.get(comboObjects.getValue());
				if(obj == null) {
					return;
				}
				if(obj.type == Type.QUAD) {
					QuadObject quad = (QuadObject)obj;
					quad.pos2.y = newValue.doubleValue();
				}
				editor.repaintCanvas();
			}
		});
		
		
		FXUtils.initSpinner(quadPos3x, 0, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				SightObject obj = dataSight.objects.get(comboObjects.getValue());
				if(obj == null) {
					return;
				}
				if(obj.type == Type.QUAD) {
					QuadObject quad = (QuadObject)obj;
					quad.pos3.x = newValue.doubleValue();
				}
				editor.repaintCanvas();
			}
		});
		
		FXUtils.initSpinner(quadPos3y, 0, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				SightObject obj = dataSight.objects.get(comboObjects.getValue());
				if(obj == null) {
					return;
				}
				if(obj.type == Type.QUAD) {
					QuadObject quad = (QuadObject)obj;
					quad.pos3.y = newValue.doubleValue();
				}
				editor.repaintCanvas();
			}
		});
		
		
		
		FXUtils.initSpinner(quadPos4x, 0, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				SightObject obj = dataSight.objects.get(comboObjects.getValue());
				if(obj == null) {
					return;
				}
				if(obj.type == Type.QUAD) {
					QuadObject quad = (QuadObject)obj;
					quad.pos4.x = newValue.doubleValue();
				}
				editor.repaintCanvas();
			}
		});
		
		FXUtils.initSpinner(quadPos4y, 0, -1000, 1000, 0.01, 2, new ChangeListener<Double>() {
			@Override public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				SightObject obj = dataSight.objects.get(comboObjects.getValue());
				if(obj == null) {
					return;
				}
				if(obj.type == Type.QUAD) {
					QuadObject quad = (QuadObject)obj;
					quad.pos4.y = newValue.doubleValue();
				}
				editor.repaintCanvas();
			}
		});
		
		
		onSelectObject(null);
	}



	
	
	
	@FXML
	void onNewObj(ActionEvent event) {
		onNewObj(comboType.getValue().toLowerCase()+"_" + dataSight.objects.size(), Type.get(comboType.getValue()));
	}

	
	
	
	void onNewObj(String name, Type type) {
		
		SightObject obj = null;
		if(type == Type.LINE) { obj = new LineObject(); }
		if(type == Type.TEXT) { obj = new TextObject(); }
		if(type == Type.CIRCLE) { obj = new CircleObject(); }
		if(type == Type.QUAD) { obj = new QuadObject(); }

		obj.name = name;
		obj.type = type;
		dataSight.objects.put(obj.name, obj);
		comboObjects.getItems().add(obj.name);
		onSelectObject(obj.name);
		editor.repaintCanvas();
	}

	
	

	void onSelectObject(String strName) {

		dataSight.selectedSubElement = "";
		selectedObject = null;
		
		if(strName == null) {
			
			objName.setText("");
			objName.setDisable(true);
			
			cmnThousandth.setSelected(false);
			cmnThousandth.setDisable(true);
			
			cmnMovement.setValue(Movement.STATIC.toString());
			cmnMovement.setDisable(true);
			
			cmnAngle.getValueFactory().setValue(0.0);
			cmnAngle.setDisable(true);
			
			cmnAutoCenter.setSelected(true);
			cmnAutoCenter.setDisable(true);
			
			cmnCenterX.getValueFactory().setValue(0.0);
			cmnCenterY.getValueFactory().setValue(0.0);
			cmnCenterX.setDisable(true);
			cmnCenterY.setDisable(true);
			
			cmnRadCenterX.getValueFactory().setValue(0.0);
			cmnRadCenterY.getValueFactory().setValue(0.0);
			cmnRadCenterX.setDisable(true);
			cmnRadCenterY.setDisable(true);
			
			cmnSpeed.getValueFactory().setValue(0.0);
			cmnSpeed.setDisable(true);
			
			boxLine.setDisable(true);
			boxText.setDisable(true);
			boxCircle.setDisable(true);
			boxQuad.setDisable(true);

			boxLine.setVisible(true);
			boxText.setVisible(false);
			boxCircle.setVisible(false);
			boxQuad.setVisible(false);
			
		} else {
			
			SightObject obj = dataSight.objects.get(strName);
			if(obj == null) {
				Logger.get().warn("Error selecting object '" + strName + "'");
				return;
			}
			
			selectedObject = obj;
			dataSight.selectedSubElement = strName;
			
			comboObjects.getSelectionModel().select(obj.name);
			
			objName.setText(obj.name);
			objName.setDisable(false);
			
			cmnThousandth.setSelected(obj.cmnUseThousandth);
			cmnThousandth.setDisable(false);
			
			cmnMovement.setValue(obj.cmnMovement.toString());
			cmnMovement.setDisable(false);
			
			cmnAngle.getValueFactory().setValue(obj.cmnAngle);
			cmnAngle.setDisable(obj.cmnMovement != Movement.MOVE_RADIAL);
			
			cmnAutoCenter.setSelected(obj.useAutoCenter);
			cmnAutoCenter.setDisable(obj.cmnMovement != Movement.MOVE_RADIAL);
			
			cmnCenterX.getValueFactory().setValue(obj.cmnCenter.x);
			cmnCenterY.getValueFactory().setValue(obj.cmnCenter.y);
			cmnCenterX.setDisable(obj.cmnMovement != Movement.MOVE_RADIAL);
			cmnCenterY.setDisable(obj.cmnMovement != Movement.MOVE_RADIAL);
			
			cmnCenterX.setDisable(obj.useAutoCenter);
			cmnCenterY.setDisable(obj.useAutoCenter);
			
			cmnRadCenterX.getValueFactory().setValue(obj.cmnRadCenter.x);
			cmnRadCenterY.getValueFactory().setValue(obj.cmnRadCenter.y);
			cmnRadCenterX.setDisable(obj.cmnMovement != Movement.MOVE_RADIAL);
			cmnRadCenterY.setDisable(obj.cmnMovement != Movement.MOVE_RADIAL);
			
			cmnSpeed.getValueFactory().setValue(obj.cmnSpeed);
			cmnSpeed.setDisable(obj.cmnMovement != Movement.MOVE_RADIAL);
			
			boxLine.setDisable(obj.type != Type.LINE);
			boxText.setDisable(obj.type != Type.TEXT);
			boxCircle.setDisable(obj.type != Type.CIRCLE);
			boxQuad.setDisable(obj.type != Type.QUAD);
			
			boxLine.setVisible(obj.type == Type.LINE);
			boxText.setVisible(obj.type == Type.TEXT);
			boxCircle.setVisible(obj.type == Type.CIRCLE);
			boxQuad.setVisible(obj.type == Type.QUAD);
			
			if(obj.type == Type.LINE) {
				LineObject line = (LineObject)obj;
				lineStartX.getValueFactory().setValue(line.start.x);
				lineStartY.getValueFactory().setValue(line.start.y);
				lineEndX.getValueFactory().setValue(line.end.x);
				lineEndY.getValueFactory().setValue(line.end.y);
			}
			
			if(obj.type == Type.TEXT) {
				TextObject text = (TextObject)obj;
				txtText.setText(text.text);
				txtPosX.getValueFactory().setValue(text.pos.x);
				txtPosY.getValueFactory().setValue(text.pos.y);
				txtSize.getValueFactory().setValue(text.size);
				txtAlign.getSelectionModel().select(text.align.toString());
			}
			
			if(obj.type == Type.CIRCLE) {
				CircleObject circle = (CircleObject)obj;
				circlePosX.getValueFactory().setValue(circle.pos.x);
				circlePosY.getValueFactory().setValue(circle.pos.y);
				circleSeg1.getValueFactory().setValue(circle.segment.x);
				circleSeg2.getValueFactory().setValue(circle.segment.y);
				circleDiameter.getValueFactory().setValue(circle.diameter);
				circleSize.getValueFactory().setValue(circle.size);
			}
			
			if(obj.type == Type.QUAD) {
				QuadObject quad = (QuadObject)obj;
				quadPos1x.getValueFactory().setValue(quad.pos1.x);
				quadPos1y.getValueFactory().setValue(quad.pos1.y);
				quadPos2x.getValueFactory().setValue(quad.pos2.x);
				quadPos2y.getValueFactory().setValue(quad.pos2.y);
				quadPos3x.getValueFactory().setValue(quad.pos3.x);
				quadPos3y.getValueFactory().setValue(quad.pos3.y);
				quadPos4x.getValueFactory().setValue(quad.pos4.x);
				quadPos4y.getValueFactory().setValue(quad.pos4.y);
			}
			
		}
		
		editor.repaintCanvas();
	}
	
	
	
	
	@FXML
	void onRemoveObj(ActionEvent event) {
		
		SightObject obj = dataSight.objects.get(comboObjects.getValue());
		if(obj == null) {
			Logger.get().warn("Error deleting object '" + comboObjects.getValue() + "'");
			return;
		}
		
		comboObjects.getItems().remove(obj.name);
		dataSight.objects.remove(obj.name);
		
		if(dataSight.objects.size() > 0) {
			for(Entry<String,SightObject> entry : dataSight.objects.entrySet()) {
				onSelectObject(entry.getKey());
				break;
			}
		} else {
			onSelectObject(null);
		}
		
		editor.repaintCanvas();
	}
	
	
	
	
	void onRenameObject(String newName) {
		
		
		SightObject obj = dataSight.objects.get(comboObjects.getValue());
		if(obj == null || dataSight.objects.containsKey(newName)) {
			return;
		}

		// remove
		comboObjects.getItems().remove(obj.name);
		dataSight.objects.remove(obj.name);
		
		// rename
		obj.name = newName;

		// add
		dataSight.objects.put(obj.name, obj);
		comboObjects.getItems().add(obj.name);
		onSelectObject(obj.name);
		
		editor.repaintCanvas();
	}
	
	
	
	
	@FXML
	void onUseThousandth(ActionEvent event) {
		SightObject obj = dataSight.objects.get(comboObjects.getValue());
		if(obj == null) {
			return;
		}
		obj.cmnUseThousandth = cmnThousandth.isSelected();
		
		if(obj.cmnUseThousandth) {
			FXUtils.initSpinner(lineStartX, Conversion.get().screenspace2mil(lineStartX.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);			FXUtils.initSpinner(lineStartX, Conversion.get().screenspace2mil(lineStartX.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);
			FXUtils.initSpinner(lineStartY, Conversion.get().screenspace2mil(lineStartY.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);
			FXUtils.initSpinner(lineEndX, Conversion.get().screenspace2mil(lineEndX.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);
			FXUtils.initSpinner(lineEndY, Conversion.get().screenspace2mil(lineEndY.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);

			FXUtils.initSpinner(txtPosX, Conversion.get().screenspace2mil(txtPosX.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);
			FXUtils.initSpinner(txtPosY, Conversion.get().screenspace2mil(txtPosY.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);

			FXUtils.initSpinner(circlePosX, Conversion.get().screenspace2mil(circlePosX.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);
			FXUtils.initSpinner(circlePosY, Conversion.get().screenspace2mil(circlePosY.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);
			FXUtils.initSpinner(circleDiameter, Conversion.get().screenspace2mil(circleDiameter.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);
		
			FXUtils.initSpinner(quadPos1x, Conversion.get().screenspace2mil(quadPos1x.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);
			FXUtils.initSpinner(quadPos1y, Conversion.get().screenspace2mil(quadPos1y.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);
			FXUtils.initSpinner(quadPos2x, Conversion.get().screenspace2mil(quadPos2x.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);
			FXUtils.initSpinner(quadPos2y, Conversion.get().screenspace2mil(quadPos2y.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);
			FXUtils.initSpinner(quadPos3x, Conversion.get().screenspace2mil(quadPos3x.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);
			FXUtils.initSpinner(quadPos3y, Conversion.get().screenspace2mil(quadPos3y.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);
			FXUtils.initSpinner(quadPos4x, Conversion.get().screenspace2mil(quadPos4x.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);
			FXUtils.initSpinner(quadPos4y, Conversion.get().screenspace2mil(quadPos4y.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.5, 1, null);
			
		} else {
			FXUtils.initSpinner(lineStartX, Conversion.get().mil2screenspace(lineStartX.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);
			FXUtils.initSpinner(lineStartY, Conversion.get().mil2screenspace(lineStartY.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);
			FXUtils.initSpinner(lineEndX, Conversion.get().mil2screenspace(lineEndX.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);
			FXUtils.initSpinner(lineEndY, Conversion.get().mil2screenspace(lineEndY.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);
		
			FXUtils.initSpinner(txtPosX, Conversion.get().mil2screenspace(txtPosX.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);
			FXUtils.initSpinner(txtPosY, Conversion.get().mil2screenspace(txtPosY.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);

			FXUtils.initSpinner(circlePosX, Conversion.get().mil2screenspace(circlePosX.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);
			FXUtils.initSpinner(circlePosY, Conversion.get().mil2screenspace(circlePosY.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);
			FXUtils.initSpinner(circleDiameter, Conversion.get().mil2screenspace(circleDiameter.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);

			FXUtils.initSpinner(quadPos1x, Conversion.get().mil2screenspace(quadPos1x.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);
			FXUtils.initSpinner(quadPos1y, Conversion.get().mil2screenspace(quadPos1y.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);
			FXUtils.initSpinner(quadPos2x, Conversion.get().mil2screenspace(quadPos2x.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);
			FXUtils.initSpinner(quadPos2y, Conversion.get().mil2screenspace(quadPos2y.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);
			FXUtils.initSpinner(quadPos3x, Conversion.get().mil2screenspace(quadPos3x.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);
			FXUtils.initSpinner(quadPos3y, Conversion.get().mil2screenspace(quadPos3y.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);
			FXUtils.initSpinner(quadPos4x, Conversion.get().mil2screenspace(quadPos4x.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);
			FXUtils.initSpinner(quadPos4y, Conversion.get().mil2screenspace(quadPos4y.getValue(), dataSight.envZoomedIn), Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01, 2, null);
		}
		
		editor.repaintCanvas();
	}
	
	
	
	
	void onSelectMovement(Movement movement) {
		SightObject obj = dataSight.objects.get(comboObjects.getValue());
		if(obj == null) {
			return;
		}
		obj.cmnMovement = movement;
		
		cmnAngle.setDisable(obj.cmnMovement != Movement.MOVE_RADIAL);
		cmnCenterX.setDisable(obj.cmnMovement != Movement.MOVE_RADIAL);
		cmnCenterY.setDisable(obj.cmnMovement != Movement.MOVE_RADIAL);
		cmnRadCenterX.setDisable(obj.cmnMovement != Movement.MOVE_RADIAL);
		cmnRadCenterY.setDisable(obj.cmnMovement != Movement.MOVE_RADIAL);
		cmnSpeed.setDisable(obj.cmnMovement != Movement.MOVE_RADIAL);
		cmnAutoCenter.setDisable(obj.cmnMovement != Movement.MOVE_RADIAL);
		
		if(obj.cmnMovement == Movement.MOVE_RADIAL) {
			if(cmnAutoCenter.isSelected()) {
				cmnCenterX.setDisable(true);
				cmnCenterY.setDisable(true);
			} else {
				cmnCenterX.setDisable(false);
				cmnCenterY.setDisable(false);
			}
		}
		
		editor.repaintCanvas();
	}
	
	
	
	
	@FXML
	void onCmnAutoCenter(ActionEvent event) {
		SightObject obj = dataSight.objects.get(comboObjects.getValue());
		if(obj == null) {
			return;
		}
		obj.useAutoCenter = cmnAutoCenter.isSelected();
		cmnCenterX.setDisable(obj.useAutoCenter);
		cmnCenterY.setDisable(obj.useAutoCenter);
		editor.repaintCanvas();
	}
	
	
	
	
	@FXML
	void initialize() {
		assert comboType != null : "fx:id=\"comboType\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert comboObjects != null : "fx:id=\"comboObjects\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert objName != null : "fx:id=\"objName\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert cmnMovement != null : "fx:id=\"cmnMovement\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert cmnAngle != null : "fx:id=\"cmnAngle\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert cmnCenterX != null : "fx:id=\"cmnCenterX\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert cmnCenterY != null : "fx:id=\"cmnCenterY\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert cmnRadCenterX != null : "fx:id=\"cmnRadCenterX\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert cmnRadCenterY != null : "fx:id=\"cmnRadCenterY\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert cmnSpeed != null : "fx:id=\"cmnSpeed\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert boxLine != null : "fx:id=\"boxLine\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert lineStartX != null : "fx:id=\"lineStartX\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert lineStartY != null : "fx:id=\"lineStartY\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert lineEndX != null : "fx:id=\"lineEndX\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert lineEndY != null : "fx:id=\"lineEndY\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert boxText != null : "fx:id=\"boxText\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert txtText != null : "fx:id=\"txtText\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert txtPosX != null : "fx:id=\"txtPosX\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert txtPosY != null : "fx:id=\"txtPosY\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert txtSize != null : "fx:id=\"txtSize\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert txtAlign != null : "fx:id=\"txtAlign\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert boxCircle != null : "fx:id=\"boxCircle\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert circlePosX != null : "fx:id=\"circlePosX\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert circlePosY != null : "fx:id=\"circlePosY\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert circleDiameter != null : "fx:id=\"circleDiameter\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert circleSeg1 != null : "fx:id=\"circleSeg1\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert circleSeg2 != null : "fx:id=\"circleSeg2\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert circleSize != null : "fx:id=\"circleSize\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert boxQuad != null : "fx:id=\"boxQuad\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert quadPos1x != null : "fx:id=\"quadPos1x\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert quadPos1y != null : "fx:id=\"quadPos1y\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert quadPos2x != null : "fx:id=\"quadPos2x\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert quadPos2y != null : "fx:id=\"quadPos2y\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert quadPos3x != null : "fx:id=\"quadPos3x\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert quadPos3y != null : "fx:id=\"quadPos3y\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert quadPos4x != null : "fx:id=\"quadPos4x\" was not injected: check your FXML file 'layout_objects.fxml'.";
		assert quadPos4y != null : "fx:id=\"quadPos4y\" was not injected: check your FXML file 'layout_objects.fxml'.";

	}

}
