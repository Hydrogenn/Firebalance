package hydrogenn.locks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
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
		
		ConfigManager.init(this);

		getLogger().info("Locks v" + getDescription().getVersion() + " enabled");

	}

	public void onDisable() {

		ConfigManager.save();
		getLogger().info("Locks v" + getDescription().getVersion() + " disabled");

	}


}
