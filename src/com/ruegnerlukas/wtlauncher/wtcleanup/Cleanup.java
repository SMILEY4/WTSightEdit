package com.ruegnerlukas.wtlauncher.wtcleanup;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import com.ruegnerlukas.simpleutils.JarLocation;
import com.ruegnerlukas.simpleutils.logging.LogLevel;
import com.ruegnerlukas.simpleutils.logging.filter.FilterLevel;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.simpleutils.logging.target.LogFileTarget;


public class Cleanup {

	public static boolean DEV_MODE = false;
	
	public static void main(String[] args) {
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(args.length < 3) {
			Logger.get().info("Invalid arguments");
			System.exit(0);
		}
		
		// dev mode
		if(args.length > 3 && "dev".equals(args[3])) {
			DEV_MODE = true;
		}
		
		// base dir
		String BASE_DIR = new File(JarLocation.getJarFile(Cleanup.class)).getParentFile().getAbsolutePath();
		BASE_DIR = BASE_DIR.replaceAll("%20", " ");
		
		// logger
		Logger.get().redirectStdOutput(LogLevel.DEBUG, LogLevel.ERROR);
	    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
	        public void run() {
	    		Logger.get().info("=========================================");
	    		Logger.get().blankLine();
	    		Logger.get().close();
	        }
	    }, "Shutdown-thread"));

		if(DEV_MODE) {
			Logger.get().getFilterManager().addFilter(FilterLevel.only(LogLevel.values()));
			
		} else {
			Logger.get().getFilterManager().addFilter(FilterLevel.not(LogLevel.DEBUG));
			File logFile = new File(BASE_DIR + "\\log.txt");
			if(!logFile.exists()) {
				try {
					if(!logFile.getParentFile().exists()) {
						logFile.getParentFile().mkdir();
					}
					logFile.createNewFile();
				} catch (IOException e) {
					Logger.get().error(e);
				}
			}
			
			Logger.get().setLogTarget(new LogFileTarget(logFile, true));
		}
		
		
		Logger.get().info("=============== CLEANUP ===============");
		
		String pathLauncherOld = args[0];
		String pathLauncherNew = args[1];
		String pathUpdateDir   = args[2];

		File fileLauncherOld = new File(pathLauncherOld);
		File fileLauncherNew = new File(pathLauncherNew);
		File fileLauncherDest = new File(fileLauncherOld.getParentFile().getAbsolutePath() + "\\" + fileLauncherOld.getName());
		
		try {
			
			// delete old launcher
			Logger.get().info("Delete " + fileLauncherOld.toPath());
			Files.deleteIfExists(fileLauncherOld.toPath());
			
			// move + rename new launcher
			Logger.get().info("Move " + fileLauncherNew.toPath() + "  ->  " + fileLauncherDest.toPath());
			Files.move(fileLauncherNew.toPath(), fileLauncherDest.toPath(), StandardCopyOption.REPLACE_EXISTING);
			
			// delete update directory
			Logger.get().info("Delete " + pathUpdateDir);
			delete(new File(pathUpdateDir));
			
			// RUN NEW LAUNCHER
			Logger.get().info("Run new launcher: " + fileLauncherDest.getAbsolutePath());
			ProcessBuilder builder = new ProcessBuilder("java", "-jar", fileLauncherDest.getAbsolutePath());
			try {
				Process proc = builder.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	

	public static void delete(File file) throws IOException {

		if (file.isDirectory()) {

			if (file.list().length == 0) {
				file.delete();

			} else {
				String files[] = file.list();
				for (String temp : files) {
					File fileDelete = new File(file, temp);
					delete(fileDelete);
				}
				if (file.list().length == 0) {
					file.delete();
				}
			}

		} else {
			file.delete();
		}
	}
	
	
}
