package hydrogenn.locks;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager {
	
	private static Locks plugin;
	
	private static FileConfiguration config;
	
	private static YamlConfiguration getConfig(String path) {

		return getConfig(path, path);

	}
	
	public static void init(Locks plugin) {

		ConfigManager.plugin = plugin;

		load();

	}

	public static void load() {
		
		config = getConfig("config.yml");
		
		loadChests();

	}

	public static void save() {
		
		saveChests();

	}
	
	private static void loadChests() {

		File chests = getFolder("chests");

		List<File> files = Arrays.asList(chests.listFiles());

		ChestSpec.list.clear();

		for (File f : files) {

			YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);

			ChestSpec.list.add(ChestSpec.loadFromConfig(conf));

		}

	}

	private static void saveChests() {

		File chests = getFolder("chests");
		
		for (ChestSpec spec : ChestSpec.list) {
			
			if (spec == null) {
				continue;
			}

			File f = new File(chests, spec.getId() + ".yml");
			YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);

			try {
				spec.saveToConfig(conf).save(f);
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
