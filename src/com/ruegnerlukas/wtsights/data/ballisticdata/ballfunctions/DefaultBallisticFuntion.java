package com.ruegnerlukas.wtsights.data.ballisticdata.ballfunctions;

import java.util.ArrayList;
import java.util.List;

import com.ruegnerlukas.simplemath.vectors.vec2.Vector2d;
import com.ruegnerlukas.simplemath.vectors.vec3.Vector3d;
import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticElement;
import com.ruegnerlukas.wtsights.data.ballisticdata.Marker;
import com.ruegnerlukas.wtsights.data.vehicle.Vehicle;
import com.ruegnerlukas.wtutils.SightUtils;

public class DefaultBallisticFuntion implements IBallisticFunction {

	
	private Vector3d params = new Vector3d();
	
	
	
	public static DefaultBallisticFuntion create(BallisticElement element, Vehicle vehicle, boolean zoomedIn) {
		if(element.markerData == null) {
			return null;
		}
		
		List<Marker> markers = element.markerData.markers;
		
		double[] distMeters = new double[markers.size()+1];
		double[] yPos = new double[markers.size()+1];
		distMeters[0] = 0;
		yPos[0] = 0;
		for(int i=0; i<markers.size(); i++) {
			distMeters[i+1] = markers.get(i).distMeters;
			yPos[i+1] = markers.get(i).yPos / (zoomedIn ? (vehicle.fovOut / vehicle.fovIn) : 1);
		}

		List<Vector2d> points = new ArrayList<Vector2d>();
		for(int i=0; i<yPos.length; i++) {
			points.add(new Vector2d(distMeters[i]/100.0, yPos[i]));
		}
		Vector3d params = SightUtils.fitBallisticFunction(points, 1);
		
		return new DefaultBallisticFuntion(params);
	}
	
	
	
	private DefaultBallisticFuntion(Vector3d params) {
		this.params = params;
	}
	
	
	
	
	
	
	@Override
	public double eval(double distanceMeters) {
		return SightUtils.ballisticFunction(distanceMeters/100.0, params);
	}

	
}
