package com.rayzr522.battlebricks;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.inventory.ShapedRecipe;
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

	private ShapedRecipe brickRecipe = new ShapedRecipe(BrickItem.PLACEHOLDER).shape("III", "IBI", "GGG")
			.setIngredient('I', Material.IRON_INGOT).setIngredient('B', Material.CLAY_BRICK)
			.setIngredient('G', Material.GOLD_INGOT);

	@Override
	public void onEnable() {

		logger = getLogger();
		pdf = getDescription();

		saveResource("config.yml", false);

		Timed.init(this);

		ConfigManager.init(this);
		ConfigManager.load();

		getCommand("battlebricks").setExecutor(new BattleBricksCommand(this));

		getServer().addRecipe(brickRecipe);

		getServer().getPluginManager().registerEvents(new BBListener(), this);

		logger.info(pdf.getName() + " v" + pdf.getVersion() + " enabled");

	}

	@Override
	public void onDisable() {

		ConfigManager.save();

		logger.info(pdf.getName() + " v" + pdf.getVersion() + " disabled");

	}

	public ShapedRecipe getRecipe() {
		return brickRecipe;
	}

}
