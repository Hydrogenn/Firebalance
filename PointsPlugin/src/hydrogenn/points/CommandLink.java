package hydrogenn.points;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandLink implements CommandExecutor {

	private Points points;
	
	public CommandLink(Points plugin) {
		points = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		ArrayList<String> listOfLinks = points.getListOfLinks(label);
		if (listOfLinks.size() == 0) {
			sender.sendMessage(ChatColor.RED + "No link found for '"+label+"'");
		}
		for (int i = 0; i < listOfLinks.size(); i++) {
			String link = listOfLinks.get(i);
			TextComponent message;
			if (listOfLinks.size() != 1) 
				message = new TextComponent(label + " link "+(i+1));
			else
				message = new TextComponent(label + " link");
			message.setClickEvent( new ClickEvent( ClickEvent.Action.OPEN_URL, link ) );
			sender.spigot().sendMessage(message);
		}
		return true;
	}

}
