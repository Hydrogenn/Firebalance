package hydrogenn.notes;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandStamp implements CommandExecutor {
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
        	Player player = (Player) sender;
			ItemStack paper = player.getInventory().getItemInMainHand();
			ItemStack stamp = player.getInventory().getItemInOffHand();
            ItemMeta stampMeta = stamp.getItemMeta();
            if (paper.getType() == Material.PAPER && stamp.getType() == Material.WOOD_BUTTON)
            	paper.setItemMeta(stampMeta);
            else
            	player.sendMessage("You need to hold the stamp in the offhand and paper in the main hand.");
        }
        return true;
    }
}