package hydrogenn.firebalance;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class CommandAnonymBook implements CommandExecutor {
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length<1) return false;
		if (sender instanceof Player) {
			Player player = (Player) sender;
			ItemStack book = player.getInventory().getItemInMainHand();
			StringBuilder builder = new StringBuilder();
        	for (String string: args) {
        	    if (builder.length() > 0) {
        	        builder.append(" ");
        	    }
        	    builder.append(string);
        	}
        	if (builder.toString().length()>16) {
        		player.sendMessage("Name too long, shorten plz");
        		return true;
        	}
			if (book.getType() == Material.BOOK_AND_QUILL) {
				BookMeta bookMeta = (BookMeta) book.getItemMeta();
				bookMeta.setAuthor("§kMr. Man");
				bookMeta.setTitle(builder.toString());
				book.setType(Material.WRITTEN_BOOK);
				book.setItemMeta(bookMeta);
			}
		}
		return true;
	}
}