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

public class CommandNoteUnsign implements CommandExecutor {
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
			ItemStack paper = player.getInventory().getItemInMainHand();
            ItemMeta paperMeta = paper.getItemMeta();
            if ((paper.getType() != Material.PAPER && paper.getType() != Material.WOOD_BUTTON) || paperMeta.getDisplayName() == null) {
            	//player.sendMessage("...why? WHY? It doesn't even... I don't...");
            	//player.sendMessage("I have a question. Can you sign this? No. Can you unsign something you didn't sign? §lNO.");
            	player.sendMessage("I don't even want to explain why that wouldn't work.");
            	//player.sendMessage("You can't write on that. Well, the server would crash if you did.");
            	//player.sendMessage("Nobody wants you to do that. So basically? Don't.");
            	//player.sendMessage("Logic? What's logic? §7§o-You");
            	//player.sendMessage("It was at that moment that you realize your arms can't hold a pen.");
            }
            else {
	        	int sign = -1;
	        	List<String> lore = new ArrayList<>();
	        	if (paperMeta.getLore() != null) {
	        		lore.addAll(paperMeta.getLore());
		        	for (int i=0; i < paperMeta.getLore().size();i++) {
		        		 if (paperMeta.getLore().get(i).equals("§7§o"+player.getName())) {
		        			 sign = i;
		        		 }
		        	}
	        	}
	        	if (sign > -1) {
	        		lore.set(sign, "§7§o§m"+player.getName());
		            paperMeta.setLore(lore);
		            paper.setItemMeta(paperMeta);
	        	}
	        	else {
	        		player.sendMessage("You don't have a valid signature here.");
	        	}
	        }
        }
        // If the player (or console) uses our command correct, we can return true
        // Returning false will imply that the command is invalid and will display the usage to the player.
        return true;
    }
}
