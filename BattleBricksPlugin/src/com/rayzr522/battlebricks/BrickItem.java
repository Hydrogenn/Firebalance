package com.rayzr522.battlebricks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.base.Strings;

/* 
 * BrickItem.java
 * Made by Rayzr522
 * Date: Jul 11, 2016
 */
/**
 * A BrickItem is an extension of {@link ItemStack} that also stores data such
 * as level and XP. To create a new BrickItem, call
 * {@link BrickItem#createItem()}
 * 
 * @author Rayzr522
 *
 */
public class BrickItem extends ItemStack {

	public static ItemStack PLACEHOLDER;
	public static ItemStack PLACEHOLDER_2;
	public static ItemStack PLACEHOLDER_3;

	static {
		PLACEHOLDER = new ItemStack(Material.CLAY_BRICK, 0);
		ItemMeta meta = PLACEHOLDER.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "PLACEHOLDER");
		PLACEHOLDER.setItemMeta(meta);

		PLACEHOLDER_2 = new ItemStack(Material.CLAY_BRICK, 0);
		ItemMeta meta2 = PLACEHOLDER_2.getItemMeta();
		meta2.setDisplayName(ChatColor.RED + "PLACEHOLDER_2");
		PLACEHOLDER_2.setItemMeta(meta2);

		PLACEHOLDER_3 = new ItemStack(Material.CLAY_BRICK, 0);
		ItemMeta meta3 = PLACEHOLDER_3.getItemMeta();
		meta3.setDisplayName(ChatColor.RED + "PLACEHOLDER_3");
		PLACEHOLDER_3.setItemMeta(meta3);
	}

	public static final List<String> BOY_NAMES = Arrays.asList("Bob", "Joe", "Seinfeld", "Pfaff", "Allen", "Jeff", "Gary", "Joshua",
			"Peter", "Nathan", "Henry", "Xavier", "Andrew", "Tony", "Doggo", "Kyle", "Karl", "Richard", "Metel", "Caleb", "Jacob",
			"Jenkins", "Matthew", "Stephen Hawking", "Steven", "Roy", "Donkey Kong", "Charizard", "Oliver", "&2Zombo", "Orteil",
			"Nicholas", "Jay", "Brandon", "Max", "Marx", "Ghandi", "Gandhi", "Garrett", "Noah", "Sam", "Pterodactyl");
	public static final List<String> GIRL_NAMES = Arrays.asList("Alice", "Kate", "Melissa", "Samantha", "Sarah",
			"Sally", "Beatrice", "Noelle", "Jessica", "Alexa", "Cosmo Wanda", "Jessica", "Ashley", "Kaitlin", "Chloe",
			"Katherine", "Alissa", "Alane", "Bridgette", "Hannah", "Kirby", "Amy", "Generic Brick Name", "Skeltal",
			"Type F to Pay Respects", "Taylor", "Marie", "Lynn", "Patricia");
	public static final List<String> RARE_NAMES = Arrays.asList("Pepe","Sample Text", "Slim Shady");

	public static final Enchantment BRICK_ENCHANT = Enchantment.PROTECTION_ENVIRONMENTAL;
	public static final String IDENTIFIER_LORE = ChatColor.translateAlternateColorCodes('&', "&b&r&1&c&k");

	public static final String SEPARATOR = ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + Strings.repeat("-", 40);

	private static Random rand = new Random();

	private long level = 1;
	private long nextLevel = 0;
	private long xp = 0;

	private UUID id;

	private BrickItem() {

		super(Material.CLAY_BRICK);

		id = UUID.randomUUID();

	}

	private BrickItem(ItemStack item) {

		super(item);

	}

	/**
	 * Create a new randomly named {@link BrickItem} and return it.
	 * 
	 * @return The new {@link BrickItem}.
	 */
	public static BrickItem createItem() {

		BrickItem brick = new BrickItem();

		brick.addUnsafeEnchantment(BRICK_ENCHANT, 10);

		ItemMeta meta = brick.getItemMeta();

		String name = randomName();
		if (RARE_NAMES.contains(name)) meta.setDisplayName(ChatColor.GOLD + name); else
		meta.setDisplayName(BOY_NAMES.contains(name) ? ChatColor.BLUE + name : ChatColor.LIGHT_PURPLE + name);

		brick.setItemMeta(meta);

		brick.updateNextLevel();

		brick.updateLore();

		return brick;

	}

	public static String randomName() {

		boolean boy = rand.nextBoolean();
		boolean isShiny = rand.nextInt(8191)==1;
		
		if (isShiny) return RARE_NAMES.get(rand.nextInt(RARE_NAMES.size()));
		return boy ? BOY_NAMES.get(rand.nextInt(BOY_NAMES.size())) : GIRL_NAMES.get(rand.nextInt(GIRL_NAMES.size()));

	}

	/**
	 * Update the lore of this item to correctly represent the level, XP and
	 * requirements for the next level in the lore, as well as storing the data
	 * persistently on a nearly invisible ({@link ChatColor#BLACK}) line of
	 * lore.
	 * 
	 */
	public void updateLore() {

		ItemMeta meta = getItemMeta();

		List<String> lore = new ArrayList<String>();

		lore.add(IDENTIFIER_LORE + ChatColor.BLACK + id.toString() + ":" + level + ":" + xp);

		lore.add(SEPARATOR);

		lore.add(ChatColor.GRAY + "Level: " + ChatColor.YELLOW + level);
		lore.add(ChatColor.GRAY + "XP: " + ChatColor.YELLOW + formatNumber(xp));
		lore.add(ChatColor.GRAY + "Next level: " + ChatColor.YELLOW + formatNumber(nextLevel));

		lore.add(SEPARATOR);

		meta.setLore(lore);

		setItemMeta(meta);

	}

	/**
	 * [Attempt to] translate an {@link ItemStack} into a {@link BrickItem} by
	 * reading its lore.
	 * 
	 * @param item
	 * @return The {@link BrickItem} as read from the {@link ItemStack}, null if
	 *         <code>item</code> was invalid, or a new {@link BrickItem} if it
	 *         failed to do {@link BrickItem#loadFromLore()
	 */
	public static BrickItem fromItem(ItemStack item) {

		if (!isValid(item)) {

			return null;

		}

		BrickItem brick = new BrickItem(item);
		brick.loadFromLore();

		return brick;

	}

	/**
	 * Whether or not <code>item</code> is a valid {@link BrickItem} (determined
	 * by item type & lore).
	 * 
	 * @param item
	 *            = the item to check.
	 * @return Whether or not the specified {@link ItemStack} is valid.
	 */
	public static boolean isValid(ItemStack item) {

		if (item == null || item.getType() != Material.CLAY_BRICK) {
			return false;
		}
		ItemMeta meta = item.getItemMeta();
		return meta.hasLore() && meta.getLore().get(0).startsWith(IDENTIFIER_LORE);

	}

	/**
	 * Loads all the data from an almost invisible ({@link ChatColor#BLACK})
	 * line of lore. If this runs into some problem it will essentially treat
	 * this as a new {@link BrickItem}.
	 */
	public void loadFromLore() {

		if (!isValid(this)) {
			return;
		}

		try {

			String data = ChatColor.stripColor(getLore(0));
			if (data == null || data == "") {
				updateNextLevel();
				updateLore();
				return;
			}
			String[] split = data.split(":");
			if (split.length != 3) {
				updateNextLevel();
				updateLore();
				return;
			}
			id = UUID.fromString(split[0]);
			level = Long.parseLong(split[1]);
			xp = Long.parseLong(split[2]);

			updateNextLevel();
			updateLore();

		} catch (Exception e) {
			System.err.println("Failed to load Battle Brick from lore");
			e.printStackTrace();
			updateNextLevel();
			updateLore();
		}

	}

	/**
	 * Gets the lore from a specific line. May cause an error if that line
	 * doesn't exist on the item.
	 * 
	 * @param line
	 *            = the line of lore you want to retrieve.
	 * @return The line of lore specified by <code>line</code>, or an empty
	 *         string if the item has no lore.
	 */
	public String getLore(int line) {
		return getItemMeta().hasLore() ? getItemMeta().getLore().get(line) : "";
	}

	/**
	 * Updates the <code>nextLevel</code> variable which is what is used to
	 * calculate the amount of XP require to level up.
	 * 
	 * @return the updated <code>nextLevel</code> value. Note: this is not just
	 *         returned but also stored onto the {@link BrickItem}.
	 */
	public long updateNextLevel() {

		return nextLevel = Math.round((Math.pow(10*level, 0.7)*0.3*level+7) * 2) * 5;

	}

	public long nextLevel() {

		return nextLevel;

	}

	public long getLevel() {
		return level;
	}

	public long getXp() {
		return xp;
	}

	public long setXp(long newXp) {
		return xp = newXp;
	}

	/**
	 * Add XP to the {@link BrickItem}, also has a check for whether you've
	 * leveled up once the XP changes.
	 * 
	 * @param amountToAdd
	 *            = the amount of XP to be added to this {@link BrickItem}.
	 * @return The new XP level.
	 */
	public long addXp(long amountToAdd) {

		xp += amountToAdd;

		while (xp >= nextLevel) {

			xp -= nextLevel;
			level++;
			updateNextLevel();

		}

		updateLore();

		return xp;

	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BrickItem) {
			BrickItem brick = (BrickItem) obj;
			return brick.id.equals(id);
		} else if (obj instanceof ItemStack) {
			ItemStack item = (ItemStack) obj;
			if (isValid(item)) {
				return equals(fromItem(item));
			}
			return super.equals(item);
		} else {
			return super.equals(obj);
		}
	}

	/**
	 * 
	 * Formats a large number by inserting commas, as is usually when writing
	 * numbers by hand.
	 * 
	 * @param num
	 *            = the number to format.
	 * @return A formated string representation of the number.
	 */
	public static String formatNumber(long num) {
		String in = num + "";
		if (num < 1000) {
			return in;
		}
		String out = in.substring(0, in.length() % 3);
		for (int i = in.length() % 3; i < in.length(); i += 3) {
			out += (i > 0 ? "," : "") + in.substring(i, i + 3 > in.length() ? in.length() : i + 3);
		}
		return out;
	}

	/**
	 * 
	 * Formats a large number by inserting commas, as is usually when writing
	 * numbers by hand.
	 * 
	 * No longer used, as I (Rayzr522) found a better method to do this: {@link BrickItem#formatNumber(long)}
	 * 
	 * @param num
	 *            = the number to format.
	 * @return a formated string representation of the number.
	 * 
	 */
	@Deprecated
	public static String largeNumber(double num) {

		String output = num + "";

		String[] split = output.split("\\.");

		String part1 = split[0];
		String part12 = part1.substring(part1.length() % 3);

		List<String> split2 = matches(part12, "[0-9]{3}");

		part1 = part1.substring(0, part1.length() % 3);

		for (String str : split2) {

			part1 += "," + str;

		}

		String part2 = split.length > 1 ? split[1] : "";

		output = part1 + (part2.length() > 0 ? "." + part2 : "");

		return output;

	}

	/**
	 * Returns all the matches to a certain regex contained within the input.
	 * 
	 * @param input
	 *            = the string to match.
	 * @param regex
	 *            = the regex to match the string with.
	 * @return A list of strings that matched the regex. The list can be empty
	 *         if no matches were found.
	 */
	public static List<String> matches(String input, String regex) {

		List<String> matches = new ArrayList<String>();

		String partial = "";

		while (input.length() > 0) {

			partial += input.substring(0, 1);
			input = input.substring(1, input.length());

			if (partial.matches(regex)) {

				matches.add(partial);
				partial = "";

			}

		}

		return matches;

	}

}
