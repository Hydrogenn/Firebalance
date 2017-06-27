package hydrogenn.kingdoms.utils.parseCommand;

import java.util.Arrays;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import hydrogenn.kingdoms.utils.TextUtils;

/**
 * Created by Rayzr522 on 7/4/16.
 */
public class ParseName extends ParseCommand {

	private List<String> descriptors = Arrays.asList("named", "with name", "name");

	public List<String> getDescriptors() {
		return descriptors;
	}

	public ItemStack apply(ItemStack base, String args) {

		if (args.length() <= 0) {
			return base;
		}

		String name = TextUtils.colorize(args);

		ItemMeta meta = base.getItemMeta();

		meta.setDisplayName(name);

		base.setItemMeta(meta);

		return base;

	}

}
