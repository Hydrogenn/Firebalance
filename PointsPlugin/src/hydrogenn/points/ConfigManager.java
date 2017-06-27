package hydrogenn.points;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * Manages file i/o. Inspired by Rayzr's implementation in Firebalance.
 * @author Hydrogenn
 *
 */
public class ConfigManager {
	
	private static Plugin plugin;
	
	public static void init(Plugin plugin) {
		ConfigManager.plugin = plugin;
		load();
	}
	
	public static void load() {
		
		loadPoints();

	}
	
	public static void loadPoints() {
		
		File disguises = getFolder("points");

		List<File> files = Arrays.asList(disguises.listFiles());

		for (File f : files) {

			YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);

			//TODO load points from config

		}
	}
	
	public static void save() {
		
		savePoints();

	}
	
	public static void savePoints() {


		File points = getFolder("points");
		
		for (File file : points.listFiles()) {
			file.delete();
		};

		for (Entry<UUID,Integer> entry : ((Points)plugin).getEntries()) {
			File f = new File(points, Bukkit.getOfflinePlayer(entry.getKey()).getName() + ".yml");
			YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);

			/*
			try {
				//TODO save to config
			} catch (IOException e) {
				e.printStackTrace();
			}*/

		}

	}

	public static YamlConfiguration getConfig(String path, String pathToDefault) {

		File f = getFile(path);

		if (!f.exists()) {
			try {
				plugin.saveResource(pathToDefault, true);
			} catch (Exception e) {
				try {
					f.createNewFile();
				} catch (Exception e1) {
					System.err.println("Failed to create file: " + path);
					e1.printStackTrace();
				}
			}
		}

		return YamlConfiguration.loadConfiguration(f);

	}

	public static void saveConfig(YamlConfiguration conf) {

		try {
			conf.save(new File(conf.getCurrentPath()));
		} catch (IOException e) {
			System.err.println("Failed to save config file: " + conf.getName());
			e.printStackTrace();
		}

	}
	
	public static File getFolder(String path) {

		File f = getFile(path);

		if (!f.exists()) {
			f.mkdirs();
		}

		return f;

	}
	
	public static File getFile(String path) {

		return new File(plugin.getDataFolder() + File.separator + path);

	}
}
