package hydrogenn.points;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Points extends JavaPlugin {

	FileConfiguration config = getConfig();
	
	HashMap<String,ArrayList<String>> links;
	HashMap<UUID,Integer> points;
	
	@Override
    public void onEnable() {
        
        ConfigManager.init(this);
        
		///agh automatically register comamnds is pain.
        
		// Register command
        getCommand("vote").setExecutor(new CommandLink(this));
        getCommand("donate").setExecutor(new CommandLink(this));
        getCommand("discord").setExecutor(new CommandLink(this));
        getCommand("forums").setExecutor(new CommandLink(this));
        
	}
	
	@Override
    public void onDisable() {
		ConfigManager.save();
	}

	public ArrayList<String> getListOfLinks(String type) {
		return links.get(type);
	}

	public Set<Entry<UUID,Integer>> getEntries() {
		return points.entrySet();
	}
}
