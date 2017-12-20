package hydrogenn.points;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Points extends JavaPlugin {

	FileConfiguration config = getConfig();
	
	HashMap<String,ArrayList<String>> links;
	HashMap<UUID,Integer> points;
	
	@Override
    public void onEnable() {
        
        ConfigManager.init(this);
        FileConfiguration config = getConfig();
        
		///agh automatically register comamnds is pain.
        
        ConfigurationSection linksInConfig = config.getConfigurationSection("links");
        
        for (String key : linksInConfig.getKeys(false)) {
        	links.put(key, (ArrayList<String>) linksInConfig.getStringList(key));
        	new CommandLink(key, this);
        }
        
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

	public void loadFromConfig(YamlConfiguration conf) {
		for (String key: conf.getKeys(false)) {
			points.put(UUID.fromString(key), conf.getInt(key));
		}
	}
}
