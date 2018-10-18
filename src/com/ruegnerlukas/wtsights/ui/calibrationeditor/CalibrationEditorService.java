package com.ruegnerlukas.wtsights.ui.calibrationeditor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import javafx.scene.image.Image;
import javafx.stage.FileChooser;



public class CalibrationEditorService implements IViewService {

	private File fileSight;
	private Map<Object,Image> imageCache = new HashMap<Object,Image>();
	private BallisticData dataBallistic;

	private Object currentObject;
	private Image currentImage;
	
	
	
	
	@Override
	public void initService() {
		fileSight = null;
		imageCache.clear();
		dataBallistic = null;
		currentObject = null;
		currentImage = null;
	}
	
	
	
	
	public void initNewBallisticData(Vehicle vehicle, Map<BallisticElement,File> imagesBallistic, File[] imagesZoom) {
		
		BallisticData dataBallistic = new BallisticData();
		dataBallistic.vehicle = vehicle;
		dataBallistic.elements.addAll(imagesBallistic.keySet());
		
		try {
			for(Entry<BallisticElement,File> entry : imagesBallistic.entrySet()) {
				BallisticElement element  = entry.getKey();
				File file = entry.getValue();
				if(file != null) {
					BufferedImage img = ImageIO.read(file);
					dataBallistic.imagesBallistic.put(element, img);
				}
			}
			if(imagesZoom[0] != null) { // zoomed in
				BufferedImage img = ImageIO.read(imagesZoom[0]);
				dataBallistic.imagesZoom.put(true, img);
			}
			if(imagesZoom[1] != null) { // zoomed out
				BufferedImage img = ImageIO.read(imagesZoom[1]);
				dataBallistic.imagesZoom.put(false, img);
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
	
	
	
	
	public List<BallisticElement> getBallisticElements(boolean asCopy) {
		if(asCopy) {
			List<BallisticElement> elements = new ArrayList<BallisticElement>();
			elements.addAll(dataBallistic.elements);
			return elements;
		} else {
			return dataBallistic.elements;
		}
	}
	
	
	
	public List<Object> getSelectableObjects() {
		List<Object> objects = new ArrayList<Object>();
		objects.addAll(dataBallistic.elements);
		objects.addAll(dataBallistic.imagesZoom.keySet());
		return objects;
	}
	
	
	
	public void selectElement(Object obj) {
		
		if(obj == null) {
			currentImage = null;
			currentObject = null;
			
			
		} else if(obj instanceof BallisticElement) {
			BallisticElement element = (BallisticElement)obj;
			
			// get current image
			if(dataBallistic.imagesBallistic.get(element) == null) {
				currentImage = null;
			} else if(imageCache.containsKey(element)) {
				currentImage = imageCache.get(element);
				Logger.get().debug("Image retrieved from cache");
			} else {
				BufferedImage bufImg = dataBallistic.imagesBallistic.get(element);
				currentImage = SwingFXUtils.toFXImage(bufImg, null);
				imageCache.put(element, currentImage);
			}
			this.currentObject = element;
			
			Logger.get().debug("Ballistic Element selected: " + this.currentObject);
		
			
		} else if(obj instanceof Boolean) {
			boolean zoomedIn = (Boolean) obj;

			// get current image
			if(dataBallistic.imagesZoom.get(zoomedIn) == null) {
				currentImage = null;
			} else if(imageCache.containsKey(obj)) {
				currentImage = imageCache.get(obj);
				Logger.get().debug("Image retrieved from cache");
			} else {
				BufferedImage bufImg = dataBallistic.imagesZoom.get(obj);
				currentImage = SwingFXUtils.toFXImage(bufImg, null);
				imageCache.put(obj, currentImage);
			}
			this.currentObject = obj;
			
			Logger.get().debug("zoomed image selected: " + zoomedIn);
		}
		
	}
	
	
	
	
	public void setZoomModifier(double value, boolean zoomedIn) {
		if(zoomedIn) {
			dataBallistic.zoomModIn = value;
		} else {
			dataBallistic.zoomModOut = value;
		}
	}

	
	
	public double getZoomModifier(boolean zoomedIn) {
		if(zoomedIn) {
			return dataBallistic.zoomModIn;
		} else {
			return dataBallistic.zoomModOut;
		}
	}
	
	
	public List<Marker> getMarkers(boolean asCopy) {
		
		if(this.currentObject != null && this.currentObject instanceof BallisticElement) {
			BallisticElement currentElement = (BallisticElement) currentObject;
			
			if( currentElement != null && currentElement.markerData != null ) {
				
				for(int i=0; i<currentElement.markerData.markers.size(); i++) {
					Marker marker = currentElement.markerData.markers.get(i);
					marker.id = i+1;
				}
				
				if(asCopy) {
					List<Marker> markers = new ArrayList<Marker>();
					markers.addAll(currentElement.markerData.markers);
					return markers;
				} else {
					return currentElement.markerData.markers;
				}

			}
		}
	
		return new ArrayList<Marker>();
	}
	
	
	
	
	public List<Vector2f> getApproxRangeIndicators(double canvasWidth) {
		
		List<Vector2f> indicators = new ArrayList<Vector2f>();
		
		if(this.currentObject != null && this.currentObject instanceof BallisticElement) {
			BallisticElement currentElement = (BallisticElement) currentObject;
			
			if(currentElement.markerData != null) {
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
		}
		
		return indicators;
	}
	
	
	
	
	public List<Vector3d> getHorzRangeIndicators(double width, double height) {
		
		List<Vector3d> indicators = new ArrayList<Vector3d>();
		
		Conversion.get().initialize(
				width,
				height,
				dataBallistic.vehicle.fovOut*dataBallistic.zoomModOut,
				dataBallistic.vehicle.fovIn*dataBallistic.zoomModIn,
				Thousandth.USSR);
		
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
		if(this.currentObject != null && this.currentObject instanceof BallisticElement) {
			BallisticElement currentElement = (BallisticElement) currentObject;
			if(currentElement != null && currentElement.markerData != null) {
				MarkerData dataMarker = currentElement.markerData;
				return dataMarker.markers;
			}
		}
		return new ArrayList<Marker>();
	}
	
	
	
	
	public Marker getSelectedMarker(int cursorX, int cursorY) {
		
		if(this.currentObject != null && this.currentObject instanceof BallisticElement) {
			BallisticElement currentElement = (BallisticElement) currentObject;

			if(currentElement.markerData != null) {
				
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
				
			}
		}
		
		return null;
	}
	
	
	
	
	public double getCenterMarkerY() {
		
		if(this.currentObject != null && this.currentObject instanceof BallisticElement) {
			BallisticElement currentElement = (BallisticElement) currentObject;

			if(currentElement != null && currentElement.markerData != null) {
				return currentElement.markerData.yPosCenter;
			} else {
				if(currentImage != null) {
					return currentImage.getHeight()/2;
				}
			}
		}
		
		return 720/2;
	}
	
	
	
	
	public void addMarker(double y) {
		
		if(this.currentObject != null && this.currentObject instanceof BallisticElement && currentImage != null) {
			BallisticElement currentElement = (BallisticElement) currentObject;

			// get marker data (create new if neccessary)
			if(currentElement.markerData == null) {
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
			currentElement.function = createFunctionFromMarkers(currentElement);
		}
		
	}
	
	
	
	
	public void editMarkerRange(Marker marker, int distMeters) {
		if(this.currentObject != null && this.currentObject instanceof BallisticElement) {
			BallisticElement currentElement = (BallisticElement) currentObject;
			if(currentElement != null) {
				marker.distMeters = distMeters;
				currentElement.function = createFunctionFromMarkers(currentElement);
			}
		}
	}
	
	
	
	
	public void deleteMarker(double x, double y) {
		Marker selectedMarker = getSelectedMarker((int)x, (int)y);
		if(selectedMarker != null) {
			deleteMarker(selectedMarker);
		}
	}
	
	
	
	
	public void deleteMarker(Marker marker) {
		
		if(this.currentObject != null && this.currentObject instanceof BallisticElement && currentImage != null) {
			BallisticElement currentElement = (BallisticElement) currentObject;
			if(marker != null) {
				MarkerData dataMarker = currentElement.markerData;
				dataMarker.markers.remove(marker);
				currentElement.function = createFunctionFromMarkers(currentElement);
				Logger.get().debug("Deleted marker " + marker);
			}
		}
		
	}
	
	
	
	
	public IBallisticFunction createFunctionFromMarkers(BallisticElement element) {
		MarkerData dataMarker = element.markerData;
		if(dataMarker != null && dataMarker.markers.size() >= 3) {
			return DefaultBallisticFuntion.create(element, dataBallistic.vehicle, isZoomedIn());
		} else {
			return new NullBallisticFunction();
		}
	}
	
	
	
	
	public void exportData() {
		
		if(!validateMarkers()) {
			FXUtils.showAlert(ViewManager.getResources().getString("ce_alert_export_missing"), ViewManager.getStage(View.CALIBRATION_EDITOR));
			return;
		}
		
		FileChooser fc = new FileChooser();
		fc.setTitle(ViewManager.getResources().getString("ce_export_title"));
		
		File fileSelected = fc.showSaveDialog(ViewManager.getStage(View.CALIBRATION_EDITOR));
		if(fileSelected == null) {
			return;
		}
		
		File file = new File(fileSelected.getAbsolutePath() + (fileSelected.getAbsolutePath().endsWith(".xml") ? "" : ".xml") );
		
		try {
			if(!DataWriter.saveExternalBallisticFile(dataBallistic, file)) {
				FXUtils.showAlert(ViewManager.getResources().getString("ce_alert_export_failed"), ViewManager.getStage(View.CALIBRATION_EDITOR));
			} else {
				Logger.get().info("Saved Ballistic Data to " + file);
			}
		} catch (Exception e) {
			Logger.get().error(e);
		}
		
	}
	
	
	
	
	public void editSight() {
		
		if(!validateMarkers()) {
			FXUtils.showAlert(ViewManager.getResources().getString("ce_alert_export_missing"), ViewManager.getStage(View.CALIBRATION_EDITOR));
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
		if(this.currentObject != null && this.currentObject instanceof BallisticElement && currentImage != null) {
			BallisticElement currentElement = (BallisticElement) currentObject;
			if(currentElement != null) {
				dataBallistic.zoomedIn.put(currentElement, zoomedIn);
				currentElement.function = DefaultBallisticFuntion.create(currentElement, dataBallistic.vehicle, isZoomedIn());
			}
		}
	}
	
	
	
	
	public boolean isZoomedIn() {
		if(this.currentObject != null && currentImage != null) {
			if(this.currentObject instanceof BallisticElement) {
				BallisticElement currentElement = (BallisticElement) currentObject;
				return dataBallistic.zoomedIn.containsKey(currentElement) ? dataBallistic.zoomedIn.get(currentElement) : false;
			} else {
				return (Boolean)currentObject;
			}
		} else {
			return false;
		}
	}
	
	
	
	
	public Image getCurrentImage() {
		return currentImage;
	}
	
	
	
	
	public BallisticData getBallisticData() {
		return this.dataBallistic;
	}

	
}
