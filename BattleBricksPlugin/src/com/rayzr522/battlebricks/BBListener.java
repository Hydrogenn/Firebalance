package com.rayzr522.battlebricks;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

/* 
 * BBListener.java
 * Made by Rayzr522
 * Date: Jul 11, 2016
 */
/*
 * Basically completely rewritten by Hydrogenn
 * In case you wanted to know ;)
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
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getAction()==Action.PHYSICAL || !BattleBricksCommand.isOnFight(e.getPlayer())) return;
		Competitor comp = BattleBricksCommand.findCompetitor(e.getPlayer());
		boolean isLeft = e.getAction()==Action.LEFT_CLICK_AIR || e.getAction()==Action.LEFT_CLICK_BLOCK;
		if (comp.nextIsLeft()==isLeft) BattleBricksCommand.hit(comp);
		else {
			BattleBricksCommand.miss(comp);
		}
		e.setCancelled(true);
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
	public void onInventoryClick(InventoryClickEvent e) {
		
	}
	
	@EventHandler
	public void onPlayerChangeItem(PlayerItemHeldEvent e) {
		Competitor comp = BattleBricksCommand.findCompetitor(e.getPlayer());
		if (comp==null) return;
		if (BattleBricksCommand.isOnFight(e.getPlayer())) {
			comp.getPlayer().sendMessage("The fight was cancelled.");
			BattleBricksCommand.requests.get(comp).getPlayer().sendMessage("The fight was cancelled.");
			BattleBricksCommand.requests.remove(BattleBricksCommand.requests.get(comp));
			BattleBricksCommand.requests.remove(comp);
		}
		else {
			comp.getPlayer().sendMessage("Your request has been cancelled.");
		}
		BattleBricksCommand.requests.remove(comp);
	}

}
