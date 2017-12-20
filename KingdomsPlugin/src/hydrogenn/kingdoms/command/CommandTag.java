package hydrogenn.kingdoms.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import hydrogenn.kingdoms.Kingdom;
import hydrogenn.kingdoms.PlayerSpec;
import net.md_5.bungee.api.ChatColor;

public class CommandTag extends CommandClass {

	public CommandTag() {super("tag","<tag>","Give your people a chat tag. Must be leader.");}

	@Override
	public boolean run(CommandSender sender, String[] args) {
		if (args.length < 1) return false;
		if (!(sender instanceof Player)) {
			sender.sendMessage("No can do.");
		}
		else {
			Player player = (Player) sender;
			PlayerSpec spec = PlayerSpec.getSpec(player);
			Kingdom kingdom = spec.getKingdom();
			String tag = ChatColor.translateAlternateColorCodes('&',args[0]);
			String textTag = ChatColor.stripColor(tag);
			if (kingdom == null) {
				player.sendMessage("Remind me again what you're changing the tag of?");
			}
			else {
				if (!kingdom.hasNamePermission(player)) {
					player.sendMessage("You have permission to do that? Yep... believable.");
				}
				else if (textTag.length() == 0) {
					player.sendMessage(ChatColor.GRAY + "[] /tag: "+ ChatColor.WHITE +"A fine choice. Very distinct.");
				}
				else if (textTag.length() > 5) {
					player.sendMessage(ChatColor.GRAY + "["+tag+ChatColor.GRAY+"] /tag: "+ ChatColor.WHITE +"Doesn't this seem a bit long of a tag?");
				}
				else {
					Kingdom other = Kingdom.getByTag(tag);
					if (other != null) {
						String nameOfOther = PlayerSpec.getByUUID(other.getLeader()).getName();
						player.sendMessage(ChatColor.GRAY + "["+tag+ChatColor.GRAY+"] "+nameOfOther+": "+ ChatColor.WHITE +"Someone's beat you to it, pal!");
					}
					else {
						kingdom.setTag(tag);
						player.sendMessage("Tag changed.");
					}
				}
			}
		}
		return true;
	}

}
