package hydrogenn.locks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Locks extends JavaPlugin {

	public void onEnable() {

		// Register key crafting recipe
		ItemStack key = new ItemStack(Material.TRIPWIRE_HOOK);
		ItemMeta keyMeta = key.getItemMeta();
		List<String> keyLore = new ArrayList<>();
		keyLore.add(ChatColor.GRAY + "");
		keyMeta.setLore(keyLore);
		keyMeta.setDisplayName(ChatColor.WHITE + "KeyC");
		key.setItemMeta(keyMeta);

		ShapedRecipe keyCraft = new ShapedRecipe(key);
		keyCraft.shape("A", "A");
		keyCraft.setIngredient('A', Material.GOLD_INGOT);
		getServer().addRecipe(keyCraft);

		// Register key dupe recipe
		ItemStack keyD = new ItemStack(Material.TRIPWIRE_HOOK);
		keyMeta.setDisplayName(ChatColor.WHITE + "KeyD");
		keyD.setItemMeta(keyMeta);

		ShapelessRecipe keyDupe = new ShapelessRecipe(keyD);
		keyDupe.addIngredient(Material.GOLD_INGOT);
		keyDupe.addIngredient(Material.TRIPWIRE_HOOK);
		getServer().addRecipe(keyDupe);

		// Register key add recipe
		ItemStack keyA = new ItemStack(Material.TRIPWIRE_HOOK);
		keyMeta.setDisplayName(ChatColor.WHITE + "KeyA");
		keyA.setItemMeta(keyMeta);

		ShapedRecipe keyAdd = new ShapedRecipe(keyA);
		keyAdd.shape("AA");
		keyAdd.setIngredient('A', Material.TRIPWIRE_HOOK);
		getServer().addRecipe(keyAdd);

		getServer().getPluginManager().registerEvents(new LocksListener(), this);

		getLogger().info("Locks v" + getDescription().getVersion() + " enabled");

	}

	public void onDisable() {

		getLogger().info("Locks v" + getDescription().getVersion() + " disabled");

	}
	
	public void saveData() {
		clearObject("chest");
		for (int i = 0; i < ChestSpec.list.size(); i++) {
			String line = "";
			line += ChestSpec.list.get(i).getCoords().getBlockX();
			line += ":" + ChestSpec.list.get(i).getCoords().getBlockY();
			line += ":" + ChestSpec.list.get(i).getCoords().getBlockZ();
			line += ":" + ChestSpec.list.get(i).getId();
			storeObject(line, "chest");
		}
	}
	
	public void loadData() {
		List<String> lineList = new ArrayList<>();
		lineList = displayObjects("chest");
		for (int i = 0; i < lineList.size(); i++) {
			String[] subList = lineList.get(i).split(":");
			if (subList.length < 4) {
				ChestSpec.list.add(new ChestSpec(new Location(Bukkit.getWorld(Bukkit.getWorlds().get(0).getName()),
						Integer.parseInt(subList[0]), Integer.parseInt(subList[1]), Integer.parseInt(subList[2])), ""));
			} else
				ChestSpec.list.add(new ChestSpec(new Location(Bukkit.getWorld(Bukkit.getWorlds().get(0).getName()),
						Integer.parseInt(subList[0]), Integer.parseInt(subList[1]), Integer.parseInt(subList[2])),
						subList[3]));
		}
	}
	
	public void storeObject(String input, String file) {
		String dir = getDataFolder() + File.separator + "locks." + file;
		try (BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(dir, true), "utf-8"))) {
			writer.write(input);
			writer.newLine();
		} catch (UnsupportedEncodingException e) {
			Bukkit.getLogger().info("Unsupported Encoding");
		} catch (FileNotFoundException e) {
			Bukkit.getLogger().info("File Not Found");
		} catch (IOException e) {
			Bukkit.getLogger().info("IO Exception");
		}
	}

	public void clearObject(String file) {
		String dir = getDataFolder() + File.separator + "locks." + file;
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dir), "utf-8"))) {
			writer.write("");
		} catch (UnsupportedEncodingException e) {
			Bukkit.getLogger().info("Unsupported Encoding");
		} catch (FileNotFoundException e) {
			Bukkit.getLogger().info("File Not Found");
		} catch (IOException e) {
			Bukkit.getLogger().info("IO Exception");
		}
	}

	public ArrayList<String> displayObjects(String file) {
		String dir = "plugins/Firebalance/locks." + file;
		ArrayList<String> lineList = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(dir), "utf-8"))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				lineList.add(line);
			}
		} catch (UnsupportedEncodingException e) {
			Bukkit.getLogger().info("Unsupported Encoding");
		} catch (FileNotFoundException e) {
			Bukkit.getLogger().info("File Not Found");
		} catch (IOException e) {
			Bukkit.getLogger().info("IO Exception");
		}
		return lineList;
	}


}
