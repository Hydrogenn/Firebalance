package hydrogenn.kingdoms.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import hydrogenn.kingdoms.Kingdom;
import hydrogenn.kingdoms.PlayerSpec;
import net.md_5.bungee.api.ChatColor;

public class CommandCapital extends CommandClass {
	
	public CommandCapital()  {super("capital","","Sets the capital. Must be leader.");}

	@Override
	public boolean run(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Sorry, I can't seem to find your location. Because I never tried, because I'm not dumb.");
		}
		else {
			Player player = (Player) sender;
			PlayerSpec spec = PlayerSpec.getSpec(player);
			Kingdom kingdom = spec.getKingdom();
			if (kingdom == null) {
				player.sendMessage("Okay, setting the spawn of "+ChatColor.ITALIC+"nobody"+ChatColor.RESET+" to right here.");
			}
			else {
				if (!kingdom.isLeader(player.getUniqueId())) {
					player.sendMessage("This sets the capital. Which you cannot do, peasant.");
				}
				else {
					player.sendMessage("Spawn is now here, and not anywhere else.");
					kingdom.setSpawn(player.getLocation());
				}
			}
		}
		return true;
	}
	
}
