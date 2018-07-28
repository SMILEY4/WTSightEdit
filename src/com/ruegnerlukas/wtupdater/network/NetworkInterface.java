package com.ruegnerlukas.wtupdater.network;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface NetworkInterface {

	static NetworkInterface netInterface = new DefaultNetworkInterface();
	
	public static NetworkInterface get() {
		return netInterface;
	}
	
	
	
	
	
	public boolean checkNetworkStatus();
	
	
	public List<String> getFileContent(String strUrl) throws IOException;
	
	
	public boolean downloadFile(String fileName, File outFile) throws IOException;

	
	
}
