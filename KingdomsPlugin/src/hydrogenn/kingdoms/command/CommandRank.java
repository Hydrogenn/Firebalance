
package hydrogenn.kingdoms.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import hydrogenn.kingdoms.Kingdom;
import hydrogenn.kingdoms.PlayerSpec;

public class CommandRank extends CommandClass {

	public CommandRank() {super("rank","[player] [promote/demote]","Edit successors to the throne. Must be leader.");}

	public boolean run(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("I'm sure you've heard, but "+ChatColor.BOLD+"you cannot do that.");
		}
		else {
			Player player = (Player) sender;
			PlayerSpec spec = PlayerSpec.getSpec(player);
			Kingdom kingdom = spec.getKingdom();
			if (kingdom == null) {
				player.sendMessage("No, that's not how that works...");
			}
			else {
				if (args.length < 1) {
					player.sendMessage("Chain of Command for " + kingdom.getName() + ": " + kingdom.printChainOfCommand());
				}
				else {
					PlayerSpec target = PlayerSpec.getByName(args[0]);
					if (target == null) {
						player.sendMessage("Whozat?");
					}
					else {
						if (!target.getKingdom().equals(kingdom)) {
							player.sendMessage("You can't tell them what to do.");
						}
						else if (!kingdom.isLeader(player.getUniqueId())) {
							player.sendMessage("You can't tell anyone what to do...");
						}	
						else {
							if (kingdom.isInLine(target)) {
								if (args.length >= 2 && args[1].equalsIgnoreCase("promote")) {
									player.sendMessage(target.getName() + " is already in line.");
								}
								else {
									kingdom.removeFromChain(target.getUuid());
									player.sendMessage("Done and done. No longer in line for your throne.");
								}
							}
							else {
								if (args.length >= 2 && args[1].equalsIgnoreCase("demote")) {
									player.sendMessage(target.getName() + " isn't in line. Paranoid much?");
								}
								else {
									kingdom.addToChain(target.getUuid());
									player.sendMessage("Done and done. "+target.getName()+" can now kill you and take over.");
								}
							}
						}
					}
				}
			}
		}
		return true;
	}
}