package hydrogenn.kingdoms.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import hydrogenn.kingdoms.Kingdom;
import hydrogenn.kingdoms.PlayerSpec;
import net.md_5.bungee.api.ChatColor;

public class CommandPermission extends CommandClass {
	
	public CommandPermission() {super("permission","<permission> <# officers>","Set how many officers below you can do a thing.");}

	@Override
	public boolean run(CommandSender sender, String[] args) {
		if (args.length < 2) return false;
		if (!(sender instanceof Player)) {
			sender.sendMessage("stop plz");
		}
		else {
			Player player = (Player) sender;
			PlayerSpec spec = PlayerSpec.getSpec(player);
			Kingdom kingdom = spec.getKingdom();
			if (kingdom == null) {
				player.sendMessage("You and what army?");
			}
			else if (!kingdom.hasAllPermission(player.getUniqueId())) {
				player.sendMessage("Sorry, you can't do that unless people respect you. As a leader.");
			}
			else {
				String permission = args[1];
				try {
					int rank = Integer.parseInt(args[2]);
					
					if (permission.equals("invite"))
						kingdom.setInvitePermission(rank);
					else if (permission.equals("name"))
						kingdom.setNamePermission(rank);
					else if (permission.equals("capital"))
						kingdom.setCapitalPermission(rank);
					else {
						player.sendMessage("Permissions: 'invite', 'name' and 'capital'.");
						return true;
					}
					player.sendMessage("Permissions updated.");
					
				} catch (NumberFormatException e) {
					player.sendMessage(ChatColor.RED + "Try putting in a "+ChatColor.ITALIC+"number"+ ChatColor.RED +" of officers next time! >:(");
					return true;
				}
			}
		}
		return true;
	}
}
