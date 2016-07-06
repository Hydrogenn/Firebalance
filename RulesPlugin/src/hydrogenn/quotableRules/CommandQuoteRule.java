package hydrogenn.quotableRules;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandQuoteRule implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length<1) return false;
		try {
			int x = Integer.parseInt(args[0])-1;
			Bukkit.broadcastMessage(ChatColor.GOLD+"Rule #"+(x+1)+": "+QuotableRules.ruleSet.get(x));
		} catch (NumberFormatException e) {
			sender.sendMessage("That's not a rule number. Just the number will do.");
		} catch (IndexOutOfBoundsException e) {
			sender.sendMessage("There aren't THAT many rules.");
		}
		return true;
	}

}