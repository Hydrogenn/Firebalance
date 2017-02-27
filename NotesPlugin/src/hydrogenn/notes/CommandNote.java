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

public class CommandNote implements CommandExecutor {
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length<1) return false;
        if (args[0].equals("desc") && (args.length<2 ||
        		(!args[1].equals("set") &&
        		!args[1].equals("add") &&
        		!args[1].equals("remove")))) return false;
        
		if (!(sender instanceof Player))
			return true;
		
        Player player = (Player) sender;
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item==null) {
			player.sendMessage("Hey, you're kind of forgetting something. The paper?");
			return true;
		}
        ItemMeta itemMeta = item.getItemMeta();
		ChatColor prefix = ChatColor.WHITE; //TODO implement color prefixes
		if (itemMeta.getLore()!=null) {
			for (int i=0; i < itemMeta.getLore().size();i++) {
				if (itemMeta.getLore().get(i).contains(ChatColor.GRAY.toString() + ChatColor.ITALIC)) {
       				player.sendMessage("This has been signed. No further changes can be made.");
       				return true;
       		 	}
			}
		}
        if (item.getType() != Material.PAPER && item.getType() != Material.WOOD_BUTTON)
        	player.sendMessage("You probably don't want to try writing on that.");
        else if (itemMeta.getDisplayName() == null && args[0].equals("desc"))
        	player.sendMessage("You can only write on named paper.");
        else {
        	StringBuilder builder = new StringBuilder();
        	for (String string : args) {
        	    if (builder.length() > 0) {
        	        builder.append(" ");
        	    }
        	    builder.append(string);
        	}
        	builder.delete(0, args[0].length()+1);
        	if (args[0].equals("name")) {
            	builder.delete(0, 5);
        		if (args.length < 2) {
	        		itemMeta.setDisplayName(null);
	            }
	        	else {
	        		itemMeta.setDisplayName(prefix+builder.toString());
	        	}
	            itemMeta.setLore(null);
	            item.setItemMeta(itemMeta);
        	}
        	else if (args[0].equals("desc")) {
        		builder.delete(0, args[1].length()+1);
        		List<String> lore = new ArrayList<>();
	        	if (itemMeta.getLore() != null) {
	        		lore.addAll(itemMeta.getLore());
	        	}
            	if (args[1].equals("set")) {
            		if (lore.size() > 0) {
            			lore.remove(lore.size()-1);
            		}
            	}
            	if (args[1].equals("remove")) { //TODO test if this breaks without arguments on blank paper
            		try {
            			for (int i = lore.size()-Integer.parseInt(args[2]); i>=0 && i<itemMeta.getLore().size();) {
            				lore.remove(i);
            			}
            		} catch (NumberFormatException e) {
            			lore.remove(lore.size()-1);
            		} catch (ArrayIndexOutOfBoundsException e) {
            			lore.remove(lore.size()-1);
            		}
            		
            	}
            	else {
            		lore.add(prefix+builder.toString());
            	}
	            itemMeta.setLore(lore);
	            item.setItemMeta(itemMeta);
        	}
        }
        return true;
    }
}
