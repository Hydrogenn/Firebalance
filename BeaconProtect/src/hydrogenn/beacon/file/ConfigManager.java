package hydrogenn.beacon.file;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import hydrogenn.beacon.BeaconProtect;

/**
 * Manages file i/o. Inspired by Rayzr's implementation in Firebalance.
 * @author Hydrogenn
 *
 */
public class ConfigManager {
	
	private static BeaconProtect plugin;
	
	public static void init(BeaconProtect plugin) {
		ConfigManager.plugin = plugin;
		load();
	}
	
	public static void load() {
		
		if (!getFile("config.yml").exists()) {
			plugin.saveResource("config.yml", false);
			plugin.reloadConfig();
		}

		File evidence = getFolder("beacons");

		List<File> files = Arrays.asList(evidence.listFiles());

		for (File f : files) {

			YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);

			BeaconSpec.loadFromConfig(conf);

		}

	}
	
	public static void save() {

		File evidence = getFolder("beacons");
		
		for (File file : evidence.listFiles()) {
			file.delete();
		};

		int p = 0;
		long sysTime = System.currentTimeMillis();
		Iterator<BeaconSpec> iter = BeaconSpec.iterator();
		while (iter.hasNext()) {
			BeaconSpec beaconSpec = iter.next();
			
			if (beaconSpec == null) {
				p++;
				continue;
			}

			Location location = beaconSpec.getLocation();
			File f = new File(evidence, location.getWorld().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ() + ".yml");
			YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);

			try {
				beaconSpec.saveToConfig(conf).save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			p++;
			if (System.currentTimeMillis()-sysTime>100) Bukkit.getLogger().info("Processed "+p+" evidence (/"+BeaconSpec.count()+")"+" ["+(System.currentTimeMillis()-sysTime)+"]");
			sysTime = System.currentTimeMillis();

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
