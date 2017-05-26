package hydrogenn.omd;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.BanEntry;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

//TODO fix up corpse carrying
//TODO replace the husk with a dead player
//TODO add disguise
public class DeadPlayer {

	private static HashMap<Inventory,UUID> activeInventories = new HashMap<Inventory,UUID>();
	private static HashMap<UUID,DeadPlayer> carriers = new HashMap<UUID,DeadPlayer>();
	private static HashMap<UUID,DeadPlayer> deadPlayers = new HashMap<UUID,DeadPlayer>();
	private static HashMap<Creature,UUID> dummies = new HashMap<Creature,UUID>();
	
	private Location location;
	private Inventory inventory;
	private String name;
	private UUID uuid;
	private UUID carrier;
	private boolean isStillDead;
	private Creature dummy;
	
	public DeadPlayer() {
		
	}
	
	public DeadPlayer(Player player) {
		location = player.getLocation();
		name = player.getName();
		inventory = Bukkit.createInventory(null,45,name);
		inventory.setContents(player.getInventory().getContents());
		uuid = player.getUniqueId();
		carrier = null;
		isStillDead = true;
		makeDummy();
	}
	
	public void makeDummy() {
		dummy = (Creature) location.getWorld().spawnEntity(location,EntityType.HUSK);
		dummy.setCustomName(name);
		dummy.setInvulnerable(true);
		dummy.setAI(false);
	}
	
	
	public Inventory getMainInventory() {
		return inventory;
	}

	public void setMainInventory(Inventory mainInventory) {
		this.inventory = mainInventory;
	}


	public Creature getDummy() {
		return dummy;
	}
	
	public Location getLocation() {
		if (carrier == null) return location;
		else return Bukkit.getServer().getPlayer(carrier).getLocation();
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public UUID getUuid() {
		return uuid;
	}
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	public UUID getCarrier() {
		return carrier;
	}
	public void setCarrier(UUID carrier) {
		if (this.carrier != null) carriers.remove(this.carrier);
		if (carrier != null) carriers.put(carrier,this);
		this.carrier = carrier;
	}

	public boolean isStillDead() {
		return isStillDead;
	}
	
	private void ban() {
		Bukkit.getBanList(Type.NAME).addBan(
				name, OnlyMostlyDead.getBanMessage(), null, "Only Mostly Dead");
	}

	public void setStillDead(boolean isStillDead) {
		this.isStillDead = isStillDead;
		if (isStillDead == false) {
			BanEntry banEntry = Bukkit.getBanList(Type.NAME).getBanEntry(name);
			if (banEntry != null && banEntry.getSource().equals("Only Mostly Dead")) {
				Bukkit.getBanList(Type.NAME).pardon(name);
			}
		}
		else {
			ban();
		}
	}

	public void unload(Player player) {
		ItemStack[] contents = inventory.getContents();
		Inventory playerInventory = player.getInventory();
		for (int i = 0; i < 41; i++) {
			playerInventory.setItem(i, contents[i]);
		}
		player.updateInventory();
		dummy.remove();
	}
	
	public Inventory openInventory() {
		activeInventories.put(inventory, uuid);
		return inventory;
	}

	public boolean isBeingLooted() {
		return activeInventories.containsValue(uuid);
	}

	public static boolean isDead(Player player) {
		if (deadPlayers.containsKey( player.getUniqueId() ) )
			return true;
		return false;
	}

	public static boolean isActiveInventory(Inventory inventory) {
		return activeInventories.containsKey(inventory);
	}

	public static void closeInventory(Inventory inventory) {
		DeadPlayer deadPlayer = deadPlayers.get(activeInventories.get(inventory));
		Location location = deadPlayer.getLocation();
		World world = location.getWorld();
		for (int i = 41; i < 45; i++) {
			ItemStack item = inventory.getItem(i);
			if (item != null) {
				world.dropItem(location, item);
				inventory.clear(i);
			};
		}
		
		ItemStack head = inventory.getItem(39);
		if (head != null) {
			Material helmet = head.getType();
			if (!(helmet == Material.DIAMOND_HELMET ||
					helmet == Material.IRON_HELMET ||
					helmet == Material.GOLD_HELMET ||
					helmet == Material.CHAINMAIL_HELMET ||
					helmet == Material.LEATHER_HELMET ||
					helmet == Material.PUMPKIN ||
					helmet == Material.SKULL_ITEM)) {
				world.dropItem(location, head);
				inventory.clear(39);
			}
		}
		
		ItemStack chest = inventory.getItem(38);
		if (chest != null) {
			Material chestplate = chest.getType();
			if (!(chestplate == Material.DIAMOND_CHESTPLATE ||
					chestplate == Material.IRON_CHESTPLATE ||
					chestplate == Material.GOLD_CHESTPLATE ||
					chestplate == Material.CHAINMAIL_CHESTPLATE ||
					chestplate == Material.LEATHER_CHESTPLATE ||
					chestplate == Material.ELYTRA)) {
				world.dropItem(location, chest);
				inventory.clear(38);
			}
		}
		
		ItemStack legs = inventory.getItem(37);
		if (legs != null) {
			Material leggings = legs.getType();
			if (!(leggings == Material.DIAMOND_LEGGINGS ||
					leggings == Material.IRON_LEGGINGS ||
					leggings == Material.GOLD_LEGGINGS ||
					leggings == Material.CHAINMAIL_LEGGINGS ||
					leggings == Material.LEATHER_LEGGINGS)) {
				world.dropItem(location, legs);
				inventory.clear(37);
			}
		}
		
		ItemStack feet = inventory.getItem(36);
		if (feet != null) {
			Material boots = feet.getType();
			if (!(boots == Material.DIAMOND_BOOTS ||
					boots == Material.IRON_BOOTS ||
					boots == Material.GOLD_BOOTS ||
					boots == Material.CHAINMAIL_BOOTS ||
					boots == Material.LEATHER_BOOTS)) {
				world.dropItem(location, feet);
				inventory.clear(36);
			}
		}
		//I'm sorry you had to see that
		
		activeInventories.remove(inventory);
	}

	public static boolean isCarrier(Player player) {
		return carriers.containsKey(player.getUniqueId());
	}

	public static DeadPlayer getCarrying(Player player) {
		return carriers.get(player.getUniqueId());
	}

	public static boolean isCarried(UUID uuid) {
		return carriers.containsValue(uuid);
	}
	
	public static DeadPlayer getDeadPlayer(UUID uuid) {
		return deadPlayers.get(uuid);
	}
	
	public static DeadPlayer getDeadPlayer(String name) {
		for (DeadPlayer deadPlayer : deadPlayers.values()) {
			if (deadPlayer.getName().equals(name))
				return deadPlayer;
		}
		return null;
	}
	
	//-------- player functions --------

	public static void carry(Player player, DeadPlayer target) {
		if (isCarrier(player)) {
			player.sendMessage("You can't carry more than one player!");
			return;
		}
		if (target.getCarrier() != null) {
			player.sendMessage("Someone is already carrying this corpse.");
			return;
		}
		target.setCarrier(player.getUniqueId());
	}

	public static void loot(Player player, DeadPlayer target) {
		Inventory lootTable = target.openInventory();
		player.openInventory(lootTable);
	}

	public static void revive(Player target) {
		DeadPlayer formerStats = deadPlayers.get(target.getUniqueId());
		deadPlayers.remove(target.getUniqueId());
		formerStats.unload(target);
		target.setGameMode(GameMode.SURVIVAL); //TODO detect default gamemode in server.properties
		target.teleport(formerStats.getLocation());
		target.sendMessage(ChatColor.AQUA + "You have been revived! Welcome back.");
		target.setHealth(5);
		target.setFoodLevel(8);
	}

	public static void addDeadPlayer(Player player) {
		if (!deadPlayers.containsKey(player.getUniqueId())) {
			DeadPlayer deadPlayer = new DeadPlayer(player);
			deadPlayers.put(player.getUniqueId(),deadPlayer);
			player.spigot().respawn();
			deadPlayer.ban();
			player.kickPlayer(OnlyMostlyDead.getBanMessage());
		}
	}

	public static void stopCarrying(Player player) {
		if (!DeadPlayer.isCarrier(player))
			player.sendMessage("You're not carrying anything!");
		DeadPlayer deadPlayer = DeadPlayer.getCarrying(player);
		deadPlayer.setLocation(player.getLocation());
		deadPlayer.setCarrier(null);
	}

	public static void addFromConfig(YamlConfiguration config) {

		String name = config.getString("name");
		
		World world = null;
		for (World w : Bukkit.getWorlds()) {
			if (config.getString("world").equals(w.getName())) {
				world = w;
				break;
			}
		}
		if (world == null) {
			Bukkit.getLogger().warning("Could not load the dead player "+name+" because they are in a non-existent world!");
			return;
		}
		
		UUID uuid = UUID.fromString(config.getString("uuid"));
		float x = config.getInt("x") + 0.5F;
		float y = config.getInt("y");
		float z = config.getInt("z") + 0.5F;
		boolean isStillDead = config.getBoolean("dead");
		
		@SuppressWarnings("unchecked")
		ItemStack[] itemstacks = ((List<ItemStack>) config.getList("inventory")).toArray(new ItemStack[45]);
		Inventory inventory = Bukkit.createInventory(null,45,name);
		inventory.setContents(itemstacks);
		
		Location location = new Location(world,x,y,z);
		
		DeadPlayer newDeadPlayer = new DeadPlayer();
		newDeadPlayer.setName(name);
		newDeadPlayer.setUuid(uuid);
		newDeadPlayer.setLocation(location);
		newDeadPlayer.isStillDead = isStillDead;
		newDeadPlayer.setCarrier(null);
		newDeadPlayer.setMainInventory(inventory);
		newDeadPlayer.makeDummy();
		
		DeadPlayer.deadPlayers.put(uuid,newDeadPlayer);

	}

	public YamlConfiguration saveToConfig(YamlConfiguration config) {

		config.set("x", location.getBlockX());
		config.set("y", location.getBlockY());
		config.set("z", location.getBlockZ());
		config.set("world", location.getWorld().getName());
		config.set("uuid", uuid.toString());
		config.set("name", name);
		config.set("inventory", inventory.getContents());
		config.set("dead", isStillDead);

		dummy.remove();
		
		return config;
	}

	public static Collection<DeadPlayer> getList() {
		return deadPlayers.values();
	}

	public static boolean isDummy(Creature dummy) {
		return dummies.containsKey(dummy);
	}
	
	public static UUID getOwner(Creature dummy) {
		return dummies.get(dummy);
	}
	
	
}
