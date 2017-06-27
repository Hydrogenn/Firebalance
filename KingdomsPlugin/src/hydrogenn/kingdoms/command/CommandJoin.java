package hydrogenn.kingdoms.command;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import hydrogenn.kingdoms.Kingdom;
import hydrogenn.kingdoms.PlayerSpec;
import net.md_5.bungee.api.ChatColor;

public class CommandJoin extends CommandClass {
	
	public CommandJoin() {super("join","<nation>","Join some people.");}

	@Override
	public boolean run(CommandSender sender, String[] args) {
		if (args.length < 1) return false;
		if (!(sender instanceof Player)) {
			sender.sendMessage("This is only for players to use.");
		}
		else {
			Player player = (Player) sender;
			String name = StringUtils.join(args," ");
			Kingdom kingdom = Kingdom.get(name);
			if (kingdom == null) {
				player.sendMessage("You cannot join "+name+" because it does not exist.");
			}
			else if (!kingdom.isInvited(player.getUniqueId())) {
				player.sendMessage("You need to be invited first! Try asking them politely?");
			}
			else {
				PlayerSpec spec = PlayerSpec.getSpec(player);
				try {
					spec.joinKingdom(kingdom);
					player.sendMessage("Booyah! You're in.");
				} catch (IllegalArgumentException e) {
					player.sendMessage(ChatColor.RED + "Now you've broken something. Tell your admin to tell me about that nasty stack trace.");
					player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "What did I say about being invited?!? >:(");
					e.printStackTrace();
				}
			}
		}
		return true;
	}
}
