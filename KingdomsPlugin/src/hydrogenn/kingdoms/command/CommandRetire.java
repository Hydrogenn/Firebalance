package hydrogenn.kingdoms.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import hydrogenn.kingdoms.Kingdom;
import hydrogenn.kingdoms.PlayerSpec;

public class CommandRetire extends CommandClass {

	public CommandRetire() {
		super("retire", "", "Stop being a leader.");
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean run(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("no");
		}
		else {
			Player player = (Player) sender;
			PlayerSpec spec = PlayerSpec.getSpec(player);
			Kingdom kingdom = spec.getKingdom();
			if (kingdom == null || !kingdom.hasAllPermission(player.getUniqueId())) {
				player.sendMessage("Retire from what, exactly?");
			} else {
				kingdom.removeFromChain(player.getUniqueId());
				player.sendMessage("Done and done! Free at last! Now you can do whatever you want.");
			}
		}
		return true;
	}

}
