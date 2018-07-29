package com.ruegnerlukas.wtupdater;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ruegnerlukas.simpleutils.JarLocation;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtupdater.network.NetworkInterface;
import com.ruegnerlukas.wtutils.Checksum;
import com.ruegnerlukas.wtutils.Config;

public class UpdateChecker {

	
	public static enum UpdateState {
		NO_UPDATE,
		AUTO_UPDATE,
		REPAIR
	}
	
	
	public void checkForUpdates(UpdateController controller) {

		Thread thread = new Thread() {
			@Override
			public void run() {
				
				// check network connection
				boolean hasNet = NetworkInterface.get().checkNetworkStatus();
				if(!hasNet) {
					controller.onUpdateCheckDone(null, null, UpdateState.NO_UPDATE);
					return;
				}
				
				// get update data lines
				List<String> lines = null;
				try {
					lines = getUpdateFileContent();
				} catch (IOException e) {
					Logger.get().warn(this, e);
					controller.onUpdateCheckDone(null, null, UpdateState.NO_UPDATE);
					return;
				}
				if(lines == null) {
					controller.onUpdateCheckDone(null, null, UpdateState.NO_UPDATE);
					return;
				}
				
				// parse data
				Map<String,ArrayList<String>> data = parseData(lines);
				
				// check if new
				List<String[]> changedFiles = new ArrayList<String[]>();
				UpdateState updateState = getUpdateState(data, changedFiles);
				
				// finish
				controller.onUpdateCheckDone(data, changedFiles, updateState);
				return;
				
			}
		};
		thread.start();
		
	}
	
	
	
	
	
	private List<String> getUpdateFileContent() throws IOException {
		
		Logger.get().debug("Download update.txt...");
		
		String link = Config.getValue("update_path") + "/update.txt";
		List<String> lines = NetworkInterface.get().getFileContent(link);
		
//		List<String> lines = new ArrayList<String>();
//		
//		// check update
//		String link = Config.getValue("update_path") + "/update.txt";
//		
//		URL url = new URL(link);
//		
//		HttpURLConnection http = (HttpURLConnection) url.openConnection();
//		
//		Map<String, List<String>> header = http.getHeaderFields();
//		while (isRedirected(header)) {
//			
//			link = header.get("Location").get(0);
//			url = new URL(link);
//			http = (HttpURLConnection) url.openConnection();
//			header = http.getHeaderFields();
//		}
//
//		BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
//		String inputLine;
//		while( (inputLine=in.readLine()) != null) {
//			Logger.get().debug("Read ", inputLine);
//			lines.add(inputLine);
//		}
//		
//		in.close();
//		http.disconnect();
//		
		Logger.get().debug("... Download done");
		return lines;
	}
	
	
	
	
	private Map<String,ArrayList<String>> parseData(List<String> lines) {
		
		Logger.get().debug("Parsing data...");
		
		Map<String,ArrayList<String>> data = new HashMap<String,ArrayList<String>>();
		
		for(int i=0; i<lines.size(); i++) {
			String line = lines.get(i);
			
			Logger.get().debug("Parse line: ", line); 
			
			if(line.isEmpty() || line.startsWith(";")) {
				continue;
			}
			
			
			String key = line.split("=")[0].trim();
			String value = line.split("=")[1].trim();

			Logger.get().debug("Parse: ", line, " -- ", "key=", key, "value=", value);

			if(!data.containsKey(key)) {
				data.put(key, new ArrayList<String>());
			}
			
			data.get(key).add(value);
			
		}
		
		Logger.get().debug("... done parsing");
		
		return data;
	}
	
	
	
	
	public UpdateState getUpdateState(Map<String,ArrayList<String>> data, List<String[]> changedFilesOut) {
		
		Logger.get().debug("Check if new update");
		
		if(data.isEmpty()) {
			return UpdateState.NO_UPDATE;
		}
		
		// compare versions
		String onlineBuildVersion = data.get("build_version") != null ? data.get("build_version").get(0) : "?";
		String localBuildVersion = Config.getValue("build_version");
		boolean isNewVersion = localBuildVersion != null && !onlineBuildVersion.equalsIgnoreCase(localBuildVersion);
		Logger.get().info("Compare versions: ", "local=", localBuildVersion, " online=", onlineBuildVersion); 
		
		// compare files
		if(!data.containsKey("file")) {
			Logger.get().debug("No files in update.txt");
			return UpdateState.NO_UPDATE;
		}
		ArrayList<String> files = data.get("file");
		changedFilesOut.clear();
		
		for(int i=0; i<files.size(); i++) {
			String strFile = files.get(i);
			String fileName = strFile.split(":")[0];
			String fileChecksum = strFile.split(":")[1];
			changedFilesOut.add(new String[]{fileName, fileChecksum});
		}
		
		File localData = new File(JarLocation.getJarLocation(WTSightsStart.class) + "/data");
		if(localData != null && localData.exists()) {
			for(File locFile : localData.listFiles()) {
				
				for(int i=0; i<changedFilesOut.size(); i++) {
					String[] changedFile = changedFilesOut.get(i);
					if(changedFile[0].equals(locFile.getName())) {
						Logger.get().debug(changedFile[0], "Compare checksums: ", changedFile[1], " vs ", Checksum.generate(locFile));
						if(changedFile[1].equals(Checksum.generate(locFile))) {
							changedFilesOut.remove(i);
							break;
						}
					}
				}
				
			}
		}
		
		for(String[] str : changedFilesOut) {
			Logger.get().debug("changedFile: ", str[0], " ", str[1]);
		}
		
		if(changedFilesOut.isEmpty()) {
			return UpdateState.NO_UPDATE;
		}
	
		if(isNewVersion) {
			return UpdateState.AUTO_UPDATE;
		} else {
			return UpdateState.REPAIR;
		}
	}
	
	
	
	
	
	
}
