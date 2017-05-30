package hydrogenn.omd;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import hydrogenn.omd.file.ConfigManager;
import net.md_5.bungee.api.ChatColor;

public class OnlyMostlyDead extends JavaPlugin {
	
	private static int useDistance;
	private static String banMessage;

	FileConfiguration config = getConfig();

	@Override
    public void onEnable() {
		//Set up configs
        config.addDefault("use-distance", 10);
        config.addDefault("ban-message", ChatColor.DARK_RED + "You have died! You can only return when someone has revived you." +
        		"\n" + ChatColor.RESET + "This server has no contact. Just check in every now and then.");
        config.options().copyDefaults(true);
        saveConfig();
        
        useDistance = config.getInt("use-distance");
        banMessage = config.getString("ban-message");
        
		// Register commands
        getCommand("omd").setExecutor(new CommandOmd());

		// Register the event listener
        getServer().getPluginManager().registerEvents(new OmdListener(), this);
        
        ConfigManager.init(this);
	}
	
	@Override
    public void onDisable() {
		ConfigManager.save();
	}
	
	public static final int getUseDistance() {
		return useDistance;
	}
	public static String getBanMessage() {
		return banMessage;
	}
}
