package com.ruegnerlukas.wtlauncher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;

public class FileMerger {

	public static boolean merge(File fileOld, File fileNew, String mergeRoutine) throws IOException {

		if("config".equalsIgnoreCase(mergeRoutine)) {
			
			
			// parse files
			JsonElement rootElementOld = null;
			JsonElement rootElementNew = null;

			rootElementOld = toJson(fileOld);
			rootElementNew = toJson(fileNew);
			if(rootElementOld == null || rootElementNew == null) {
				Logger.get().warn("Could not merge files " + fileOld + ", " + fileNew + " (" + mergeRoutine + ")");
				Files.move(fileNew.toPath(), fileOld.toPath(), StandardCopyOption.REPLACE_EXISTING);
				return false;
			}

			// merge files
			try {
				
				JsonObject objUserOld = rootElementOld.getAsJsonObject().get("user").getAsJsonObject();
				JsonObject objUserNew = rootElementNew.getAsJsonObject().get("user").getAsJsonObject();
				JsonObject objDefaultNew = rootElementNew.getAsJsonObject().get("default").getAsJsonObject();

				for(String oldKey : objUserOld.keySet()) {
					if(objDefaultNew.keySet().contains(oldKey)) {
						JsonElement userSetting = objUserOld.get(oldKey);
						if(userSetting.isJsonArray()) {
							objUserNew.add(oldKey, userSetting.getAsJsonArray());
						} else {
							objUserNew.add(oldKey, userSetting.getAsJsonPrimitive());
						}
					}
				}
				
	            FileWriter fileWriter = new FileWriter(fileNew);
	            fileWriter.write(new GsonBuilder().setPrettyPrinting().create().toJson(rootElementNew));
	            fileWriter.close();
				Files.move(fileNew.toPath(), fileOld.toPath(), StandardCopyOption.REPLACE_EXISTING);
				Logger.get().info("Merge successful (" + mergeRoutine +")");

	            
			} catch(Exception e) {
				Logger.get().warn("Merge failed (" + mergeRoutine +")");
				Files.move(fileNew.toPath(), fileOld.toPath(), StandardCopyOption.REPLACE_EXISTING);
				return false;
			}

			return true;
			
		} else {
			Logger.get().warn("Unknown file-merge-routine: " + mergeRoutine + ". Using default 'replace' strategy.");
			Files.move(fileNew.toPath(), fileOld.toPath(), StandardCopyOption.REPLACE_EXISTING);
			return false;
		}
		
	}
	
	
	
	
	private static JsonElement toJson(File file) {
		
		try {
		
			// read file content
			BufferedReader reader = new BufferedReader(new FileReader(file));
			StringBuilder sb = new StringBuilder();
			String str;
			while( (str = reader.readLine()) != null) {
				sb.append(str);
			}
			reader.close();
			String content = sb.toString();
			
			// parse json
			JsonParser parser = new JsonParser();
			return parser.parse(content);
			
		} catch(Exception e) {
			Logger.get().warn("Error when parsing json-file: " + file + " => "+ e.getClass().getName());
			return null;
		}
	}
	
	
}
