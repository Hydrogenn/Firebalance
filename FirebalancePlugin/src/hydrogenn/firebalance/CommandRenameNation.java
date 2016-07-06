
package hydrogenn.firebalance;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRenameNation implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length < 1) return false;
		if (sender instanceof Player) {
			Player player = (Player) sender;
			for (PlayerSpec s : Firebalance.playerSpecList) {
				if (s.getKing() == 1 && s.getName().equals(player.getName())) {
					StringBuilder builder = new StringBuilder();
					for (String string : args) {
						if (builder.length() > 0) {
							builder.append(" ");
						}
						builder.append(string);
					}
					try {
						Firebalance.nationNameList.set((int) (Math.log10(s.getNation()) / Math.log10(2)), builder.toString());
						player.chat("I declare that my nation shall be referred to, from now on, as §n" + Firebalance.getNationName(s.getNation(), true) + ".");
					} catch (ArrayIndexOutOfBoundsException e) {
						Bukkit.broadcastMessage("§cfax ur gaem jesh");
					}
				} else if (s.getName().equals(player.getName())) player.sendMessage("Only leaders can do that.");
			}
		}
		return true;
	}
}