package hydrogenn.heurensics;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import hydrogenn.heurensics.file.ConfigManager;


public class Heurensics extends JavaPlugin {
	
	private static int maxData;
	private static int decayWeight;
	private static boolean detectSneaking;
	private static boolean detectInvisible;
	
	FileConfiguration config = getConfig();

	@Override
    public void onEnable() {
		
        ConfigManager.init(this);

        LogType.BLOCK_PLACE.probability = config.getDouble("place-detect-rate");
        LogType.BLOCK_DESTROY.probability = config.getDouble("break-detect-rate");
        LogType.BLOCK_INTERACT.probability = config.getDouble("interact-detect-rate");
        LogType.PLAYER_MOVE.probability = config.getDouble("walk-detect-rate");
        LogType.PLAYER_HURT.probability = config.getDouble("damage-detect-rate");
        LogType.PLAYER_DEATH.probability = config.getDouble("death-detect-rate");
        maxData = config.getInt("max-data");
        decayWeight = config.getInt("decay-weight") * 60 / 10; //adjust to seconds, then to segments
        detectSneaking = config.getBoolean("detect-sneaking");
        detectInvisible = config.getBoolean("detect-invisible");
        
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
	public static long getDecayWeight() {
		return decayWeight;
	}
	public static boolean detectInvisible() {
		return detectInvisible;
	}
	public static boolean detectSneaking() {
		return detectSneaking;
	}
}
