package hydrogenn.firebalance;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import hydrogenn.firebalance.utils.Messenger;

public class CommandSetNation implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// TODO readd a more limited BOP check
		// byte currentNation =
		// Firebalance.getPlayerFromName(sender.getName()).getNation();
		int[] count = { 0, 0, 0, 0, 0 };
		boolean[] options = { true, true, true, false, true };
		String optionString = "";
		for (PlayerSpec s : Firebalance.playerSpecList) {
			if (s.getNation() != -1)
				count[s.getNation()]++;
		}
		if (args.length < 1) {
			for (int i = 0; i <= 4; i++) {
				// TODO readd a more limited BOP check
				/*
				 * count[i]++; if (currentNation!=-1) count[currentNation]--; if
				 * (i!=0&&count[i]>Math.ceil(1.5*Math.min(Math.min(count[1],
				 * count[2]), count[4]))) { options[i]=false; count[i]--; if
				 * (currentNation!=-1) count[currentNation]++; } else if
				 * (i==0&&count[i]>Math.ceil(2*Math.min(Math.min(count[1],
				 * count[2]), count[4]))) { options[i]=false; count[i]--; if
				 * (currentNation!=-1) count[currentNation]++; }
				 */
				if (options[i]) {
					if (optionString.length() == 0)
						optionString += Firebalance.getNationName((byte) i, false);
					else
						optionString += ", " + Firebalance.getNationName((byte) i, false) + ChatColor.RESET;
				}
			}
			sender.sendMessage("Available options: " + optionString + ".");
			return false;
		}
		if (sender instanceof Player) {
			Player player = (Player) sender;
			byte nationValue = -1;
			boolean setKing = true;
			boolean setKingQuery = true;
			boolean noChange = false;
			PlayerSpec result = null;
			nationValue = Firebalance.getNationByte(args[0]);
			if (nationValue == -1) {
				Messenger.send(player, "&cThe computer didn't understand, so I guess you're a freelancer now.");
				nationValue = 0;
			}
			if (nationValue == 0) {
				setKingQuery = false;
				setKing = false;
			}
			String nationString = "the freelancers";
			String nationColor = ChatColor.WHITE + "";
			for (PlayerSpec s : Firebalance.playerSpecList) {
				if (s.getName().equals(player.getName())) {
					// TODO readd a more limited BOP check
					/*
					 * count[nationValue]++; if (s.getNation()!=-1)
					 * count[s.getNation()]--; if
					 * (nationValue!=0&&count[nationValue]>Math.ceil(1.5*Math.
					 * min(Math.min(count[1], count[2]), count[4]))) {
					 * player.sendMessage(
					 * "You can't join this nation. It would break the balance of power."
					 * ); noChange=true; count[nationValue]--; if
					 * (s.getNation()!=-1) count[s.getNation()]++; } else if
					 * (nationValue==0&&count[nationValue]>Math.ceil(2*Math.min(
					 * Math.min(count[1], count[2]), count[4]))) {
					 * player.sendMessage(
					 * "You can't play freelance, there are too many.");
					 * noChange=true; count[nationValue]--; if
					 * (s.getNation()!=-1) count[s.getNation()]++; } else {
					 */
					if (s.getNation() != nationValue) {
						s.setKing(0);
						s.setNation(nationValue);
					} else {
						player.sendMessage("Changing from your current nation to your current nation.");
						setKingQuery = false;
						setKing = false;
						noChange = true;
					}
					if (Firebalance.getNationName(nationValue, true) != null) {
						nationString = Firebalance.getNationName(nationValue, true);
						nationColor = Firebalance.getNationColor(nationValue, true);
					}
					result = s;
					// }
				}
			}
			if (setKingQuery == true) {
				for (PlayerSpec s : Firebalance.playerSpecList) {
					if (s.getNation() == nationValue && !s.getName().equals(player.getName())) {
						setKing = false;
					}
				}
			}
			if (setKing) {
				result.setKing(1);
				Bukkit.broadcastMessage(
						nationColor + player.getName() + " has claimed the throne of " + nationString + "!");
			} else if (!noChange)
				Bukkit.broadcastMessage(nationColor + player.getName() + " has joined " + nationString + "!");
		}
		return true;
	}
}
