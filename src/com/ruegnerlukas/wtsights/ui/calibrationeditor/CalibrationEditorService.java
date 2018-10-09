package com.ruegnerlukas.wtsights.ui.calibrationeditor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2f;
import com.ruegnerlukas.simplemath.vectors.vec3.Vector3d;
import com.ruegnerlukas.simpleutils.collectionbuilders.MapBuilder;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.DataLoader;
import com.ruegnerlukas.wtsights.data.DataWriter;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticData;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticElement;
import com.ruegnerlukas.wtsights.data.ballisticdata.Marker;
import com.ruegnerlukas.wtsights.data.ballisticdata.MarkerData;
import com.ruegnerlukas.wtsights.data.ballisticdata.ballfunctions.DefaultBallisticFuntion;
import com.ruegnerlukas.wtsights.data.ballisticdata.ballfunctions.IBallisticFunction;
import com.ruegnerlukas.wtsights.data.ballisticdata.ballfunctions.NullBallisticFunction;
import com.ruegnerlukas.wtsights.data.sight.SightData;
import com.ruegnerlukas.wtsights.data.vehicle.Vehicle;
import com.ruegnerlukas.wtsights.ui.view.IViewService;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.ParamKey;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;
import com.ruegnerlukas.wtutils.Conversion;
import com.ruegnerlukas.wtutils.FXUtils;
import com.ruegnerlukas.wtutils.SightUtils.Thousandth;
import com.ruegnerlukas.wtutils.Workflow;
import com.ruegnerlukas.wtutils.Workflow.Step;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;



public class CalibrationEditorService implements IViewService {

	private File fileSight;
	private Map<BallisticElement,Image> imageCache = new HashMap<BallisticElement,Image>();
	private Image currentImage;
	private BallisticElement currentElement;
	private BallisticData dataBallistic;
	
	
	
	
	public void initNewBallisticData(Vehicle vehicle, List<BallisticElement> dataList, Map<BallisticElement,File> imageMap) {
		
		BallisticData dataBallistic = new BallisticData();
		dataBallistic.vehicle = vehicle;
		dataBallistic.elements.addAll(dataList);
		
		try {
			for(int i=0; i<dataList.size(); i++) {
				BallisticElement element  = dataList.get(i);
				File file = imageMap.get(element);
				if(file != null) {
					BufferedImage img = ImageIO.read(file);
					dataBallistic.images.put(element, img);
				}
			}
			
		} catch (IOException e) {
			Logger.get().error(e);
		}
		
		initNewBallisticData(dataBallistic);
	}
	
	
	
	
	public void initNewBallisticData(BallisticData dataBallistic) {
		this.dataBallistic = dataBallistic;
	}
	
	
	
	
	public void initFileSight(File file) {
		this.fileSight = file;
	}
	
	
	
	
	public String getVehicleName() {
		return dataBallistic.vehicle.namePretty;
	}
	
	
	
	
	public double getZoomMod() {
		return dataBallistic.zoomMod;
	}
	
	
	
	
	public void setZoomMod(double zoomMod) {
		this.dataBallistic.zoomMod = zoomMod;
	}
	
	
	
	
	public List<BallisticElement> getBallisticElements(boolean asCopy) {
		if(asCopy) {
			List<BallisticElement> elements = new ArrayList<BallisticElement>();
			elements.addAll(dataBallistic.elements);
			return elements;
		} else {
			return dataBallistic.elements;
		}
	}
	
	
	
	
	public void selectElement(BallisticElement element) {
		
		if(element == null) {
			currentImage = null;
			currentElement = null;
			
		} else {
			
			// get current image
			if(dataBallistic.images.get(element) == null) {
				currentImage = null;
			} else if(imageCache.containsKey(element)) {
				currentImage = imageCache.get(element);
				Logger.get().debug("Image retrieved from cache");
			} else {
				BufferedImage bufImg = dataBallistic.images.get(element);
				currentImage = SwingFXUtils.toFXImage(bufImg, null);
				imageCache.put(element, currentImage);
			}
			
			this.currentElement = element;
			
			Logger.get().debug("Ballistic Element selected: " + this.currentElement);
		}
		
	}

	
	
	
	public List<Marker> getMarkers(boolean asCopy) {
		
		
		if( this.currentElement != null && this.currentElement.markerData != null ) {
		
			for(int i=0; i<this.currentElement.markerData.markers.size(); i++) {
				Marker marker = this.currentElement.markerData.markers.get(i);
				marker.id = i+1;
			}
			
			if(asCopy) {
				List<Marker> markers = new ArrayList<Marker>();
				markers.addAll(this.currentElement.markerData.markers);
				return markers;
			} else {
				return this.currentElement.markerData.markers;
			}
			
		} else {
			return new ArrayList<Marker>();
		}
		
	}
	
	
	
	
	public void editMarkerRange(Marker marker, int distMeters) {
		if(currentElement != null) {
			marker.distMeters = distMeters;
			MarkerData dataMarker = currentElement.markerData;
			if(dataMarker.markers.size() >= 3) {
				currentElement.function = DefaultBallisticFuntion.create(currentElement, dataBallistic.vehicle, isZoomedIn());
			} else {
				currentElement.function = new NullBallisticFunction();
			}
		}
	}
	
	
	
	
	public List<Vector2f> getApproxRangeIndicators(double canvasWidth) {
		
		List<Vector2f> indicators = new ArrayList<Vector2f>();
		
		if(currentElement != null && currentElement.markerData != null) {
			MarkerData dataMarker = currentElement.markerData;
			double zoom = dataBallistic.vehicle.fovOut / dataBallistic.vehicle.fovIn;
			
			IBallisticFunction func = currentElement.function;
			for(int d=200; d<=2800; d+=200) {
				double p = func.eval(d) * (isZoomedIn() ? zoom : 1.0);
				float x = (float) (canvasWidth/2) - 20;
				float y = (float) (dataMarker.yPosCenter + p);
				indicators.add(new Vector2f(x,y));
			}
			
		}
		
		return indicators;
	}
	
	
	
	
	public List<Vector3d> getHorzRangeIndicators(double width, double height) {
		
		List<Vector3d> indicators = new ArrayList<Vector3d>();
		
		Conversion.get().initialize(width, height, dataBallistic.vehicle.fovOut, dataBallistic.vehicle.fovIn, Thousandth.USSR, dataBallistic.zoomMod);
		
		for(int i=-32; i<=32; i+=4) {
			
			final int mil = i;
			final boolean isMajor = mil % 8 == 0;
			
			final double x = width/2 + Conversion.get().mil2pixel(mil, height, this.isZoomedIn());
			final double y = height/2;
			
			Vector3d pos = new Vector3d(x, y, isMajor ? +1 : -1);
			indicators.add(pos);
			
		}	 
		
		return indicators;
	}
	
	
	
	
	public List<Marker> getMarkers() {
		if(currentElement != null && currentElement.markerData != null) {
			MarkerData dataMarker = currentElement.markerData;
			return dataMarker.markers;
		} else {
			return new ArrayList<Marker>();
		}
	}
	
	
	
	
	public Marker getSelectedMarker(int cursorX, int cursorY) {
		
		if(currentElement != null && currentElement.markerData != null) {
			
			MarkerData dataMarker = currentElement.markerData;
			
			double minDist = Integer.MAX_VALUE;
			Marker minMarker = null;
			
			Vector2d tmp = new Vector2d();
			for(int i=0; i<dataMarker.markers.size(); i++) {
				Marker marker = dataMarker.markers.get(i);
				double dist = tmp.set(cursorX, marker.yPos+dataMarker.yPosCenter).dist(cursorX, cursorY);
				if(dist < minDist) {
					minDist = dist;
					minMarker = marker;
				}
			}
			
			return minMarker;
			
		} else {
			return null;
		}
		
	}
	
	
	
	
	public double getCenterMarkerY() {
		if(currentElement != null && currentElement.markerData != null) {
			return currentElement.markerData.yPosCenter;
		} else {
			if(currentImage != null) {
				return currentImage.getHeight()/2;
			} else {
				return 720/2;
			}
		}
	}
	
	
	
	
	public void addMarker(double y) {
		
		if(currentElement == null || currentImage == null) {
			return;
		}
		
		// get marker data (create new if neccessary)
		if(this.currentElement.markerData == null) {
			currentElement.markerData = new MarkerData();
			currentElement.markerData.yPosCenter = currentImage != null ? currentImage.getHeight()/2 : 720/2;
		}
		MarkerData dataMarker = currentElement.markerData;
		double mc = dataMarker.yPosCenter;
		
		// add new marker
		if(dataMarker.markers.size() == 0) {
			Marker marker = new Marker(200, y-mc);
			marker.id = dataMarker.markers.size()+1;
			dataMarker.markers.add(marker);
		} else {
			Marker marker = new Marker(dataMarker.markers.get(dataMarker.markers.size()-1).distMeters+200, y-mc);
			marker.id = dataMarker.markers.size()+1;
			dataMarker.markers.add(marker);
		}
		
		// update function
		if(dataMarker.markers.size() >= 3) {
			currentElement.function = DefaultBallisticFuntion.create(currentElement, dataBallistic.vehicle, isZoomedIn());
		} else {
			currentElement.function = new NullBallisticFunction();
		}
		
	}
	
	
	
	
	public void deleteMarker(double x, double y) {
		Marker selectedMarker = getSelectedMarker((int)x, (int)y);
		if(selectedMarker != null) {
			deleteMarker(selectedMarker);
		}
	}
	
	
	
	
	public void deleteMarker(Marker marker) {
		
		if(marker != null) {
			MarkerData dataMarker = currentElement.markerData;
			dataMarker.markers.remove(marker);
			if(dataMarker.markers.size() >= 3) {
				currentElement.function = DefaultBallisticFuntion.create(currentElement, dataBallistic.vehicle, isZoomedIn());
			} else {
				currentElement.function = new NullBallisticFunction();
			}
			Logger.get().debug("Deleted marker " + marker);
		}
		
	}
	
	
	
	
	public void exportData() {
		
		if(!validateMarkers()) {
			FXUtils.showAlert("Can not save data. At least one shell does not have enough markers.", ViewManager.getStage(View.CALIBRATION_EDITOR));
			return;
		}
		
		FileChooser fc = new FileChooser();
		fc.setTitle("Save Ballistic Data");
		
		File fileSelected = fc.showSaveDialog(ViewManager.getStage(View.CALIBRATION_EDITOR));
		if(fileSelected == null) {
			return;
		}
		
		File file = new File(fileSelected.getAbsolutePath() + (fileSelected.getAbsolutePath().endsWith(".xml") ? "" : ".xml") );
		
		try {
			if(!DataWriter.saveExternalBallisticFile(dataBallistic, file)) {
				FXUtils.showAlert("Ballistic Data could not be saved.", ViewManager.getStage(View.CALIBRATION_EDITOR));
			} else {
				Logger.get().info("Saved Ballistic Data to " + file);
			}
		} catch (Exception e) {
			Logger.get().error(e);
		}
		
	}
	
	
	
	
	public void editSight() {
		
		if(!validateMarkers()) {
			Logger.get().warn("(Alert) Can not save data. At least one shell does not have enough markers.");
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText("Can not save data. At least one shell does not have enough markers.");
			alert.showAndWait();
			return;
		}
		
		if(this.fileSight == null) {
			Workflow.steps.add(Step.EDIT_CALIBRATION);
			ViewManager.getLoader(View.SIGHT_EDITOR).openNew(
					null, new MapBuilder<ParamKey,Object>().add(ParamKey.BALLISTIC_DATA, dataBallistic).get());
			

		} else {
			Workflow.steps.add(Step.EDIT_CALIBRATION);
			SightData dataSight = DataLoader.loadSight(fileSight, dataBallistic);
			ViewManager.getLoader(View.SIGHT_EDITOR).openNew(
					null, new MapBuilder<ParamKey,Object>().add(ParamKey.BALLISTIC_DATA, dataBallistic).add(ParamKey.SIGHT_DATA, dataSight).get());
		}
		
	}
	
	
	
	
	private boolean validateMarkers() {
		for(BallisticElement element : this.dataBallistic.elements) {
			boolean needsMarker = !element.isRocketElement;
			if(needsMarker) {
				if(element.markerData == null || element.markerData.markers.size() < 3) {
					return false;
				}
			}
		}
		return true;
	}
	
	
	
	
	public void setZoomedIn(boolean zoomedIn) {
		if(currentElement != null) {
			dataBallistic.zoomedIn.put(currentElement, zoomedIn);
			currentElement.function = DefaultBallisticFuntion.create(currentElement, dataBallistic.vehicle, isZoomedIn());
		}
	}
	
	
	
	
	public boolean isZoomedIn() {
		return dataBallistic.zoomedIn.containsKey(currentElement) ? dataBallistic.zoomedIn.get(currentElement) : false;
	}
	
	
	
	
	public Image getCurrentImage() {
		return currentImage;
	}
	
	
	
	
	public BallisticData getBallisticData() {
		return this.dataBallistic;
	}

	
}
