package com.ruegnerlukas.wtutils;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class JSONUtils {

	
	public static void findObject(JsonObject root, String name, List<JsonObject> results) {
		
		if(root.has(name)) {
			Object obj = root.get(name);
			if(obj instanceof JsonObject) {
				results.add((JsonObject)obj);
			} else if(obj instanceof JsonArray) {
				// object with given name is array
			}
			
		}
		
		for(String key : root.keySet()) {
			Object obj = root.get(key);
			if(obj instanceof JsonObject) {
				JsonObject jsonObj = (JsonObject)obj;
				findObject(jsonObj, name, results);
			} else if(obj instanceof JsonArray) {
				JsonArray jsonArray = (JsonArray)obj;
				findObject(jsonArray, name, results);
			}
		}
		
		
	}
	
	
	public static void findObject(JsonArray root, String name, List<JsonObject> results) {
		for(int i=0; i<root.size(); i++) {
			Object obj = root.get(i);
			
			if(obj instanceof JsonObject) {
				JsonObject jsonObj = (JsonObject)obj;
				findObject(jsonObj, name, results);
				
			} else if(obj instanceof JsonArray) {
				JsonArray jsonArray = (JsonArray)obj;
				findObject(jsonArray, name, results);
			}
			
		}
		
	}
	
	
	
	
	public static void findArray(JsonObject root, String name, List<JsonArray> results) {
		
		if(root.has(name)) {
			Object obj = root.get(name);
			if(obj instanceof JsonArray) {
				results.add((JsonArray)obj);
			} else if(obj instanceof JsonObject) {
				// object with given name is object
			}
			
		}
		
		for(String key : root.keySet()) {
			Object obj = root.get(key);
			if(obj instanceof JsonObject) {
				JsonObject jsonObj = (JsonObject)obj;
				findArray(jsonObj, name, results);
			} else if(obj instanceof JsonArray) {
				JsonArray jsonArray = (JsonArray)obj;
				findArray(jsonArray, name, results);
			}
		}
		
		
	}
	
	
	public static void findArray(JsonArray root, String name, List<JsonArray> results) {
		for(int i=0; i<root.size(); i++) {
			Object obj = root.get(i);
			
			if(obj instanceof JsonObject) {
				JsonObject jsonObj = (JsonObject)obj;
				findArray(jsonObj, name, results);
				
			} else if(obj instanceof JsonArray) {
				JsonArray jsonArray = (JsonArray)obj;
				findArray(jsonArray, name, results);
			}
			
		}
		
	}
	
	
}
