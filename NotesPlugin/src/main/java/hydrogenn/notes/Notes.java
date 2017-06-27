package hydrogenn.notes;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class Notes extends JavaPlugin {
    @Override
    @SuppressWarnings("deprecation")
    public void onEnable() {
        // Set up configs
        getConfig().addDefault("fb-enabled", false);
        getConfig().options().copyDefaults(true);
        saveConfig();

        // Register command
        CommandExecutor noteExecutor = new CommandNote();

        if (getConfig().getBoolean("fb-enabled")) {
            if (getServer().getPluginManager().isPluginEnabled("Firebalance")) {
                getLogger().info("Firebalance found and enabled, using FB integration");
                noteExecutor = new CommandNoteFB();
            } else {
                getLogger().warning("Firebalance plugin wasn't found, using default");
            }
        }

        getCommand("note").setExecutor(noteExecutor);

        // Register events
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
    }
}
