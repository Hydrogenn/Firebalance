package hydrogenn.heurensics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/* TODO:
 * Make evidence decay over time.
 * Make player ids change over time.
 * Allow servers to enable/disable UUID-HID connection, since that algorithm kinda sucks.
 */
public class Heurensics extends JavaPlugin {

	static private HashMap<UUID,HID> playerIds = new HashMap<UUID,HID>();
	static private HashMap<Location,HSet> globalHeurensics = new HashMap<Location,HSet>(); //any better ideas? these are linked to a block.
	
	static public ArrayList<UUID> investigators = new ArrayList<UUID>();
	
	FileConfiguration config = getConfig();

	@Override
    public void onEnable() {
		//Set up configs
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
		//TODO actually load the data
        
		// Register commands
        getCommand("investigate").setExecutor(new CommandInvestigate());

		//Register the event listener
        getServer().getPluginManager().registerEvents(new HeurensicsListener(), this);
        
	}
	@Override
    public void onDisable() {
		cleanHeurensics();
		//TODO actually store the data
	}
	
	public static HID getId(Player player) {
		return playerIds.get(player.getUniqueId());
	}
	
	public static boolean toggleInvestigator(UUID uuid) {
		if (investigators.contains(uuid)) {
			investigators.remove(uuid);
			return false;
		}
		else {
			investigators.add(uuid);
			return true;
		}
	}
	
	public static boolean isInvestigator(UUID uuid) {
		if (investigators.contains(uuid))
			return true;
		else return false;
	}
	
	public static HSet getHSet(Location location) {
		for (Location storedLocation : globalHeurensics.keySet()) {
			if (storedLocation.equals(location)) {
				return globalHeurensics.get(storedLocation);
			}
		}
		return null;
	}
	
	public static void addPlayer(UUID uuid) {
		if (!playerIds.containsKey(uuid))
		playerIds.put(uuid, new HID(uuid));
	}
	
	public static void cleanHeurensics() {
		ArrayList<Location> locationsToRemove = new ArrayList<Location>();
		for (Location location : globalHeurensics.keySet()) {
			if (location.getBlock().getType().equals(Material.AIR))
				locationsToRemove.add(location);
			else if (globalHeurensics.get(location).getHid().isAllMissing())
				locationsToRemove.add(location);
		}
		for (Location location : locationsToRemove) {
			globalHeurensics.remove(location);
		}
	}
	
	public static void removeHSet(Location location) {
		if (globalHeurensics.containsKey(location))
			globalHeurensics.remove(location);
	}
	public static void putHSet(Location location, HSet hSet) {
		globalHeurensics.put(location, hSet);
		
	}
}
