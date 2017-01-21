package hydrogenn.firebalance.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import hydrogenn.firebalance.PlayerSpec;
import hydrogenn.firebalance.utils.Messenger;

public class CommandAggressive implements CommandExecutor {
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
        	Player player = (Player) sender;
        	if (PlayerSpec.aggressives.contains(player.getUniqueId())!=true) {
        		PlayerSpec.markAsAggressive(player.getUniqueId());
    			Messenger.send(player, "&cYou are now aggresive. Kicks and bans are enabled.");
        	} else {
        		PlayerSpec.markAsPassive(player.getUniqueId());
        		Messenger.send(player, "&aYou are now passive. You will not ban or kick other passive players.");
        	}
        }
        return true;
    }
}
