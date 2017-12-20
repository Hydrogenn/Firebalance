package hydrogenn.points;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Source:
 * me.mrCookieSlime.CSCoreLibPlugin.general.Chat.Command.CSCommand
 * https://github.com/TheBusyBiscuit/CS-CoreLib/blob/master/src/me/mrCookieSlime/CSCoreLibPlugin/general/Chat/Command/CSCommand.java
 */
public class CommandLink {
	
	Points plugin;
	String linkType;
	
	/**
	 * Constructor for a new Command
	 *
	 * @param  command The Name of the Command
	 * @param  description The Description for the Command
	 * @param  usage The Usage for the Command
	 * @param  plugin The Plugin which this Command belongs to
	 * @param  aliases The Aliases for the Command
	 */ 
	public CommandLink(String linkType, Points plugin, String... aliases) {
		this.plugin = plugin;
        try {
        	Object map = plugin.getServer().getClass().getMethod("getCommandMap").invoke(plugin.getServer());
            Command cmd = new Command(linkType, "Gets a link.", "/"+linkType, Arrays.asList(aliases)) {

    			@Override
    			public boolean execute(CommandSender arg0, String arg1, String[] arg2) {
    				return run(arg0, arg2);
    			}
            	
            };
			map.getClass().getMethod("register", String.class, Command.class).invoke(map, linkType, cmd);
		} catch (Exception e) { //ew
			e.printStackTrace();
		} 
	}
	
	/**
	 * Behind-The-Scenes Method for the Command Execution
	 *
	 * @param  sender The Sender of the Command
	 * @param  args The Arguments the Sender specified
	 * @return      Whether the Command succeeded or not
	 */ 
	protected boolean run(CommandSender sender, String[] args) {
		if (args[0].equalsIgnoreCase("help") && displayHelpPage(sender)) return true;
		else if (args[0].equals("?") && displayHelpPage(sender)) return true;
		else return onCommand(sender, args);
	}

	/**
	 * A help method. It's very helpful.
	 *
	 * @param  sender The Sender of the Command
	 * @return      Whether the Command succeeded or not
	 */ 
	public boolean displayHelpPage(CommandSender sender) {
		sender.sendMessage("Just run the command normally to get the link.");
		return true;
	}
	
	/**
	 * The actual command execution.
	 *
	 * @param  sender The Sender of the Command
	 * @param  args The Arguments the Sender specified
	 * @return      Whether the Command succeeded or not
	 */ 
	public boolean onCommand(CommandSender sender, String[] args) {
		ArrayList<String> listOfLinks = plugin.getListOfLinks(linkType);
		if (listOfLinks.size() == 0) {
			sender.sendMessage(ChatColor.RED + "No link found for '"+linkType+"'");
		}
		for (int i = 0; i < listOfLinks.size(); i++) {
			String link = listOfLinks.get(i);
			TextComponent message;
			if (listOfLinks.size() != 1) 
				message = new TextComponent(linkType + " link "+(i+1));
			else
				message = new TextComponent(linkType + " link");
			message.setClickEvent( new ClickEvent( ClickEvent.Action.OPEN_URL, link ) );
			message.setUnderlined(true);
			message.setColor(ChatColor.BLUE);
			sender.spigot().sendMessage(message);
		}
		return true;
	}
}
