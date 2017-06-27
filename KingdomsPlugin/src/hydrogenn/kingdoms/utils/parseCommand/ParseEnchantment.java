package hydrogenn.kingdoms.utils.parseCommand;

import java.util.Arrays;
import java.util.List;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import hydrogenn.kingdoms.utils.ArrayUtils;

/**
 * Created by Rayzr522 on 7/4/16.
 */
public class ParseEnchantment extends ParseCommand {

	private List<String> descriptors = Arrays.asList("with enchantment", "with enchant", "enchanted with", "enchant");

	public List<String> getDescriptors() {
		return descriptors;
	}

	public ItemStack apply(ItemStack base, String args) {

		String[] enchants = args.split("and|&");

		for (String enchString : enchants) {

			String[] separate = enchString.split(" ");
			int level = 0;
			boolean customLevel = false;

			if (separate.length > 1) {
				try {
					level = Integer.parseInt(separate[separate.length - 1]);
					customLevel = true;
				} catch (Exception e) {
					level = 1;
				}
			} else {
				level = 1;
			}

			String enchantName = (customLevel ? ArrayUtils.concatArray(ArrayUtils.removeLast(separate), " ")
					: enchString).trim().replace(" ", "_");

			Enchantment enchant = Enchantment.getByName(enchantName.toUpperCase());

			if (enchant == null) {

				System.out.println("Unknown enchantment '" + enchantName + "'");
				continue;

			}

			base.addUnsafeEnchantment(enchant, level);

		}

		return base;
	}
}
