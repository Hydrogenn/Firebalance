package hydrogenn.kingdoms.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import hydrogenn.kingdoms.Kingdom;
import hydrogenn.kingdoms.PlayerSpec;

public class CommandInvite extends CommandClass {
	
	public CommandInvite() {super("invite","<player>","Invite other people. Must be leader.");}

	@Override
	public boolean run(CommandSender sender, String[] args) {
		if (args.length < 1) return false;
		if (!(sender instanceof Player)) {
			sender.sendMessage("Negatory. That's not a thing that you can do.");
		}
		else {
			Player player = (Player) sender;
			PlayerSpec spec = PlayerSpec.getSpec(player);
			Kingdom kingdom = spec.getKingdom();
			if (kingdom == null) {
				player.sendMessage("Invite them to where, exactly?");
			}
			else if (!kingdom.isLeader(player.getUniqueId())) {
				sender.sendMessage("You cannot do that! You aren't the leader.");
			}
			else {
				Player target = Bukkit.getPlayer(args[0]);
				if (target == null) {
					sender.sendMessage("That player isn't online.");
				}
				if (kingdom.isInvited(target.getUniqueId())) {
					sender.sendMessage("They're already invited, don't worry!");
				}
				else {
					kingdom.invite(target.getUniqueId());
					target.sendMessage("You have been invited to "+kingdom.getName()+". You may join at any time.");
				}
			}
		}
		return true;
	}
}
