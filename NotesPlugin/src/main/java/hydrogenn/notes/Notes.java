package main.java.hydrogenn.notes;

import org.bukkit.plugin.java.JavaPlugin;

public class Notes extends JavaPlugin {
    @Override
    public void onEnable() {
        getCommand("note").setExecutor(new CommandNote());
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
    }
}
