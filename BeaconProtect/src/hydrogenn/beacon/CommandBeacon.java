package hydrogenn.beacon;

import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import hydrogenn.beacon.file.BeaconSpec;
import hydrogenn.beacon.lib.TimeLib;
import net.md_5.bungee.api.ChatColor;

public class CommandBeacon implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command whoUsesThese, String whatEvenDoesThisDo, String[] args) {
		if (args.length < 1) return false;
		if (args[0].equals("check")) return check(sender);
		if (args[0].equals("sabotage")) return sabotage(sender);
		if (args[0].equals("help")) return help(sender);
		return false;
	}
	
	public boolean help(CommandSender sender) {
		sender.sendMessage(ChatColor.BLUE + "sabotage: " + ChatColor.WHITE + "Toggle between repairing and damaging beacons with the items.");
		sender.sendMessage(ChatColor.BLUE + "check:" + ChatColor.WHITE + "Check the status of beacons that concern you.");
		return true;
	}

	public boolean check(CommandSender sender) {
		Iterator<BeaconSpec> iter = BeaconSpec.iterator();
		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		}
		while ( iter.hasNext() ) {
			BeaconSpec beaconSpec = iter.next();
			if (player == null || beaconSpec.isOwner(player) || beaconSpec.inRange(player.getLocation())) {
				Location loc = beaconSpec.getLocation();
				ChatColor chatColor = (player != null && beaconSpec.isOwner(player) ? ChatColor.GREEN : ChatColor.DARK_GREEN);
				sender.sendMessage( chatColor + 
						"X: " + loc.getBlockX() + ", Z:" + loc.getBlockZ() + " " +
						(beaconSpec.isEnabled() ?
							"Duration left: " + TimeLib.breakdown(beaconSpec.getDuration())
						:
							ChatColor.RED + "(Not enabled!)"));
			}
		}
		return true;
	}
	
	public boolean sabotage(CommandSender sender) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (BeaconSpec.toggleSabotage(player)) {
				player.sendMessage("You are now sabotaging beacons.");
			}
			else {
				player.sendMessage("You are now repairing beacons.");
			}
		}
		return true;
	}
	
}
