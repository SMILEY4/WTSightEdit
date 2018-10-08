package com.ruegnerlukas.wtsights.ui.sighteditor.general;

import com.ruegnerlukas.wtsights.data.DataPackage;
import com.ruegnerlukas.wtsights.ui.view.IViewService;
import com.ruegnerlukas.wtutils.SightUtils.Thousandth;

public class GeneralService implements IViewService {


	private DataPackage data;




	public void setDataPackage(DataPackage data) {
		this.data = data;
	}




	public Thousandth getThousandth() {
		return data.dataSight.gnrThousandth;
	}




	public void setThousandth(Thousandth thousandth) {
		data.dataSight.gnrThousandth = thousandth;
		data.dataSight.setElementsDirty();
	}




	public double getFontScale() {
		return data.dataSight.gnrFontScale;
	}




	public void setFontScale(double scale) {
		data.dataSight.gnrFontScale = scale;
		data.dataSight.setElementsDirty();
	}




	public double getLineSize() {
		return data.dataSight.gnrLineSize;
	}




	public void setLineSize(double size) {
		data.dataSight.gnrLineSize = size;
		data.dataSight.setElementsDirty();
	}




	public boolean applyCorrectionToGun() {
		return data.dataSight.gnrApplyCorrectionToGun;
	}




	public void setApplyCorrectionToGun(boolean apply) {
		data.dataSight.gnrApplyCorrectionToGun = apply;
		data.dataSight.setElementsDirty();
	}

}
