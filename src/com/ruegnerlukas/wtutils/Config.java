//package com.ruegnerlukas.wtutils;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.HashMap;
//import java.util.Map;
//
//import com.ruegnerlukas.simpleutils.logging.logger.Logger;
//
//public class Config {
//
//	
//	private static Map<String,String> configMap = new HashMap<String,String>();
//	
//	
//	
//	
//	
//	public static void load(File file) {
//
//		configMap.clear();
//		
//		if(!file.exists()) {
//			Logger.get().warn("Config file does not exist. Creating default config file.");
//			create(file);
//		}
//		
//		try {
//			
//			
//			BufferedReader reader = new BufferedReader(new FileReader(file));
//			String line = null;
//			
//			while( (line = reader.readLine()) != null) {
//				
//				line = line.trim();
//				if(line.isEmpty() || line.startsWith(";")) {
//					continue;
//				}
//				
//				try {
//					String key = line.split("=")[0].toLowerCase().trim();
//					String value = line.split("=")[1].trim();
//					configMap.put(key, value);
//				} catch(Exception e) {
//					// ignore
//				}
//			}
//			
//			reader.close();
//			
//			Logger.get().info("Config file loaded (" + file.getAbsolutePath() + ")");
//			
//		} catch (IOException e) {
//			Logger.get().error(e);
//		}
//		
//		
//	}
//
//	
//	
//	
//	public static void create(File file) {
//		
//		final String default_config =
//				"; updater\r\n" + 
//				"auto_update = true\r\n" + 
//				"update_path = https://raw.githubusercontent.com/SMILEY4/WTSightEdit/master/data\r\n" + 
//				"\r\n" + 
//				"; application\r\n" + 
//				"window_size = 1280,720\r\n" + 
//				"skin = default";
//		
//		try {
//			if(!file.getParentFile().exists()) {
//				file.getParentFile().mkdir();
//			}
//			file.createNewFile();
//			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, false)));
//			out.write(default_config);
//			out.close();
//		} catch (IOException e) {
//			Logger.get().error(e);
//		}
//		
//	}
//	
//	
//	
//	
//	public static String getValue(String key) {
//		return configMap.get(key.toLowerCase().trim());
//	}
//	
//	
//	public static String[] getValues(String key) {
//		String value = configMap.get(key.toLowerCase().trim());
//		if(value != null) {
//			return value.split(",");
//		} else {
//			return null;
//		}
//	}
//	
//	
//	
//}
