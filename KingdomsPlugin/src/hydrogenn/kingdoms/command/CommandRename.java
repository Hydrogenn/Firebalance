
package hydrogenn.kingdoms.command;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import hydrogenn.kingdoms.Kingdom;
import hydrogenn.kingdoms.PlayerSpec;
import net.md_5.bungee.api.ChatColor;

public class CommandRename extends CommandClass {

	public CommandRename()  {super("rename","<name>","Give your people a new, shinier name. Must be leader.");}

	@Override
	public boolean run(CommandSender sender, String[] args) {
		if (args.length < 1) return false;
		if (!(sender instanceof Player)) {
			sender.sendMessage("Don't you have anything better to do?");
		}
		else {
			Player player = (Player) sender;
			String name = StringUtils.join(args," ");
			PlayerSpec spec = PlayerSpec.getSpec(player);
			Kingdom kingdom = spec.getKingdom();
			if (kingdom == null) {
				player.sendMessage("Remind me again what you're changing the name of?");
			}
			else {
				if (!kingdom.isLeader(player.getUniqueId())) {
					player.sendMessage("You can definitely do that, you being leader and all.");
				}
				else if (name.length() == 0) {
					player.sendMessage("What a great name! ...wait");
				}
				else if (name.length() < 4) {
					player.sendMessage("Pfft! No " + ChatColor.ITALIC + "real" + ChatColor.RESET +" name is that short.");
				}
				else {
					Kingdom other = Kingdom.get(name);
					if (other != null) {
						player.sendMessage("Sounds a bit familiar, don't you think?");
					}
					else {
						player.sendMessage("Could not find kingdom: "+kingdom.getName()+". Because it's now "+name+"!");
						kingdom.setName(name);
					}
				}
			}
		}
		return true;
	}

}