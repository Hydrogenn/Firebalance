package hydrogenn.nqp;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandNqp implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
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
		
		if (!NotQuitePermadeath.isDead(target)) {
			player.sendMessage("That player isn't dead.");
			return true;
		}
		
		if (player.getLocation().distance(NotQuitePermadeath.locationOf(target)) > NotQuitePermadeath.getUseDistance()) {
			player.sendMessage("This player is too far away.");
			return true;
		}
		
		if (args[0].equals("revive")) {
			
			try {
				NotQuitePermadeath.revive(target);
				sender.sendMessage("Successfully revived "+target.getDisplayName());
			} catch (NullPointerException e) {
				sender.sendMessage("Failed to revive that player.");
			}
			
		}
		
		else if (args[0].equals("loot"))
				NotQuitePermadeath.loot(player,target);
		
		return true;
	}

}
