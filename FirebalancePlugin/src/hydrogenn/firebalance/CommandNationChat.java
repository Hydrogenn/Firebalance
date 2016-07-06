
package hydrogenn.firebalance;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import hydrogenn.firebalance.utils.ArrayUtils;
import hydrogenn.firebalance.utils.Messenger;

public class CommandNationChat implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			return true;
		}

		if (args.length < 1) {
			Messenger.send(sender, "You need to say something!");
			Messenger.send(sender, "Usage: /nationchat <msg>");
			return true;
		}

		Player p = (Player) sender;

		PlayerSpec spec = Firebalance.getPlayerFromName(p.getName());

		if (spec == null) {
			Messenger.send(p, "&cSomething went &nHORRIBLY&c wrong");
			return false;
		}

		if (spec.getNation() <= 0) {

			Messenger.send(p, "You aren't part of a nation");
			return true;

		}

		byte nation = spec.getNation();

		String msg = getMessage(p, ArrayUtils.concatArray(args, " "), nation);

		for (PlayerSpec player : Firebalance.playerSpecList) {

			if (player.getNation() != nation || player.getPlayer() == null) {
				continue;
			}

			Messenger.send(player.getPlayer(), msg);

		}

		return true;
	}

	private String getMessage(Player p, String msg, byte nation) {

		return "&7&l(&r" + Firebalance.getNationColor(nation, false) + Firebalance.getNationName(nation, false)
				+ "&7&l)&6 " + p.getDisplayName() + "&7&l>&r " + msg;

	}

}