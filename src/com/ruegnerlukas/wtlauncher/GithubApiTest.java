package com.ruegnerlukas.wtlauncher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;

public class GithubApiTest {

	
	public static void main(String[] args) {
		new GithubApiTest();
	}
	
	
	
	public GithubApiTest() {
		
		try {
			
			// get releases
			String apiURL = "https://api.github.com";
			String owner = "SMILEY4";
			String repo = "WTSightEdit";
			String releases = getRequest(apiURL + "/repos/"+owner+"/"+repo+"/releases");
			Logger.get().info(prettyJson(releases));
			// -> "browser_download_url": "https://github.com/SMILEY4/WTSightEdit/releases/download/v0.7.1/WTSightEdit.zip"

			
			// download zip
//			String urlPath = "https://github.com/SMILEY4/WTSightEdit/releases/download/v0.7.1/WTSightEdit.zip";
//			downloadZIP(urlPath, "C:\\Users\\LukasRuegner\\Desktop\\testDir");
					
		} catch (Exception e) {
			Logger.get().error(e);
		}
		
	}
	
	
	
	
	
	private void downloadZIP(String urlPath, String destDir) throws IOException {
		
	       URL url = new URL(urlPath);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("GET");
	        
	        InputStream in = connection.getInputStream();
	        ZipInputStream zis = new ZipInputStream(in);
	        ZipEntry zipEntry = zis.getNextEntry();
	        
	        byte[] buffer = new byte[1024];
	        
	        while(zipEntry != null) {
	        	
	        	if(zipEntry.isDirectory()) {
	        		File dir = new File(destDir + "\\" + zipEntry.getName());
	        		dir.mkdirs();
	        		
	        	} else {
	        		String fileName = zipEntry.getName();
	        		File file = new File(destDir + "\\" + fileName);
	        		if(!file.getParentFile().exists()) {
	        			file.getParentFile().mkdirs();
	        		}
	        		FileOutputStream fos = new FileOutputStream(file);
	        		int len;
	        		while( (len=zis.read(buffer)) > 0 ) {
	        			fos.write(buffer, 0, len);
	        		}
	        		fos.close();
	        	}
	        	
	        	zipEntry = zis.getNextEntry();
	        }
	        
	        zis.closeEntry();
	        zis.close();
	        
	        
	}
	
	
	
	
	
	private final String USER_AGENT = "Mozilla/5.0";
	
	private String getRequest(String strURL) throws Exception {
		
		URL url = new URL(strURL); 
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		
		int responseCode = con.getResponseCode();
		Logger.get().info("Sending GET request to " + strURL);
		Logger.get().info("Response code: " + responseCode);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return response.toString();
	}
	
	
	
	private String prettyJson(String strJson) {
		JsonParser parser = new JsonParser();
		JsonElement json = parser.parse(strJson);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(json);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
