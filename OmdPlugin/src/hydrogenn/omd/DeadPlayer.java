package hydrogenn.omd;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.BanEntry;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.EnumGamemode;
import net.minecraft.server.v1_12_R1.PacketPlayOutBed;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_12_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_12_R1.PacketPlayOutPosition;
import net.minecraft.server.v1_12_R1.PacketPlayOutPosition.EnumPlayerTeleportFlags;
import net.minecraft.server.v1_12_R1.PacketPlayOutRespawn;
import net.minecraft.server.v1_12_R1.PlayerConnection;
import net.minecraft.server.v1_12_R1.PlayerInteractManager;

//TODO fix up corpse carrying
//TODO replace the husk with a dead player
public class DeadPlayer {

	private static HashMap<Inventory,UUID> activeInventories = new HashMap<Inventory,UUID>();
	private static HashMap<UUID,DeadPlayer> deadPlayers = new HashMap<UUID,DeadPlayer>();
	private static HashMap<UUID,DeadPlayer> carriers = new HashMap<UUID,DeadPlayer>();
	private static HashMap<UUID,DeadPlayer> disguises = new HashMap<UUID,DeadPlayer>();
	private static HashMap<Integer,UUID> owners = new HashMap<Integer,UUID>();
	
	private static final List<PotionEffect> revivalPotionEffects = new ArrayList<PotionEffect>(
		Arrays.asList(
			(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 160, 4)),
			(new PotionEffect(PotionEffectType.WEAKNESS, 160, 4))
	));
	
	private Location location;
	private Inventory inventory;
	private UUID uuid;
	private UUID carrier; //awful people who drag you around
	private UUID disguiser; //awful people who pretend they're like you
	private List<UUID> cursors; //awful people who want you gone and awaaaaaaay
	private boolean wasDead;
	private boolean isNotManuallyRevived;
	private EntityHuman dummy;
	private long banEnd;
	private int xpLevel;
	
	public DeadPlayer() {
		
	}
	
	public DeadPlayer(Player player, boolean dead) {
		setLocation(player.getLocation());
		uuid = player.getUniqueId();
		inventory = Bukkit.createInventory(null,45,getName());
		inventory.setContents(player.getInventory().getContents());
		carrier = null;
		disguiser = null;
		cursors = new ArrayList<UUID>();
		wasDead = dead;
		isNotManuallyRevived = dead;
		banEnd = (long) (System.currentTimeMillis() + 0.75 * 86400000);
		xpLevel = player.getLevel() / 2;
		makeDummy();
		if (dead) {
			player.spigot().respawn();
			ban();
			player.kickPlayer(OnlyMostlyDead.getBanMessage());
			if (isDisguised(player.getUniqueId()))
				stopDisguising(player);
		}
	}
	
	public void makeDummy() {
		GameProfile fakeProfile;
		try {
			fakeProfile = GameProfileBuilder.fetch(getUuid());
			dummy = new EntityPlayer(
					((CraftServer) Bukkit.getServer()).getServer(),
					((CraftWorld) getLocation().getWorld()).getHandle(),
					fakeProfile,
					new PlayerInteractManager(((CraftWorld) getLocation().getWorld()).getHandle()));
			Field ff = fakeProfile.getClass().getDeclaredField("name");
    		ff.setAccessible(true);
    		ff.set(fakeProfile, getName());
			dummy.setLocation(location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
			dummy.setInvulnerable(true);
			owners.put(dummy.getId(), uuid);
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (inRange(player.getLocation()))
					show(player);
			}
		} catch (IOException e) {
			Bukkit.getLogger().warning("Was unable to create a profile from the UUID "+getUuid()+"!");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void killDummy(boolean hide) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			hide(player,hide);
		}
		owners.remove(dummy);
		dummy = null;
	}
	
	
	public Inventory getMainInventory() {
		return inventory;
	}

	public void setMainInventory(Inventory mainInventory) {
		this.inventory = mainInventory;
	}


	public EntityHuman getDummy() {
		return dummy;
	}
	
	public Location getLocation() {
		if (carrier == null) return location;
		else return Bukkit.getServer().getPlayer(carrier).getLocation();
	}
	public void setLocation(Location location) {
		if (location.getY() < 0) {
			location.setY(0);
		}
		if (location.getY() > 256) {
			location.setY(256);
		}
		this.location = location;
	}
	public String getName() {
		return Bukkit.getOfflinePlayer(getUuid()).getName();
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

	public boolean isRevived() {
		return !isNotManuallyRevived;
	}
	
	private void ban() {
		Bukkit.getBanList(Type.NAME).addBan(
				getName(), OnlyMostlyDead.getBanMessage(), Date.from(Instant.ofEpochMilli(banEnd)), "Only Mostly Dead");
	}
	
	private void pardon() {
		BanEntry banEntry = Bukkit.getBanList(Type.NAME).getBanEntry(getName());
		if (banEntry != null && banEntry.getSource().equals("Only Mostly Dead")) {
			Bukkit.getBanList(Type.NAME).pardon(getName());
		}
	}

	public void setManuallyRevived(boolean isManuallyRevived) {
		isNotManuallyRevived = !isManuallyRevived;
		if (isManuallyRevived) {
			pardon();
		}
		else {
			wasDead = true;
			ban();
		}
	}

	public void unload(Player player) {
		for (HumanEntity viewer : inventory.getViewers()) {
			viewer.getOpenInventory().close(); //so people don't view the shadow inventory after its inaccessible
		}
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

	public static boolean isBodyAndWasDead(Player player) {
		UUID uuid = player.getUniqueId();
		if (deadPlayers.containsKey( uuid ) && deadPlayers.get(uuid).wasDead() )
			return true;
		return false;
	}
	
	private boolean wasDead() {
		return wasDead;
	}

	public boolean inRange(Location location) {
		try {
			return this.getLocation().distance(location) <= OnlyMostlyDead.getViewDistance();
		}
		catch (IllegalArgumentException e) {}
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
		target.killDummy(true);
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
	
	public static void curse(Player player, DeadPlayer target) {
		if (target.cursors.size() == 5) {
			player.sendMessage("This player has already been completely cursed.");
			return;
		}
		if (target.cursors.contains(player.getUniqueId())) {
			player.sendMessage("You've already cursed this player!");
			return;
		}
		target.banEnd += DeadPlayer.getBanTimeMillis(target.cursors.size());
		target.cursors.add(player.getUniqueId());
		if (Bukkit.getBanList(Type.NAME).isBanned(target.getName())) {
			Bukkit.getBanList(Type.NAME).getBanEntry(target.getName()).setExpiration(Date.from(Instant.ofEpochMilli(target.banEnd)));
		}
		try {
			player.sendMessage(target.getName() + " is now banned for: " + getDurationBreakdown(
					target.banEnd - System.currentTimeMillis()));
		}
		catch (IllegalArgumentException e) {
			player.sendMessage(target.getName() + " has been cursed, but still isn't banned.");
		}
	}

	public static void loot(Player player, DeadPlayer target) {
		Inventory lootTable = target.openInventory();
		player.openInventory(lootTable);
	}

	public static void revive(Player target) {
		if (!deadPlayers.containsKey(target.getUniqueId())) return; //save the trouble if no body exists
		DeadPlayer formerStats = deadPlayers.get(target.getUniqueId());
		deadPlayers.remove(target.getUniqueId());
		formerStats.setDisguiser(null);
		formerStats.setCarrier(null);
		formerStats.unload(target);
		formerStats.killDummy(false);
		target.setGameMode(GameMode.SURVIVAL); //TODO detect default gamemode in server.properties
		target.teleport(formerStats.getLocation());
		if (formerStats.wasDead()) {
			target.sendMessage(ChatColor.AQUA + "You have been revived! Welcome back.");
			target.setHealth(5);
			target.setFoodLevel(8);
			target.setLevel(formerStats.xpLevel);
			target.addPotionEffects(revivalPotionEffects);
		}
		else {
			target.sendMessage(ChatColor.RED + "Because you did not sleep in a bed, your body was left behind. "
					+ "Anyone could have killed you or stolen from your inventory.");
		}
	}

	public static void addDeadPlayer(Player player, boolean dead) {
		if (!deadPlayers.containsKey(player.getUniqueId())) {
			DeadPlayer deadPlayer = new DeadPlayer(player,dead);
			deadPlayers.put(player.getUniqueId(),deadPlayer);
		}
	}

	public static void stopCarrying(Player player) {
		if (!DeadPlayer.isCarrier(player)) {
			player.sendMessage("You're not carrying anyone!");
			return;
		}
		DeadPlayer deadPlayer = DeadPlayer.getCarrying(player);
		deadPlayer.setLocation(player.getLocation());
		deadPlayer.getLocation().setYaw(0);
		deadPlayer.getLocation().setPitch(0);
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
	
	public static void interact(Player player, DeadPlayer deadPlayer) {
		String deadName = deadPlayer.getName();
		
		TextComponent jsonText = new TextComponent("---  Body of "+deadName+"  ---\n");
		if (Bukkit.getBanList(Type.NAME).isBanned(deadName)) {
			long millis = Bukkit.getBanList(Type.NAME).getBanEntry(deadName).getExpiration().getTime() -
					System.currentTimeMillis();
			String breakdown;
			try {
				breakdown = "Will auto-revive in: " + DeadPlayer.getDurationBreakdown(millis);
			}
			catch (IllegalArgumentException e) {
				breakdown = "Auto-revival is complete. This player is revived.";
			}
			jsonText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
					breakdown).create() ));
		}
		if (deadPlayer.isRevived()) {
			jsonText.addExtra(getTextComponent("Kill",deadName));
		}
		else {
			jsonText.addExtra(getTextComponent("Revive",deadName));
		}
		jsonText.addExtra("  ");
		if (DeadPlayer.getCarrying(player) != null) {
			jsonText.addExtra(getTextComponent("Drop",deadName));
		} else {
			jsonText.addExtra(getTextComponent("Carry",deadName));
		}
		jsonText.addExtra("  ");
		jsonText.addExtra(getTextComponent("Loot",deadName));
		jsonText.addExtra("  ");
		if (DeadPlayer.isDisguised(player.getUniqueId())) {
			jsonText.addExtra(getTextComponent("Undisguise",deadName));
		}
		else {
			jsonText.addExtra(getTextComponent("Disguise",deadName));
		}
		if (!deadPlayer.cursors.contains(player.getUniqueId())) {
			jsonText.addExtra("  ");
			jsonText.addExtra(getTextComponent("Curse",deadName));
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
			helpText = "Carry this player somewhere else. (You cannot sprint while doing this.)";
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
			helpText = "Drop the player you are carrying. (/omd drop)";
			break;
		case "Undisguise":
			textComponent.setColor(ChatColor.LIGHT_PURPLE);
			helpText = "Look like yourself again. (/omd undisguise)";
			break;
		case "Curse":
			textComponent.setColor(ChatColor.RED);
			helpText = "Delay this player's auto-revival.";
			break;
		}
		textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/omd "+type.toLowerCase()+" "+name));
		textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(helpText).create() ));
		return textComponent;
	}
	
	//-------- config functions --------

	/*
	private long banEnd;
	 */
	public static void addFromConfig(YamlConfiguration config) {
		
		World world = null;
		for (World w : Bukkit.getWorlds()) {
			if (config.getString("world").equals(w.getName())) {
				world = w;
				break;
			}
		}
		if (world == null) {
			Bukkit.getLogger().warning("Could not load the dead player "+config.getName()+" because they are in a non-existent world!");
			return;
		}
		
		UUID uuid = UUID.fromString(config.getString("uuid"));
		float x = config.getInt("x") + 0.5F;
		float y = config.getInt("y");
		float z = config.getInt("z") + 0.5F;
		boolean isRevived;
		boolean wasDead;
		if (!config.contains("dead")) { //backwards compatibility
			isRevived = config.getBoolean("revived");
			wasDead = config.getBoolean("was-dead");
		}
		else {
			isRevived = !config.getBoolean("dead");
			wasDead = true;
		}
		long banEnd = config.getLong("auto-revive-date");
		int xpLevel = config.getInt("experience-level");
		
		List<UUID> cursors = new ArrayList<UUID>();
		for (String cursor : (List<String>) config.getStringList("cursors")) {
			cursors.add(UUID.fromString(cursor));
		}
		
		Location location = new Location(world,x,y,z);
		
		DeadPlayer newDeadPlayer = new DeadPlayer();
		newDeadPlayer.setUuid(uuid);
		
		@SuppressWarnings("unchecked")
		ItemStack[] itemstacks = ((List<ItemStack>) config.getList("inventory")).toArray(new ItemStack[45]);
		Inventory inventory = Bukkit.createInventory(null,45,newDeadPlayer.getName());
		inventory.setContents(itemstacks);
		
		newDeadPlayer.setLocation(location);
		newDeadPlayer.wasDead = wasDead;
		newDeadPlayer.isNotManuallyRevived = !isRevived;
		newDeadPlayer.setCarrier(null);
		newDeadPlayer.setDisguiser(null);
		newDeadPlayer.cursors = cursors;
		newDeadPlayer.setMainInventory(inventory);
		newDeadPlayer.makeDummy();
		newDeadPlayer.banEnd = banEnd;
		newDeadPlayer.xpLevel = xpLevel;
		
		DeadPlayer.deadPlayers.put(uuid,newDeadPlayer);

	}

	public YamlConfiguration saveToConfig(YamlConfiguration config) {

		config.set("x", location.getBlockX());
		config.set("y", location.getBlockY());
		config.set("z", location.getBlockZ());
		config.set("world", location.getWorld().getName());
		config.set("uuid", uuid.toString());
		config.set("inventory", inventory.getContents());
		config.set("revived", !isNotManuallyRevived);
		config.set("was-dead", wasDead);
		config.set("auto-revive-date", banEnd);
		config.set("experience-level", xpLevel);

		List<String> nCursors = new ArrayList<String>();
		for (UUID cursor : cursors) {
			nCursors.add(cursor.toString());
		}
		config.set("cursors", nCursors);

		killDummy(true);
		
		return config;
	}
	
	//-------- accessor functions --------

	public static Collection<DeadPlayer> getList() {
		return deadPlayers.values();
	}

	public static boolean isDummy(int i) {
		return owners.containsKey(Integer.valueOf(i));
	}
	
	public static UUID getOwner(int id) {
		return owners.get(id);
	}
	
	public static void setSkinAndName(UUID uuid, Player player) {
        try {
         
            GameProfile changeProfile = GameProfileBuilder.fetch(uuid, false);
         
            GameProfile playerProfile = ((CraftPlayer) player).getHandle().getProfile();
            
    		Field ff = playerProfile.getClass().getDeclaredField("name");
    		ff.setAccessible(true);
    		ff.set(playerProfile, changeProfile.getName());
    		
    		player.setDisplayName(changeProfile.getName());
         
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
		Location loc = player.getLocation();
		Set<EnumPlayerTeleportFlags> tpf = new HashSet<EnumPlayerTeleportFlags>();
		PacketPlayOutPosition teleport = new PacketPlayOutPosition(loc.getX(),loc.getY(),loc.getZ(),loc.getPitch(),loc.getYaw(), tpf, dimension);
		for (Player pOnline : Bukkit.getServer().getOnlinePlayers()) {
			if (pOnline.getUniqueId().equals(player.getUniqueId())) {
				con.sendPacket(removeInfo);
				con.sendPacket(addInfo);
				con.sendPacket(respawn);
				con.sendPacket(teleport);
				player.updateInventory();
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

	private void logFakePlayer(Player viewer) {
		PacketPlayOutPlayerInfo fakePacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,(EntityPlayer) getDummy());
		((CraftPlayer)viewer).getHandle().playerConnection.sendPacket(fakePacket);
	}
	
	private void delogFakePlayer(Player viewer) {
		PacketPlayOutPlayerInfo fakePacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,(EntityPlayer) getDummy());
		((CraftPlayer)viewer).getHandle().playerConnection.sendPacket(fakePacket);
	}

	
	public void hide(Player player, boolean hide) {
		if (dummy == null) return;
		if (hide) delogFakePlayer(player);
		PacketPlayOutEntityDestroy removeEntity = new PacketPlayOutEntityDestroy(dummy.getId());
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(removeEntity);
	}

	@SuppressWarnings("deprecation")
	public void show(Player player) {
		if (carrier != null) return;
		logFakePlayer(player);
		Location blockLoc = location.clone();
		blockLoc.setY(0);
		//Block block = blockLoc.getBlock();
		PacketPlayOutNamedEntitySpawn addEntity = new PacketPlayOutNamedEntitySpawn(dummy);
		PacketPlayOutBed sleep = new PacketPlayOutBed(dummy,new BlockPosition(blockLoc.getX(),blockLoc.getY(),blockLoc.getZ()));
		PacketPlayOutEntityTeleport teleport = new PacketPlayOutEntityTeleport(dummy);
		PlayerConnection pCon = ((CraftPlayer) player).getHandle().playerConnection;
		pCon.sendPacket(addEntity);
		player.sendBlockChange(blockLoc, Material.BED_BLOCK, (byte) 0);
		pCon.sendPacket(sleep);
		//player.sendBlockChange(blockLoc, block.getType(), (byte) block.getData());
		pCon.sendPacket(teleport);
	}
	
	//Created by Brent Nash on StackExchange. Copied for convenience, not because I can't do it.
	public static String getDurationBreakdown(long millis) {
	    if (millis < 0) {
	      throw new IllegalArgumentException("Duration must be greater than zero!");
	    }

	    long days = TimeUnit.MILLISECONDS.toDays(millis);
	    if (days > 365 * 100) {
	    	return "A long time";
	    }
	    millis -= TimeUnit.DAYS.toMillis(days);
	    long hours = TimeUnit.MILLISECONDS.toHours(millis);
	    millis -= TimeUnit.HOURS.toMillis(hours);
	    long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
	    millis -= TimeUnit.MINUTES.toMillis(minutes);
	    long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

	    StringBuilder sb = new StringBuilder(64);
	    sb.append(days);
	    sb.append(" Days ");
	    sb.append(hours);
	    sb.append(" Hours ");
	    sb.append(minutes);
	    sb.append(" Minutes ");
	    sb.append(seconds);
	    sb.append(" Seconds.");
	    return sb.toString();
	}
	
	public static long getBanTimeMillis(int previousBans) {
		double banTime = 0;
		switch (previousBans) {
		case 0:
			banTime = 0.5;
			break;
		case 1:
			banTime = 1.25;
			break;
		case 2:
			banTime = 4;
			break;
		case 3:
			banTime = 23;
			break;
		case 4:
			banTime = 365 * 1000; //a millennium ought to do it.
			break;
		default:
			throw new IllegalArgumentException("It's gotta be either 0 to 4 previous bans.");
		}
		return (long) (banTime * 86400000); //converts milliseconds to days
	}
	
}
