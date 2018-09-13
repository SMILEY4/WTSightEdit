package com.ruegnerlukas.wtutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2i;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;

public class Config {

	private static File file;
	
	public static String build_version = "version_error";
	public static String build_date = "date_error";
	
	public static boolean update_auto = true;
	public static Vector2i app_window_size = new Vector2i(1280, 720);
	public static String app_style = "default";

	
	
	public static void load(File cfgFile) {
		
		Logger.get().info("Loading config: " + cfgFile.getAbsolutePath());
		file = cfgFile;
		
		// read file
		String strJson = "";
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(cfgFile));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			strJson = response.toString();
		} catch(IOException e) {
			Logger.get().warn("Loading config failed" + e);
			return;
		}

		
		// parse json
		JsonParser parser = new JsonParser();
		JsonObject jsonConfig = null;
		try {
			jsonConfig = parser.parse(strJson).getAsJsonObject();
		} catch(Exception e) {
			Logger.get().warn("Parsing config failed" + e);
			return;
		}

		// get data
		JsonObject jsonInfo = jsonConfig.has("info") ? jsonConfig.get("info").getAsJsonObject() : null;
		JsonObject jsonDefault = jsonConfig.has("default") ? jsonConfig.get("default").getAsJsonObject() : null;

		// info
		if(jsonInfo != null) {
			if(jsonInfo.has("build_version")) { build_version = jsonInfo.get("build_version").getAsString(); }
			if(jsonInfo.has("build_date")) 	  { build_date = jsonInfo.get("build_date").getAsString(); }
		}
		
		// default
		if(jsonDefault != null) {
			if(jsonDefault.has("update_auto")) { update_auto = jsonDefault.get("update_auto").getAsBoolean(); }
			if(jsonDefault.has("app_window_size")) {
				String[] strSize = jsonDefault.get("app_window_size").getAsString().split(",");
				app_window_size = new Vector2i(Integer.parseInt(strSize[0]), Integer.parseInt(strSize[1]));
			}
			if(jsonDefault.has("app_style")) 	  { app_style = jsonDefault.get("app_style").getAsString(); }
		}
		
		Logger.get().info("Config successfuly loaded.");
		
	}
	
	
	
	
	public static void write() {
		
		Logger.get().info("Saving Config to " + file.getAbsolutePath());
		
		if(file != null && file.exists()) {
			
			JsonObject jsonConfig = new JsonObject();
			
			JsonObject jsonInfo = new JsonObject();
			jsonInfo.addProperty("build_version", build_version);
			jsonInfo.addProperty("build_date", build_date);
			jsonConfig.add("info", jsonInfo);

			
			JsonObject jsonDefault = new JsonObject();
			jsonDefault.addProperty("update_auto", update_auto);
			jsonDefault.addProperty("app_window_size", app_window_size.x + "," + app_window_size.y);
			jsonDefault.addProperty("app_style", app_style);
			jsonConfig.add("default", jsonDefault);

			try {
				Writer writer = new FileWriter(file);
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				gson.toJson(jsonConfig, writer);
				writer.flush();
				writer.close();
				Logger.get().info("Config successfuly saved.");
			} catch (IOException e) {
				Logger.get().error("Could not save config.", e);
			}
			
		} else {
			Logger.get().info("Config could not be saved. Check if the file does (still) exist.");
		}
	}
	
	
	
}








