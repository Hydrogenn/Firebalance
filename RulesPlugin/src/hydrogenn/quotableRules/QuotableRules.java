package hydrogenn.quotableRules;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class QuotableRules extends JavaPlugin {
	FileConfiguration config = getConfig();
	public static List<String> ruleSet = new ArrayList<String>();
	@Override
    public void onEnable() {
		config.addDefault("rules", new ArrayList<String>());
        config.options().copyDefaults(true);
        saveConfig();
        ruleSet = config.getStringList("rules");
		this.getCommand("quote").setExecutor(new CommandQuoteRule());
		this.getCommand("rules").setExecutor(new CommandViewRules());
		//this.getCommand("newrule").setExecutor(new CommandChangeRule());
	}
	@Override
    public void onDisable() {
		//config.set("rules", ruleSet);
	}
}
