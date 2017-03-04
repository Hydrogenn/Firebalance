package hydrogenn.nqp;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;


public class NotQuitePermadeath extends JavaPlugin {
	
	private static int useDistance;

	static public HashMap<UUID,DeadPlayer> deadPlayers = new HashMap<UUID,DeadPlayer>();
	
	FileConfiguration config = getConfig();

	@Override
    public void onEnable() {
		//Set up configs
        config.addDefault("use-distance", 10);
        config.options().copyDefaults(true);
        saveConfig();
        
        useDistance = config.getInt("use-distance");
        
		// Register commands
        getCommand("nqp").setExecutor(new CommandNqp());

		// Register the event listener
        getServer().getPluginManager().registerEvents(new NqpListener(), this);
        
	}
	@Override
    public void onDisable() {
		//TODO actually store the data
	}
	public static boolean isDead(Player player) {
		if (deadPlayers.containsKey( player.getUniqueId() ) )
			return true;
		return false;
	}
	
	public static boolean isStillDead(Player player) {
		return deadPlayers.get( player.getUniqueId() ).isStillDead();
	}
	
	public static Location locationOf(Player player) {
		return deadPlayers.get(player.getUniqueId()).getLocation();
	}
	

	public static void addDeadPlayer(Player player) {
		if (!deadPlayers.containsKey(player.getUniqueId())) {
			deadPlayers.put(player.getUniqueId(),new DeadPlayer(player));
			player.setGameMode(GameMode.SPECTATOR);
			player.sendMessage(ChatColor.DARK_RED + "You have died. You cannot respawn until someone revives you.");
		}
		
	}
	
	public static void revive(Player target) {
		DeadPlayer formerStats = deadPlayers.get(target.getUniqueId());
		deadPlayers.remove(target.getUniqueId());
		formerStats.unload(target);
		target.setGameMode(GameMode.SURVIVAL); //TODO detect default gamemode in server.properties
		target.sendMessage(ChatColor.AQUA + "You have been revived!");
	}
	
	public static void loot(Player player, Player target) {
		DeadPlayer targetStats = deadPlayers.get(target.getUniqueId());
		Inventory lootTable = targetStats.openInventory();
		player.openInventory(lootTable);
	}
	public static void carry(Player player, Player target) {
		DeadPlayer targetCorpse = deadPlayers.get(target.getUniqueId());
		targetCorpse.setCarrier(player.getUniqueId());
		ItemStack head = new ItemStack(Material.SKULL_ITEM,1);
		SkullMeta headMeta = (SkullMeta) head.getItemMeta();
		headMeta.setOwner(player.getName());
		player.getInventory().addItem(head);
		player.updateInventory();
		//TODO give the player a player head of the other person
		//TODO add a bunch of hooks for corpse carriers
	}
	public static final int getUseDistance() {
		return useDistance;
	}
}
