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
public class BrickItem extends ItemStack {

	public static ItemStack PLACEHOLDER;
	static {
		PLACEHOLDER = new ItemStack(Material.CLAY_BRICK, 0);
		ItemMeta meta = PLACEHOLDER.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "PLACEHOLDER");
		PLACEHOLDER.setItemMeta(meta);
	}

	public static final List<String> BOY_NAMES = Arrays.asList("Bob", "Joe", "Alex", "Allen", "Jeff", "Garry", "Joshua",
			"Peter", "Nathan", "Henry");
	public static final List<String> GIRL_NAMES = Arrays.asList("Alice", "Kate", "Melissa", "Samantha", "Sarah",
			"Sally", "Beatrice");

	public static final Enchantment BRICK_ENCHANT = Enchantment.SILK_TOUCH;
	public static final String IDENTIFIER_LORE = ChatColor.translateAlternateColorCodes('&', "&b&r&1&c&k");

	public static final String SEPARATOR = ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + Strings.repeat("-", 40);

	private static Random rand = new Random();

	private long level = 1;
	private long nextLevel = 0;
	private long xp = 0;

	private UUID id;

	public BrickItem() {

		super(Material.CLAY_BRICK);

		id = UUID.randomUUID();

	}

	public BrickItem(ItemStack item) {

		super(item);

	}

	public static ItemStack createItem() {

		BrickItem brick = new BrickItem();

		brick.addUnsafeEnchantment(BRICK_ENCHANT, 10);

		ItemMeta meta = brick.getItemMeta();

		String name = randomName();
		meta.setDisplayName(BOY_NAMES.contains(name) ? ChatColor.BLUE + name : ChatColor.LIGHT_PURPLE + name);

		brick.setItemMeta(meta);

		brick.updateNextLevel();

		brick.updateLore();

		return brick;

	}

	public static String randomName() {

		boolean boy = rand.nextBoolean();

		return boy ? BOY_NAMES.get(rand.nextInt(BOY_NAMES.size())) : GIRL_NAMES.get(rand.nextInt(GIRL_NAMES.size()));

	}

	public void updateLore() {

		ItemMeta meta = getItemMeta();

		List<String> lore = new ArrayList<String>();

		lore.add(IDENTIFIER_LORE + ChatColor.BLACK + id.toString() + ":" + level + ":" + xp);

		lore.add(SEPARATOR);

		lore.add(ChatColor.GRAY + "Level: " + ChatColor.YELLOW + level);
		lore.add(ChatColor.GRAY + "XP: " + ChatColor.YELLOW + xp);
		lore.add(ChatColor.GRAY + "Next level: " + ChatColor.YELLOW + nextLevel);

		lore.add(SEPARATOR);

		meta.setLore(lore);

		setItemMeta(meta);

	}

	public static BrickItem fromItem(ItemStack item) {

		if (!isValid(item)) {

			return null;

		}

		BrickItem brick = new BrickItem(item);
		brick.loadFromLore();

		return brick;

	}

	public static boolean isValid(ItemStack item) {

		if (item == null || item.getType() != Material.CLAY_BRICK) {
			return false;
		}
		ItemMeta meta = item.getItemMeta();
		return meta.hasLore() && meta.getLore().get(0).startsWith(IDENTIFIER_LORE);

	}

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
		}

	}

	public String getLore(int line) {
		return getItemMeta().hasLore() ? getItemMeta().getLore().get(line) : "";
	}

	public long updateNextLevel() {

		return nextLevel = Math.round(Math.pow(100, Math.pow(level, 0.2)) / 10) * 10;

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

	public long addXp(long amountToAdd) {

		xp += amountToAdd;

		if (xp >= nextLevel) {
			xp -= nextLevel;
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
