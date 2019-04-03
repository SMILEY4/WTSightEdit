package com.ruegnerlukas.wtsights.ui.sighteditor.environment;

import com.ruegnerlukas.simpleutils.collectionbuilders.ArrayListBuilder;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticElement;
import com.ruegnerlukas.wtsights.data.ballisticdata.NullElement;
import com.ruegnerlukas.wtsights.data.sight.sightElements.ElementType;
import com.ruegnerlukas.wtsights.ui.view.IViewService;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class EnvironmentService implements IViewService {


	private DataPackage data;




	@Override
	public void initService() {
		data = null;
	}




	public void setDataPackage(DataPackage data) {
		this.data = data;
	}




	public List<BallisticElement> getBallisticElements() {
		if (data.dataBallistic.elements.isEmpty()) {
			return new ArrayListBuilder<BallisticElement>().add(new NullElement()).get();
		} else {
			return data.dataBallistic.elements;
		}
	}




	public void selectBallisticElement(BallisticElement element) {
		data.elementBallistic = element;
		Logger.get().debug("Selected ballistic element: " + (data.elementBallistic == null ? "null" : data.elementBallistic));
		data.dataSight.setElementsDirty();
	}




	public void setZoomMode(boolean zoomedIn) {
		data.dataSight.envZoomedIn = zoomedIn;
		data.dataSight.setElementsDirty();
	}




	public boolean isZoomedIn() {
		return data.dataSight.envZoomedIn;
	}




	public boolean isRangefinderShown() {
		return data.dataSight.envShowRangeFinder;
	}




	public void showRangefinder(boolean show) {
		data.dataSight.envShowRangeFinder = show;
		data.dataSight.setElementsDirty(ElementType.RANGEFINDER);
	}




	public double getRangefinderProgress() {
		return data.dataSight.envRFProgress;
	}




	public void setRangefinderProgress(double progress) {
		data.dataSight.envRFProgress = progress;
		data.dataSight.setElementsDirty(ElementType.RANGEFINDER);
	}




	public int getRangeCorrection() {
		return data.dataSight.envRangeCorrection;
	}




	public void setRangeCorrection(int range) {
		data.dataSight.envRangeCorrection = range;
		data.dataSight.setElementsDirty();
	}




	public void setCrosshairLighting(boolean enabled) {
		if (enabled) {
			data.dataSight.envSightColor = new Color(1.0, 75.0 / 255.0, 55.0 / 255.0, 1.0);
		} else {
			data.dataSight.envSightColor = Color.BLACK;
		}
	}




	public void setBackground(File file) {
		if (file == null) {
			data.dataSight.envBackground = null;
		} else {
			try {
				data.dataSight.envBackground = new Image(new FileInputStream(file));
				Logger.get().info("Selected background: " + file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}




	public Image getBackground() {
		return data.dataSight.envBackground;
	}




	public void setBackgroundOffX(int x) {
		data.dataSight.envBackgroundOffX = x;
	}




	public void setBackgroundOffY(int y) {
		data.dataSight.envBackgroundOffY = y;
	}




	public int getBackgroundOffX() {
		return data.dataSight.envBackgroundOffX;
	}




	public int getBackgroundOffY() {
		return data.dataSight.envBackgroundOffY;
	}




	public void setBackgroundScale(double scale) {
		data.dataSight.envBackgroundScale = scale;
	}




	public double getBackgroundScale() {
		return data.dataSight.envBackgroundScale;
	}




	public void setBackgroundRotation(double rotation) {
		data.dataSight.envBackgroundRotation = rotation;
	}




	public double getBackgroundRotation() {
		return data.dataSight.envBackgroundRotation;
	}




	public void setResolution(int width, int height) {
		data.dataSight.setElementsDirty();
	}




	public boolean isGridShown() {
		return data.dataSight.envDisplayGrid;
	}




	public void showGrid(boolean show) {
		data.dataSight.envDisplayGrid = show;
	}




	public double getGridWidth() {
		return data.dataSight.envGridWidth;
	}




	public void setGridWidth(double width) {
		data.dataSight.envGridWidth = width;
	}




	public double getGridHeight() {
		return data.dataSight.envGridHeight;
	}




	public void setGridHeight(double height) {
		data.dataSight.envGridHeight = height;
	}




	public Color getGridColor() {
		return data.dataSight.envColorGrid;
	}




	public void setGridColor(Color color) {
		data.dataSight.envColorGrid = color;
	}

}
