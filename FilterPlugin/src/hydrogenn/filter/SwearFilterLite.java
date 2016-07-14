package hydrogenn.filter;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class SwearFilterLite extends JavaPlugin {

	private ConfigManager cm;
	private SwearListener listener;

	public void onEnable() {

		cm = new ConfigManager(this);
		listener = new SwearListener(this);

		listener.loadFilters();

		getServer().getPluginManager().registerEvents(listener, this);

		getCommand("swearfilterlite").setExecutor(new CommandHandler(this));

		getLogger().info("SFL v" + getDescription().getVersion() + " enabled");

	}

	public void reload() {

		cm.reload();

		listener.loadFilters();

	}

	public void onDisable() {

		getLogger().info("SFL v" + getDescription().getVersion() + " disabled");

	}

	private class ConfigManager {

		private SwearFilterLite plugin;
		@SuppressWarnings("unused")
		private FileConfiguration config;

		public ConfigManager(SwearFilterLite plugin) {

			this.plugin = plugin;
			this.config = plugin.getConfig();

			if (!getFile("config.yml").exists()) {
				saveResource("config.yml", false);
				reload();
			}

		}

		public File getFile(String path) {

			return new File(plugin.getDataFolder() + File.pathSeparator + path);

		}

		public void reload() {

			plugin.reloadConfig();
			config = plugin.getConfig();

		}

	}

}
