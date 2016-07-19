package hydrogenn.locks;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

public class ChestSpec {

	private Location coords;
	private String lock;
	private Location extra;
	private int id;
	private static int idNext = 0;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public static List<ChestSpec> list = new ArrayList<>();
	public static Hashtable<Location, ChestSpec> table = new Hashtable<Location, ChestSpec>();
	
	public ChestSpec(Location coords, Location largeChest, String lock) {
		this.setCoords(coords);
		this.setExtra(largeChest);
		this.setLock(lock);
		this.setId(idNext());
		//this.setLarge(another location; null if not a large chest; should be updated actively);
		//TODO send new items to table
		//table.put(coords, this);
	}
	
	public int idNext() {
		idNext++;
		return idNext;
	}

	public Location getExtra() {
		return extra;
	}

	public void setExtra(Location largeChest) {
		this.extra = largeChest;
	}

	public Location getCoords() {
		return coords;
	}

	public void setCoords(Location coords) {
		this.coords = coords;
	}

	public String getLock() {
		return lock;
	}

	public void setLock(String lock) {
		this.lock = lock;
	}
	
	public static ChestSpec loadFromConfig(YamlConfiguration config) {

		int x = config.getInt("x");
		int y = config.getInt("y");
		int z = config.getInt("z");
		World world = Bukkit.getServer().getWorld(config.getString("world"));
		String lock = config.getString("lock");
		int extraX = config.getInt("extraX");
		int extraY = config.getInt("extraY");
		int extraZ = config.getInt("extraZ");

		return new ChestSpec(new Location(world,x,y,z),new Location(world,extraX,extraY,extraZ),lock);

	}

	public YamlConfiguration saveToConfig(YamlConfiguration config) {

		config.set("x", coords.getBlockX());
		config.set("y", coords.getBlockY());
		config.set("z", coords.getBlockZ());
		config.set("world", coords.getWorld().getName());
		config.set("extraX", extra.getBlockX());
		config.set("extraY", extra.getBlockY());
		config.set("extraZ", extra.getBlockZ());
		config.set("lock", lock);
		
		return config;

	}
	
}
