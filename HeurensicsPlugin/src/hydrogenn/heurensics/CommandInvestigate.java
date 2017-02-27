package hydrogenn.heurensics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandInvestigate implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only players can use this.");
			return true;
		}
		Player player = (Player) sender;
		if (Heurensics.toggleInvestigator(player.getUniqueId())) {
			player.sendMessage(ChatColor.AQUA + "Investigator enabled. Right-click while sneaking to use.");
			player.sendMessage(ChatColor.AQUA + "Water bottles can be used to store the evidence.");
		}
		else
			player.sendMessage(ChatColor.AQUA + "Investigator disabled.");
		return true;
	}

}
