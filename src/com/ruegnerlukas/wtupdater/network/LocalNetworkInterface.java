package com.ruegnerlukas.wtupdater.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;

public class LocalNetworkInterface extends NetworkInterface {

	
	private File directory = null;
	
	
	
	
	public void setDirectory(File dir) {
		if(!dir.exists()) {
			Logger.get().warn("Local update directory does not exist: " + dir.getPath());
		}
		this.directory = dir;
	}
	
	
	
	
	@Override
	public boolean checkNetworkStatus() {
		return directory != null && directory.exists();
	}

	
	
	
	@Override
	public List<String> getFileContent(String strUrl) throws IOException {
		String fileName = strUrl.substring(strUrl.lastIndexOf("/")+1, strUrl.length());
		Logger.get().debug("getContent: " + strUrl + "  ->  " + directory.getPath() + "/" + fileName);
		
		File file = null;
		for(File f : directory.listFiles()) {
			if(f.getName().equals(fileName)) {
				file = f;
				break;
			}
		}
		
		if(file == null) {
			Logger.get().error("File " + fileName + " does not exist!");
		}
		
		List<String> lines = new ArrayList<String>();
		
		BufferedReader in = new BufferedReader(new FileReader(file));
		String inputLine;
		while( (inputLine=in.readLine()) != null) {
			lines.add(inputLine);
		}
		in.close();
		
		return lines;
	}

	
	
	
	@Override
	public boolean downloadFile(String fileName, File outFile) throws IOException {
		Logger.get().debug("download: " + fileName);
		Files.copy(Paths.get(directory.getPath() + "/" + fileName), outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		return true;
	}

	
	
}










