package com.ruegnerlukas.wtlauncher.network;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.ruegnerlukas.wtlauncher.githubApi.APIRelease;
import com.ruegnerlukas.wtlauncher.githubApi.Asset;
import com.ruegnerlukas.wtlauncher.githubApi.Release;

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
	public Release getLatestRelease(String author, String repository) {
		return APIRelease.getLatestRelease(author, repository);
	}




	@Override
	public void download(File fileUpdateDir, Release release) throws IOException {

		Asset asset = release.assets.get(0);
		fileUpdateDir.mkdirs();
		
		 URL url = new URL(asset.browser_download_url);
		 HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		 connection.setRequestMethod("GET");
		 InputStream in = connection.getInputStream();
		 ZipInputStream zipIn = new ZipInputStream(in);

		ZipEntry entry = zipIn.getNextEntry();
		byte[] buffer = new byte[1024];

		while (entry != null) {

			File outFile = new File(fileUpdateDir.getAbsolutePath() + File.separator + entry.getName());

			if (!entry.isDirectory()) {

				new File(outFile.getParent()).mkdirs();
				FileOutputStream out = new FileOutputStream(outFile);
				int len;
				while ((len = zipIn.read(buffer)) > 0) {
					out.write(buffer, 0, len);
				}
				out.close();

			} else {
				outFile.mkdirs();
			}

			zipIn.closeEntry();
			entry = zipIn.getNextEntry();

		}

		zipIn.close();
	
	}

	
	
	

}
