package com.ruegnerlukas.githubApi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;

public class GitHubAPI {

	
	protected static final String USER_AGENT = "Mozilla/5.0";
	protected static final String API_URL = "https://api.github.com";
	
	
	
	
	protected static String getRequest(String strURL) throws Exception {
		
		URL url = new URL(strURL); 
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		
		int responseCode = con.getResponseCode();
		Logger.get().debug("Sending GET request to " + strURL);
		Logger.get().debug("Response code: " + responseCode);
		if(responseCode == 404) {
			throw new Exception("HTTP response message status: 404 - Not Found");
		}
		if(responseCode == 401) {
			throw new Exception("HTTP response message status: 401 - Unauthorized");
		}
		if(responseCode == -1) {
			throw new Exception("HTTP response message status: -1 - Unknown Error");
		}
		
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return response.toString();
	}
	
}
