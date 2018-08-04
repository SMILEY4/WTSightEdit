package com.ruegnerlukas.wtlauncher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.ruegnerlukas.githubApi.APIRelease;
import com.ruegnerlukas.githubApi.Asset;
import com.ruegnerlukas.githubApi.Release;

public class Downloader {

	
	
	
	public static void download(File fileUpdateDir, Release release) throws IOException {
		Asset asset = release.assets.get(0);
		fileUpdateDir.mkdirs();
		downloadZipFile(asset.browser_download_url, fileUpdateDir);
	}
	
	
	
	
	private static void downloadZipFile(String strURL, File dstDirectory) throws IOException {
		// URL url = new URL(strURL);
		// HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		// connection.setRequestMethod("GET");
		// InputStream in = connection.getInputStream();
		// ZipInputStream zipIn = new ZipInputStream(in);
		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(new File("C:\\Users\\LukasRuegner\\Desktop\\WTSightEdit_071.zip"))); // TMP

		ZipEntry entry = zipIn.getNextEntry();
		byte[] buffer = new byte[1024];

		while (entry != null) {

			File outFile = new File(dstDirectory.getAbsolutePath() + File.separator + entry.getName());

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
