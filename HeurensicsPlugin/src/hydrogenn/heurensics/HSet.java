package hydrogenn.heurensics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class HSet implements Comparable<HSet> {
	

	static private HashMap<UUID,HID> playerIds = new HashMap<UUID,HID>();
	static private HashMap<Location,HSet> globalHeurensics = new HashMap<Location,HSet>(); //any better ideas? these are linked to a block.
	static private PriorityQueue<HSet> queue = new PriorityQueue<HSet>();
	
	static public ArrayList<UUID> investigators = new ArrayList<UUID>();
	
	private static int data = 0;
	
	private Location location;
	private HID hid;
	private LogType logType;
	private long priority;
	

	public HSet(HID hid, LogType logType, Location location) {
		init(hid,logType,location);
	}
	
	public HSet(HID hid, LogType logType, Location location, long priority) {
		init(hid,logType,location);
		this.priority = priority;
	}
	
	public void init(HID hid, LogType logType, Location location) {
		this.hid = new HID(hid);
		this.logType = logType;
		this.location = location;
		if (globalHeurensics.containsKey(location))
			queue.remove(globalHeurensics.get(location));
		globalHeurensics.put(location, this);
		queue.add(this);
		data += hid.flags();
		if (data > Heurensics.getMaxData()) {
			decay(data - Heurensics.getMaxData());
			data = Heurensics.getMaxData();
		}
	}

	public static void decay(int times) {
		while (times > 0) {
			HSet topOfList = queue.poll();
			if (!topOfList.getHid().decay()) {
				globalHeurensics.remove(topOfList.getLocation());
			}
			else {
				topOfList.priority += Heurensics.getDecayWeight();
				queue.add(topOfList);
			}
			times--;
		}
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
		
		World world = null;
		for (World w : Bukkit.getWorlds()) {
			if (conf.getString("world").equals(w.getName())) {
				world = w;
				break;
			}
		}
		if (world == null) {
			Bukkit.getLogger().warning("Could not load evidence because it is in a non-existent world!");
			return;
		}
		
		int x = conf.getInt("x");
		int y = conf.getInt("y");
		int z = conf.getInt("z");
		long priority = conf.getLong("age");
		Location location = new Location(world,x,y,z);
		LogType logType = LogType.valueOf(conf.getString("type"));
		HID hid = HID.fromString(conf.getString("hid"));
		new HSet(hid, logType, location, priority);
		
	}

	public FileConfiguration saveToConfig(YamlConfiguration conf) {
		conf.set("hid", hid.toString());
		conf.set("world", location.getWorld().getName());
		conf.set("x", location.getX());
		conf.set("y", location.getY());
		conf.set("z", location.getZ());
		conf.set("type", logType.name());
		conf.set("age", priority);
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

	@Override
	public int compareTo(HSet o) {
		return (int) (getPriority() - o.getPriority());
	}

	public long getPriority() {
		return priority;
	}
}
