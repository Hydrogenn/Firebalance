package hydrogenn.nqp;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandNqp implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		//FIXME currently requires the other player to be online
		
		Player target;
		try {
			target = Bukkit.getServer().getPlayer(args[1]);
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
		
		if (target == null) {
			sender.sendMessage("Could not find player: "+target);
			return true;
		}
		
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only players can use this.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (NotQuitePermadeath.isDead(player)) {
			player.sendMessage("You cannot do that while dead.");
			return true;
		}
		
		if (!NotQuitePermadeath.isDead(target)) {
			player.sendMessage("That player isn't dead.");
			return true;
		}
		
		if (player.getLocation().distance(NotQuitePermadeath.locationOf(target)) > NotQuitePermadeath.getUseDistance()) {
			player.sendMessage("This player is too far away.");
			return true;
		}
		
		if (args[0].equals("revive")) {
			
			if (DeadPlayer.isCarried(target)) {
				sender.sendMessage("This player is being carried and cannot be revived.");
			}
			else {
				NotQuitePermadeath.revive(target);
				sender.sendMessage("Successfully revived "+target.getDisplayName());
			}
			
		}
		
		else if (args[0].equals("loot")) {
			NotQuitePermadeath.loot(player,target);
		}
		
		else if (args[0].equals("carry")) {
			NotQuitePermadeath.carry(player,target);
		}
		
		return true;
	}

}
