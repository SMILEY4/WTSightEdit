package com.ruegnerlukas.wtcleanup;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;


public class Cleanup {

	public static void main(String[] args) {
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(args.length != 3) {
			Logger.get().info("Invalid arguments");
			System.exit(0);
		}
		
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

			// directory is empty, then delete it
			if (file.list().length == 0) {

				file.delete();

			} else {

				// list all the directory contents
				String files[] = file.list();

				for (String temp : files) {
					// construct the file structure
					File fileDelete = new File(file, temp);

					// recursive delete
					delete(fileDelete);
				}

				// check the directory again, if empty then delete it
				if (file.list().length == 0) {
					file.delete();
				}
			}

		} else {
			// if file, then delete it
			file.delete();
		}
	}
	
}
