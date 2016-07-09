package hydrogenn.firebalance.utils;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import hydrogenn.firebalance.utils.parseCommand.ParseCommand;
import hydrogenn.firebalance.utils.parseCommand.ParseEnchantment;
import hydrogenn.firebalance.utils.parseCommand.ParseName;

/**
 * Created by Rayzr522 on 7/4/16.
 */
public class ItemUtils {

	private static List<ParseCommand> parsers = Arrays.asList(new ParseEnchantment(), new ParseName());

	public static final ItemStack ERROR = new ItemStack(Material.BARRIER, 0);

	public static ItemStack enchantItem(ItemStack base, Enchant... enchantments) {

		for (Enchant ench : enchantments) {

			base.addEnchantment(ench.getType(), ench.getLevel());

		}

		return base;

	}

	public static class Enchant {

		private int level;
		private Enchantment type;

		public Enchant(Enchantment type, int level) {
			this.level = level;
			this.type = type;
		}

		public int getLevel() {
			return level;
		}

		public Enchantment getType() {
			return type;
		}

	}

	public static ItemStack makeItem(String description) {

		String[] statements = description.split(",");

		if (statements.length < 1) {

			System.out.println("Requires more words");
			return ERROR;

		}

		String typeString = statements[0].trim();

		int amount = 0;

		try {
			amount = Integer.parseInt(typeString.split(" ")[0]);
			typeString = typeString.replaceFirst("^[0-9]+", "");
		} catch (Exception e) {
			amount = 1;
		}

		typeString = typeString.trim();

		Material type = null;

		try {

			type = Material.valueOf(typeString.replace(" ", "_").toUpperCase());

		} catch (Exception e) {

			System.out.println("Invalid type '" + typeString + "'");
			return ERROR;

		}

		ItemStack output = new ItemStack(type, amount);

		for (int i = 1; i < statements.length; i++) {

			String statement = statements[i].trim();

			for (ParseCommand cmd : parsers) {

				for (String str : cmd.getDescriptors()) {

					if (statement.startsWith(str)) {

						output = cmd.apply(output, statement.replaceFirst(str, "").trim());
						break;

					}

				}

			}

		}

		return output;

	}

	public static ItemStack setName(ItemStack item, String name) {

		if (name == null) {
			return item;
		}

		ItemMeta im = item.getItemMeta();
		im.setDisplayName(TextUtils.colorize(name));
		item.setItemMeta(im);

		return item;

	}

	public static ItemStack setLore(ItemStack item, String... lore) {

		if (lore != null) {

		}

		ItemMeta im = item.getItemMeta();
		im.setLore(TextUtils.colorizeMultiple(lore));
		item.setItemMeta(im);

		return item;

	}

	public static ItemStack addLore(ItemStack item, String lore) {

		if (lore != null) {

		}

		ItemMeta im = item.getItemMeta();

		if (!im.hasLore()) {
			return setLore(item, lore);
		}

		im.getLore().add(TextUtils.colorize(lore));

		item.setItemMeta(im);

		return item;

	}

}
