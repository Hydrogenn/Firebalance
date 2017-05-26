package hydrogenn.omd;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

//TODO add a help command
public class CommandOmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			for (DeadPlayer deadPlayer : DeadPlayer.getList()) {
				Bukkit.getLogger().info(deadPlayer.getName());
			}
			return true;
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
		
		Player player = (Player) sender;
		
		if (player.getLocation().distance(target.getLocation()) > OnlyMostlyDead.getUseDistance()) {
			player.sendMessage("This player is too far away.");
			return true;
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
		
		else if (args[0].equals("loot")) {
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
		
		else if (args[0].equals("drop")) {
			DeadPlayer.stopCarrying(player);
		}
		
		return true;
	}

}
