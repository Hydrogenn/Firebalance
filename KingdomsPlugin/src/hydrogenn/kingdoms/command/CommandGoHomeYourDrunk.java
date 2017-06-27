package hydrogenn.kingdoms.command;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import hydrogenn.kingdoms.Kingdom;
import hydrogenn.kingdoms.PlayerSpec;

public class CommandGoHomeYourDrunk extends CommandClass {

	public CommandGoHomeYourDrunk() {super("home", "","Teleport back home.");}

	@Override
	public boolean run(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Can you get off my back, m8? You're a console. Or a command block. Whatever. You know I never even wanted to add this bit. I didn't even want this plugin. Why'd I make it? Because someone else wanted it, and that someone else was on my server. Back to you. You don't have a home. You're a hobo. Now get outta here.");
		}
		else {
			Player player = (Player) sender;
			PlayerSpec spec = PlayerSpec.getSpec(player);
			Kingdom kingdom = spec.getKingdom();
			if (kingdom == null) {
				player.sendMessage("What home? Hobo.");
			} else {
				Location spawn = kingdom.getSpawn();
				if (spawn == null) {
					player.sendMessage("Your people don't have a home. Nag your leader about that.");
				} else {
					player.teleport(spawn);
					//TODO teleport in 15 seconds if no damage is taken
				}
			}
		}
		return true;
	}

}
