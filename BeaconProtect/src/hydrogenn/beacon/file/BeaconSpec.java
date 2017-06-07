package hydrogenn.beacon.file;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
	private static List<UUID> saboteurs = new ArrayList<UUID>();

	private Location location;
	private long expiration;
	private UUID owner;
	
	private static final long maxBeaconLength = 2 * 24 * 60 * 60 * 1000; //2 days; only used when increasing
	private static final long minBeaconLength = 5 * 60 * 1000; //5 minutes; only used when decreasing
	
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
	
	private long lengthen(long durationOf) {
		if (!isEnabled()) {
			expiration = System.currentTimeMillis() + durationOf;
		}
		else {
			long timeUntilFull = maxBeaconLength - (expiration - System.currentTimeMillis());
			double percentToKeep = (double) timeUntilFull / (double) maxBeaconLength;
			long realDurationOf = (long) (percentToKeep * durationOf);
			expiration += realDurationOf;
		}
		return expiration;
	}
	
	private long shorten(long durationOf) {
		if (!isEnabled()) {
			expiration = System.currentTimeMillis() + 1000;
			return expiration;
		}
		else if (expiration - System.currentTimeMillis() < minBeaconLength) {
			return expiration;
		}
		else {
			long timeUntilEmpty = (expiration - System.currentTimeMillis()) - minBeaconLength;
			double percentToKeep = (double) timeUntilEmpty / (double) (maxBeaconLength - minBeaconLength);
			long realDurationOf = (long) (percentToKeep * durationOf);
			expiration -= realDurationOf;
		}
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
		if (saboteurs.contains(uuid))
			return beaconSpec.shorten(durationOf(item.getType()));
		return beaconSpec.lengthen(durationOf(item.getType()));
	}
	
	public static boolean toggleSabotage(Player player) {
		UUID uuid = player.getUniqueId();
		if (!saboteurs.contains(uuid)) {
			return saboteurs.add(uuid);
		}
		return !saboteurs.remove(uuid);
	}

	public static long durationOf(Material material) {
		switch (material) {
		case DIAMOND:
			return 24 * 60 * 60 * 1000;
		case EMERALD:
			return 24 * 60 * 60 * 1000;
		case GOLD_INGOT:
			return 6 * 60 * 60 * 1000;
		case IRON_INGOT:
			return 30 * 60 * 1000;
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
	
}
