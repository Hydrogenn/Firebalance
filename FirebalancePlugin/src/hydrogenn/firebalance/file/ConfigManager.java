package hydrogenn.firebalance.file;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import hydrogenn.firebalance.Firebalance;
import hydrogenn.firebalance.PlayerSpec;

/* 
 * ConfigManager.java
 * Made by Rayzr522
 * Date: Jul 9, 2016
 */
public class ConfigManager {

	private static Firebalance plugin;

	private static FileConfiguration config;
	private static YamlConfiguration nationGlobal;
	private static YamlConfiguration nation1;
	private static YamlConfiguration nation2;
	private static YamlConfiguration nation3;

	public static void init(Firebalance plugin) {

		ConfigManager.plugin = plugin;

		loadConfig();

	}

	public static void loadConfig() {

		config = getConfig("config.yml");
		nationGlobal = getConfig("nations/global.yml");
		nationGlobal = getConfig("nations/nation1.yml");
		nationGlobal = getConfig("nations/nation2.yml");
		nationGlobal = getConfig("nations/nation3.yml");

		loadPlayers();

		loadChunks();

	}

	private static void loadPlayers() {

		File players = getFolder("players");

		List<File> files = Arrays.asList(players.listFiles());

		PlayerSpec.list.clear();

		for (File f : files) {

			YamlConfiguration pconf = YamlConfiguration.loadConfiguration(f);

			PlayerSpec.list.add(PlayerSpec.loadFromConfig(pconf));

		}

	}

	private static void loadChunks() {

	}

	private static YamlConfiguration getConfig(String path) {

		return getConfig(path, path);

	}

	private static YamlConfiguration getConfig(String path, String pathToDefault) {

		File f = getFile(path);

		if (!f.exists()) {
			plugin.saveResource(pathToDefault, true);
		}

		return YamlConfiguration.loadConfiguration(f);

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
