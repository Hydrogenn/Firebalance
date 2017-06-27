package hydrogenn.kingdoms.utils.parseCommand;

import java.util.List;

import org.bukkit.inventory.ItemStack;

/**
 * Created by Rayzr522 on 7/4/16.
 */
public abstract class ParseCommand {

	public abstract List<String> getDescriptors();

	public abstract ItemStack apply(ItemStack base, String args);

}
