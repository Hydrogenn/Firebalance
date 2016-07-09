package hydrogenn.locks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import hydrogenn.firebalance.ChestSpec;
import hydrogenn.firebalance.utils.Messenger;

public class LocksListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void customCrafting(PrepareItemCraftEvent e) {
		ItemStack r = e.getInventory().getResult();
		if (r.getType() == Material.TRIPWIRE_HOOK && r.getItemMeta().getDisplayName() != null
				&& r.getItemMeta().getDisplayName().contains(ChatColor.WHITE + "Key")) {
			// Do all the universal variable declarations
			boolean success = true;
			String dn = e.getInventory().getResult().getItemMeta().getDisplayName();
			ItemStack result = new ItemStack(Material.TRIPWIRE_HOOK);
			ItemMeta resultMeta = result.getItemMeta();
			List<String> keyLore = new ArrayList<>();
			keyLore.add(ChatColor.GRAY + "");
			resultMeta.setLore(keyLore);
			resultMeta.setDisplayName(ChatColor.WHITE + "Key");
			// Set the amount to double if it's a dupe function
			if (dn.equals(ChatColor.WHITE + "KeyD"))
				result.setAmount(2);
			// Show that a new key is being crafted if one is
			if (dn.equals(ChatColor.WHITE + "KeyC")) {
				resultMeta.setDisplayName(ChatColor.GOLD + "New Key");
				List<String> resultLore = new ArrayList<>();
				resultLore.add("****");
				resultMeta.setLore(resultLore);
			}
			// Run the item loop if it's not a craft function
			if (!dn.equals(ChatColor.WHITE + "KeyC"))
				for (ItemStack s : e.getInventory()) {
					if (s != null) {
						if (s.getType() == Material.TRIPWIRE_HOOK && s.getItemMeta().getLore() != null) {
							String keyID = s.getItemMeta().getLore().get(0).replace(ChatColor.GRAY + "", "");
							String oldKeyID = resultMeta.getLore().get(0);
							if (oldKeyID.length() < 18) {
								List<String> resultLore = resultMeta.getLore();
								resultLore.set(0, oldKeyID + keyID);
								resultMeta.setLore(resultLore);
							}

						} else if (dn.equals(ChatColor.WHITE + "KeyD") && s.getType() == Material.TRIPWIRE_HOOK)
							result.setAmount(1);
						else if (s.getType() == Material.TRIPWIRE_HOOK)
							success = false;
					}
				}
			// Change the result to what is intended
			if (!success) {
				resultMeta.setDisplayName("Nice try, pal!");
				resultMeta.setLore(null);
			}

			result.setItemMeta(resultMeta);
			e.getInventory().setResult(result);
		}

	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		ItemStack i = event.getCurrentItem();
		if (event.getResult() == Result.ALLOW && i != null && i.getType() == Material.TRIPWIRE_HOOK && i.hasItemMeta()
				&& i.getItemMeta().hasDisplayName()
				&& i.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "New Key")) {
			ItemMeta im = i.getItemMeta();
			im.setDisplayName(ChatColor.WHITE + "Key");
			Random rand = new Random();
			String randid = String.format("%04x", rand.nextInt(65536));
			List<String> resultLore = new ArrayList<>();
			resultLore.add(ChatColor.GRAY + randid);
			im.setLore(resultLore);
			i.setItemMeta(im);
			event.setCurrentItem(i);
		}
	}

	@EventHandler
	public void onInteractBlock(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		if (event.getClickedBlock().getType() == Material.CHEST
				|| event.getClickedBlock().getType() == Material.TRAPPED_CHEST) {

			Player player = event.getPlayer();
			Block chest = event.getClickedBlock();

			String chestId = null;
			ItemStack key = event.getItem();
			ItemMeta keyMeta = null;
			if (key != null)
				keyMeta = key.getItemMeta();

			boolean isKey = key.getType() == Material.TRIPWIRE_HOOK && keyMeta.hasDisplayName()
					&& keyMeta.getDisplayName().equals(ChatColor.WHITE + "Key");

			for (ChestSpec s : ChestSpec.list) {
				if (s.getCoords().equals(chest.getLocation())) {
					chestId = s.getId();
					if (chestId.length() < 1) {
						chestId = null;
						return;
					}
				}
			}

			if (chestId != null) {
				try {

					if (keyMeta.getLore().get(0).contains(chestId)) {
					} else {
						Messenger.send(player,
								"&cThis chest is locked with id " + chestId.substring(0, chestId.length() * 3 / 4)
										+ "****".substring(0, chestId.length() / 4));
						event.setCancelled(true);
						if (chest.getType() == Material.TRAPPED_CHEST) {
							// TODO allow the device to emit a single redstone
							// pulse
						}
					}
				} catch (NullPointerException e) {
					Messenger.send(player,
							"&cThis chest is locked with id " + chestId.substring(0, chestId.length() * 3 / 4)
									+ "****".substring(0, chestId.length() / 4));
					event.setCancelled(true);
				}
			} else if (isKey && !player.isSneaking()) {
				ChestSpec.list.add(
						new ChestSpec(chest.getLocation(), keyMeta.getLore().get(0).replace(ChatColor.GRAY + "", "")));
				event.setCancelled(true);
			}
			if (key != null & keyMeta != null)
				key.setItemMeta(keyMeta);
		}
	}

	@EventHandler
	public void onBreakBlock(BlockBreakEvent event) {

		if (!event.isCancelled()) {
			for (Iterator<ChestSpec> i = ChestSpec.list.iterator(); i.hasNext();) {
				ChestSpec s = (ChestSpec) i.next();
				if (s.getCoords().equals(event.getBlock().getLocation())) {
					i.remove();
					Messenger.send(event.getPlayer(), "Chest lock removed");
				}
			}
		}

	}

	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent event) {

		// Check if the player is placing a blacklisted block
		if (event.getItemInHand().getItemMeta().getDisplayName() != null) {
			if (event.getBlock().getType() == Material.TRIPWIRE_HOOK
					&& event.getItemInHand().getItemMeta().getLore() != null) {
				event.setCancelled(true);
				return;
			}
		}

	}

}
