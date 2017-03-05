package hydrogenn.nqp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class NqpListener implements Listener {
	
	@EventHandler
	public static void onDeadPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		if (NotQuitePermadeath.isDead(player) && !NotQuitePermadeath.isStillDead(player)) {
			NotQuitePermadeath.revive(player);
		}
	}
	
	@EventHandler
	public static void onPlayerDeath(PlayerDeathEvent e) {
		Player player = e.getEntity();
		e.getDrops().clear();
		NotQuitePermadeath.addDeadPlayer(player);
	}
	
	@EventHandler
	public static void onPlayerMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if (NotQuitePermadeath.isDead(player)) {
			Location location = NotQuitePermadeath.locationOf(player);
			e.getTo().setX(location.getX());
			e.getTo().setY(location.getY());
			e.getTo().setZ(location.getZ());
			e.getTo().setWorld(location.getWorld());
		}
		if (DeadPlayer.isCarrier(player)) {
			Player other = Bukkit.getServer().getPlayer(DeadPlayer.getCarrying(player).getUuid());
			if (other != null) {
				other.setSpectatorTarget(player);
			}
		}
	}
	
	@EventHandler
	public static void onPlayerLootItem(InventoryClickEvent e) {
		Inventory inventory = e.getInventory();
		if (DeadPlayer.isActiveInventory(inventory)) {
			if (e.getSlot() >= 41)
				e.setCancelled(true);
			else
				e.setCancelled(false);
		}
	}
	
	@EventHandler
	public static void onPlayerStopLooting(InventoryCloseEvent e) {
		if (DeadPlayer.isActiveInventory(e.getInventory())) {
			DeadPlayer.closeInventory(e.getInventory());
		}
	}
	
	
	/*TODO fix this up when the forum post is answered.
	@EventHandler
	public static void stopDeadPlayerSpectating(Event e) {
		Player player = (Player) e.getEntity();
		player.sendMessage("You've mounted an entity!");
		if (player.getGameMode() == GameMode.SPECTATOR && NotQuitePermadeath.isDead(player)) {
			e.setCancelled(true);
		}
	}
	*/
	
	@EventHandler
	public static void detectPlayerDropped(BlockPlaceEvent e) {
		Player player = e.getPlayer();
		if (DeadPlayer.isCarrier(player)) { //Currently carrying a dead body
			ItemStack item = e.getItemInHand();
			if (item.getType() == Material.SKULL_ITEM) { //Holding a skull while placing a block
				SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
				DeadPlayer deadPlayer = DeadPlayer.getCarrying(player);
				String deadPlayerName = deadPlayer.getName();
				if (skullMeta.getOwner().equals(deadPlayerName)) { //Has the dead player's name
					Player otherPlayer = Bukkit.getServer().getPlayer(deadPlayer.getUuid());
					
					deadPlayer.setLocation(e.getBlock().getLocation().add(0.5,0,0.5));
					deadPlayer.setCarrier(null);
					otherPlayer.setSpectatorTarget(null);
					otherPlayer.teleport(deadPlayer.getLocation());
					e.setCancelled(true);
					item.setAmount(0);
				}
			}
			
		}
	}
	
	@EventHandler
	public static void detectCorpseMeddling(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		if (DeadPlayer.isCarrier(player)) { //Currently carrying a dead body
			ItemStack item = e.getCurrentItem();
			ItemStack otherItem = e.getCursor();
			DeadPlayer deadPlayer = DeadPlayer.getCarrying(player);
			String deadPlayerName = deadPlayer.getName();
			if (item.getType() == Material.SKULL_ITEM) { //Has the dead player's name
				SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
				if (skullMeta.getOwner().equals(deadPlayerName)) { //Holding a skull while placing a block
					e.setCancelled(true);
					e.setCurrentItem(item);
					player.updateInventory();
				}
			}
			if (otherItem.getType() == Material.SKULL_ITEM) {
				SkullMeta otherSkullMeta = (SkullMeta) otherItem.getItemMeta();
				if (otherSkullMeta.getOwner().equals(deadPlayerName)) {
					e.setCancelled(true);
					e.setCurrentItem(item);
					player.updateInventory();
				}
			}
		}
	}
	
	@EventHandler
	public static void detectCorpseDropping(PlayerDropItemEvent e) {
		Player player = (Player) e.getPlayer();
		if (DeadPlayer.isCarrier(player)) {
			ItemStack item = e.getItemDrop().getItemStack();
			DeadPlayer deadPlayer = DeadPlayer.getCarrying(player);
			String deadPlayerName = deadPlayer.getName();
			if (item.getType() == Material.SKULL_ITEM) {
				SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
				if (skullMeta.getOwner().equals(deadPlayerName)) {
					e.setCancelled(true);
					player.updateInventory();
				}
			}
		}
	}
	
	@EventHandler
	public static void onDeadPlayerRespawn(PlayerRespawnEvent e) {
		Player player = e.getPlayer();
		if (!NotQuitePermadeath.isDead(player)) return;
		e.setRespawnLocation(NotQuitePermadeath.locationOf(player));
		player.sendTitle(ChatColor.DARK_RED + "You have died.",
				ChatColor.DARK_RED + "You cannot respawn until someone revives you.",
	            100,
	            500,
	            100);
	}
	
}
