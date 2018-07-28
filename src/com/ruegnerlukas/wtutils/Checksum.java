package com.ruegnerlukas.wtutils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;

public class Checksum {

	public static String generate(File file) {
		
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
	
	
}
