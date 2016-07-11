package com.rayzr522.battlebricks;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/* 
 * BBListener.java
 * Made by Rayzr522
 * Date: Jul 11, 2016
 */
public class BBListener implements Listener {

	private BattleBricks plugin;

	public BBListener(BattleBricks plugin) {

		this.plugin = plugin;

	}

	@EventHandler
	public void onPlayerLogin(PlayerJoinEvent e) {

		if (!PlayerData.hasData(e.getPlayer())) {

			PlayerData.createData(e.getPlayer());

		}

	}

}
