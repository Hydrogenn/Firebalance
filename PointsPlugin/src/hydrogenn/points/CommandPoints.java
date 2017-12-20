package hydrogenn.points;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;

public class CommandPoints implements CommandExecutor {

	private Points points;
	
	public CommandPoints(Points plugin) {
		points = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("points.list") && args[0].equals("list")) {
			Set<Entry<UUID,Integer>> pointEntries = points.getEntries();
			
			int numPages = (int) Math.ceil((double)pointEntries.size() / 8d);
			if (numPages == 0) {
				sender.sendMessage(ChatColor.RED + "No one found!");
				return true;
			}
			
			int page;
			try {
				if (args.length >= 2) page = Integer.parseInt(args[1]);
				else page = 1;
			} catch (NumberFormatException e) {
				page = 1;
			}
			if (page > numPages) page = numPages;
			sender.sendMessage("-- Page "+page+"/"+numPages+" --");
			
			Iterator<Entry<UUID,Integer>> pointIterator = pointEntries.iterator();
			
			for (int i = 0; i < (page) * 8; i++) {
				Entry<UUID,Integer> points = pointIterator.next();
				if (i < (page - 1) * 8) continue;
				String name = Bukkit.getOfflinePlayer(points.getKey()).getName();
				sender.sendMessage("["+i+"] "+name+": "+points.getValue()+" Points");
			}
		}
		else {
			sender.sendMessage("This server has points hidden for your rank.");
		}
		return true;
	}

}
