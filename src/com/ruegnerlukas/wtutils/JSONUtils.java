package com.ruegnerlukas.wtutils;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JSONUtils {

	
	public static void findPrimitive(JsonObject root, String name, List<JsonPrimitive> results) {
		
		if(root.has(name)) {
			Object obj = root.get(name);
			if(obj instanceof JsonObject) {
				// object with given name is object
			} else if(obj instanceof JsonArray) {
				// object with given name is array
			} else if(obj instanceof JsonPrimitive) {
				results.add((JsonPrimitive)obj);
			}
			
		}
		
		for(String key : root.keySet()) {
			Object obj = root.get(key);
			if(obj instanceof JsonObject) {
				JsonObject jsonObj = (JsonObject)obj;
				findPrimitive(jsonObj, name, results);
			} else if(obj instanceof JsonArray) {
				JsonArray jsonArray = (JsonArray)obj;
				findPrimitive(jsonArray, name, results);
			}
		}
		
		
	}
	
	
	public static void findPrimitive(JsonArray root, String name, List<JsonPrimitive> results) {
		for(int i=0; i<root.size(); i++) {
			Object obj = root.get(i);
			
			if(obj instanceof JsonObject) {
				JsonObject jsonObj = (JsonObject)obj;
				findPrimitive(jsonObj, name, results);
				
			} else if(obj instanceof JsonArray) {
				JsonArray jsonArray = (JsonArray)obj;
				findPrimitive(jsonArray, name, results);
				
			}
			
		}
		
	}
	
	
	
	public static void findObject(JsonObject root, String name, List<JsonObject> results) {
		
		if(root.has(name)) {
			Object obj = root.get(name);
			if(obj instanceof JsonObject) {
				results.add((JsonObject)obj);
			} else if(obj instanceof JsonArray) {
				// object with given name is array
			} else if(obj instanceof JsonPrimitive) {
				// obj with given name is primitive
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
