package hydrogenn.mobBorder;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGetMobBuff implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] arg3) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Cannot do as non-player");
			return true;
		}
		Player player = (Player) sender;
		int pLevel = player.getLevel();
		int mLevel = MobBorderPlugin.getLevelByLocation(player.getLocation());
		
		int mBuff;
		if (pLevel < mLevel) {
			mBuff = 100 + 25*(mLevel - pLevel);
		}
		else {
			mBuff = 100;
		}
		player.sendMessage("Mob level: "+mLevel+" (Buff at "+mBuff+"%)");
		return true;
	}

}
