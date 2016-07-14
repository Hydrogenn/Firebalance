package hydrogenn.quotableRules;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class QuotableRules extends JavaPlugin {
	public static List<String> ruleSet = new ArrayList<String>();

	private ConfigManager cm;

	@Override
	public void onEnable() {

		cm = new ConfigManager(this);

		saveConfig();
		ruleSet = cm.config.getStringList("rules");

		this.getCommand("quote").setExecutor(new CommandQuoteRule());
		this.getCommand("rules").setExecutor(new CommandViewRules());
		this.getCommand("rule").setExecutor(new CommandChangeRule(this));

	}

	@Override
	public void onDisable() {

		saveRules();

	}

	public void reload() {

		cm.reload();
		ruleSet = cm.config.getStringList("rules");

	}

	private class ConfigManager {

		private QuotableRules plugin;
		private FileConfiguration config;

		public ConfigManager(QuotableRules plugin) {

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

	public void saveRules() {

		cm.config.set("rules", ruleSet);
		saveConfig();

	}

}
