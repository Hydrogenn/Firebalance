package hydrogenn.kingdoms.command;

import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;

public abstract class CommandClass {
	
	protected String name;
	protected String help;
	protected String description;
	
	public CommandClass(String name, String help, String description) {
		this.name = name;
		this.help = "/k " + name + " " + help;
		this.description = ChatColor.BLUE + "/k " + name + ": " + ChatColor.WHITE + description;
	}

	public String getName() {
		return name;
	}
	
	public void execute(CommandSender sender, String[] args) {
		if (!run(sender, args)) {
			sendHelp(sender);
		}
	}
	
	public void sendHelp(CommandSender sender) {
		sender.sendMessage(help);
	}
	
	public abstract boolean run(CommandSender sender, String[] args);

	public String getDescription() {
		return description;
	}
	
}
