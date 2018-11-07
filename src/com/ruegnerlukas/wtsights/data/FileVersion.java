package com.ruegnerlukas.wtsights.data;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;

public enum FileVersion {
	
	AUTO_DETECT("autodetect"),
	DUMMY("dummy"),
	V_1_DEFAULT("1"),
	V_2("2"),
	V_3("3");

	
	public final String fileversion;
	
	
	private FileVersion(String fileversion) {
		this.fileversion = fileversion;
	}
	
	
	
	public static FileVersion getFromString(String strVersion) {
		for(FileVersion version : FileVersion.values()) {
			if(version.fileversion.equalsIgnoreCase(strVersion)) {
				return version;
			}
		}
		Logger.get().error("Could not parse version of data file: " + strVersion);
		return null;
	}

}