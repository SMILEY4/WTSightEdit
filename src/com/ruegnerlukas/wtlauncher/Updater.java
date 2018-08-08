package com.ruegnerlukas.wtlauncher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.ruegnerlukas.simpleutils.JarLocation;
import com.ruegnerlukas.simpleutils.SystemUtils;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtlauncher.githubApi.APIRelease;
import com.ruegnerlukas.wtlauncher.githubApi.Asset;
import com.ruegnerlukas.wtlauncher.githubApi.Release;
import com.ruegnerlukas.wtlauncher.network.DefaultNetworkInterface;
import com.ruegnerlukas.wtlauncher.network.LocalNetworkInterface;
import com.ruegnerlukas.wtlauncher.network.NetworkInterface;
import com.ruegnerlukas.wtutils.Config2;

import javafx.application.Platform;

public class Updater {

	
	public static enum SearchStatus {
		FOUND_UPDATE,
		NO_UPDATE,
		NO_CONNECTION,
		SKIPPED;
	}
	
	
	public static enum InstallStatus {
		FATAL,
		FAILED,
		DONE
	}
	
	
//	private static NetworkInterface net = new LocalNetworkInterface();
	private static NetworkInterface net = new DefaultNetworkInterface();

	
	
	
	
	public static void searchUpdate(LauncherController controller) {
		
		Thread thread = new Thread() {
			@Override
			public void run() {
			
				Logger.get().info("Checking network status");
				if(!net.checkNetworkStatus()) {
					controller.onUpdateSearchDone(SearchStatus.NO_CONNECTION, "-", "-");
					return;
				}
				
				Logger.get().info("Get latest release");
				Release latestRelease = net.getLatestRelease("SMILEY4", "WTSightEdit");
				if(latestRelease == null) {
					controller.onUpdateSearchDone(SearchStatus.NO_UPDATE, "-", "-");
					return;
				}
				
				String latestVersion = latestRelease.tag_name;
				String localVersion = Config2.build_version;
				Logger.get().info("Latest release is " + latestVersion + ". Local version is " + localVersion);
				
				if(compareVersions(localVersion, latestVersion) == 1) {
					controller.onUpdateSearchDone(SearchStatus.FOUND_UPDATE, localVersion, latestVersion);
					return;
				} else {
					controller.onUpdateSearchDone(SearchStatus.NO_UPDATE, localVersion, latestVersion);
					return;	
				}
				
				
			}
		};
		thread.setDaemon(true);
		thread.start();
		
	}
	
	
	
	public static void installUpdate(LauncherController controller) {
		
		Thread thread = new Thread() {
			@Override
			public void run() {
			
				Logger.get().info("Checking network status");
				if(!net.checkNetworkStatus()) {
					controller.onInstallUpdateDone(InstallStatus.FAILED);
					return;
				}
				
				Release latestRelease = net.getLatestRelease("SMILEY4", "WTSightEdit");
				if(latestRelease == null) {
					controller.onInstallUpdateDone(InstallStatus.FAILED);
					return;
				}
				String latestVersion = latestRelease.tag_name;
				
				if(!latestRelease.assets.isEmpty()) {
					
					// DOWNLOAD UPDATE
					try {
						Logger.get().info("Download latest version " + latestRelease.assets.get(0).browser_download_url + " to " + WTSELauncher.BASE_DIR+"\\_data_update_"+latestVersion);
						net.download(new File(WTSELauncher.BASE_DIR+"\\_data_update_"+latestVersion), latestRelease);
					} catch (IOException e) {
						Logger.get().error(e);
						controller.onInstallUpdateDone(InstallStatus.FATAL);
						return;
					}
					
					
					// UPDATE DATA
					List<FileData> fileDataOld = null;;
					List<FileData> fileDataNew = null;
					try {
						fileDataOld = FileData.load(new File(WTSELauncher.BASE_DIR+"\\data\\files.json"));
						fileDataNew = FileData.load(new File(WTSELauncher.BASE_DIR+"\\_data_update_"+latestVersion + "\\data\\files.json"));
					} catch (IOException e) {
						Logger.get().error(e);
						controller.onInstallUpdateDone(InstallStatus.FATAL);
						return;
					}
					
					for(int i=0; i<fileDataOld.size(); i++) {
						FileData fileData = fileDataOld.get(i);
						File file = new File(WTSELauncher.BASE_DIR+"\\data\\" + fileData.fileName);
						if(file.exists()) {
							fileData.file = file;
						}
					}
					for(int i=0; i<fileDataNew.size(); i++) {
						FileData fileData = fileDataNew.get(i);
						File file = new File(WTSELauncher.BASE_DIR+"\\_data_update_"+latestVersion + "\\data\\" + fileData.fileName);
						if(file.exists()) {
							fileData.file = file;
						}
					}
					
					List<FileData> fileDataOldSkipped = new ArrayList<FileData>();
					fileDataOldSkipped.addAll(fileDataOld);
					
					
					// INSTALL DATA
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
							try {
								Files.deleteIfExists(fileData.file.toPath());
								fileDataOldSkipped.remove(fileData);
								Logger.get().info("Deleted " + fileData.file.getAbsolutePath());
							} catch (IOException e) {
								Logger.get().error(e);
								controller.onInstallUpdateDone(InstallStatus.FATAL);
								return;
							}
						}
					}

					
					// move new files
					for(int i=0; i<fileDataNew.size(); i++) {
						FileData fileData = fileDataNew.get(i);
						if(fileData.file != null && !fileData.hasMergeStrat()) {
							try {
								Files.move(fileData.file.toPath(), Paths.get(WTSELauncher.BASE_DIR+"\\data\\"+fileData.fileName), StandardCopyOption.REPLACE_EXISTING);
								Logger.get().info("Moved " + fileData.file.getAbsolutePath() + "  ->  " + WTSELauncher.BASE_DIR+"\\data\\"+fileData.fileName);
							} catch (IOException e) {
								Logger.get().error(e);
								controller.onInstallUpdateDone(InstallStatus.FATAL);
								return;
							}
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
								try {
									Files.move(fileData.file.toPath(), Paths.get(WTSELauncher.BASE_DIR+"\\data\\"+fileData.fileName), StandardCopyOption.REPLACE_EXISTING);
									Logger.get().info("Merged (move) " + fileData.file.getAbsolutePath() + "  ->  " + WTSELauncher.BASE_DIR+"\\data\\"+fileData.fileName);
								} catch (IOException e) {
									Logger.get().error(e);
									controller.onInstallUpdateDone(InstallStatus.FATAL);
									return;
								}
							} else {
								try {
									FileMerger.merge(oldFileData.file, fileData.file, fileData.mergeRoutine);
									fileDataOldSkipped.remove(oldFileData);
									Logger.get().info("Merged ("+fileData.mergeRoutine+") " + fileData.file.getAbsolutePath() + "  ->  " + WTSELauncher.BASE_DIR+"\\data\\"+fileData.fileName);
								} catch (IOException e) {
									Logger.get().error(e);
									controller.onInstallUpdateDone(InstallStatus.FATAL);
									return;
								}
							}
							
							
							// UPDATE LAUNCHER
							File fileLauncherCurr = new File(WTSELauncher.BASE_DIR+"\\WTSightEdit.jar");
							File fileLauncherNew = new File(WTSELauncher.BASE_DIR+"\\_data_update_"+latestVersion+"\\WTSightEdit.jar");
							File fileUpdateDir = new File(WTSELauncher.BASE_DIR+"\\_data_update_"+latestVersion);
							File fileCleanup = new File(WTSELauncher.BASE_DIR+"\\data\\cleanup.jar");
							
							Logger.get().info("Starting cleanup");
							ProcessBuilder builder = null;
							if(WTSELauncher.DEV_MODE) {
								builder = new ProcessBuilder("java", "-jar", fileCleanup.getAbsolutePath(), fileLauncherCurr.getAbsolutePath(), fileLauncherNew.getAbsolutePath(), fileUpdateDir.getAbsolutePath(), "dev");
							} else {
								builder = new ProcessBuilder("java", "-jar", fileCleanup.getAbsolutePath(), fileLauncherCurr.getAbsolutePath(), fileLauncherNew.getAbsolutePath(), fileUpdateDir.getAbsolutePath());
							}
							
							try {
								Process proc = builder.start();
								controller.onInstallUpdateDone(InstallStatus.DONE);

							} catch (IOException e) {
								Logger.get().error(e);
								controller.onInstallUpdateDone(InstallStatus.FATAL);
								return;
							}
							
							
						}
					}
					
					
				}
				
				
			}
		};
		
		thread.setDaemon(true);
		thread.start();
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



