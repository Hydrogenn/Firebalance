package com.rayzr522.battlebricks;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/* 
 * Timed.java
 * Made by Rayzr522
 * Date: Jul 11, 2016
 */
public class Timed {

	private static BattleBricks plugin;

	public static void init(BattleBricks plugin) {
		Timed.plugin = plugin;
	}

	public static void message(double seconds, String message, Player... players) {

		new BukkitRunnable() {

			@Override
			public void run() {
				for (Player player : players) {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
				}
			}

		}.runTaskLaterAsynchronously(plugin, (long) (seconds * 20));

	}

	public static void messageSynced(double seconds, String message, Player... players) {

		new BukkitRunnable() {

			@Override
			public void run() {
				for (Player player : players) {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
				}
			}

		}.runTaskLater(plugin, (long) (seconds * 20));

	}

	public static void runnable(double seconds, BukkitRunnable runnable) {

		runnable.runTaskLaterAsynchronously(plugin, (long) (seconds * 20));

	}

	public static void runnableSynced(double seconds, BukkitRunnable runnable) {

		runnable.runTaskLater(plugin, (long) (seconds * 20));

	}

}
