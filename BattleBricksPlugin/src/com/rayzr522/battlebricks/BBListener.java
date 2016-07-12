package com.rayzr522.battlebricks;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

/* 
 * BBListener.java
 * Made by Rayzr522
 * Date: Jul 11, 2016
 */
public class BBListener implements Listener {

	public BBListener() {

	}

	@EventHandler
	public void onPlayerLogin(PlayerJoinEvent e) {

		if (!PlayerData.hasData(e.getPlayer())) {

			PlayerData.createData(e.getPlayer());

		}

	}

	@EventHandler
	public void onPlayerCrouch(PlayerToggleSneakEvent e) {

		if (BattleBricksCommand.twerkers.containsKey(e.getPlayer())) {

			Player p = e.getPlayer();
			BattleBricksCommand.twerkers.put(p, BattleBricksCommand.twerkers.get(p) + 1);

		}

	}

	@EventHandler
	public void onPlayerLogout(PlayerQuitEvent e) {

		ConfigManager.save(e.getPlayer());

	}

	@EventHandler
	public void onPlayerPreCraft(PrepareItemCraftEvent e) {

		CraftingInventory inv = e.getInventory();

		ItemStack result = inv.getResult();

		if (result.equals(BrickItem.PLACEHOLDER)) {

			inv.setResult(BrickItem.createItem());
			return;

		} else if (result.equals(BrickItem.PLACEHOLDER_2)) {

			BrickItem brick = null;

			for (ItemStack item : inv.getMatrix()) {
				if (BrickItem.isValid(item)) {
					brick = BrickItem.fromItem(item);
				}
			}

			if (brick != null) {
				brick.addXp(10);
				inv.setResult(brick);
			} else {
				inv.setResult(null);
			}

			return;

		} else if (result.equals(BrickItem.PLACEHOLDER_3)) {

			BrickItem brick = null;

			for (ItemStack item : inv.getMatrix()) {
				if (BrickItem.isValid(item)) {
					brick = BrickItem.fromItem(item);
				}
			}

			if (brick != null) {
				brick.addXp(90);
				inv.setResult(brick);
			} else {
				inv.setResult(null);
			}

			return;

		}

	}

	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent e) {

	}

}
