package hydrogenn.omd;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;

import net.md_5.bungee.api.ChatColor;

public class OmdListener implements Listener {
	
	@EventHandler
	public static void onDeadPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		if (DeadPlayer.isDead(player)) {
			DeadPlayer.revive(player);
		}
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
	
	/*
	@EventHandler
	public static void onPlayerJoinShowDead(PlayerJoinEvent e) {
		OnlyMostlyDead.displayCorpsesTo(e.getPlayer());
	}
	*/
	
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
				deadPlayer.hide(player);
			}
		}
	}
	
	@EventHandler
	public static void hideDeathLeaveMessage(PlayerQuitEvent e) {
		if (DeadPlayer.isDead(e.getPlayer())) {
			e.setQuitMessage(null);
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
	public static void onPlayerDieWithCorpse(PlayerDeathEvent e) {
		Player player = (Player) e.getEntity();
		if (DeadPlayer.isCarrier(player)) {
			DeadPlayer.stopCarrying(player);
		}
	}
	
	@EventHandler
	public static void onPlayerDeath(PlayerDeathEvent e) {
		Player player = e.getEntity();
		e.getDrops().clear();
		DeadPlayer.addDeadPlayer(player);
	}
	
	@EventHandler
	public static void onPlayerStopLooting(InventoryCloseEvent e) {
		if (DeadPlayer.isActiveInventory(e.getInventory())) {
			DeadPlayer.closeInventory(e.getInventory());
		}
	}
	
	@EventHandler
	public static void detectPlayerRunWithCorpse(PlayerToggleSprintEvent e) {
		Player player = e.getPlayer();
		if (!player.isSprinting() && DeadPlayer.isCarrier(player))
			DeadPlayer.stopCarrying(player);
	}
	
}
