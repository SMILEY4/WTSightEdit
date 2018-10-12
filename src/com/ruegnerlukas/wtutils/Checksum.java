package com.ruegnerlukas.wtutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;

public class Checksum {

	public static String generate(File file) {
		return generateFromFile(file);
//		String fileFormat = file.getName().split("\\.")[1];
//		if(fileFormat.equals("txt") || fileFormat.equals(".xml")) {
//			return generateFromString(file);
//		} else {
//			return generateFromFile(file);
//		}
	}
	
	
	
	public static String generateFromFile(File file) {
		
		try {
			
			byte[] buffer = new byte[1024];
			MessageDigest md = MessageDigest.getInstance("MD5");
			InputStream in = Files.newInputStream(Paths.get(file.getAbsolutePath()));
			
			int numRead;
			do {
				numRead = in.read(buffer);
				if(numRead > 0) {
					md.update(buffer, 0, numRead);
				}
			} while(numRead != -1);
			in.close();
			byte[] digest = md.digest();
			
			String result = "";
			for(int i=0; i<digest.length; i++) {
				result += Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 );
			}
			return result;
			
		} catch (NoSuchAlgorithmException | IOException e) {
			Logger.get().error(e);
		}
		
		return null;
	}
	
	
	public static String generateFromString(File file) {
		
		try {
			
			BufferedReader in = new BufferedReader(new FileReader(file));
			String inputLine;
			String content = "";
			while( (inputLine=in.readLine()) != null) {
				content += inputLine + "\\n\\r";
			}
			
			in.close();
	        MessageDigest md;
			md = MessageDigest.getInstance("MD5");
	        byte[] messageDigest = md.digest(content.getBytes());
	        BigInteger number = new BigInteger(1, messageDigest);
	        String hashtext = number.toString(16);
			
			return hashtext;
			
		} catch(Exception e) {
			Logger.get().error(e);
		}
		
		return "error";
		
	}
	
	
}
