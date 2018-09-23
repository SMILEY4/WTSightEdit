package com.ruegnerlukas.wtsights.ui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtutils.Config;

public class AmmoIcons {

	
	public static final int ICON_WIDTH = 55;
	public static final int ICON_HEIGHT = 55;

	public static Map<String, BufferedImage[]> icons = new HashMap<String,BufferedImage[]>();

	
	
	
	public static void load(String path, boolean insideJar) {
		Logger.get().info("Loading ammunition icons: " + path + " (" + insideJar + ")");
		
		icons.clear();

		// LOAD FILE
		BufferedImage imgIcons = null;
		
		if(insideJar) {
			try {
				imgIcons = ImageIO.read(AmmoIcons.class.getResourceAsStream(path));
			} catch (IOException e) {
				Logger.get().warn("Loading ammunition icons failed", e);
				return;
			}
			
		} else {
			try {
				imgIcons = ImageIO.read(new File(path));
			} catch (IOException e) {
				Logger.get().warn("Loading ammunition icons failed", e);
				return;
			}
		}
		
		if(imgIcons == null) {
			Logger.get().warn("Loading ammunition icons failed (img=null)");
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


		// MAP ICONS TO NAME
		icons.put("unknown", arrIcons[17]);
		icons.put("machinegun", arrIcons[16]);
		icons.put("apcr_tank", arrIcons[4]);
		icons.put("apds_tank", arrIcons[5]);
		icons.put("apds_fs_long_tank", arrIcons[6]);
		icons.put("apds_fs_full_body_steel_tank", arrIcons[6]);
		icons.put("apds_fs_tungsten_small_core_tank", arrIcons[6]);
		icons.put("apds_fs_tungsten_l10_l15_tank", arrIcons[6]);
		icons.put("apbc_tank", arrIcons[2]);
		icons.put("ap_large_caliber_tank", arrIcons[0]);
		icons.put("apc_tank", arrIcons[1]);
		icons.put("apcbc_tank", arrIcons[3]);
		icons.put("ap_tank", arrIcons[0]);
		icons.put("heat_tank", arrIcons[9]);
		icons.put("heat_fs_tank", arrIcons[10]);
		icons.put("hesh_tank", arrIcons[8]);
		icons.put("shrapnel_tank", arrIcons[13]);
		icons.put("atgm_tank", arrIcons[11]);
		icons.put("rocket_tank", arrIcons[14]);
		icons.put("smoke_tank", arrIcons[12]);
		icons.put("he_frag_fs_tank", arrIcons[7]);
		icons.put("he_frag_tank", arrIcons[7]);
		icons.put("he_grenade_tank", arrIcons[15]);
		icons.put("heat_grenade_tank", arrIcons[15]);
		icons.put("aphe_tank", arrIcons[0]);
		icons.put("aphebc_tank", arrIcons[2]);
		
	}

	
	
	public static BufferedImage getIcon(String type) {
		return getIcon(type, "dark".equals(Config.app_style));
	}
	
	
	public static BufferedImage getIcon(String type, boolean dark) {
		BufferedImage[] imgs = icons.get(type);
		if(imgs == null) {
			imgs = icons.get("unknown");
		}
		return dark ? imgs[1] : imgs[0];
	}
	
	
	
}
