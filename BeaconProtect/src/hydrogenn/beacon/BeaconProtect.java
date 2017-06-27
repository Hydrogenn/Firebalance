package hydrogenn.beacon;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import hydrogenn.beacon.file.ConfigManager;


public class BeaconProtect extends JavaPlugin {
	
	FileConfiguration config = getConfig();

	@Override
    public void onEnable() {
		
        ConfigManager.init(this);

		//Register the event listener
        getServer().getPluginManager().registerEvents(new BeaconListener(), this);
        
        //Register command
        getCommand("beacon").setExecutor(new CommandBeacon());
        
	}
	@Override
    public void onDisable() {
		ConfigManager.save();
	}
}
