package com.ruegnerlukas.wtupdater;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ruegnerlukas.simpleutils.JarLocation;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtutils.Checksum;
import com.ruegnerlukas.wtutils.Config;

public class UpdateInstaller {

	
	public void install(UpdateController controller, Map<String,ArrayList<String>> updateData, List<String[]> changedFiles) {
		
		Thread thread = new Thread() {
			@Override
			public void run() {
				
				// check network connection
				boolean hasNet = checkNetworkConnection();
				if(!hasNet) {
					controller.onUpdateInstallDone(0);
					return;
				}
				
				// check if data dir exists -> and create
				File localData = new File(JarLocation.getJarLocation(WTSightsStart.class) + "/data");
				if(!localData.exists()) {
					localData.mkdir();
				}
				
				// create temporary directory
				File dirTmp = new File(JarLocation.getJarLocation(WTSightsStart.class) + "/dataTmp_" + Long.toHexString(System.currentTimeMillis()));
				dirTmp.mkdir();
		
				// download files
				boolean successful = false;
				try {
					successful = downloadFiles(changedFiles, dirTmp);
				} catch (IOException e) {
					Logger.get().error(e);
					successful = false;
				}
				if(!successful) {
					controller.onUpdateInstallDone(0);
					return;
				}

				// replace files
				try {
					replaceFiles(dirTmp, localData);
				} catch (IOException e) {
					deleteTmp(dirTmp);
					controller.onUpdateInstallDone(0);
					return;
				}
				
				// delete tmp files
				deleteTmp(dirTmp);
				controller.onUpdateInstallDone(1);
				
			}
		};
		thread.start();
	}
	

	
	
	
	private boolean checkNetworkConnection() {
		try {
			final URL url = new URL("https://github.com");
			final URLConnection conn = url.openConnection();
			conn.connect();
			conn.getInputStream().close();
			return true;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			return false;
		}
	}
	
	
	
	
	private boolean downloadFiles(List<String[]> filesToDownload, File dirTmp) throws IOException {
		
		for(int i=0; i<filesToDownload.size(); i++) {
			String[] fileToDownload = filesToDownload.get(i);
			String fileName = fileToDownload[0];
			String fileChecksum = fileToDownload[1];

			Logger.get().debug("Start download ", fileName, ":", fileChecksum, "...");
			
			String link = Config.getValue("update_path") + "/" + fileName;
			URL url = new URL(link);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			Map<String, List<String>> header = http.getHeaderFields();
			while (isRedirected(header)) {
				link = header.get("Location").get(0);
				url = new URL(link);
				http = (HttpURLConnection) url.openConnection();
				header = http.getHeaderFields();
			}
			
			File outFile = new File(dirTmp.getAbsolutePath() + "/" + fileName);
			Files.copy(http.getInputStream(), outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			Logger.get().debug("Downloaded", fileName);
			
			String checksumOut = Checksum.generate(outFile);
			if(!fileChecksum.equals(checksumOut)) {
				Logger.get().error("File rejected! Wrong Checksum. (" + fileName + " expected: " + fileChecksum + ",  loaded: " + checksumOut);
				return false;
			}
			
			
		}
		
		return true;
	}
	
	
	private boolean isRedirected(Map<String, List<String>> header) {
		for (String hv : header.get(null)) {
			if (hv.contains(" 301 ") || hv.contains(" 302 "))
				return true;
		}
		return false;
	}
	
	
	
	
	private void replaceFiles(File dirTmp, File dirDst) throws IOException {
		for(File file : dirTmp.listFiles()) {
			Files.move(file.toPath(), Paths.get(dirDst.getAbsolutePath()+"/"+file.getName()), StandardCopyOption.REPLACE_EXISTING);
		}
	}
	
	
	private void deleteTmp(File dirTmp) {
		try {
			Files.delete(dirTmp.toPath());
		} catch (IOException e) {
			Logger.get().error(e);
		}
	}
	
	
	
	
	
	
}
