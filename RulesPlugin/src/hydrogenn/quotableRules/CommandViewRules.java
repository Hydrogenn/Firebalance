package hydrogenn.quotableRules;

import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandViewRules implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		int x = 1;

		for (Iterator<String> i = QuotableRules.ruleSet.iterator(); i.hasNext();) {

			sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "" + x + ". " + ChatColor.GOLD + i.next());
			x++;

		}

		return true;
	}

}
