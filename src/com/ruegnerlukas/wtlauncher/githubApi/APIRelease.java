package com.ruegnerlukas.wtlauncher.githubApi;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.ruegnerlukas.simpleutils.PrettyJSON;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;

public class APIRelease {

	
	public static List<Release> getAllReleases(String owner, String repo) {
		
		List<Release> releases = new ArrayList<Release>(); 

		try {
			String strJson = GitHubAPI.getRequest(GitHubAPI.API_URL + "/repos/"+owner+"/"+repo+"/releases");
			JsonParser parser = new JsonParser();
			JsonArray jsonReleases = parser.parse(strJson).getAsJsonArray();
			
			for(int i=0; i<jsonReleases.size(); i++) {
				JsonElement jsonElementRelease = jsonReleases.get(i);
				if(jsonElementRelease == null || !jsonElementRelease.isJsonObject()) {
					continue;
				}
				JsonObject jsonRelease = jsonElementRelease.getAsJsonObject();
				Release release = jsonToRelease(jsonRelease);
				releases.add(release);
			}
			
		} catch (Exception e) {
			Logger.get().error(e);
		}
		
		return releases;
	}
	
	
	
	public static Release getSingleRelease(String owner, String repo, int release_id) {
		
		try {
			String strJson = GitHubAPI.getRequest(GitHubAPI.API_URL + "/repos/"+owner+"/"+repo+"/releases/" + release_id);
			JsonParser parser = new JsonParser();
			JsonObject jsonRelease = parser.parse(strJson).getAsJsonObject();
			
			Release release = jsonToRelease(jsonRelease);
			return release;
			
		} catch (Exception e) {
			Logger.get().error(e);
		}
		
		return null;
	}
	
	
	
	public static Release getLatestRelease(String owner, String repo) {
		
		try {
			String strJson = GitHubAPI.getRequest(GitHubAPI.API_URL + "/repos/"+owner+"/"+repo+"/releases/latest");
		
			JsonParser parser = new JsonParser();
			JsonObject jsonRelease = parser.parse(strJson).getAsJsonObject();
			
			Release release = jsonToRelease(jsonRelease);
			return release;
			
		} catch (Exception e) {
			Logger.get().error(e);
		}
		
		return null;
	}
	
	
	
	
	public static Release getReleaseByTagName(String owner, String repo, String tag) {
		
		try {
			String strJson = GitHubAPI.getRequest(GitHubAPI.API_URL + "/repos/"+owner+"/"+repo+"/releases/tags/" + tag);
			JsonParser parser = new JsonParser();
			JsonObject jsonRelease = parser.parse(strJson).getAsJsonObject();
			
			Release release = jsonToRelease(jsonRelease);
			return release;
			
		} catch (Exception e) {
			Logger.get().error(e);
		}
		
		return null;
	}
	
	
	
	
	private static void jsonInitPrimitives(JsonObject json, Object target) {
		for(String key : json.keySet()) {
			if(json.has(key) && json.get(key).isJsonPrimitive()) {
				JsonPrimitive e = json.get(key).getAsJsonPrimitive();
				if(e.isBoolean()) {
					ReflectionUtils.setBoolField(target, key, e.getAsBoolean());
					continue;
				}
				if(e.isNumber()) {
					ReflectionUtils.setIntField(target, key, e.getAsInt());
					continue;
				}
				if(e.isString()) {
					ReflectionUtils.setStringField(target, key, e.getAsString());
					continue;
				}
			}
		}
	}
	
	
	
	
	public static Release jsonToRelease(JsonObject jsonRelease) {
		
		Release release = new Release();
		jsonInitPrimitives(jsonRelease, release);
		
		if(jsonRelease.has("assets") && !jsonRelease.get("assets").isJsonNull()) {
			JsonArray jsonAssetsArr = jsonRelease.get("assets").getAsJsonArray();
		
			for(int j=0; j<jsonAssetsArr.size(); j++) {
				JsonElement jsonElementAsset = jsonAssetsArr.get(j);
				if(jsonElementAsset == null || !jsonElementAsset.isJsonObject()) {
					continue;
				}
				JsonObject jsonAsset = jsonElementAsset.getAsJsonObject();
				Asset asset = jsonToAsset(jsonAsset);
				release.assets.add(asset);
			}
			
		}
		if(jsonRelease.has("author") && !jsonRelease.get("author").isJsonNull()) {
			Author author = jsonToAuthor(jsonRelease.get("author").getAsJsonObject());
			release.author = author;
		}
		
		
		return release;
	}

	
	
	
	private static Author jsonToAuthor(JsonObject jsonAuthor) {
		Author author = new Author();
		jsonInitPrimitives(jsonAuthor, author);
		return author;
	}
	
	
	
	
	private static Asset jsonToAsset(JsonObject jsonAsset) {
		Asset asset = new Asset();
		jsonInitPrimitives(jsonAsset, asset);
		if(jsonAsset.has("uploader") && !jsonAsset.get("uploader").isJsonNull()) {
			Author uploader = jsonToAuthor(jsonAsset.get("uploader").getAsJsonObject());
			asset.uploader = uploader;
		}
		return asset;
	}
	
	
	
}
