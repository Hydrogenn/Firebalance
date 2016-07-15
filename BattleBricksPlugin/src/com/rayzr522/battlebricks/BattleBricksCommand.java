package com.rayzr522.battlebricks;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.rayzr522.battlebricks.utils.ActionBarUtil;

/* 
 * BattleBricksCommand.java
 * Made by Rayzr522
 * Date: Jul 11, 2016
 */
public class BattleBricksCommand implements CommandExecutor {

	public static HashMap<Competitor, Competitor> requests = new HashMap<Competitor, Competitor>();
	public static HashMap<Competitor, Integer> timeouts = new HashMap<Competitor, Integer>();

	private static final String HORIZONTAL_BAR = ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH
			+ "----------------------------------------------------";
	private static final String HELP_PREFIX = "&7&l|&b ";

	private static final String NO_PERMISSION = "&cYou don't have permission to do that!";

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

				if (requests.containsKey(p1)) {

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

								if (!(p1.isValid() && p2.isValid())) {

									msg(p, "&cSomething went wrong...");
									msg(other, "&cSomething went wrong...");

								}
								
								requests.put(p1, p2);

								Timed.message(0, "&eStarting battle in 5 seconds...", p, other);
								Timed.message(3, "&c3...", p, other);
								Timed.message(4, "&c2...", p, other);
								Timed.message(5, "&c1...", p, other);
								Timed.message(6, "&eBEGIN FIGHT!", p, other);

								Timed.runnable(6, new BukkitRunnable() {

									@Override
									public void run() {

										p1.setFighting(true);
										requests.remove(findCompetitor(other));
										p2.setFighting(true);
										requests.put(p2, p1);
										updateActionBar(p1);

									}

								});
								
								/*Timed.message(55, "&c5...", p, other);
								Timed.message(56, "&c4...", p, other);
								Timed.message(57, "&c3...", p, other);
								Timed.message(58, "&c2...", p, other);
								Timed.message(59, "&c1...", p, other);

								Timed.runnable(60, new BukkitRunnable() {

									@Override
									public void run() {

										fightComplete(p1, p2);

									}

								});*/

							}

						} else {

							p1.msg("&bSent fight request to &9" + p2.getName() + "&b.");
							p2.msg("&9" + p1.getName() + "&b would like to battle you.");
							p2.msg("&bTo accept, do /bb fight &9" + p1.getName());
							p2.msg("&bThis request will timeout in 2 minutes");

							requests.put(p1, p2);
							startTimeout(p1);

						}

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
	public static void removeFromLists(Competitor p) {

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
	 * Called after the 5 seconds of fighting
	 * 
	 * @param p1
	 *            = the first competitor
	 * @param p2
	 *            = the second competitor
	 */
	public static void fightComplete(Competitor p1, Competitor p2) {

		long score1 = p1.getDamage()*1000/p1.getHealth();
		long score2 = p2.getDamage()*1000/p2.getHealth();

		long level1 = p1.getBrick().getLevel();
		long level2 = p2.getBrick().getLevel();

		if (score1 == score2) {

			p1.msg("&bIt was a tie!");
			p2.msg("&bIt was a tie!");

			playSound(p1.getPlayer(), Sound.ENTITY_CREEPER_DEATH, 1.0f, 1.0f);
			playSound(p2.getPlayer(), Sound.ENTITY_CREEPER_DEATH, 1.0f, 1.0f);

		} else if (score1 < score2) {

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

		removeFromLists(p1);
		removeFromLists(p2);
		
	}

	public static void playSound(Player p, Sound s, float pitch, float volume) {

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
	
	public static boolean isOnFight(Player p) {
		Competitor c = findCompetitor(p);
		if (c!=null && c.isFighting()) return true;
		else return false;
	}
	
	public static Competitor findCompetitor(Player p) {
		for(Competitor c: requests.keySet()) {
			if (c.getPlayer()==p) return c;
		}
		return null;
	}

	public static void hit(Competitor c) {
		c.newThrow();
		if (c.mustRecover()) c.recover();
		else requests.get(c).takeHit();
	}

	public static void miss(Competitor c) {
		c.miss();
	}
	
	public static void updateActionBar(Competitor c) {
		//TODO clean this up a bit; functional but seriously ugly
		Competitor c2 = requests.get(c);
		String message = c.nextIsLeft() ? "LEFT" : "RIGHT";
		String messageAlt = c2.nextIsLeft() ? "LEFT" : "RIGHT";
		if (c.mustRecover() && c2.mustRecover()) {
			message=ChatColor.YELLOW + Strings.repeat('!', c.getRecovery()) + " : " + message + ChatColor.YELLOW + " : " + Strings.repeat('!', c2.getRecovery());
			messageAlt=ChatColor.YELLOW + Strings.repeat('!', c2.getRecovery()) + " : " + messageAlt + ChatColor.YELLOW + " : " + Strings.repeat('!', c.getRecovery());
		}
		else if (c.mustRecover()) {
			message=ChatColor.RED + Strings.repeat('!', c.getRecovery()) + " : " + message + ChatColor.RED + " : x" + c.getCombo();
			messageAlt=ChatColor.GREEN + " : x" + c.getCombo() + " : " + messageAlt + ChatColor.GREEN + " : " + Strings.repeat('!', c.getRecovery());
		} else if (c2.mustRecover()) {
			message=ChatColor.GREEN + " : x" + c2.getCombo() + " : " + message + ChatColor.GREEN + " : " + Strings.repeat('!', c2.getRecovery());
			messageAlt=ChatColor.RED + Strings.repeat('!', c2.getRecovery()) + " : " + messageAlt + ChatColor.RED + " : x" + c2.getCombo();
		} else {
			message=ChatColor.WHITE + "x1 : " + message + ChatColor.WHITE + " : x1";
			messageAlt=ChatColor.WHITE + "x1 : " + messageAlt + ChatColor.WHITE + " : x1";
		}
		
		message = ChatColor.WHITE+c.getBrick().getItemMeta().getDisplayName()+ChatColor.WHITE+" : "+message+ChatColor.WHITE+" : "+c2.getBrick().getItemMeta().getDisplayName();
		messageAlt = ChatColor.WHITE+c2.getBrick().getItemMeta().getDisplayName()+" : "+messageAlt+ChatColor.WHITE+" : "+c.getBrick().getItemMeta().getDisplayName();
		
		message = ChatColor.GREEN+c.getHealthBar(true) + " : " +message+ ChatColor.GREEN+" : " + c2.getHealthBar(false);
		messageAlt = ChatColor.GREEN+c2.getHealthBar(true) + " : " +messageAlt+ ChatColor.GREEN+" : " + c.getHealthBar(false);
		
		ActionBarUtil.sendActionBar(message,c.getPlayer());
		ActionBarUtil.sendActionBar(messageAlt,c2.getPlayer());
	}
	

}
