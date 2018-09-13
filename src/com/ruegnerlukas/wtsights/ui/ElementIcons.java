package com.ruegnerlukas.wtsights.ui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtutils.Config;

public class ElementIcons {

	public static final int ICON_WIDTH = 20;
	public static final int ICON_HEIGHT = 20;

	public static BufferedImage[][] icons = new BufferedImage[0][0];
	

	
	
	
	public static void load(String path, boolean insideJar) {
		Logger.get().info("Loading element icons: " + path + " (" + insideJar + ")");
		
		// LOAD FILE
		BufferedImage imgIcons = null;
		
		if(insideJar) {
			try {
				imgIcons = ImageIO.read(AmmoIcons.class.getResourceAsStream(path));
			} catch (IOException e) {
				Logger.get().warn("Loading element icons failed", e);
				return;
			}
			
		} else {
			try {
				imgIcons = ImageIO.read(new File(path));
			} catch (IOException e) {
				Logger.get().warn("Loading element icons failed", e);
				return;
			}
		}
		
		if(imgIcons == null) {
			Logger.get().warn("Loading element icons failed (img=null)");
			return;
		}
		
		// CUT OUT ICONS
		int nIcons = imgIcons.getWidth()/ICON_WIDTH;
		BufferedImage[][] arrIcons = new BufferedImage[nIcons][2];
		
		for(int i=0; i<nIcons; i++) {
			BufferedImage iconLight = new BufferedImage(ICON_WIDTH, ICON_HEIGHT, BufferedImage.TYPE_INT_ARGB);
			BufferedImage iconDark = new BufferedImage(ICON_WIDTH, ICON_HEIGHT, BufferedImage.TYPE_INT_ARGB);
			iconLight.getGraphics().drawImage(imgIcons, 0, 0, ICON_WIDTH, ICON_HEIGHT, ICON_WIDTH*i, 0, ICON_WIDTH*(i+1), ICON_HEIGHT, null);
			iconDark.getGraphics().drawImage(imgIcons, 0, 0, ICON_WIDTH, ICON_HEIGHT, ICON_WIDTH*i, ICON_HEIGHT, ICON_WIDTH*(i+1), ICON_HEIGHT*2, null);
			arrIcons[i][0] = iconLight;
			arrIcons[i][1] = iconDark;
		}

		icons = arrIcons;
		
		
	}

	
	
	
	public static BufferedImage getIcon(int index) {
		return getIcon(index, "dark".equals(Config.app_style));
	}
	
	
	public static BufferedImage getIcon(int index, boolean dark) {
		return icons[index][dark ? 1 : 0];
	}
	
}
