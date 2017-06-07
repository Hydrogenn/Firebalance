package hydrogenn.beacon;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import hydrogenn.beacon.file.ConfigManager;


public class BeaconProtect extends JavaPlugin {
	
	private static int maxData;
	private static int decayWeight;
	private static boolean detectSneaking;
	private static boolean detectInvisible;
	
	FileConfiguration config = getConfig();

	@Override
    public void onEnable() {
		
        ConfigManager.init(this);
        maxData = config.getInt("max-data");
        decayWeight = config.getInt("decay-weight") * 60 / 10; //adjust to seconds, then to segments
        detectSneaking = config.getBoolean("detect-sneaking");
        detectInvisible = config.getBoolean("detect-invisible");

		//Register the event listener
        getServer().getPluginManager().registerEvents(new BeaconListener(), this);
        
        //Register command
        getCommand("beacon").setExecutor(new CommandBeacon());
        
	}
	@Override
    public void onDisable() {
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
