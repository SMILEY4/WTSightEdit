package com.ruegnerlukas.wtlauncher.network;

import java.io.File;
import java.io.IOException;

import com.ruegnerlukas.wtlauncher.githubApi.Release;

public interface NetworkInterface {

	public boolean checkNetworkStatus();
	
	public Release getLatestRelease(String author, String repository);
	
	public void download(File fileUpdateDir, Release release) throws IOException;

}

