package hydrogenn.notes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandNoteSign implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			ItemStack paper = player.getInventory().getItemInMainHand();
			ItemMeta paperMeta = paper.getItemMeta();
			if ((paper.getType() != Material.PAPER && paper.getType() != Material.WOOD_BUTTON)
					|| paperMeta.getDisplayName() == null) {
				player.sendMessage("You can only sign named papers.");
			} else {
				List<String> lore = new ArrayList<>();
				boolean sign = true;
				if (paperMeta.getLore() != null) {
					lore.addAll(paperMeta.getLore());
					for (int i = 0; i < paperMeta.getLore().size(); i++) {
						if (paperMeta.getLore().get(i).contains(player.getName())) {
							player.sendMessage("You've already signed this in the past.");
							sign = false;
						}
					}
				}
				if (sign == true) {
					lore.add("§7§o" + player.getName());
					paperMeta.setLore(lore);
					paper.setItemMeta(paperMeta);
				}
			}
		}
		// If the player (or console) uses our command correct, we can return
		// true
		// Returning false will imply that the command is invalid and will
		// display the usage to the player.
		return true;
	}
}
