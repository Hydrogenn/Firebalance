package com.rayzr522.battlebricks;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/* 
 * BattleBricksCommand.java
 * Made by Rayzr522
 * Date: Jul 11, 2016
 */
public class BattleBricksCommand implements CommandExecutor {

	private static final String HORIZONTAL_BAR = ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH
			+ "----------------------------------------------------";
	private static final String HELP_PREFIX = "&7&l|&b ";

	private static final String NO_PERMISSION = "&cYou dont' have permission to do that!";

	@Override
	public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("Only players can use this!");
			return true;
		}

		Player p = (Player) sender;

		if (args.length < 1) {
			help(p);
			return true;
		}

		if (args[0].equalsIgnoreCase("fight")) {

			if (args.length < 2) {
				help(p, "Usage: /bb fight <player>");
			} else {

			}

		} else if (args[0].equalsIgnoreCase("config")) {
			if (!has(p, "BattleBricks.admin")) {
				msg(p, NO_PERMISSION);
				return true;
			}

			if (args.length < 2) {
				help(p, "Usage: /bb config <save|load>");
			} else {

				if (args[1].equalsIgnoreCase("save")) {
					
					ConfigManager.save();

				} else if (args[1].equalsIgnoreCase("load")) {
					
					ConfigManager.load();

				}

			}

		}

		return true;
	}

	private boolean has(Player p, String perm) {

		return p.hasPermission(perm);

	}

	private void help(Player p, String msg) {

		msg(p, HELP_PREFIX + msg.replace("/", ChatColor.GREEN + "/"));

	}

	private void help(Player p) {

		msg(p, HORIZONTAL_BAR);
		help(p, "&9&lBattleBricks&b usage:");
		help(p, "/bb fight <player>");

		if (has(p, "BattleBricks.admin")) {
			help(p, "/bb config <save|reload>");
		}

		msg(p, HORIZONTAL_BAR);

	}

	private void msg(Player p, String msg) {

		p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));

	}

}
