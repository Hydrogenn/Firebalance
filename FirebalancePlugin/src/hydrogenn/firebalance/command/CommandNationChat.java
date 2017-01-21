
package hydrogenn.firebalance.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import hydrogenn.firebalance.Firebalance;
import hydrogenn.firebalance.PlayerSpec;
import hydrogenn.firebalance.utils.ArrayUtils;

public class CommandNationChat implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) { return true; }

		if (args.length < 1) {
			sender.sendMessage("You need to say something!");
			return false;
		}

		Player p = (Player) sender;

		PlayerSpec spec = PlayerSpec.getPlayerFromName(p.getName());

		if (spec == null) {
			p.sendMessage("Something went horribly wrong. It's a free credit if you ask for one.");
			return false;
		}
		
		String prefix = "&r";
		if (spec.getRole()==1) prefix = "&6";

		if (spec.getNation() <= 0) {

			p.sendMessage("'Description: Send a message to your nation.' Now that won't work too well for you, will it?");
			return true;

		}

		byte nation = spec.getNation();

		String msg = getMessage(p, ArrayUtils.concatArray(args, " "), prefix, nation);

		for (PlayerSpec player : PlayerSpec.getPlayers()) {

			if (player.getNation() != nation || !player.getOnline()) continue;

			Bukkit.getPlayer(player.getUUID()).sendMessage(msg);
		}

		return true;
	}

	private String getMessage(Player p, String msg, String prefix, byte nation) {

		return ChatColor.translateAlternateColorCodes('&', "&7&l(&r" + Firebalance.getNationColor(nation, false) + Firebalance.getNationName(nation, false) + "&7&l)&f " + p.getDisplayName() + "&7&l> " + prefix + msg);

	}

}