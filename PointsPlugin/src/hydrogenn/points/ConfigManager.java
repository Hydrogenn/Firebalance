package hydrogenn.points;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Manages file i/o. Inspired by Rayzr's implementation in Firebalance.
 * @author Hydrogenn
 *
 */
public class ConfigManager {
	
	private static Points plugin;
	
	public static void init(Points plugin) {
		ConfigManager.plugin = plugin;
		load();
	}
	
	public static void load() {
		
		loadPoints();

	}
	
	public static void loadPoints() {
		
		File points = getFile("points.yml");

		YamlConfiguration conf = YamlConfiguration.loadConfiguration(points);

		plugin.loadFromConfig(conf);
		
	}
	
	public static void save() {
		
		savePoints();

	}
	
	public static void savePoints() {


		File points = getFile("points.yml");
		YamlConfiguration conf = YamlConfiguration.loadConfiguration(points);

		for (Entry<UUID,Integer> entry : plugin.getEntries()) {
			conf.set(entry.getKey().toString(), entry.getValue());
		}
		
		try {
			conf.save(points);
		} catch (IOException e) {
			e.printStackTrace();
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
