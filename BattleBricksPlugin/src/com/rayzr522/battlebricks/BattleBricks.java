package com.rayzr522.battlebricks;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/* 
 * BattleBricks.java
 * Made by Rayzr522
 * Date: Jul 11, 2016
 */
public class BattleBricks extends JavaPlugin {
	
	private Logger logger;
	private PluginDescriptionFile pdf;
	
	@Override
	public void onEnable() {
		
		logger = getLogger();
		pdf = getDescription();
		
		logger.info(pdf.getName() + " v" + pdf.getVersion() + " enabled");
		
	}
	
	@Override
	public void onDisable() {
		
		logger.info(pdf.getName() + " v" + pdf.getVersion() + " disabled");
		
	}

}
