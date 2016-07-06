
package hydrogenn.firebalance;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandEnthrone implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length > 1 && !args[1].equals("official") && !args[1].equals("citizen")) return false;
		if (sender instanceof Player) {
			Player player = (Player) sender;
			String newKing = "";
			int result = -1;
			boolean success = false;
			boolean selfPromote = false;
			String nationString = "?";
			String nationColor = "§f";
			for (int i = 0; i < Firebalance.playerSpecList.size(); i++) {
				if (Firebalance.playerSpecList.get(i).getName().equals(player.getName())) {
					byte nationValue = Firebalance.playerSpecList.get(i).getNation();
					try {
						newKing = args[0];
					} catch (ArrayIndexOutOfBoundsException e) {
						if (Firebalance.playerSpecList.get(i).getNation() <= 0) {
							player.sendMessage("You have to be part of a nation to take a throne.");
							return false;
						} else {
							for (SchedulerCache s : Firebalance.scheduleList) {
								if (s.callerName == nationString && Firebalance.playerSpecList.get(i).getKing() < 1 && Bukkit.getServer().getScheduler().isQueued(s.id))
									player.sendMessage("Can't do that. The leader's officials are in queue right now.");
								return true;
							}
							for (PlayerSpec s : Firebalance.playerSpecList) {
								if (s.getKing() == 1 && s.getNation() == nationValue) {
									player.sendMessage("Can't do that. There's already a king.");
									return true;
								}
							}
						}
						if (Firebalance.playerSpecList.get(i).getKing() == 2) {
							for (Iterator<SchedulerCache> i2 = Firebalance.scheduleList.iterator(); i2.hasNext();) {
								SchedulerCache s = i2.next();
								if (s.callerName.equals(nationString)) if (Bukkit.getScheduler().isQueued(s.id)) {
									Bukkit.getScheduler().cancelTask(s.id);
									i2.remove();
								}
							}
						}
						Firebalance.playerSpecList.get(i).setKing(1);
						selfPromote = true;
					}
					if (Firebalance.playerSpecList.get(i).getKing() == 1) {
						if (nationValue > 0) {
							nationString = Firebalance.getNationName(nationValue, false);
							nationColor = Firebalance.getNationColor(nationValue, false);
							if (!selfPromote && args.length < 2) Firebalance.playerSpecList.get(i).setKing(0);
							result = i;
						}
					} else if (nationValue > 0) {
						player.sendMessage("I'm not sure the leader would appreciate you running the nation behind their back.");
						result = -1;
					} else player.sendMessage("Freelancers can't do that.");
				}
			}
			if (result != -1) {
				if (!selfPromote) {
					for (PlayerSpec s : Firebalance.playerSpecList) {
						if (s.getName().equals(newKing) && s.getNation() == Firebalance.playerSpecList.get(result).getNation()) {
							try {
								if (args[1].equals("official")) {
									s.setKing(2);
									Bukkit.broadcastMessage(nationColor + player.getName() + " has promoted " + newKing + " to official!");
								}
								if (args[1].equals("citizen")) {
									s.setKing(0);
									Bukkit.broadcastMessage(nationColor + player.getName() + " has demoted " + newKing + " to citizen!");
								}
							} catch (ArrayIndexOutOfBoundsException e) {
								s.setKing(1);
								Bukkit.broadcastMessage(nationColor + player.getName() + " has given " + nationString + "'s throne to " + newKing + "!");
							}
							success = true;
						}
					}
				}
				if (selfPromote) Bukkit.broadcastMessage(nationColor + player.getName() + " has claimed the throne of " + nationString + "!");
				else if (!success && result != -1) {
					Firebalance.playerSpecList.get(result).setKing(1);
					player.sendMessage("You offered the throne to " + newKing + ", but they thought it was uncomfortable.");
				}
			}
		}
		return true;
	}
}