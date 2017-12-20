package hydrogenn.kingdoms.command;

import java.util.Iterator;

import org.bukkit.command.CommandSender;

import hydrogenn.kingdoms.Kingdom;
import net.md_5.bungee.api.ChatColor;

public class CommandList extends CommandClass {

	public CommandList() {
		super("list", "", "List off what people call themselves.");
	}

	@Override
	public boolean run(CommandSender sender, String[] args) {
		Iterator<Kingdom> kingdomIterator = Kingdom.iterator();
		while (kingdomIterator.hasNext()) {
			Kingdom kingdom = kingdomIterator.next();
			sender.sendMessage(ChatColor.GRAY + "[" + kingdom.getTag() + ChatColor.GRAY + "] " +
					kingdom.getName());
		}
		return true;
	}

}
