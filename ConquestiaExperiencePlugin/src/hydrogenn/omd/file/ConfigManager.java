package hydrogenn.omd.file;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

import hydrogenn.omd.DeadPlayer;
import hydrogenn.omd.OnlyMostlyDead;

/**
 * Manages file i/o. Inspired by Rayzr's implementation in Firebalance.
 * @author Hydrogenn
 *
 */
public class ConfigManager {
	
	private static OnlyMostlyDead plugin;
	
	public static void init(OnlyMostlyDead plugin) {
		ConfigManager.plugin = plugin;
		load();
	}
	
	public static void load() {
		
		loadCorpses();
		loadDisguises();

	}
	
	public static void loadCorpses() {
		
		File corpses = getFolder("corpses");

		List<File> files = Arrays.asList(corpses.listFiles());

		for (File f : files) {

			YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);

			DeadPlayer.addFromConfig(conf);

		}
	}
	
	public static void loadDisguises() {
		
		File disguises = getFolder("disguises");

		List<File> files = Arrays.asList(disguises.listFiles());

		for (File f : files) {

			YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);

			DeadPlayer.addDisguiseFromConfig(conf);

		}
	}
	
	public static void save() {
		
		saveCorpses();
		saveDisguises();

	}
	
	public static void saveCorpses() {


		File corpses = getFolder("corpses");
		
		for (File file : corpses.listFiles()) {
			file.delete();
		};

		for (DeadPlayer deadPlayer : DeadPlayer.getList()) {

			File f = new File(corpses, deadPlayer.getName() + ".yml");
			YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);

			try {
				deadPlayer.saveToConfig(conf).save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
	
	public static void saveDisguises() {


		File disguises = getFolder("disguises");
		
		for (File file : disguises.listFiles()) {
			file.delete();
		};

		for (UUID uuid : DeadPlayer.getDisguises()) {

			File f = new File(disguises, uuid + ".yml");
			YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);

			try {
				DeadPlayer.saveDisguise(uuid, conf).save(f);
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
