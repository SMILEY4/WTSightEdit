package com.ruegnerlukas.wtlauncher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.ruegnerlukas.githubApi.APIRelease;
import com.ruegnerlukas.githubApi.Asset;
import com.ruegnerlukas.githubApi.Release;
import com.ruegnerlukas.simpleutils.JarLocation;
import com.ruegnerlukas.simpleutils.SystemUtils;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;

public class Launcher {

	
	public static void main(String[] args) {
		try {
			new Launcher().startTest();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	public boolean startTest() throws Exception{
		
		// log system info
		Logger.get().info("===========================");
		Logger.get().info("Java Runtime = " + SystemUtils.getJavaRuntimeName());
		Logger.get().info("Java Version = " + SystemUtils.getJavaVersion());
		Logger.get().info("OS           = " + SystemUtils.getOSName());
		Logger.get().info("===========================");

		
		// get latest release
		Release latestRelease = APIRelease.getLatestRelease("SMILEY4", "WTSightEdit");
		if(latestRelease == null) {
			return false;
		}
		
		String latestVersion = latestRelease.tag_name;
		String localVersion = getLocalVersion();

		// start update, if new version exists
		if(compareVersions(localVersion, latestVersion) == 1) {
			Logger.get().info("Update available. Local: " + localVersion + ". Latest: " + latestVersion); 
			
			if(!latestRelease.assets.isEmpty()) {
				
				final String BASE_DIR = "C:\\Users\\LukasRuegner\\Desktop\\WTSightEdit_06";
				
				// DOWNLOAD UPDATE
				Downloader.download(new File(BASE_DIR+"\\_data_update_"+latestVersion), latestRelease);
				
				
				// UPDATE DATA
				List<FileData> fileDataOld = FileData.load(new File(BASE_DIR+"\\data\\files.json"));
				List<FileData> fileDataNew = FileData.load(new File(BASE_DIR+"\\_data_update_"+latestVersion + "\\data\\files.json"));
				
				for(int i=0; i<fileDataOld.size(); i++) {
					FileData fileData = fileDataOld.get(i);
					File file = new File(BASE_DIR+"\\data\\" + fileData.fileName);
					if(file.exists()) {
						fileData.file = file;
					}
				}
				for(int i=0; i<fileDataNew.size(); i++) {
					FileData fileData = fileDataNew.get(i);
					File file = new File(BASE_DIR+"\\_data_update_"+latestVersion + "\\data\\" + fileData.fileName);
					if(file.exists()) {
						fileData.file = file;
					}
				}
				
				List<FileData> fileDataOldSkipped = new ArrayList<FileData>();
				fileDataOldSkipped.addAll(fileDataOld);
				
				
				// delete old files that are no longer needed
				for(int i=0; i<fileDataOld.size(); i++) {
					FileData fileData = fileDataOld.get(i);
					boolean canDelete = true;
					if(fileData.hasMergeStrat() && fileData.file != null) {
						FileData newFileData = null;
						for(int j=0; j<fileDataNew.size(); j++) {
							if(fileDataNew.get(j).equals(fileData)) {
								newFileData = fileDataNew.get(j);
								break;
							}
						}
						canDelete = newFileData == null;
					}
					if(canDelete && fileData.file != null) {
						Files.deleteIfExists(fileData.file.toPath());
						fileDataOldSkipped.remove(fileData);
						Logger.get().info("Deleted " + fileData.file.getAbsolutePath());
					}
				}

				// move new files
				for(int i=0; i<fileDataNew.size(); i++) {
					FileData fileData = fileDataNew.get(i);
					if(fileData.file != null && !fileData.hasMergeStrat()) {
						Files.move(fileData.file.toPath(), Paths.get(BASE_DIR+"\\data\\"+fileData.fileName), StandardCopyOption.REPLACE_EXISTING);
						Logger.get().info("Moved " + fileData.file.getAbsolutePath() + "  ->  " + BASE_DIR+"\\data\\"+fileData.fileName);
					}
				}
				
				
				// merge files
				for(int i=0; i<fileDataNew.size(); i++) {
					FileData fileData = fileDataNew.get(i);
					if(fileData.file != null && fileData.hasMergeStrat()) {
						
						FileData oldFileData = null;
						for(int j=0; j<fileDataOld.size(); j++) {
							if(fileDataOld.get(j).equals(fileData)) {
								oldFileData = fileDataOld.get(j);
								break;
							}
						}
						
						
						
						if(oldFileData == null) {
							Files.move(fileData.file.toPath(), Paths.get(BASE_DIR+"\\data\\"+fileData.fileName), StandardCopyOption.REPLACE_EXISTING);
							Logger.get().info("Merged (move) " + fileData.file.getAbsolutePath() + "  ->  " + BASE_DIR+"\\data\\"+fileData.fileName);
						} else {
							FileMerger.merge(oldFileData.file, fileData.file, fileData.mergeRoutine);
							fileDataOldSkipped.remove(oldFileData);
							Logger.get().info("Merged ("+fileData.mergeRoutine+") " + fileData.file.getAbsolutePath() + "  ->  " + BASE_DIR+"\\data\\"+fileData.fileName);
						}
						
					}
				}
				
				// UPDATE LAUNCHER
				File fileLauncherCurr = new File(BASE_DIR+"\\WTSightEdit.jar"); //new File(JarLocation.getJarFile(Launcher.class));
				File fileLauncherNew = new File(BASE_DIR+"\\_data_update_"+latestVersion+"\\WTSightEdit.jar");
				File fileUpdateDir = new File(BASE_DIR+"\\_data_update_"+latestVersion);
				File fileCleanup = new File(BASE_DIR+"\\data\\cleanup.jar");
				
				
				Logger.get().info("Starting cleanup");
				ProcessBuilder builder = new ProcessBuilder("java", "-jar", fileCleanup.getAbsolutePath(), fileLauncherCurr.getAbsolutePath(), fileLauncherNew.getAbsolutePath(), fileUpdateDir.getAbsolutePath());
				try {
					Process proc = builder.start();
				} catch (IOException e) {
					Logger.get().error(e);
				}
				
				System.exit(0);

			}
			
		} else {
			Logger.get().info("No update available. Local: " + localVersion + ". Latest: " + latestVersion); 
			return false;
		}
		
		return false;
	}
	
	
	
	
	
	
	
	
	
	
	private String getLocalVersion() {
		return "0.6";
	}
	
	
	
	
	/**
	 * @return -1 = local is newer;   +1 = remote is newer;   0 = equal versions
	 * */
	public static int compareVersions(String local, String remote) {
		
		String[] sLoc = local.split("\\.");
		String[] sRem = remote.split("\\.");
		
		int[] iLoc = new int[Math.max(sLoc.length, sRem.length)];
		int[] iRem = new int[Math.max(sLoc.length, sRem.length)];

		for(int i=0; i<sLoc.length; i++) {
			iLoc[i] = Integer.parseInt(sLoc[i]);
		}
		for(int i=0; i<sRem.length; i++) {
			iRem[i] = Integer.parseInt(sRem[i]);
		}

		for(int i=0; i<iLoc.length; i++) {
			int vLoc = iLoc[i];
			int vRem = iRem[i];
			
			if(vLoc > vRem) {
				return -1;
				
			} else if(vLoc < vRem) {
				return 1;
				
			} else if(vLoc == vRem) {
				continue;
			}
			
		}
		
		return 0;
	}
	
	
	
}



