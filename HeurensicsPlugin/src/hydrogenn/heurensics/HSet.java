package hydrogenn.heurensics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class HSet {
	

	static private HashMap<UUID,HID> playerIds = new HashMap<UUID,HID>();
	static private HashMap<Location,HSet> globalHeurensics = new HashMap<Location,HSet>(); //any better ideas? these are linked to a block.
	
	static public ArrayList<UUID> investigators = new ArrayList<UUID>();
	
	private static int data;
	
	private Location location;
	private HID hid;
	private LogType logType;
	

	public HSet(HID hid, LogType logType, Location location) {
		this.hid = hid;
		this.logType = logType;
		this.location = location;
		globalHeurensics.put(location, this);
		data += 13;
		if (data > Heurensics.getMaxData()) {
			while (data > Heurensics.getMaxData()) {
				decayRandom();
			}
		}
	}
	
	public static void decayRandom() {
		ArrayList<Location> keyList = new ArrayList<Location>(globalHeurensics.keySet());
		int randomIndex = new Random().nextInt(keyList.size());
		if (!globalHeurensics.get(keyList.get(randomIndex)).getHid().decay(true)) {
			globalHeurensics.remove(randomIndex);
		}
		data--;
	}
	
	public HID getHid() {
		return hid;
	}

	public void setHid(HID hid) {
		this.hid = hid;
	}

	public LogType getLogType() {
		return logType;
	}

	public void setLogType(LogType logType) {
		this.logType = logType;
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

	public static void loadFromConfig(YamlConfiguration conf) {
		// TODO Auto-generated method stub
		
	}

	public FileConfiguration saveToConfig(YamlConfiguration conf) {
		conf.set("hid", hid);
		conf.set("world", location.getWorld());
		conf.set("x", location.getX());
		conf.set("y", location.getY());
		conf.set("z", location.getZ());
		conf.set("type", logType);
		return conf;
	}

	public static Collection<HID> getPlayerHids() {
		return playerIds.values();
	}

	public static Collection<HSet> getHSets() {
		return globalHeurensics.values();
	}

	public Location getLocation() {
		return location;
	}
}
