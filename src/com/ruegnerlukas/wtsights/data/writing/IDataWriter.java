package com.ruegnerlukas.wtsights.data.writing;

import java.io.File;

import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticData;
import com.ruegnerlukas.wtsights.data.sight.SightData;

public interface IDataWriter {

	public boolean saveExternalBallisticFile(BallisticData data, File outputFile) throws Exception;

	public boolean saveSight(SightData data, BallisticData dataBall, File outputFile);

}
