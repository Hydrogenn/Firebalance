package hydrogenn.omd;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.EquipmentSlot;

public class OmdListener implements Listener {
	
	@EventHandler
	public static void onDeadPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		if (DeadPlayer.isDead(player) && !DeadPlayer.getDeadPlayer(player.getUniqueId()).isStillDead()) {
			DeadPlayer.revive(player);
		}
	}
	
	@EventHandler
	public static void onPlayerQuitWithCorpose(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		if (DeadPlayer.isCarrier(player)) {
			DeadPlayer targetCorpse = DeadPlayer.getCarrying(player);
			targetCorpse.setCarrier(null);
			targetCorpse.setLocation(player.getLocation());
		}
	}
	
	@EventHandler
	public static void onPlayerDieWithCorpse(PlayerDeathEvent e) {
		Player player = (Player) e.getEntity();
		if (DeadPlayer.isCarrier(player)) {
			DeadPlayer targetCorpse = DeadPlayer.getCarrying(player);
			targetCorpse.setCarrier(null);
			targetCorpse.setLocation(player.getLocation());
		}
	}
	
	@EventHandler
	public static void onPlayerDeath(PlayerDeathEvent e) {
		Player player = e.getEntity();
		e.getDrops().clear();
		DeadPlayer.addDeadPlayer(player);
	}
	
	@EventHandler
	public static void onInteractWithCorpse(PlayerInteractEntityEvent e) {
		if (e.getHand().equals(EquipmentSlot.OFF_HAND)) return; //Don't count the offhand
		Entity entity = e.getRightClicked();
		if (entity instanceof Creature && DeadPlayer.isDummy((Creature) entity)) {
			Creature dummy = (Creature) entity;
			DeadPlayer deadPlayer = DeadPlayer.getDeadPlayer(DeadPlayer.getOwner(dummy));
			DeadPlayer.interact(e.getPlayer(),deadPlayer);
		}
	}
	
	@EventHandler
	public static void onMoveWithCorpse(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if (DeadPlayer.isCarrier(player)) {
			DeadPlayer.getCarrying(player).getDummy().teleport(player);
		}
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
