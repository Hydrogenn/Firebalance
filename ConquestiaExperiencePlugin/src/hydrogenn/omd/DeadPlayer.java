package hydrogenn.omd;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.BanEntry;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_11_R1.EntityPlayer;
import net.minecraft.server.v1_11_R1.EnumGamemode;
import net.minecraft.server.v1_11_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_11_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_11_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_11_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_11_R1.PacketPlayOutRespawn;
import net.minecraft.server.v1_11_R1.PlayerConnection;

//TODO fix up corpse carrying
//TODO replace the husk with a dead player
//TODO add disguise
public class DeadPlayer {

	private static HashMap<Inventory,UUID> activeInventories = new HashMap<Inventory,UUID>();
	private static HashMap<UUID,DeadPlayer> deadPlayers = new HashMap<UUID,DeadPlayer>();
	private static HashMap<UUID,DeadPlayer> carriers = new HashMap<UUID,DeadPlayer>();
	private static HashMap<UUID,DeadPlayer> disguises = new HashMap<UUID,DeadPlayer>();
	private static HashMap<Creature,UUID> dummies = new HashMap<Creature,UUID>();
	
	private Location location;
	private Inventory inventory;
	private String name;
	private UUID uuid;
	private UUID carrier;
	private UUID disguiser;
	private boolean isStillDead;
	private Creature dummy;
	
	public DeadPlayer() {
		
	}
	
	public DeadPlayer(Player player) {
		location = player.getLocation();
		if (location.getY() < 0) {
			location.setY(0); //dying in the void would *suck.*
		}
		name = player.getDisplayName();
		inventory = Bukkit.createInventory(null,45,name);
		inventory.setContents(player.getInventory().getContents());
		uuid = player.getUniqueId();
		carrier = null;
		disguiser = null;
		isStillDead = true;
		makeDummy();
	}
	
	public void makeDummy() {
		dummy = (Creature) location.getWorld().spawnEntity(location,EntityType.HUSK);
		dummy.setCustomName(name);
		dummy.setInvulnerable(true);
		dummy.setAI(false);
		dummies.put(dummy, uuid);
	}
	
	public void killDummy() {
		dummy.remove();
		dummies.remove(dummy);
		dummy = null;
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
	public UUID getDisguiser() {
		return disguiser;
	}
	public void setDisguiser(UUID disguiser) {
		if (this.disguiser != null) disguises.remove(this.disguiser);
		if (disguiser != null) disguises.put(disguiser,this);
		this.disguiser = disguiser;
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
		target.killDummy();
		player.spigot().sendMessage(getTextComponent("Drop",target.getName()));
	}

	public static boolean isDisguised(UUID uuid) {
		return disguises.containsKey(uuid);
	}

	public static DeadPlayer getDisguise(UUID uuid) {
		return disguises.get(uuid);
	}

	public static void disguise(Player player, DeadPlayer target) {
		if (isDisguised(player.getUniqueId())) {
			player.sendMessage("You can't disguise as more than one player!");
			return;
		}
		if (target.getDisguiser() != null) {
			player.sendMessage("Someone is already disguised as this person.");
			return;
		}
		target.setDisguiser(player.getUniqueId());
		setSkinAndName(target.getUuid(),player);
		player.spigot().sendMessage(getTextComponent("Undisguise",target.getName()));
	}

	public static void loot(Player player, DeadPlayer target) {
		Inventory lootTable = target.openInventory();
		player.openInventory(lootTable);
	}

	public static void revive(Player target) {
		DeadPlayer formerStats = deadPlayers.get(target.getUniqueId());
		deadPlayers.remove(target.getUniqueId());
		formerStats.unload(target);
		formerStats.killDummy();
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
			player.sendMessage("You're not carrying anyone!");
		DeadPlayer deadPlayer = DeadPlayer.getCarrying(player);
		deadPlayer.setLocation(player.getLocation());
		deadPlayer.setCarrier(null);
		deadPlayer.makeDummy();
	}
	
	public static void stopDisguising(Player player) {
		if (!DeadPlayer.isDisguised(player.getUniqueId()))
			player.sendMessage("You're not disguised!");
		DeadPlayer deadPlayer = DeadPlayer.getDisguise(player.getUniqueId());
		deadPlayer.setDisguiser(null);
		player.setDisplayName(null);
		player.setPlayerListName(null);
		setSkinAndName(player.getUniqueId(), player);
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

		killDummy();
		
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

	public static void interact(Player player, DeadPlayer deadPlayer) {
		String deadName = deadPlayer.getName();
		
		TextComponent jsonText = new TextComponent("---  Body of "+deadName+"  ---\n");
		if (!deadPlayer.isStillDead()) {
			jsonText.addExtra(getTextComponent("Kill",deadName));
		}
		else if (DeadPlayer.getCarrying(player) != null) {
			jsonText.addExtra(getTextComponent("Drop",deadName));
		} else {
			jsonText.addExtra(getTextComponent("Revive",deadName));
			jsonText.addExtra("  ");
			jsonText.addExtra(getTextComponent("Loot",deadName));
			jsonText.addExtra("  ");
			jsonText.addExtra(getTextComponent("Carry",deadName));
			jsonText.addExtra("  ");
			jsonText.addExtra(getTextComponent("Disguise",deadName));
		}
		if (DeadPlayer.isDisguised(player.getUniqueId())) {
			jsonText.addExtra("  ");
			jsonText.addExtra(getTextComponent("Undisguise",deadName));
		}
		player.spigot().sendMessage(jsonText);
	}
	
	private static TextComponent getTextComponent(String type, String name) {
		TextComponent textComponent = new TextComponent(type);
		String helpText = "";
		switch (type) {
		case "Revive":
			textComponent.setColor(ChatColor.AQUA);
			helpText = "Revive this player so they can continue playing.";
			break;
		case "Loot":
			textComponent.setColor(ChatColor.GOLD);
			helpText = "Open this player's inventory.";
			break;
		case "Carry":
			textComponent.setColor(ChatColor.DARK_GRAY);
			helpText = "Carry this player somewhere else.";
			break;
		case "Disguise":
			textComponent.setColor(ChatColor.DARK_PURPLE);
			helpText = "Change your appearance and name to this player.";
			break;
		case "Kill":
			textComponent.setColor(ChatColor.RED);
			helpText = "Undo a previous revival, so this player stays dead.";
			break;
		case "Drop":
			textComponent.setColor(ChatColor.DARK_GRAY);
			helpText = "Drop the player you are carrying. (/omd drop | sprinting)";
			break;
		case "Undisguise":
			textComponent.setColor(ChatColor.LIGHT_PURPLE);
			helpText = "Look like yourself again. (/omd undisguise)";
		}
		textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/omd "+type.toLowerCase()+" "+name));
		textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(helpText).create() ));
		return textComponent;
	}
	
	public static void setSkinAndName(UUID uuid, Player player) {
        try {
        	
    		player.setPlayerListName(getDisguise(player.getUniqueId()).getName());
    		player.setDisplayName(getDisguise(player.getUniqueId()).getName());
         
            GameProfile changeProfile = GameProfileBuilder.fetch(uuid, false);
         
            GameProfile playerProfile = ((CraftPlayer) player).getHandle().getProfile();
            
    		Field ff = playerProfile.getClass().getDeclaredField("name");
    		ff.setAccessible(true);
    		ff.set(playerProfile, changeProfile.getName());
         
            PropertyMap propertyMap = playerProfile.getProperties();
            propertyMap.get("textures").clear();
            propertyMap.put("textures", changeProfile.getProperties().get("textures")
                    .iterator().next());
            
            updateAppearance(player);
            
        } catch (Exception e) { //gotta catch 'em all!
            e.printStackTrace();
        }
    }
	
	public static void updateAppearance(Player player) {
		
		//Credit to NahuLD for this and the setSkin stuff.
		//Information found here if you want to make it: https://www.spigotmc.org/threads/1-11-2-change-skin-not-working.214570/
		EntityPlayer ep = ((CraftPlayer) player).getHandle();
		PlayerConnection con = ep.playerConnection;
		@SuppressWarnings("deprecation")
		int dimension = player.getWorld().getEnvironment().getId();
		PacketPlayOutEntityDestroy removeEntity = new PacketPlayOutEntityDestroy(ep.getId());
		PacketPlayOutNamedEntitySpawn addEntity = new PacketPlayOutNamedEntitySpawn(ep);
		PacketPlayOutPlayerInfo removeInfo = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, ep);
		PacketPlayOutPlayerInfo addInfo = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, ep);
		@SuppressWarnings("deprecation")
		PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(dimension, ep.getWorld().getDifficulty(),
				ep.getWorld().worldData.getType(), EnumGamemode.getById(player.getGameMode().getValue()));
		for (Player pOnline : Bukkit.getServer().getOnlinePlayers()) {
			if (pOnline.getUniqueId().equals(player.getUniqueId())) {
				con.sendPacket(removeInfo);
				con.sendPacket(addInfo);
				con.sendPacket(respawn);
			}
			else if (pOnline.canSee(player)) {
				PlayerConnection oCon = ((CraftPlayer) pOnline).getHandle().playerConnection;
				oCon.sendPacket(removeEntity);
				oCon.sendPacket(removeInfo);
				oCon.sendPacket(addInfo);
				oCon.sendPacket(addEntity);
			}
		}
	}

	public static Set<UUID> getDisguises() {
		return disguises.keySet();
	}

	public static FileConfiguration saveDisguise(UUID uuid, YamlConfiguration config) {

		config.set("disguiser", uuid.toString());
		config.set("disguise", getDisguise(uuid).getUuid().toString());
		
		return config;
	}

	public static void addDisguiseFromConfig(YamlConfiguration config) {

		UUID deadUuid = UUID.fromString(config.getString("disguise"));
		UUID uuid = UUID.fromString(config.getString("disguiser"));
		DeadPlayer deadPlayer = getDeadPlayer(deadUuid);
		
		deadPlayer.setDisguiser(uuid);
		
	}
	
	
}
