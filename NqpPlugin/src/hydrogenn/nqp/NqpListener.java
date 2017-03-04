package hydrogenn.nqp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;

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
			Player other = Bukkit.getServer().getPlayer(DeadPlayer.getCarrying(player));
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
