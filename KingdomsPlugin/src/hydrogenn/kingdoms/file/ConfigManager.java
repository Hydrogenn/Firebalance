package hydrogenn.kingdoms.file;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import hydrogenn.kingdoms.Kingdom;
import hydrogenn.kingdoms.Kingdoms;
import hydrogenn.kingdoms.PlayerSpec;

/* 
 * ConfigManager.java
 * Made by Rayzr522
 * Date: Jul 9, 2016
 */
public class ConfigManager {

	private static Kingdoms plugin;

	public static void init(Kingdoms plugin) {

		ConfigManager.plugin = plugin;

		load();

	}

	public static void load() {

		loadKingdoms();
		loadPlayers();

	}

	private static void loadKingdoms() {

		File players = getFolder("kingdoms");

		List<File> files = Arrays.asList(players.listFiles());

		for (File f : files) {

			YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);

			Kingdom.loadFromConfig(conf);

		}

	}

	public static void save() {

		savePlayers();
		saveKingdoms();

	}

	private static void loadPlayers() {

		File players = getFolder("players");

		List<File> files = Arrays.asList(players.listFiles());

		for (File f : files) {

			YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);

			PlayerSpec.loadFromConfig(conf);

		}

	}

	private static void savePlayers() {

		File players = getFolder("players");
		
		Iterator<PlayerSpec> iter = PlayerSpec.iterator();
		while (iter.hasNext()) {
			PlayerSpec spec = iter.next();
			
			if (spec == null) {
				continue;
			}

			File f = new File(players, spec.getName() + ".yml");
			YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);

			try {
				spec.saveToConfig(conf).save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
	
	private static void saveKingdoms() {

		File players = getFolder("kingdoms");
		
		Iterator<Kingdom> iter = Kingdom.iterator();
		while (iter.hasNext()) {
			Kingdom kingdom = iter.next();
			
			if (kingdom == null) {
				continue;
			}

			File f = new File(players, kingdom.getName() + ".yml");
			YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);

			try {
				kingdom.saveToConfig(conf).save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}

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

	public static File getFile(String path) {

		return new File(plugin.getDataFolder() + File.separator + path);

	}

	public static File getFolder(String path) {

		File f = getFile(path);

		if (!f.exists()) {
			f.mkdirs();
		}

		return f;

	}

}
