package hydrogenn.omd;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;

import net.md_5.bungee.api.ChatColor;

public class OmdListener implements Listener {
	
	static List<UUID> sleepers = new ArrayList<UUID>();
	
	@EventHandler
	public static void onDeadPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		DeadPlayer.revive(player);
	}
	
	@EventHandler
	public static void onDisguisedPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		if (DeadPlayer.isDisguised(uuid)) {
			DeadPlayer.setSkinAndName(DeadPlayer.getDisguise(uuid).getUuid(),player);
		}
		e.setJoinMessage(ChatColor.YELLOW + player.getDisplayName() + " has joined the game");
	}
	
	@EventHandler
	public static void onPlayerJoinEmpty(PlayerJoinEvent e) {
		if (Bukkit.getOnlinePlayers().size() == 1) {
			for (World world : Bukkit.getWorlds()) {
				world.setTime(0);
			}
		}
	}
	
	@EventHandler
	public static void onInteractBed(PlayerInteractEvent e) {
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (e.getHand() != EquipmentSlot.HAND) return;
		if (e.getClickedBlock() == null) return;
		if (e.getClickedBlock().getType() != Material.BED_BLOCK) return;
		Player player = e.getPlayer();
		sleepers.add(player.getUniqueId());
		player.kickPlayer(OnlyMostlyDead.getSleepMessage());
	}
	
	@EventHandler
	public static void dropBodyOnLeave(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		if (sleepers.contains(uuid)) {
			sleepers.remove(uuid);
			e.setQuitMessage(ChatColor.YELLOW + player.getDisplayName() +" has gone to bed.");
		}
		else if (!player.isBanned()) {
			DeadPlayer.addDeadPlayer(e.getPlayer(), false);
		}
	}
	
	@EventHandler
	public static void onPlayerJoinShowDead(PlayerJoinEvent e) {
		OnlyMostlyDead.displayCorpsesTo(e.getPlayer());
	}
	
	@EventHandler
	public static void onPlayerMoveIntoView(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		for (DeadPlayer deadPlayer : DeadPlayer.getList()) {
			if (deadPlayer.inRange(e.getTo()) &&
					!deadPlayer.inRange(e.getFrom())) {
				deadPlayer.show(player);
			}
			else if (!deadPlayer.inRange(e.getTo()) &&
					deadPlayer.inRange(e.getFrom())) {
				deadPlayer.hide(player,true);
			}
		}
	}
	
	@EventHandler
	public static void onPlayerTeleportIntoView(PlayerTeleportEvent e) {
		Player player = e.getPlayer();
		for (DeadPlayer deadPlayer : DeadPlayer.getList()) {
			if (deadPlayer.inRange(e.getTo()) &&
					!deadPlayer.inRange(e.getFrom())) {
				deadPlayer.show(player);
			}
			else if (!deadPlayer.inRange(e.getTo()) &&
					deadPlayer.inRange(e.getFrom())) {
				deadPlayer.hide(player,true);
			}
		}
	}
	
	@EventHandler
	public static void hideDeathLeaveMessage(PlayerQuitEvent e) {
		if (DeadPlayer.isBodyAndWasDead(e.getPlayer())) {
			e.setQuitMessage(null);
		}
	}
	
	@EventHandler
	public static void playerInteractNearBody(PlayerInteractEvent e) {
		if (e.getHand() == EquipmentSlot.OFF_HAND) return;
		Player player = e.getPlayer();
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Location loc = e.getClickedBlock().getLocation();
			for (DeadPlayer deadPlayer : DeadPlayer.getList()) {
				try {
				if (deadPlayer.getLocation().distanceSquared(loc) <= 4)
					DeadPlayer.interact(player,deadPlayer);
				} catch (IllegalArgumentException error) {continue;} //different dimension is okay.
			}
		}
		if (e.getAction() == Action.RIGHT_CLICK_AIR) {
			Location loc = player.getLocation();
			for (DeadPlayer deadPlayer : DeadPlayer.getList()) {
				try {
				if (deadPlayer.getLocation().distanceSquared(loc) <= 4)
					DeadPlayer.interact(player,deadPlayer);
				} catch (IllegalArgumentException error) {continue;}
			}
		}
	}
	
	/*
	@EventHandler
	public static void radialDeathMessage(PlayerDeathEvent e) {
		Player victim = e.getEntity();
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (player.getLocation().distance(victim.getLocation()) <= OnlyMostlyDead.getChatDistance()) {
				player.sendMessage(e.getDeathMessage());
			}
		}
		e.setDeathMessage(null);
	}
	*/
	
	/*
	@EventHandler
	public static void radialChatMessage(AsyncPlayerChatEvent e) {
		Location loc = e.getPlayer().getLocation();
		List<Player> recipients = new ArrayList<Player>();
		for (Player player : e.getRecipients()) {
			if (player.getLocation().distance(loc) <= OnlyMostlyDead.getChatDistance()) {
				recipients.add(player);
			}
		}
		e.getRecipients().clear();
		e.getRecipients().addAll(recipients);
	}*/
	
	@EventHandler
	public static void onPlayerQuitWithCorpose(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		if (DeadPlayer.isCarrier(player)) {
			DeadPlayer.stopCarrying(player);
		}
	}
	
	@EventHandler
	public static void onPlayerDeath(PlayerDeathEvent e) {
		Player player = e.getEntity();
		e.getDrops().clear();
		DeadPlayer.addDeadPlayer(player,true);
	}
	
	
	/**
	 * Detects when a compass is used, and acts accordingly:
	 * <ul>
	 * <li> The closest dead player is now the player's spawn, which makes the compass point to it. </li>
	 * <li> Manually revived players are skipped. </li>
	 * <li> The range is limited to 500 blocks. </li>
	 * </ul>
	 */
	@EventHandler
	public static void onUseCompass(PlayerInteractEvent e) {
		if (!(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) return;
		if (e.getItem() == null) return;
		if (e.getItem().getType().equals(Material.COMPASS)) {
			Player player = e.getPlayer();
			Location pLocation = e.getPlayer().getLocation();
			Location bLocation = null;
			double sqDistance = 500*500;
			for (DeadPlayer deadPlayer : DeadPlayer.getList()) {
				if (deadPlayer.isRevived()) continue;
				Location dLocation = deadPlayer.getLocation();
				if (dLocation.getWorld().equals(pLocation.getWorld())) {
					double nsqDistance = dLocation.distanceSquared(pLocation);
					if (nsqDistance < sqDistance) {
						sqDistance = nsqDistance;
						bLocation = dLocation;
					}
				}
			}
			if (bLocation == null) {
				player.sendMessage("Couldn't find a dead player nearby.");
			}
			else {
				player.sendMessage("Compass updated.");
				player.setCompassTarget(bLocation);
			}
		}
	}
	
	@EventHandler
	public static void onPlayerStopLooting(InventoryCloseEvent e) {
		if (DeadPlayer.isActiveInventory(e.getInventory())) {
			DeadPlayer.closeInventory(e.getInventory());
		}
	}
	
}
