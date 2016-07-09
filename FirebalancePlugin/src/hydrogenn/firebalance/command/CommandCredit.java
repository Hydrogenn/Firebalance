package hydrogenn.firebalance.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import hydrogenn.firebalance.PlayerSpec;
import hydrogenn.firebalance.utils.Messenger;

public class CommandCredit implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.equals(Bukkit.getServer().getConsoleSender())) {
			try {
				for (PlayerSpec s : PlayerSpec.list) {
					if (s.getName().equals(args[0])) {
						s.setCredits(s.getCredits() + 1);
						Messenger.send(sender, "That player has recieved a credit!");
						for (Player p : Bukkit.getOnlinePlayers()) {
							if (p.getName().equals(args[0]))
								Messenger.send(p, "You have recieved a credit!");
						}
						return true;
					}
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				return false;
			}
		} else if (args.length < 1) {
			for (PlayerSpec s : PlayerSpec.list) {
				if (s.getName().equals(sender.getName())) {
					Messenger.send(sender, "You have " + s.getCredits() + " credits");
					return true;
				}
			}
		} else
			Messenger.send(sender, "You can only do that from console.");
		return true;
	}
}
