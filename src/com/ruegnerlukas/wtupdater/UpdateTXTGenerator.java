package com.ruegnerlukas.wtupdater;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtutils.Checksum;

public class UpdateTXTGenerator {

	static final String PATH_DATA = "D:\\LukasRuegner\\Programmieren\\Java\\Workspace\\WarThunderSightEditor\\data_localUpdate";
	static final String FILE_OUT = "D:\\LukasRuegner\\Programmieren\\Java\\Workspace\\WarThunderSightEditor\\data_localUpdate\\update.txt";

	
	
	public static void main(String[] args) {
		generateUpdateTxt("New version available (0.7).", null, "0.7");
	}
	
	
	
	
	
	public static void generateUpdateTxt(String updateMessage, String manualMessage, String versionMain) {
		
		Logger.get().info("Generating update.txt");
		Logger.get().info(" data: " + PATH_DATA);
		Logger.get().info(" out:  " + FILE_OUT);

		File dirData = new File(PATH_DATA);

		List<String> lines = new ArrayList<String>();
		
		// version
		lines.add("; version");
		lines.add("build_version = " + versionMain);
		lines.add("");
		
		// flags
		lines.add("; flags");
		lines.add("");
		
		// messages
		lines.add("; messages");
		lines.add("update_message = " + updateMessage);
		if(manualMessage != null && !manualMessage.isEmpty()) {
			lines.add("manual_message = " + manualMessage);
		}
		lines.add("");

		// files
		lines.add("; files");
		for(File file : dirData.listFiles()) {
			if(file.getName().contains("update.txt")) {
				continue;
			}
			if(file.getName().contains("log.txt")) {
				continue;
			}

			
			String checksum = Checksum.generate(file);
			if(checksum == null) {
				Logger.get().error("error generating checksum for " + file.getAbsolutePath());
				continue;
			}
			
			lines.add("file = " + file.getName() + ":" + checksum);
		}
		
		Logger.get().info("================");
		for(String line : lines) {
			Logger.get().info(line);
		}
		Logger.get().info("================");

		
		try {
			Files.write(Paths.get(FILE_OUT), lines);
		} catch (IOException e) {
			Logger.get().error(e);
		}
		
		Logger.get().info("done");
	}
	
}
