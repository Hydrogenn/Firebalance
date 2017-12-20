package hydrogenn.mobBorder;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGetMobBuff implements CommandExecutor {

	MobBorderPlugin plugin;
	
	CommandGetMobBuff(MobBorderPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] arg3) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Cannot do as non-player");
			return true;
		}
		Player player = (Player) sender;
		int pLevel = player.getLevel();
		int mLevel = plugin.getLevelByLocation(player.getLocation());
		
		String mBuff = plugin.getDisplayBuff(mLevel,pLevel);
		player.sendMessage("Mob level: "+mLevel+" ("+mBuff+")");
		return true;
	}

}
