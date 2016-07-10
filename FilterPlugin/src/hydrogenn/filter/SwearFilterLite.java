package hydrogenn.filter;

import org.bukkit.plugin.java.JavaPlugin;

public class SwearFilterLite extends JavaPlugin {
	
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new SwearListener(), this);
		
		getLogger().info("SFL v" + getDescription().getVersion() + " enabled");

	}

	public void onDisable() {

		getLogger().info("SFL v" + getDescription().getVersion() + " disabled");

	}

}
