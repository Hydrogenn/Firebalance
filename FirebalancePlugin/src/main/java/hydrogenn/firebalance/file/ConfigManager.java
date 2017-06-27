package hydrogenn.firebalance.file;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import hydrogenn.firebalance.ChunkSpec;
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

		load();

	}

	public static void load() {
		
		config = getConfig("config.yml");
		nationGlobal = getConfig("nations/global.yml");
		nation1 = getConfig("nations/nation1.yml");
		nation2 = getConfig("nations/nation2.yml");
		nation3 = getConfig("nations/nation3.yml");

		loadPlayers();

		loadChunks();

	}

	public static void save() {

		savePlayers();
		saveChunks();

	}

	private static void loadPlayers() {

		File players = getFolder("players");

		List<File> files = Arrays.asList(players.listFiles());

		PlayerSpec.clearPlayers();

		for (File f : files) {

			YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);

			PlayerSpec.loadFromConfig(conf);

		}

	}

	private static void savePlayers() {

		File players = getFolder("players");
		
		for (PlayerSpec spec : PlayerSpec.getPlayers()) {
			
			if (spec == null) {
				continue;
			}

			File f = new File(players, spec.getUUID() + ".yml");
			YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);

			try {
				spec.saveToConfig(conf).save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	private static void loadChunks() {

		File chunks = getFolder("chunks");

		List<File> files = Arrays.asList(chunks.listFiles());

		ChunkSpec.list.clear();

		for (File f : files) {

			YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);

			ChunkSpec.list.add(ChunkSpec.loadFromConfig(conf));

		}

	}

	private static void saveChunks() {

		File chunks = getFolder("chunks");

		int p = 0;
		long sysTime = System.currentTimeMillis();
		for (ChunkSpec spec : ChunkSpec.list) {
			
			if (spec == null) {
				p++;
				continue;
			}

			File f = new File(chunks, spec.getX() + "_" + spec.getY() + "_" + spec.getZ() + ".yml");
			YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);

			try {
				spec.saveToConfig(conf).save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			p++;
			if (System.currentTimeMillis()-sysTime>100) Bukkit.getLogger().info("Processed "+p+" chunks (/"+ChunkSpec.list.size()+")"+" ["+(System.currentTimeMillis()-sysTime)+"]");
			sysTime = System.currentTimeMillis();

		}

	}

	private static YamlConfiguration getConfig(String path) {

		return getConfig(path, path);

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
