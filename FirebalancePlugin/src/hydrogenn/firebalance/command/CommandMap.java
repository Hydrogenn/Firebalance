
package hydrogenn.firebalance.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import hydrogenn.firebalance.ChunkSpec;
import hydrogenn.firebalance.Firebalance;
import hydrogenn.firebalance.utils.Messenger;

public class CommandMap implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Location c = player.getLocation();
			int cx = c.getChunk().getX();
			int cz = c.getChunk().getZ();
			int dx = 26;
			int dz = 3;
			boolean isNether = player.getWorld().getName().contains("_nether");
			List<StringBuilder> output = new ArrayList<>();
			if (isNether) {
				cx *= 8;
				cz *= 8;
			}
			if (args.length > 1) {
				try {
					if (Integer.parseInt(args[0]) % 2 != 0) {
						dx = (Integer.parseInt(args[0]) - 1) / 2;
					}
					if (Integer.parseInt(args[1]) % 2 != 0) {
						dz = (Integer.parseInt(args[1]) - 1) / 2;
					}
				} catch (NumberFormatException e) {
				}
			}
			for (int z = 0; z <= dz * 2 + 1; z++) {
				output.add(new StringBuilder());
				if (z == 0)
					for (int x = 0; x < dx * 2 + 1; x++) {
						output.get(z).append("&f-");
					}
				if (z != 0)
					for (int x = 0; x < dx * 2 + 1; x++) {
						output.get(z).append("&0-");
					}
			}
			for (ChunkSpec s : ChunkSpec.list) {
				if (s.getX() <= cx + dx && s.getX() >= cx - dx && s.getZ() <= cz + dz && s.getZ() >= cz - dz) {
					int x = s.getX() - cx + dx;
					int z = s.getZ() - cz + dz;
					char ch = 'o';
					if (s.getX() == cx && s.getZ() == cz) {
						ch = 'O';
					}
					output.get(z + 1).replace(x * 3, x * 3 + 3, Firebalance.getNationColor(s.getNation(), false) + ch);
				}
			}
			for (StringBuilder o : output) {
				Messenger.send(player, o.toString());
			}
		}
		return true;
	}
}