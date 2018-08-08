//package com.ruegnerlukas.wtupdater;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLConnection;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import com.ruegnerlukas.simpleutils.JarLocation;
//import com.ruegnerlukas.simpleutils.logging.logger.Logger;
//import com.ruegnerlukas.wtutils.Checksum;
//import com.ruegnerlukas.wtutils.Config;
//
//public class Updater {
//
//	public static final String DO_NOT_UPDATE_FLAG = "DO_NOT_UPDATE";
//	
//	private static volatile boolean skipUpdateCheck = false;
//	private static volatile boolean skipUpdateInstall = false;
//
//	public static volatile boolean doneUpdateCheck = false;
//	public static volatile boolean doneUpdateInstall = false;
//
//	
//	private static volatile boolean foundUpdates = false;
//	private static volatile boolean isManuallyUpdate = false;
//
//	
//
//
//
//	private static boolean isRedirected(Map<String, List<String>> header) {
//		for (String hv : header.get(null)) {
//			if (hv.contains(" 301 ") || hv.contains(" 302 "))
//				return true;
//		}
//		return false;
//	}
//
//
//
//	
//	public static void skipUpdateCheck() {
//		skipUpdateCheck = true;
//	}
//	
//	
//	
//	
//	
//	
//	public static void installUpdate(UpdateController controller, List<String[]> changedFiles) {
//		
//		doneUpdateInstall = false;
//		foundUpdates = false;
//		isManuallyUpdate = false;
//		
//		Thread thread = new Thread() {
//			@Override
//			public void run() {
//				
//				if(changedFiles.isEmpty()) {
//					doneUpdateInstall = true;
//					controller.onUpdateInstallDone(1);
//					return;
//				}
//				
//				File dirTmp = new File(JarLocation.getJarLocation(WTSightsStart.class) + "/dataUpdate");
//				dirTmp.mkdir();
//				
//				for(String[] changedFile : changedFiles) {
//					
//					if(skipUpdateInstall) {
//						doneUpdateInstall = true;
//						controller.onUpdateInstallDone(2);
//					}
//					
//					try {
//						
//						String fileName = changedFile[0];
//						String fileChecksum = changedFile[1];
//						Logger.get().debug("Downloading ", fileName);
//						
//						String link = Config.getValue("update_path") + "/" + fileName;
//						URL url = new URL(link);
//						HttpURLConnection http = (HttpURLConnection) url.openConnection();
//						Map<String, List<String>> header = http.getHeaderFields();
//						while (isRedirected(header)) {
//							link = header.get("Location").get(0);
//							url = new URL(link);
//							http = (HttpURLConnection) url.openConnection();
//							header = http.getHeaderFields();
//						}
//						
//						File outFile = new File(dirTmp.getAbsolutePath() + "/" + fileName);
//
//						Files.copy(http.getInputStream(), outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//						
//						String checksumOut = Checksum.generate(outFile);
////						if(!checksumOut.equals(fileChecksum)) {
////							Logger.get().error(Updater.class, "File with different checksum: " + fileName + " (" + checksumOut + ")");
////							doneUpdateInstall = true;
////							controller.onUpdateInstallDone(0);
////							return;
////						}
//						
//					} catch(Exception e) {
//						doneUpdateInstall = true;
//						controller.onUpdateInstallDone(2);
//						return;
//					}
//					
//				}
//				
//				for(File file : dirTmp.listFiles()) {
//					
//					if(skipUpdateInstall) {
//						doneUpdateInstall = true;
//						controller.onUpdateInstallDone(2);
//					}
//					
//					try {
//						Files.move(file.toPath(), Paths.get(file.getAbsolutePath().replaceAll("dataUpdate", "data")), StandardCopyOption.REPLACE_EXISTING);
//					} catch (IOException e) {
//						doneUpdateInstall = true;
//						controller.onUpdateInstallDone(2);
//						return;
//					}
//				}
//				
//				try {
//					Files.delete(dirTmp.toPath());
//				} catch (IOException e) {
//					Logger.get().error(e);
//				}
//				
//				
//				if(skipUpdateInstall) {
//					doneUpdateInstall = true;
//					controller.onUpdateInstallDone(2);
//				}
//				
//				boolean successfull = true;
//				if(successfull) {
//					doneUpdateInstall = true;
//					controller.onUpdateInstallDone(1);
//				} else {
//					doneUpdateInstall = true;
//					controller.onUpdateInstallDone(0);
//				}
//				
//			}
//		};
//		thread.start();
//		
//	}
//	
//	
//	
//	
//	public static void skipUpdateInstall() {
//		skipUpdateInstall = true;
//	}
//	
//	
//}
