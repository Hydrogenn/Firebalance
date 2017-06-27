package hydrogenn.beacon;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.BeaconInventory;

import hydrogenn.beacon.file.BeaconSpec;
import hydrogenn.beacon.lib.TimeLib;

public class BeaconListener implements Listener {
	
	//TODO add an InventoryClickEvent to register to the existence of a beacon modifier here.
	@EventHandler
	public static void startBeacon(InventoryCloseEvent e) {
		if (e.getInventory().getType() == InventoryType.BEACON) {
			BeaconInventory bInv = (BeaconInventory) e.getInventory();
			if (bInv.getItem() != null) {
				long expiration = BeaconSpec.update(bInv.getLocation(), bInv.getItem(), e.getPlayer().getUniqueId());
				bInv.setItem(null);
				e.getPlayer().sendMessage("The beacon will now last "+TimeLib.breakdownFromNow(expiration)+".");
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public static void placeBlockInRange(BlockPlaceEvent e) {
		if (blockModifyEvent(e)) {
			PlayerMessage.PROTECTED.send(e.getPlayer());
		}
	}

	@EventHandler(ignoreCancelled = true)
	public static void beaconPlaced(BlockPlaceEvent e) {
		if (e.getBlock().getType() == Material.BEACON) {
			BeaconSpec.cache(e.getBlock(), e.getPlayer());
			PlayerMessage.NEW_BEACON.send(e.getPlayer());
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public static void breakBlockInRange(BlockBreakEvent e) {
		if (blockModifyEvent(e)) {
			PlayerMessage.PROTECTED.send(e.getPlayer());
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public static void beaconDestroyed(BlockBreakEvent e) {
		if (e.getBlock().getType() == Material.BEACON && !BeaconSpec.isActive(e.getBlock().getLocation())) {
			BeaconSpec.remove(e.getBlock());
		}
	}

	@EventHandler(ignoreCancelled = true)
	public static void blockExplodeInRange(EntityExplodeEvent e) {
		List<Block> blockList = e.blockList();
		Iterator<Block> iter = blockList.iterator();
		while (iter.hasNext()) {
			Block block = iter.next();
			if (BeaconSpec.isProtected(block.getLocation())) {
				iter.remove();
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public static void blockFlowInRange(BlockFromToEvent e) {
		e.setCancelled(BeaconSpec.isProtected(e.getToBlock().getLocation()));
	}
	
	@EventHandler(ignoreCancelled = true)
	public static void entityChangeInRange(EntityChangeBlockEvent e) {
		e.setCancelled(BeaconSpec.isProtected(e.getBlock().getLocation()));
	}
	
	@EventHandler(ignoreCancelled = true)
	public static void bucketUsedInRange(PlayerBucketEmptyEvent e) {
		e.setCancelled(BeaconSpec.isProtected(e.getBlockClicked().getLocation()));
		if (e.isCancelled()) {
			PlayerMessage.PROTECTED.send(e.getPlayer());
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public static void bucketUsedInRange(PlayerBucketFillEvent e) {
		e.setCancelled(BeaconSpec.isProtected(e.getBlockClicked().getLocation()));
		if (e.isCancelled()) {
			PlayerMessage.PROTECTED.send(e.getPlayer());
		}
	}
	
	private static <CancellableBlockEvent extends BlockEvent & Cancellable> boolean blockModifyEvent(CancellableBlockEvent e) {
		e.setCancelled(BeaconSpec.isProtected(e.getBlock().getLocation()));
		return e.isCancelled();
	}
	
	
}
