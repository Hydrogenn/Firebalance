package hydrogenn.kingdoms.command;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import hydrogenn.kingdoms.Kingdom;
import hydrogenn.kingdoms.PlayerSpec;

public class CommandCreate extends CommandClass {

	public CommandCreate() {super("create","<name>","Create a name for your people.");}

	@Override
	public boolean run(CommandSender sender, String[] args) {
		if (args.length < 1) return false;
		String name = StringUtils.join(args," ");
		if (!(sender instanceof Player)) {
			sender.sendMessage("Sorry! That's not your business, commando.");
		}
		else {
			Player player = (Player) sender;
			PlayerSpec spec = PlayerSpec.getSpec(player);
			if (Kingdom.get(name) != null) {
				player.sendMessage("That name sounds a bit familiar to me.");
			}
			else {
				spec.joinKingdom(new Kingdom(args[0], spec.getUuid()));
				player.sendMessage("Successfully created "+args[0]+".");
			}
		}
		return true;
	}

}
