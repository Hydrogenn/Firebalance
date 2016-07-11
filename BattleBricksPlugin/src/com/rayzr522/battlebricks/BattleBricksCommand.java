package com.rayzr522.battlebricks;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/* 
 * BattleBricksCommand.java
 * Made by Rayzr522
 * Date: Jul 11, 2016
 */
public class BattleBricksCommand implements CommandExecutor {

	public static HashMap<Player, Player> fightingPlayers = new HashMap<Player, Player>();
	public static HashMap<Player, Integer> timeouts = new HashMap<Player, Integer>();

	private static final String HORIZONTAL_BAR = ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH
			+ "----------------------------------------------------";
	private static final String HELP_PREFIX = "&7&l|&b ";

	private static final String NO_PERMISSION = "&cYou dont' have permission to do that!";

	private BattleBricks plugin;

	public BattleBricksCommand(BattleBricks plugin) {
		this.plugin = plugin;
	}

	private ListFilter<Entity> playersFilter = new ListFilter<Entity>() {

		@Override
		public boolean keep(Entity input) {
			return input instanceof Player;
		}

	};

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

				List<Entity> entities = ListFilter.apply(p.getNearbyEntities(16, 16, 16), playersFilter);

				for (Entity entity : entities) {

					if (!entity.getName().equalsIgnoreCase(args[1])) {

						continue;

					}

					Player other = (Player) entity;

					if (fightingPlayers.containsKey(other)) {

						Player otherTarget = fightingPlayers.get(other);
						if (!otherTarget.equals(p)) {
							msg(p, "&bThat person has already initiated a fight with" + otherTarget.getDisplayName()
									+ ".");
						} else {

							// TODO: Start fight

						}

					} else {

						msg(p, "&bSent fight request to &9" + other.getDisplayName() + "&b.");
						fightingPlayers.put(p, other);

					}

					fightingPlayers.put(p, other);

				}

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

				} else {

					help(p, "Usage: /bb config <save|load>");

				}

			}

		}

		return true;

	}

	public void removeFromLists(Player p) {

		fightingPlayers.remove(p);

		int schedule = timeouts.remove(p);

		if (Bukkit.getScheduler().isQueued(schedule)) {

			Bukkit.getScheduler().cancelTask(schedule);

		}

	}

	public void startTimeout(Player p) {

		BukkitRunnable runnable = new BukkitRunnable() {

			@Override
			public void run() {

				if (fightingPlayers.containsKey(p)) {

					Player other = fightingPlayers.get(p);

					msg(p, "&bYour fight with &9" + other.getDisplayName() + "&b was cancelled.");
					msg(other, "&bYour fight with &9" + p.getDisplayName() + "&b was cancelled.");

				}

				removeFromLists(p);

			}

		};

		runnable.runTaskLaterAsynchronously(plugin, 5 * 20);
		timeouts.put(p, runnable.getTaskId());

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
