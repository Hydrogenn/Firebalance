package hydrogenn.omd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class CommandOmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (args.length < 1) return false;
		
		if (args[0].equals("help")) {
			String message = ChatColor.GRAY + "On death, you are banned and drop a corpse.\n";
			message += "Right-clicking a corpse's "+ChatColor.ITALIC+"head"+ChatColor.GRAY+" brings up a menu in chat, with a few options.\n";
			message += "Every option is self-documented. Click on it to use.\n";
			message += "Players can revive you if you have died. If no one comes, you are automatically revived in 18 hours.\n";
			message += "Players can also 'curse' your auto-revival. If 5 people do this, you never get automatically revived.\n";
			message += "Worth noting is that others can disguise as dead players, carry them around, and have full access to their inventory.";
			sender.sendMessage(message);
			return true;
		}
		
		Player player = null;
		if ((sender instanceof Player)) {
			player = (Player) sender;
			
			if (args[0].equals("drop")) {
				DeadPlayer.stopCarrying(player);
				return true;
			}
			else if (args[0].equals("undisguise")) {
				DeadPlayer.stopDisguising(player);
				return true;
			}
			
		}
		
		DeadPlayer target;
		try {
			target = DeadPlayer.getDeadPlayer(args[1]);
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
		
		if (target == null) {
			sender.sendMessage("Could not find dead player: "+args[1]);
			return true;
		}
		
		if (player != null) {
			try {
				if (player.getLocation().distance(target.getLocation()) > OnlyMostlyDead.getUseDistance()) {
					player.sendMessage("Could not find dead player: "+args[1]);
					return true;
				}
			} catch (IllegalArgumentException e) {
				player.sendMessage("Could not find dead player: "+args[1]);
				return true;
			}
		}
		
		if (args[0].equals("revive")) {
			
			if (target.getCarrier() != null) {
				sender.sendMessage("This player is being carried and cannot be revived.");
			}
			else if (target.isBeingLooted()) {
				sender.sendMessage("This player is being looted and cannot be revived.");
			}
			else if (!target.isStillDead()) {
				sender.sendMessage("This player is just sleeping. (What do you mean, it looks like a zombie?)");
			}
			else {
				target.setStillDead(false);
				sender.sendMessage("Successfully revived "+target.getName());
			}
		}
		
		else if (args[0].equals("kill")) {
			if (target.isStillDead()) {
				sender.sendMessage("This player is still definitely dead.");
			}
			else {
				target.setStillDead(true);
				sender.sendMessage("You have killed it (again).");
			}
		}
		
		else if (player != null) {

			if (args[0].equals("loot")) {
				if (!target.isStillDead()) {
					sender.sendMessage("That player is just sleeping. It'd be rude to take their stuff!");
				}
				else {
					DeadPlayer.loot(player,target);
				}
			}
			else if (args[0].equals("carry")) {
				DeadPlayer.carry(player,target);
			}
			else if (args[0].equals("disguise")) {
				DeadPlayer.disguise(player,target);
			}
			else if (args[0].equals("curse")) {
				DeadPlayer.curse(player,target);
			}
		}
		
		return true;
	}

}
