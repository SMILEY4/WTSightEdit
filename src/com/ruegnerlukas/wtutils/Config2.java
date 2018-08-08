package com.ruegnerlukas.wtutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ruegnerlukas.simplemath.vectors.vec2.Vector2i;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;

public class Config2 {

	
	public static String build_version = "version_error";
	public static String build_date = "date_error";
	
	public static boolean update_auto = true;
	public static Vector2i app_window_size = new Vector2i(1280, 720);
	public static String app_style = "default";

	
	
	
	public static void load(File cfgFile) {
		
		Logger.get().info("Loading config: " + cfgFile.getAbsolutePath());

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
		JsonObject jsonUser = jsonConfig.has("user") ? jsonConfig.get("user").getAsJsonObject() : null;

		// info
		if(jsonInfo != null) {
			if(jsonInfo.has("build_version")) { build_version = jsonInfo.get("build_version").getAsString(); }
			if(jsonInfo.has("build_date")) 	  { build_date = jsonInfo.get("build_date").getAsString(); }
		}
		
		// default
		if(jsonDefault != null) {
			if(jsonInfo.has("update_auto")) { update_auto = jsonDefault.get("update_auto").getAsBoolean(); }
			if(jsonInfo.has("app_window_size")) {
				String[] strSize = jsonDefault.get("app_window_size").getAsString().split(",");
				app_window_size = new Vector2i(Integer.parseInt(strSize[0]), Integer.parseInt(strSize[1]));
			}
			if(jsonInfo.has("app_style")) 	  { app_style = jsonInfo.get("app_style").getAsString(); }
		}
		
		// user
		if(jsonUser != null) {
			if(jsonUser.has("update_auto")) { update_auto = jsonUser.get("update_auto").getAsBoolean(); }
			if(jsonUser.has("app_window_size")) {
				String[] strSize = jsonUser.get("app_window_size").getAsString().split(",");
				app_window_size = new Vector2i(Integer.parseInt(strSize[0]), Integer.parseInt(strSize[1]));
			}
			if(jsonUser.has("app_style")) 	  { app_style = jsonUser.get("app_style").getAsString(); }
		}
		
		Logger.get().info("Config successfuly loaded.");
		
	}
	
	
	
}








