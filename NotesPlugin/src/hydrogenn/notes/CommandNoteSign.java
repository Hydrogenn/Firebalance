package hydrogenn.notes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
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
		if (!(sender instanceof Player))
			return true;
		
		Player player = (Player) sender;
		ItemStack paper = player.getInventory().getItemInMainHand();
		ItemMeta paperMeta = paper.getItemMeta();
		
		if ((paper.getType() != Material.PAPER && paper.getType() != Material.WOOD_BUTTON)
				|| paperMeta.getDisplayName() == null) {
			player.sendMessage("You can only sign named papers.");
		} else {
			List<String> lore = new ArrayList<>();
			
			if (paperMeta.getLore() != null) {
				lore.addAll(paperMeta.getLore());
				if (isSigned(player.getName(), lore)) {
					player.sendMessage("You've signed this before.");
					return true;
				}
			}
			
			lore.add(ChatColor.GRAY.toString() + ChatColor.ITALIC + player.getName());
			paperMeta.setLore(lore);
			paper.setItemMeta(paperMeta);
		}
		return true;
	}
	
	private boolean isSigned(String username, List<String> description) {
		for (int i = 0; i < description.size(); i++) {
			if (description.get(i).contains(ChatColor.GRAY.toString() + ChatColor.ITALIC + username)) {
				return true;
			}
		}
		return false;
	}
}
