package hydrogenn.mobBorder;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import net.md_5.bungee.api.ChatColor;

public class MobBorderListener implements Listener {

	@EventHandler
	public static void playerHurtEvent(EntityDamageByEntityEvent e) {
		Entity attacker = e.getDamager();
		Entity victim = e.getEntity();
		
		if (victim.getType() != EntityType.PLAYER) return; //this only concerns players getting hurt
		if (attacker.getType() == EntityType.PLAYER) return; //no plz
		
		Player player = (Player) victim;
		
		int pLevel = player.getLevel();
		int mLevel = MobBorderPlugin.getLevelByLocation(attacker.getLocation());
		int relativeLevel = mLevel - pLevel; //the effective level of the mob
		
		if (relativeLevel <= 0) return; //we don't care if the mob is a lower level
		else {
			e.setDamage(e.getDamage() * (1 + 0.25*relativeLevel)); //a quarter the potency per level advantage
		}
	}
	
	@EventHandler
	public static void mobHurtEvent(EntityDamageByEntityEvent e) {
		Entity attacker = e.getDamager();
		Entity victim = e.getEntity();
		
		if (attacker.getType() != EntityType.PLAYER) return;
		if (victim.getType() == EntityType.PLAYER) return;
		
		Player player = (Player) attacker;
		
		int pLevel = player.getLevel();
		int mLevel = MobBorderPlugin.getLevelByLocation(victim.getLocation());
		int relativeLevel = mLevel - pLevel;
		
		if (relativeLevel <= 0) return;
		else {
			e.setDamage(e.getDamage() / (1 + 0.25*relativeLevel));
		}
	}
	
	@EventHandler
	public static void mobKillEvent(EntityDeathEvent e) {
		
		LivingEntity entity = e.getEntity();
		Player killer = entity.getKiller();

		if (killer == null) return; //must have been killed by a player
		if (entity.getType() == EntityType.PLAYER) return;
		
		int mLevel = MobBorderPlugin.getLevelByLocation(entity.getLocation());
		int pLevel = killer.getLevel();
		int relativeLevel = mLevel - pLevel;
		
		if (relativeLevel <= 0) return;
		else {
			e.setDroppedExp((int) (e.getDroppedExp() * (1 + 0.5*relativeLevel)));
		}
	}
	
	@EventHandler
	public static void playerMoveIntoDanger(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		
		int startLevel = MobBorderPlugin.getLevelByLocation(e.getFrom());
		int endLevel = MobBorderPlugin.getLevelByLocation(e.getTo());
		
		if (startLevel == endLevel) return; //no change, don't send a message
		
		int pLevel = player.getLevel();
		
		int relativeLevel = endLevel - pLevel;
		
		if (endLevel <= pLevel && startLevel <= pLevel) return; //still safe on both the start and end, don't bother.
		
		if (relativeLevel == 0)
			player.sendMessage(ChatColor.AQUA + "Back in safe territory. No more mob buff.");
		else if (startLevel < endLevel)
			player.sendMessage(ChatColor.RED + "Warning: Going too far for your level. Mob Buff: " + (int)((1 + 0.25*relativeLevel)*100) + "%");
		else
			player.sendMessage(ChatColor.WHITE + "Mob Buff: "+ (int)((1 + 0.25*relativeLevel)*100) + "%");
	
	}
	
}
