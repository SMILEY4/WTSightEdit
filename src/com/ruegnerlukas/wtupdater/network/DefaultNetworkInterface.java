package com.ruegnerlukas.wtupdater.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ruegnerlukas.wtutils.Config;

public class DefaultNetworkInterface implements NetworkInterface {

	@Override
	public boolean checkNetworkStatus() {
		try {
			final URL url = new URL("https://github.com");
			final URLConnection conn = url.openConnection();
			conn.connect();
			conn.getInputStream().close();
			return true;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			return false;
		}
	}

	
	
	
	
	@Override
	public boolean downloadFile(String fileName, File outFile) throws IOException {
		String link = Config.getValue("update_path") + "/" + fileName;
		URL url = new URL(link);
		HttpURLConnection http = (HttpURLConnection) url.openConnection();
		Map<String, List<String>> header = http.getHeaderFields();
		while (isRedirected(header)) {
			link = header.get("Location").get(0);
			url = new URL(link);
			http = (HttpURLConnection) url.openConnection();
			header = http.getHeaderFields();
		}
		Files.copy(http.getInputStream(), outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		return true;
	}
	
	
	
	private boolean isRedirected(Map<String, List<String>> header) {
		for (String hv : header.get(null)) {
			if (hv.contains(" 301 ") || hv.contains(" 302 "))
				return true;
		}
		return false;
	}





	@Override
	public List<String> getFileContent(String strURL) throws IOException {
		
		URL url = new URL(strURL);
		
		List<String> lines = new ArrayList<String>();
		
		HttpURLConnection http = (HttpURLConnection) url.openConnection();

		Map<String, List<String>> header = http.getHeaderFields();
		while (isRedirected(header)) {
			
			strURL = header.get("Location").get(0);
			url = new URL(strURL);
			http = (HttpURLConnection) url.openConnection();
			header = http.getHeaderFields();
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
		String inputLine;
		while( (inputLine=in.readLine()) != null) {
			lines.add(inputLine);
		}
		
		in.close();
		http.disconnect();
		
		return lines;
	}
	
	
	
	
	
}
