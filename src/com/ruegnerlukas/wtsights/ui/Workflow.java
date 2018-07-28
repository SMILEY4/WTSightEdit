package com.ruegnerlukas.wtsights.ui;

import java.util.ArrayList;
import java.util.List;

public class Workflow {

	public static enum Step {
		CREATE_CALIBRATION,
		LOAD_CALIBRATION,
		CREATE_SIGHT,
		LOAD_SIGHT,
		SELECT_VEHICLE,
		SELECT_CALIBRATION,
		UPLOAD_SCREENSHOTS,
		EDIT_CALIBRATION,
		EDIT_SIGHT,
		ABOUT,
		SETTINGS
	}
	
	
	public static List<Step> steps = new ArrayList<Step>();
	
	
	public static String toString(List<Step> steps) {
		if(steps.isEmpty()) {
			return "";
		} else {
			String strSteps = " > ";
			for(int i=0; i<steps.size()-1; i++) {
				strSteps += steps.get(i) + " > ";
			}
			strSteps += steps.get(steps.size()-1);
			return strSteps;
		}
	}
	
}
