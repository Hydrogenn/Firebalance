package hydrogenn.filter;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class SwearListener implements Listener {
	
	FileConfiguration config = Bukkit.getPluginManager().getPlugin("SwearFilterLite").getConfig();
	List<String> filters = config.getStringList("filters");
	
	@EventHandler
	public void onChatMessage(AsyncPlayerChatEvent event) {
		String message = event.getMessage();
		message = filterAll(message, filters);
		event.setMessage(message);
	}
	
	public String filter(String input, String filter) {
		String inLower = input.toLowerCase();
		if (inLower.contains(filter)) {
			String output;
			String mPre = filter(input.substring(0,inLower.indexOf(filter)),filter);
			String stars = ChatColor.RED + "";
			for(int p = 0; p < filter.length() ;p++) {
				stars += "*";
			}
			stars += ChatColor.RESET;
			String mSuf = ChatColor.getLastColors(mPre) +
					filter(input.substring(inLower.indexOf(filter)+filter.length()),filter);
			output = mPre+stars+mSuf;
			return output;
		} else return input;
	}
	
	public String filterAll(String message, List<String> filters) {
		for (String s: filters) {
			message = filter(message, s);
		}
		return message;
	}
}
