//package com.ruegnerlukas.wtupdater.network;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.List;
//
//public abstract class NetworkInterface {
//
//	public static NetworkInterface netInterface = new DefaultNetworkInterface();
//	
//	public static NetworkInterface get() {
//		return netInterface;
//	}
//	
//	
//	
//	
//	
//	public abstract boolean checkNetworkStatus();
//	
//	
//	public abstract List<String> getFileContent(String strUrl) throws IOException;
//	
//	
//	public abstract boolean downloadFile(String fileName, File outFile) throws IOException;
//
//	
//	
//}
