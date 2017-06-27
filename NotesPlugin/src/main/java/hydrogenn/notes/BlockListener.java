package main.java.hydrogenn.notes;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener {
    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        // Check if the player is placing a blacklisted block
        if (event.getBlock().getType() == Material.WOOD_BUTTON && event.getItemInHand().getItemMeta().hasDisplayName()) {
            event.setCancelled(true);
        }
    }
}
