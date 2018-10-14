package com.ruegnerlukas.wtlauncher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class FileData {

	
	
	public static List<FileData> load(File file) throws IOException {
		
		List<FileData> list = new ArrayList<FileData>();

		// read file
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
		JsonElement rootElement = parser.parse(content);
		
		// get files
		JsonArray jsonFiles = rootElement.getAsJsonArray();
		for(int i=0; i<jsonFiles.size(); i++) {
			JsonElement e = jsonFiles.get(i);
			if(e == null || !e.isJsonPrimitive()) {
				continue;
			}
			
			String fileEntry = e.getAsJsonPrimitive().getAsString();
			if(fileEntry.contains(";")) {
				list.add(new FileData(fileEntry.split(";")[0], fileEntry.split(";")[1].split(":")[0], fileEntry.split(";")[1].split(":")[1]));
			} else {
				list.add(new FileData(fileEntry));
			}
			
		}
		
		return list;
	}
	
	
	
	
	
	public final String fileName;
	public final String mergeStrat;
	public final String mergeRoutine;
	public File file;
	
	public FileData(String fileName) {
		this(fileName, "NONE", "NONE");
	}
	
	
	public FileData(String fileName, String mergeStrat, String mergeRoutine) {
		this.fileName = fileName;
		this.mergeStrat = mergeStrat;
		this.mergeRoutine = mergeRoutine;
	}
	
	
	public boolean hasMergeStrat() {
		return !"NONE".equalsIgnoreCase(mergeStrat);
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof FileData && ((FileData)obj).fileName.equals(this.fileName));
	}
	
}
