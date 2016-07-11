package com.rayzr522.battlebricks;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/* 
 * ConfigManager.java
 * Made by Rayzr522
 * Date: Jul 11, 2016
 */
public class ConfigManager {

	public static boolean DEBUG_ENABLED = false;

	private static BattleBricks plugin;
	private static FileConfiguration config;

	public static void init(BattleBricks plugin) {
		ConfigManager.plugin = plugin;
		ConfigManager.config = plugin.getConfig();
	}

	public static void load() {

		// Check debug
		DEBUG_ENABLED = config.getBoolean("debug");

		// Load players
		File playersFolder = getFile("players/");

		if (!playersFolder.exists()) {
			playersFolder.mkdirs();
			return;
		}

		PlayerData.map.clear();

		for (File playerFile : playersFolder.listFiles()) {

			if (!playerFile.getName().endsWith(".yml")) {
				continue;
			}

			YamlConfiguration conf = getConfig(playerFile);

			PlayerData data = PlayerData.loadFromConfig(conf);

			PlayerData.map.put(data.getUUID(), data);

		}

	}

	public static void save() {

		for (PlayerData data : PlayerData.map.values()) {

			save(data);

		}

	}

	public static void save(Player p) {

		if (!PlayerData.map.containsKey(p.getUniqueId())) {
			return;
		}

		PlayerData data = PlayerData.map.get(p.getUniqueId());

		save(data);

	}

	public static void save(PlayerData data) {

		UUID uuid = data.getUUID();

		File file = getFile("players/" + uuid + ".yml");

		YamlConfiguration conf = getConfig(file);

		saveConfig(data.saveToConfig(conf), file);

	}

	public static YamlConfiguration getConfig(File file) {

		return YamlConfiguration.loadConfiguration(file);

	}

	public static YamlConfiguration getConfig(String path) {

		return YamlConfiguration.loadConfiguration(getFile(path));

	}

	public static void saveConfig(YamlConfiguration conf, File file) {

		try {
			conf.save(file);
		} catch (IOException e) {
			System.err.println("Failed to save config file '" + conf.getName() + "'");
			e.printStackTrace();
		}

	}

	public static void saveConfig(YamlConfiguration conf, String path) {

		try {
			conf.save(getFile(path));
		} catch (IOException e) {
			System.err.println("Failed to save config file '" + conf.getName() + "'");
			e.printStackTrace();
		}

	}

	public static File getFile(String path) {

		return new File(plugin.getDataFolder() + File.separator + path);

	}

}
