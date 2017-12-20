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

	static MobBorderPlugin plugin;
	
	MobBorderListener(MobBorderPlugin plugin) {
		MobBorderListener.plugin = plugin;
	}
	
	@EventHandler
	public static void playerHurtEvent(EntityDamageByEntityEvent e) {
		Entity attacker = e.getDamager();
		Entity victim = e.getEntity();
		
		if (victim.getType() != EntityType.PLAYER) return; //this only concerns players getting hurt
		if (attacker.getType() == EntityType.PLAYER && !plugin.pvp()) return;
		
		Player player = (Player) victim;
		
		int pLevel = player.getLevel();
		int mLevel = plugin.getLevelByLocation(attacker.getLocation());
		if (attacker.getType() == EntityType.PLAYER) {
			mLevel = Math.min(((Player)attacker).getLevel(),mLevel);
		}
		e.setDamage(e.getDamage() * plugin.getDamageBuff(mLevel,pLevel));
	}
	
	@EventHandler
	public static void mobHurtEvent(EntityDamageByEntityEvent e) {
		Entity attacker = e.getDamager();
		Entity victim = e.getEntity();
		
		if (attacker.getType() != EntityType.PLAYER) return;
		if (victim.getType() == EntityType.PLAYER && !plugin.pvp()) return;
		
		Player player = (Player) attacker;
		
		int pLevel = player.getLevel();
		int mLevel = plugin.getLevelByLocation(victim.getLocation());
		if (victim.getType() == EntityType.PLAYER) {
			mLevel = Math.min(((Player)attacker).getLevel(),mLevel);
		}
		e.setDamage(e.getDamage() / plugin.getHealthBuff(mLevel, pLevel));
	}
	
	@EventHandler
	public static void mobKillEvent(EntityDeathEvent e) {
		
		LivingEntity entity = e.getEntity();
		Player killer = entity.getKiller();

		if (killer == null) return; //must have been killed by a player
		if (entity.getType() == EntityType.PLAYER) return;
		
		int mLevel = plugin.getLevelByLocation(entity.getLocation());
		int pLevel = killer.getLevel();
		e.setDroppedExp((int) (e.getDroppedExp() * plugin.getYield(mLevel, pLevel)));
	}
	
	@EventHandler
	public static void playerMoveIntoDanger(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		
		int startLevel = plugin.getLevelByLocation(e.getFrom());
		int endLevel = plugin.getLevelByLocation(e.getTo());
		
		if (startLevel == endLevel) return; //no change, don't send a message
		
		int pLevel = player.getLevel();
		
		int relativeLevel = endLevel - pLevel;
		
		if (endLevel <= pLevel && startLevel <= pLevel) return; //still safe on both the start and end, don't bother.
		
		if (relativeLevel == 0)
			player.sendMessage(ChatColor.AQUA + "Back in safe territory. No more mob buff.");
		else if (startLevel < endLevel)
			player.sendMessage(ChatColor.RED + "Warning: Going too far for your level. "+ plugin.getDisplayBuff(endLevel, pLevel));
		else
			player.sendMessage(ChatColor.WHITE + plugin.getDisplayBuff(endLevel, pLevel));
	
	}
	
}
