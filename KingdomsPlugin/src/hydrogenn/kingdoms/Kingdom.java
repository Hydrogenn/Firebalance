package hydrogenn.kingdoms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Kingdom {
	
	private static HashMap<String,Kingdom> kingdoms = new HashMap<String,Kingdom>();
	
	private ArrayList<UUID> members = new ArrayList<UUID>();
	private ArrayList<UUID> invited = new ArrayList<UUID>();
	private ArrayList<UUID> chainOfCommand = new ArrayList<UUID>();
	private String name = null;
	private String tag = "";
	private Location spawn = null;
	
	public Kingdom(String name, UUID creator) {
		this.setName(name);
		invite(creator);
		addToChain(creator);
		register();
	}

	public Kingdom(String name, String tag, Location spawn, ArrayList<UUID> members, ArrayList<UUID> chainOfCommand) {
		this.name = name;
		this.tag = tag;
		this.spawn = spawn;
		this.members = members;
		this.chainOfCommand = chainOfCommand;
		register();
	}

	public void invite(UUID uuid) {
		invited.add(uuid);
	}

	public void addToChain(UUID uuid) {
		chainOfCommand.add(uuid);
	}
	
	public void removeFromChain(UUID uuid) {
		chainOfCommand.remove(uuid);
	}
	
	public void add(UUID uuid) {
		if (invited.contains(uuid)) {
			members.add(uuid);
			invited.remove(uuid);
		}
		else {
			throw new IllegalArgumentException("Attempted to add a player to "+name+" who was not invited!");
		}
	}
	
	public void remove(UUID uuid) {
		members.remove(uuid);
		chainOfCommand.remove(uuid);
	}
	
	private void register() {
		kingdoms.put(name,this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		kingdoms.remove(this.name);
		this.name = name;
		kingdoms.put(name, this);
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public static Kingdom get(String name) {
		return kingdoms.get(name);
	}

	public Location getSpawn() {
		return spawn;
	}

	public void setSpawn(Location spawn) {
		this.spawn = spawn;
	}

	public boolean isLeader(UUID uuid) {
		if (chainOfCommand.get(0).equals(uuid))
			return true;
		return false;
	}

	public boolean isInvited(UUID uuid) {
		return invited.contains(uuid);
	}
	
	public UUID getLeader() {
		return chainOfCommand.get(0);
	}

	public static Kingdom getByTag(String tag) {
		for (Kingdom kingdom : kingdoms.values()) {
			if (kingdom.getTag() == tag)
				return kingdom;
		}
		return null;
	}

	public boolean isInLine(PlayerSpec target) {
		return chainOfCommand.contains(target.getUuid());
	}

	public String printChainOfCommand() {
		String ret = "";
		for (UUID uuid : chainOfCommand) {
			ret += "\n" + PlayerSpec.getByUUID(uuid).getName();
		}
		return ret;
	}

	public static Iterator<Kingdom> iterator() {
		return kingdoms.values().iterator();
	}

	public static void loadFromConfig(YamlConfiguration conf) {
		String name = conf.getString("name");
		String tag = conf.getString("tag");
		boolean spawnExists = conf.getBoolean("spawn-exists");
		Location spawn;
		if (spawnExists) {
			int x = conf.getInt("spawn-x");
			int y = conf.getInt("spawn-y");
			int z = conf.getInt("spawn-z");
			World world = null;
			String worldName = conf.getString("spawn-world");
			for (World tWorld : Bukkit.getWorlds()) {
				if (tWorld.getName() == worldName) {
					world = tWorld;
					break;
				}
			}
			if (world == null) {
				Bukkit.getLogger().warning("Could not find the world for the spawn of "+name);
				spawn = null;
			}
			else {
				spawn = new Location(world,x,y,z);
			}
		}
		else {
			spawn = null;
		}
		ArrayList<UUID> members = new ArrayList<UUID>();
		for (String member : conf.getStringList("members")) {
			members.add(UUID.fromString(member));
		}
		ArrayList<UUID> chainOfCommand = new ArrayList<UUID>();
		for (String member : conf.getStringList("leaders")) {
			chainOfCommand.add(UUID.fromString(member));
		}
		new Kingdom(name,tag,spawn,members,chainOfCommand);
	}

	public FileConfiguration saveToConfig(YamlConfiguration conf) {
		conf.set("name", name);
		conf.set("tag", tag);
		if (spawn == null) {
			conf.set("spawn-exists", false);
		}
		else {
			conf.set("spawn-exists", true);
			conf.set("spawn-x", spawn.getBlockX());
			conf.set("spawn-y", spawn.getBlockY());
			conf.set("spawn-z", spawn.getBlockZ());
			conf.set("spawn-world", spawn.getWorld().getName());
		}
		List<String> memberStrings = new ArrayList<String>();
		for (UUID member : members) {
			memberStrings.add(member.toString());
		}
		conf.set("members", memberStrings);
		List<String> leaderStrings = new ArrayList<String>();
		for (UUID member : members) {
			leaderStrings.add(member.toString());
		}
		conf.set("leaders", leaderStrings);
		return conf;
	}
	
}
