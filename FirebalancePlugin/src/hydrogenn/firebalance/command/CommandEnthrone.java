
package hydrogenn.firebalance.command;

import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import hydrogenn.firebalance.Firebalance;
import hydrogenn.firebalance.PlayerSpec;
import hydrogenn.firebalance.SchedulerCache;
import hydrogenn.firebalance.utils.Messenger;

public class CommandEnthrone implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length > 1 && !args[1].equals("official") && !args[1].equals("citizen"))
			return false;
		if (sender instanceof Player) {
			Player player = (Player) sender;
			String newKing = "";
			UUID result = null;
			boolean success = false;
			boolean selfPromote = false;
			String nationString = "?";
			String nationColor = "&f";
			PlayerSpec p = PlayerSpec.getPlayer(player.getUniqueId());
			byte nationValue = p.getNation();
			try {
				newKing = args[0];
			} catch (ArrayIndexOutOfBoundsException e) {
				if (nationValue <= 0) {
					Messenger.send(player, "You have to be part of a nation to take a throne.");
					return false;
				} else {
					for (SchedulerCache s : SchedulerCache.list) {
						if (s.getCallerName() == nationString && p.getRole() < 1
								&& Bukkit.getServer().getScheduler().isQueued(s.getId()))
							Messenger.send(player,
									"Can't do that. The leader's officials are in queue right now.");
						return true;
					}
					for (PlayerSpec s : PlayerSpec.table.values()) {
						if (s.getRole() == 1 && s.getNation() == nationValue) {
							Messenger.send(player, "Can't do that. There's already a king.");
							return true;
						}
					}
				}
				if (p.getRole() == 2) {
					for (Iterator<SchedulerCache> i2 = SchedulerCache.list.iterator(); i2.hasNext();) {
						SchedulerCache s = i2.next();
						if (s.getCallerName().equals(nationString))
							if (Bukkit.getScheduler().isQueued(s.getId())) {
								Bukkit.getScheduler().cancelTask(s.getId());
								i2.remove();
							}
					}
				}
				p.setRole(1);
				selfPromote = true;
			}
			if (p.getRole() == 1) {
				if (nationValue > 0) {
					nationString = Firebalance.getNationName(nationValue, false);
					nationColor = Firebalance.getNationColor(nationValue, false);
					if (!selfPromote && args.length < 2)
						p.setRole(0);
					result = player.getUniqueId();
				}
			} else if (nationValue > 0) {
				Messenger.send(player,
						"I'm not sure the leader would appreciate you running the nation behind their back.");
				result = null;
			} else
				Messenger.send(player, "Freelancers can't do that.");
			if (result != null) {
				if (!selfPromote) {
					PlayerSpec nk = PlayerSpec.getPlayerFromName(newKing);
					if (nk.getNation()!=p.getNation()) {
						Messenger.send(player, "Yeah, that "+Firebalance.getNationName(nk.getNation(), false)+" member would find your throne real helpful.");
						return true;
					}
					try {
						if (args[1].equals("official")) {
							nk.setRole(2);
							Bukkit.broadcastMessage(nationColor + player.getName() + " has promoted " + newKing
									+ " to official!");
						}
						if (args[1].equals("citizen")) {
							PlayerSpec.getPlayer(result).setRole(1);
							Bukkit.broadcastMessage(nationColor + player.getName() + " has demoted " + newKing
									+ " to citizen!");
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						PlayerSpec.getPlayer(result).setRole(1);
						Bukkit.broadcastMessage(nationColor + player.getName() + " has given " + nationString
								+ "'s throne to " + newKing + "!");
					}
					success = true;
				}
			}
			if (selfPromote)
				Bukkit.broadcastMessage(
						nationColor + player.getName() + " has claimed the throne of " + nationString + "!");
			else if (!success && result != null) {
				PlayerSpec.getPlayer(result).setRole(1);
				Messenger.send(player,
						"You offered the throne to " + newKing + ". They weren't all that interested.");
			}
		}
		return true;
	}
}