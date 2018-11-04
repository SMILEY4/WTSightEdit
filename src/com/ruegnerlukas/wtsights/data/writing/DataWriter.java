package com.ruegnerlukas.wtsights.data.writing;

import java.io.File;

import com.ruegnerlukas.wtsights.data.ballisticdata.BallisticData;
import com.ruegnerlukas.wtsights.data.sight.SightData;

public class DataWriter implements IDataWriter {

	private static IDataWriter dataWriter = new DataWriter_v2();
	
	
	public static IDataWriter get() {
		return dataWriter;
	}
	
	
	
	
	@Override
	public boolean saveExternalBallisticFile(BallisticData data, File outputFile) throws Exception {
		return DataWriter.get().saveExternalBallisticFile(data, outputFile);
	}

	
	
	
	@Override
	public boolean saveSight(SightData data, BallisticData dataBall, File outputFile) {
		return DataWriter.get().saveSight(data, dataBall, outputFile);
	}

}
