
package hydrogenn.firebalance.command;

import java.util.Iterator;

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
			int result = -1;
			boolean success = false;
			boolean selfPromote = false;
			String nationString = "?";
			String nationColor = "&f";
			for (int i = 0; i < PlayerSpec.list.size(); i++) {
				if (PlayerSpec.list.get(i).getName().equals(player.getName())) {
					byte nationValue = PlayerSpec.list.get(i).getNation();
					try {
						newKing = args[0];
					} catch (ArrayIndexOutOfBoundsException e) {
						if (PlayerSpec.list.get(i).getNation() <= 0) {
							Messenger.send(player, "You have to be part of a nation to take a throne.");
							return false;
						} else {
							for (SchedulerCache s : SchedulerCache.list) {
								if (s.getCallerName() == nationString && PlayerSpec.list.get(i).getKing() < 1
										&& Bukkit.getServer().getScheduler().isQueued(s.getId()))
									Messenger.send(player,
											"Can't do that. The leader's officials are in queue right now.");
								return true;
							}
							for (PlayerSpec s : PlayerSpec.list) {
								if (s.getKing() == 1 && s.getNation() == nationValue) {
									Messenger.send(player, "Can't do that. There's already a king.");
									return true;
								}
							}
						}
						if (PlayerSpec.list.get(i).getKing() == 2) {
							for (Iterator<SchedulerCache> i2 = SchedulerCache.list.iterator(); i2.hasNext();) {
								SchedulerCache s = i2.next();
								if (s.getCallerName().equals(nationString))
									if (Bukkit.getScheduler().isQueued(s.getId())) {
										Bukkit.getScheduler().cancelTask(s.getId());
										i2.remove();
									}
							}
						}
						PlayerSpec.list.get(i).setKing(1);
						selfPromote = true;
					}
					if (PlayerSpec.list.get(i).getKing() == 1) {
						if (nationValue > 0) {
							nationString = Firebalance.getNationName(nationValue, false);
							nationColor = Firebalance.getNationColor(nationValue, false);
							if (!selfPromote && args.length < 2)
								PlayerSpec.list.get(i).setKing(0);
							result = i;
						}
					} else if (nationValue > 0) {
						Messenger.send(player,
								"I'm not sure the leader would appreciate you running the nation behind their back.");
						result = -1;
					} else
						Messenger.send(player, "Freelancers can't do that.");
				}
			}
			if (result != -1) {
				if (!selfPromote) {
					for (PlayerSpec s : PlayerSpec.list) {
						if (s.getName().equals(newKing)
								&& s.getNation() == PlayerSpec.list.get(result).getNation()) {
							try {
								if (args[1].equals("official")) {
									s.setKing(2);
									Bukkit.broadcastMessage(nationColor + player.getName() + " has promoted " + newKing
											+ " to official!");
								}
								if (args[1].equals("citizen")) {
									s.setKing(0);
									Bukkit.broadcastMessage(nationColor + player.getName() + " has demoted " + newKing
											+ " to citizen!");
								}
							} catch (ArrayIndexOutOfBoundsException e) {
								s.setKing(1);
								Bukkit.broadcastMessage(nationColor + player.getName() + " has given " + nationString
										+ "'s throne to " + newKing + "!");
							}
							success = true;
						}
					}
				}
				if (selfPromote)
					Bukkit.broadcastMessage(
							nationColor + player.getName() + " has claimed the throne of " + nationString + "!");
				else if (!success && result != -1) {
					PlayerSpec.list.get(result).setKing(1);
					Messenger.send(player,
							"You offered the throne to " + newKing + ", but they thought it was uncomfortable.");
				}
			}
		}
		return true;
	}
}