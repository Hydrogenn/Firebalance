package hydrogenn.filter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/* 
 * CommandHandler.java
 * Made by Rayzr522
 * Date: Jul 14, 2016
 */
public class CommandHandler implements CommandExecutor {

	private SwearFilterLite plugin;

	public CommandHandler(SwearFilterLite plugin) {

		this.plugin = plugin;

	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (args.length < 1 || args[0].equalsIgnoreCase("help")) {

			sender.sendMessage("Usage: /sfl <reload:version>");
			return true;

		}

		if (args[0].equalsIgnoreCase("reload")) {

			plugin.reload();
			sender.sendMessage("SFL config reloaded");

		} else if (args[0].equalsIgnoreCase("version")) {

			sender.sendMessage("You are running SwearFilterLite v" + plugin.getDescription().getVersion());

		} else {

			sender.sendMessage("Unkown command: '" + args[0] + "'");

		}

		return true;

	}

}
