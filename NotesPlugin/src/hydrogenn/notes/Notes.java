package hydrogenn.notes;

import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Notes extends JavaPlugin {
	FileConfiguration config = getConfig();
	@Override
    public void onEnable() {
		//Set up configs
        config.addDefault("fb-enabled", false);
        config.options().copyDefaults(true);
        saveConfig();
		// Register commands
		this.getCommand("note").setExecutor(new CommandNoteFB());
		this.getCommand("stamp").setExecutor(new CommandStamp());
		this.getCommand("sign").setExecutor(new CommandNoteSign());
		this.getCommand("unsign").setExecutor(new CommandNoteUnsign());
		if (getServer().getPluginManager().getPlugin("Firebalance") != null) {
			if (config.getBoolean("fb-enabled")) {
				getLogger().log(Level.INFO, "Firebalance plugin is now being used");
				this.getCommand("note").setExecutor(new CommandNoteFB());
			}
			else {
				getLogger().log(Level.INFO, "Firebalance plugin was found and skipped");
				this.getCommand("note").setExecutor(new CommandNote());
			}
		} else {
			if (config.getBoolean("fb-enabled")) getLogger().log(Level.WARNING, "Firebalance plugin wasn't found, using default");
			this.getCommand("note").setExecutor(new CommandNote());
		}
		//Register the event listener
        getServer().getPluginManager().registerEvents(new MyListener(), this);
        
	}
	@Override
    public void onDisable() {
		
	}
}
