package hydrogenn.beacon.file;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BeaconSpec {
	
	private static HashMap<Location,BeaconSpec> beaconCache = new HashMap<Location,BeaconSpec>();

	private Location location;
	private long expiration;
	private UUID owner;
	
	private static final long MAX_BEACON_LENGTH = 4 * 24 * 60 * 60 * 1000; //4 days
	public static final float IRON_TIME = 1f / 192f; //starts at 30 minutes
	public static final float GOLD_TIME = 1f / 24f; // starts at 4 hours
	public static final float EMERALD_TIME = 1f / 10f; // starts at 9.6 hours
	public static final float DIAMOND_TIME = 1f / 4f; // starts at 1 day
	
	private BeaconSpec(Location location, UUID owner) {
		this.location = location;
		this.owner = owner;
		expiration = 0;
	}
	
	private BeaconSpec(Location location, long expiration, UUID owner) {
		this.location = location;
		this.expiration = expiration;
		this.owner = owner;
	}

	public Beacon getBeacon() {
		if (location.getBlock().getState() instanceof Beacon) {
			return (Beacon) location.getBlock().getState();
		}
		throw new IllegalStateException("BeaconSpec points to a non-beacon at "+location.getBlockX()+", "+location.getBlockY()+", "+location.getBlockZ());
	}

	public boolean isEnabled() {
		return System.currentTimeMillis() <= expiration;
	}

	public boolean inRange(Location otherLocation) {
		if (!otherLocation.getWorld().equals(location.getWorld())) return false;
		if (location.distanceSquared(otherLocation) <= rangeSquared()) {
			return true;
		}
		return false;
	}
	
	public Location getLocation() {
		return location;
	}

	private int rangeSquared() {
		switch (getBeacon().getTier()) {
		case 1:
			return 1600;
		case 2:
			return 3600;
		case 3:
			return 6400;
		case 4:
			return 10000;
		default:
			return 0;
		}
	}
	
	private long lengthen(float durationOf) {
		long timeUntilFull;
		if (!isEnabled()) {
			timeUntilFull = MAX_BEACON_LENGTH;
			expiration = System.currentTimeMillis();
		}
		else {
			timeUntilFull = MAX_BEACON_LENGTH - (expiration - System.currentTimeMillis());
		}
		expiration += (timeUntilFull * durationOf);
		return expiration;
	}

	public static void cache(Block block, Player owner) {
		beaconCache.put(block.getLocation(), new BeaconSpec(block.getLocation(), owner.getUniqueId()));
	}

	private static void cache(Location loc, long expiration, UUID owner) {
		beaconCache.put(loc, new BeaconSpec(loc, expiration, owner));
	}

	public static void remove(Block block) {
		beaconCache.remove(block.getLocation());
	}

	public static Iterator<BeaconSpec> iterator() {
		return beaconCache.values().iterator();
	}

	public static int count() {
		return beaconCache.size();
	}

	public FileConfiguration saveToConfig(YamlConfiguration conf) {
		conf.set("x", location.getBlockX());
		conf.set("y", location.getBlockY());
		conf.set("z", location.getBlockZ());
		conf.set("world", location.getWorld().getName());
		conf.set("expiration", expiration);
		conf.set("owner", owner.toString());
		return conf;
	}

	public static void loadFromConfig(YamlConfiguration conf) {
		int x = conf.getInt("x");
		int y = conf.getInt("y");
		int z = conf.getInt("z");
		long expiration = conf.getLong("expiration");
		World world = null;
		for (World checkWorld : Bukkit.getWorlds()) {
			if (checkWorld.getName().equals(conf.getString("world"))) {
				world = checkWorld;
				break;
			}
		}
		if (world == null) {
			throw new IllegalArgumentException("The world "+conf.getString("world")+" does not exist!");
		}
		
		Location loc = new Location(world,x,y,z);
		UUID owner = UUID.fromString(conf.getString("owner"));
		cache(loc, expiration, owner);
	}

	public static boolean isProtected(Location location) {
		for (BeaconSpec beaconSpec : beaconCache.values()) {
			if (beaconSpec.inRange(location) && beaconSpec.isEnabled()) {
				return true;
			}
		}
		return false;
	}

	public static long update(Location location, ItemStack item, UUID uuid) {
		BeaconSpec beaconSpec = beaconCache.get(location);
		return beaconSpec.lengthen(durationOf(item.getType()));
	}

	public static float durationOf(Material material) {
		switch (material) {
		case DIAMOND:
			return DIAMOND_TIME;
		case EMERALD:
			return EMERALD_TIME;
		case GOLD_INGOT:
			return GOLD_TIME;
		case IRON_INGOT:
			return IRON_TIME;
		default:
			return 0;
		}
	}

	public boolean isOwner(Player player) {
		return player.getUniqueId().equals(owner);
	}

	public long getDuration() {
		return expiration - System.currentTimeMillis();
	}

	public static boolean isActive(Location location) {
		if (!beaconCache.containsKey(location)) return false;
		BeaconSpec bSpec = beaconCache.get(location);
		return bSpec.isEnabled();
	}
	
}
