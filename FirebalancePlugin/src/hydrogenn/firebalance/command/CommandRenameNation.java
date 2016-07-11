
package hydrogenn.firebalance.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import hydrogenn.firebalance.Firebalance;
import hydrogenn.firebalance.PlayerSpec;
import hydrogenn.firebalance.utils.Messenger;

public class CommandRenameNation implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length < 1)
			return false;
		if (sender instanceof Player) {
			Player player = (Player) sender;
			PlayerSpec playerSpec = PlayerSpec.getPlayer(player.getUniqueId());
			if (playerSpec.getRole()!=1) {
				Messenger.send(player, "Only leaders can do that.");
				return true;
			}
			StringBuilder builder = new StringBuilder();
			for (String string : args) {
				if (builder.length() > 0) {
					builder.append(" ");
				}
				builder.append(string);
			}
			try {
				Firebalance.nationNameList.set((int) (Math.log10(playerSpec.getNation()) / Math.log10(2)),
						builder.toString());
				Messenger.forceChat(player, "I declare that my nation shall be referred to, from now on, as &n"
						+ Firebalance.getNationName(playerSpec.getNation(), true) + ".");
			} catch (ArrayIndexOutOfBoundsException e) {
				Messenger.broadcast("&cfax ur gaem jesh");
			}
		}
		return true;
	}
}