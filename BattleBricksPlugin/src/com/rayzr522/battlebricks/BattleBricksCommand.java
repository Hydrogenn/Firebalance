package com.rayzr522.battlebricks;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
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

	public static HashMap<Competitor, Competitor> requests = new HashMap<Competitor, Competitor>();
	public static HashMap<Competitor, Integer> timeouts = new HashMap<Competitor, Integer>();

	public static HashMap<Player, Integer> twerkers = new HashMap<Player, Integer>();

	private static final String HORIZONTAL_BAR = ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH
			+ "----------------------------------------------------";
	private static final String HELP_PREFIX = "&7&l|&b ";

	private static final String NO_PERMISSION = "&cYou dont' have permission to do that!";

	private BattleBricks plugin;

	public BattleBricksCommand(BattleBricks plugin) {
		this.plugin = plugin;
	}

	/**
	 * A simple {@link ListFilter} to remove all entities except players from a
	 * list.
	 */
	private ListFilter<Entity> playersFilter = new ListFilter<Entity>() {

		@Override
		public boolean keep(Entity input) {
			return input instanceof Player;
		}

	};

	/**
	 * Called when <code>/bb</code> or <code>/battlebricks</code> is performed.
	 */
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

		if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
			help(p);
			return true;
		}

		if (args[0].equalsIgnoreCase("fight")) {

			Competitor p1 = new Competitor(p);

			if (!p1.isValid()) {

				msg(p, "&bYou have to be holding a &9Battle Brick&b.");

			} else if (args.length < 2) {

				help(p, "Usage: /bb fight <player>");

			} else {

				if (requests.containsKey(p)) {

					msg(p, "&bYou've already sent a fight request to &9" + requests.get(p));

				} else {

					List<Entity> entities = ListFilter.apply(p.getNearbyEntities(16, 16, 16), playersFilter);

					for (Entity entity : entities) {

						if (!entity.getName().equalsIgnoreCase(args[1])) {

							continue;

						}

						Player other = (Player) entity;
						Competitor p2 = new Competitor(other);

						if (requests.containsKey(p2)) {

							Competitor otherTarget = requests.get(p2);

							if (!otherTarget.equals(p1)) {
								msg(p, "&bThat person has already initiated a fight with &9" + otherTarget.getName()
										+ "&b.");
							} else {

								removeFromLists(p1);
								removeFromLists(p2);

								if (!(p1.isValid() && p2.isValid())) {

									msg(p, "&cSomething went wrong...");
									msg(other, "&cSomething went wrong...");

								}

								Timed.message(0, "&eStarting battle in 5 seconds...", p, other);
								Timed.message(3, "&c&l3...", p, other);
								Timed.message(4, "&c&l2...", p, other);
								Timed.message(5, "&c&l1...", p, other);
								Timed.message(6, "&e&lBEGIN TWERKING", p, other);

								Timed.runnable(6, new BukkitRunnable() {

									@Override
									public void run() {

										twerkers.put(p, 0);
										twerkers.put(other, 0);

									}

								});

								Timed.message(7, "&c&l5...", p, other);
								Timed.message(8, "&c&l4...", p, other);
								Timed.message(9, "&c&l3...", p, other);
								Timed.message(10, "&c&l2...", p, other);
								Timed.message(11, "&c&l1...", p, other);

								Timed.runnable(12, new BukkitRunnable() {

									@Override
									public void run() {

										fightComplete(p1, p2);

									}

								});

							}

						} else {

							p1.msg("&bSent fight request to &9" + p2.getName() + "&b.");
							p2.msg("&9" + p1.getName() + "&b would like to battle you.");
							p2.msg("&bTo accept, do /bb fight &9" + p1.getName());
							p2.msg("&bThis request will timeout in 2 minutes");

							requests.put(p1, p2);
							startTimeout(p1);

						}

						requests.put(p1, p2);

					}

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

					msg(p, "&bSaving config...");
					ConfigManager.save();
					msg(p, "&bConfig saved");

				} else if (args[1].equalsIgnoreCase("load")) {

					msg(p, "&bLoading config...");
					ConfigManager.load();
					msg(p, "&bConfig loaded");

				} else {

					help(p, "Usage: /bb config <save|load>");

				}

			}

		} else {

			msg(p, "&cUnknown command '" + args[0] + "'");
			help(p);
			return true;

		}

		return true;

	}

	// Remove a competitor from the requests list
	/**
	 * Remove a competitor from the requests list. This also removes the
	 * competitor from the timeouts list if there was a timeout scheduled for
	 * him/her, and cancels it if the timeout was still running.
	 * 
	 * @param p
	 *            = the competitor
	 */
	public void removeFromLists(Competitor p) {

		requests.remove(p);

		// If the timeout still exists
		if (timeouts.containsKey(p)) {
			int schedule = timeouts.remove(p);

			// There still might be a schedule ID in the timeouts list, but it
			// might have already been completed
			if (Bukkit.getScheduler().isQueued(schedule)) {

				Bukkit.getScheduler().cancelTask(schedule);

			}

		}

	}

	// Start the timeout that cancels the fight request
	/**
	 * Starts the timeout that cancels the competitor's fight request.
	 * 
	 * @param p1
	 *            = the competitor
	 */
	public void startTimeout(Competitor p1) {

		BukkitRunnable runnable = new BukkitRunnable() {

			@Override
			public void run() {

				if (requests.containsKey(p1)) {

					Competitor p2 = requests.get(p1);

					p1.msg("&bYour fight with &9" + p2.getName() + "&b was cancelled.");
					p2.msg("&bYour fight with &9" + p1.getName() + "&b was cancelled.");

				}

				removeFromLists(p1);

			}

		};

		runnable.runTaskLaterAsynchronously(plugin, 120 * 20); // Cancel the
																// fight request
																// 120 seconds
																// (60 + 60)
																// later
		timeouts.put(p1, runnable.getTaskId());

	}

	/**
	 * Called after the 5 seconds of twerking
	 * 
	 * @param p1
	 *            = the first competitor
	 * @param p2
	 *            = the second competitor
	 */
	public void fightComplete(Competitor p1, Competitor p2) {

		long twerk1 = twerkers.get(p1.getPlayer());
		long twerk2 = twerkers.get(p2.getPlayer());

		long level1 = p1.getBrick().getLevel();
		long level2 = p2.getBrick().getLevel();

		long score1 = twerk1 + level1 * 10;
		long score2 = twerk2 + level2 * 10;

		if (score1 == score2) {

			p1.msg("&bIt was a tie!");
			p2.msg("&bIt was a tie!");

			playSound(p1.getPlayer(), Sound.ENTITY_CREEPER_DEATH, 1.0f, 1.0f);
			playSound(p2.getPlayer(), Sound.ENTITY_CREEPER_DEATH, 1.0f, 1.0f);

		} else if (score1 > score2) {

			p1.msg("&9" + p1.getName() + "&b won!");
			p2.msg("&9" + p1.getName() + "&b won!");

			long gainedXP = level2 * 15;
			p1.msg("&bYou gained &9" + gainedXP + " XP&b from defeating &9" + p2.getName());

			long oldLevel = p1.getBrick().getLevel();
			p1.getBrick().addXp(gainedXP);
			p1.updateBrick();

			playSound(p1.getPlayer(), Sound.ENTITY_PLAYER_LEVELUP, p1.getBrick().getLevel() == oldLevel ? 1.2f : 0.5f,
					1.0f);

		} else {

			p1.msg("&9" + p2.getName() + "&b won!");
			p2.msg("&9" + p2.getName() + "&b won!");

			long gainedXP = level1 * 15;
			p2.msg("&bYou gained &9" + gainedXP + " XP&b from defeating &9" + p1.getName());

			long oldLevel = p2.getBrick().getLevel();
			p2.getBrick().addXp(gainedXP);
			p2.updateBrick();

			playSound(p2.getPlayer(), Sound.ENTITY_PLAYER_LEVELUP, p2.getBrick().getLevel() == oldLevel ? 1.2f : 0.5f,
					1.0f);

		}

	}

	private void playSound(Player p, Sound s, float pitch, float volume) {

		p.playSound(p.getLocation(), s, pitch, volume);

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
			help(p, "/bb config <save|load>");
		}

		msg(p, HORIZONTAL_BAR);

	}

	private void msg(Player p, String msg) {

		p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));

	}

}
