package hydrogenn.heurensics;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import hydrogenn.heurensics.file.ConfigManager;

/* TODO ...
 * Make evidence decay over time.
 * Allow servers to enable/disable UUID-HID connection, since that algorithm kinda sucks.
 */
public class Heurensics extends JavaPlugin {
	
	private static int maxData;
	
	FileConfiguration config = getConfig();

	@Override
    public void onEnable() {
		//Set up configs
		config.addDefault("max-data", 13000);
        config.addDefault("place-detect-rate", 0.05);
        config.addDefault("break-detect-rate", 0.05);
        config.addDefault("interact-detect-rate", 0.05);
        config.addDefault("walk-detect-rate", 0.01); //multiplied by distance
        config.addDefault("damage-detect-rate", 0.02); //multiplied by damage done
        config.addDefault("death-detect-rate", 1);
        config.options().copyDefaults(true);
        saveConfig();

        LogType.BLOCK_PLACE.probability = config.getDouble("place-detect-rate");
        LogType.BLOCK_DESTROY.probability = config.getDouble("break-detect-rate");
        LogType.BLOCK_INTERACT.probability = config.getDouble("interact-detect-rate");
        LogType.PLAYER_MOVE.probability = config.getDouble("walk-detect-rate");
        LogType.PLAYER_HURT.probability = config.getDouble("damage-detect-rate");
        LogType.PLAYER_DEATH.probability = config.getDouble("death-detect-rate");
		
        ConfigManager.init(this);
        
		// Register commands
        getCommand("investigate").setExecutor(new CommandInvestigate());

		//Register the event listener
        getServer().getPluginManager().registerEvents(new HeurensicsListener(), this);
        
	}
	@Override
    public void onDisable() {
		HSet.cleanHeurensics();
		ConfigManager.save();
	}
	public static int getMaxData() {
		return maxData;
	}
}
