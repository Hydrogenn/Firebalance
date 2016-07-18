package hydrogenn.filter;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SwearListener implements Listener {

	List<String> filters;
	private SwearFilterLite plugin;

	public SwearListener(SwearFilterLite plugin) {

		this.plugin = plugin;

	}

	public void loadFilters() {

		filters = plugin.getConfig().getStringList("filters");

	}

	@EventHandler
	public void onChatMessage(AsyncPlayerChatEvent event) {
		String message = event.getMessage();
		message = filterAll(message, filters);
		event.setMessage(message);
	}

	@EventHandler
	public void onSignChange(SignChangeEvent e) {

		String[] lines = e.getLines();

		for (int i = 0; i < lines.length; i++) {

			e.setLine(i, filterAll(lines[i], filters));

		}

	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {

		if (e.getInventory().getType() != InventoryType.ANVIL || e.getSlotType() != SlotType.RESULT
				|| e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {

			return;

		}

		ItemStack item = e.getCurrentItem();
		ItemMeta meta = item.getItemMeta();

		if (meta.hasDisplayName()) {
			String name = filterAll(meta.getDisplayName(), filters).replace(ChatColor.RESET.toString(),
					ChatColor.RESET + "" + ChatColor.ITALIC); // The extra
																// italic is so
																// it still
																// looks right
																// when you
																// rename items
			meta.setDisplayName(name);
			item.setItemMeta(meta);
			e.setCurrentItem(item);
		}

	}

	public String filter(String input, String filter) {

		String inLower = input.toLowerCase();

		if (inLower.contains(filter)) {

			String output;
			String mPre = filter(input.substring(0, inLower.indexOf(filter)), filter);
			String stars = ChatColor.RED + "";
			for (int p = 0; p < filter.length(); p++) {
				stars += "*";
			}
			stars += ChatColor.RESET;

			String mSuf = ChatColor.getLastColors(mPre)
					+ filter(input.substring(inLower.indexOf(filter) + filter.length()), filter);

			output = mPre + stars + mSuf;
			return output;

		} else {

			return input;

		}

	}

	public String filterAll(String message, List<String> filters) {

		for (String s : filters) {
			message = filter(message, s);
		}

		return message;

	}
}
